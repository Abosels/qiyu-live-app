package org.qiyu.live.living.provider.consumer;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.qiyu.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.dto.PkScoreMQ;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.qiyu.live.im.router.interfaces.contants.ImMsgBizCodeEnum;
import org.qiyu.live.living.interfaces.dto.PkStateDTO;
import org.qiyu.live.living.provider.dao.mapper.IPkScoreRecordMapper;
import org.qiyu.live.living.provider.dao.po.PkScoreRecordPO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.qiyu.live.living.provider.service.PkEventBroadcastService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/** Consumes PK score messages with independent MySQL and Redis idempotency. */
@Slf4j
@Component
public class PkScoreConsumer implements InitializingBean {

    private static final long SCORE_DEDUPE_TTL_SECONDS = 4 * 60 * 60;
    private static final DefaultRedisScript<List> PK_SCORE_SCRIPT;

    static {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/pk_score.lua"));
        script.setResultType(List.class);
        PK_SCORE_SCRIPT = script;
    }

    @Resource private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource private RedisTemplate<Object, Object> redisTemplate;
    @Resource private LivingProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource private IPkScoreRecordMapper pkScoreRecordMapper;
    @Resource private ILivingRoomService livingRoomService;
    @Resource private PkEventBroadcastService pkEventBroadcastService;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setVipChannelEnabled(false);
        consumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        consumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + PkScoreConsumer.class.getSimpleName());
        consumer.setConsumeMessageBatchMaxSize(10);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(GiftProviderTopicNames.PK_SCORE, "");
        consumer.setMessageListener((MessageListenerConcurrently) (messages, context) -> {
            for (MessageExt message : messages) {
                ConsumeConcurrentlyStatus status = consume(message);
                if (status == ConsumeConcurrentlyStatus.RECONSUME_LATER) {
                    return status;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        log.info("[PkScoreConsumer] started, namesrvAddr={}", rocketMQConsumerProperties.getNameSrv());
    }

    ConsumeConcurrentlyStatus consume(MessageExt message) {
        PkScoreMQ scoreMessage;
        try {
            scoreMessage = JSON.parseObject(new String(message.getBody(), StandardCharsets.UTF_8), PkScoreMQ.class);
        } catch (Exception e) {
            log.error("[PkScoreConsumer] invalid MQ JSON, msgId={}", message.getMsgId(), e);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        if (!isValid(scoreMessage)) {
            log.error("[PkScoreConsumer] invalid PK score MQ, msgId={}", message.getMsgId());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        PkScoreRecordPO record = createOrLoadRecord(scoreMessage);
        if (record == null) {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        if (Integer.valueOf(1).equals(record.getRedisSyncStatus())) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        try {
            List<?> result = redisTemplate.execute(PK_SCORE_SCRIPT,
                    Arrays.asList(cacheKeyBuilder.buildPkState(scoreMessage.getPkId()),
                            cacheKeyBuilder.buildPkScoreDedupe(scoreMessage.getPkId(), scoreMessage.getBizId())),
                    scoreMessage.getSide(), String.valueOf(scoreMessage.getAddScore()),
                    String.valueOf(SCORE_DEDUPE_TTL_SECONDS));
            if (result == null || result.isEmpty()) {
                markRetry(record, "pk_score.lua returned empty");
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            String resultCode = String.valueOf(result.getFirst());
            if ("-1".equals(resultCode)) {
                markInvalid(record, "PK is no longer running");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            if ("-2".equals(resultCode)) {
                markSynced(record);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            markSynced(record);
            PkStateDTO state = livingRoomService.queryCurrentPkState(scoreMessage.getRoomId());
            pkEventBroadcastService.broadcastState(state, ImMsgBizCodeEnum.LIVING_ROOM_PK_SCORE_UPDATE);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            log.error("[PkScoreConsumer] Redis score failed, pkId={}, bizId={}", scoreMessage.getPkId(), scoreMessage.getBizId(), e);
            markRetry(record, e.getMessage());
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    private PkScoreRecordPO createOrLoadRecord(PkScoreMQ message) {
        PkScoreRecordPO record = new PkScoreRecordPO();
        record.setPkId(message.getPkId());
        record.setBizId(message.getBizId());
        record.setSenderId(message.getSenderId());
        record.setReceiverId(message.getReceiverId());
        record.setRoomId(message.getRoomId());
        record.setSide(message.getSide());
        record.setGiftId(message.getGiftId());
        record.setGiftNum(message.getGiftNum() == null ? 1 : message.getGiftNum());
        record.setGiftPrice(0);
        record.setAddScore(message.getAddScore());
        record.setScoreStatus(1);
        record.setRedisSyncStatus(0);
        record.setRetryCount(0);
        record.setGiftCreateTime(message.getGiftCreateTime() == null ? LocalDateTime.now()
                : LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getGiftCreateTime()), ZoneId.systemDefault()));
        try {
            pkScoreRecordMapper.insert(record);
            return record;
        } catch (DuplicateKeyException ignored) {
            return pkScoreRecordMapper.selectOne(new LambdaQueryWrapper<PkScoreRecordPO>()
                    .eq(PkScoreRecordPO::getPkId, message.getPkId())
                    .eq(PkScoreRecordPO::getBizId, message.getBizId())
                    .last("limit 1"));
        }
    }

    private void markSynced(PkScoreRecordPO record) {
        updateRecord(record, 1, null, null);
    }

    private void markInvalid(PkScoreRecordPO record, String reason) {
        updateRecord(record, 1, reason, 0);
    }

    private void markRetry(PkScoreRecordPO record, String reason) {
        updateRecord(record, 2, reason, null);
    }

    /** Persists retry state without allowing a failed Redis call to fail the consumer thread. */
    private void updateRecord(PkScoreRecordPO record, int syncStatus, String error, Integer scoreStatus) {
        PkScoreRecordPO update = new PkScoreRecordPO();
        update.setId(record.getId());
        update.setRedisSyncStatus(syncStatus);
        update.setRetryCount((record.getRetryCount() == null ? 0 : record.getRetryCount()) + (syncStatus == 2 ? 1 : 0));
        update.setLastError(error == null ? null : error.substring(0, Math.min(500, error.length())));
        update.setScoreStatus(scoreStatus);
        pkScoreRecordMapper.updateById(update);
    }

    private boolean isValid(PkScoreMQ message) {
        return message != null && message.getPkId() != null && message.getBizId() != null && !message.getBizId().isBlank()
                && message.getRoomId() != null && message.getSenderId() != null && message.getSide() != null
                && ("A".equals(message.getSide()) || "B".equals(message.getSide()))
                && message.getAddScore() != null && message.getAddScore() >= 0;
    }
}
