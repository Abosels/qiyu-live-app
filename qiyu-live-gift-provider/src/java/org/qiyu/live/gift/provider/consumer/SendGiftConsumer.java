package org.qiyu.live.gift.provider.consumer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.dto.PkScoreMQ;
import org.qiyu.live.common.interfaces.dto.SendGiftMQ;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.qiyu.live.im.contants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.interfaces.contants.ImMsgBizCodeEnum;
import org.qiyu.live.im.router.interfaces.rpc.ImRouterRpc;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.PkStateDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 发送礼物消费者。
 *
 * <p>消费 SEND_GIFT Topic，负责：</p>
 * <ol>
 *   <li>调用资金服务幂等扣款</li>
 *   <li>普通房间 → 单房间广播礼物特效</li>
 *   <li>PK 房间 → 双房间广播 PK 礼物特效 + 发送 PK_SCORE_TOPIC</li>
 *   <li>扣款失败 → 推送失败通知给送礼人</li>
 * </ol>
 */
@Slf4j
@Configuration
public class SendGiftConsumer implements InitializingBean {

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    @DubboReference(check = false)
    private IQiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;
    @DubboReference(check = false)
    private ImRouterRpc imRouterRpc;
    @DubboReference(check = false)
    private ILivingRoomRpc livingRoomRpc;
    @Resource
    private MQProducer mqProducer;


    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        // 老版本中会开启，新版本的mq不需要使用到
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + SendGiftConsumer.class.getSimpleName());
        // 一次从broker中拉取10条消息到本地内存中进行消费
        mqPushConsumer.setConsumeMessageBatchMaxSize(10);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 监听礼物缓存数据更新的行为
        mqPushConsumer.subscribe(GiftProviderTopicNames.SEND_GIFT, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msgExt : msgs) {
                SendGiftMQ sendGiftMQ;
                try {
                    sendGiftMQ = JSON.parseObject(
                            new String(msgExt.getBody(), StandardCharsets.UTF_8),
                            SendGiftMQ.class);
                } catch (Exception e) {
                    log.error("[SendGiftConsumer] invalid MQ json, msgId={}",
                            msgExt.getMsgId(), e);
                    continue;
                }

                if (sendGiftMQ == null
                        || sendGiftMQ.getUserId() <= 0
                        || sendGiftMQ.getGiftId() == null
                        || sendGiftMQ.getGiftId() <= 0
                        || sendGiftMQ.getPrice() == null
                        || sendGiftMQ.getPrice() <= 0
                        || sendGiftMQ.getRoomId() == null
                        || sendGiftMQ.getRoomId() <= 0
                        || sendGiftMQ.getReceiveId() == null
                        || sendGiftMQ.getReceiveId() <= 0
                        || sendGiftMQ.getUuId() == null
                        || sendGiftMQ.getUuId().isBlank()) {
                    log.error("[SendGiftConsumer] invalid gift MQ, msgId={}, body={}",
                            msgExt.getMsgId(),
                            new String(msgExt.getBody(), StandardCharsets.UTF_8));
                    continue;
                }
                // Redis 五分钟锁不能作为资金幂等依据，使用 bank-provider 的数据库唯一流水。
                AccountTradeReqDTO accountTradeReqDTO = new AccountTradeReqDTO();
                accountTradeReqDTO.setUserId(sendGiftMQ.getUserId());
                accountTradeReqDTO.setNumber(sendGiftMQ.getPrice());
                accountTradeReqDTO.setBizId(sendGiftMQ.getUuId());
                AccountTradeRespDTO tradeRespDTO = qiyuCurrencyAccountRpc.consumeForSendGift(accountTradeReqDTO);

                /*
                 * 扣币成功后，先取 roomId。
                 * 调 living 的 RPC 查 PK 状态：
                 * - PK 房间 → 双房间广播 + 发送 PK_SCORE_TOPIC
                 * - 普通房间 → 单房间广播（原有逻辑）
                 */
                if (tradeRespDTO.isSuccess()) {
                    // 特效已投递检查（Redis 去重，防止 MQ 重试重复播放）
                    String effectSentKey = "gift-effect-sent:" + sendGiftMQ.getUuId();
                    if (Boolean.TRUE.equals(redisTemplate.hasKey(effectSentKey))) {
                        log.info("[SendGiftConsumer] effect already delivered, skip broadcast, uuId={}",
                                sendGiftMQ.getUuId());
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    if (!tradeRespDTO.isFirstProcessed()) {
                        log.info("[SendGiftConsumer] duplicate gift consume, retry effect delivery, uuId={}",
                                sendGiftMQ.getUuId());
                    }

                    Integer roomId = sendGiftMQ.getRoomId();

                    // 构建礼物特效数据（PK 和非 PK 共用）
                    JSONObject giftEffectData = new JSONObject();
                    giftEffectData.put("roomId", roomId);
                    giftEffectData.put("sendUserId", sendGiftMQ.getUserId());
                    giftEffectData.put("receiveUserId", sendGiftMQ.getReceiveId());
                    giftEffectData.put("giftId", sendGiftMQ.getGiftId());
                    giftEffectData.put("giftName", sendGiftMQ.getGiftName());
                    giftEffectData.put("price", sendGiftMQ.getPrice());
                    giftEffectData.put("url", sendGiftMQ.getUrl());
                    giftEffectData.put("svgaUrl", sendGiftMQ.getSvgaUrl());
                    giftEffectData.put("uuId", sendGiftMQ.getUuId());

                    // === PK 检测：判断当前房间是否在 PK 中 ===
                    PkStateDTO pkState = livingRoomRpc.queryCurrentPkState(roomId);
                    if (pkState != null && "RUNNING".equals(pkState.getStatus())) {
                        // === PK 房间：双房间广播 + 发送 PK_SCORE_TOPIC ===

                        // 1. 确定本方是 A 还是 B
                        String side = roomId.equals(pkState.getRoomIdA()) ? "A" : "B";
                        // 本方加分数（第一版：price 即分数，后续可 × 倍率）
                        long addScore = sendGiftMQ.getPrice() != null ? sendGiftMQ.getPrice() : 0;

                        giftEffectData.put("pkId", pkState.getPkId().toString());
                        giftEffectData.put("side", side);

                        // 2. 两个房间在线用户合并去重
                        LivingRoomReqDTO roomReqA = new LivingRoomReqDTO();
                        roomReqA.setRoomId(pkState.getRoomIdA());
                        roomReqA.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                        LivingRoomReqDTO roomReqB = new LivingRoomReqDTO();
                        roomReqB.setRoomId(pkState.getRoomIdB());
                        roomReqB.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());

                        Set<Long> targetUserIdSet = new LinkedHashSet<>();
                        List<Long> usersA = livingRoomRpc.queryUserIdByRoomId(roomReqA);
                        List<Long> usersB = livingRoomRpc.queryUserIdByRoomId(roomReqB);
                        if (usersA != null) targetUserIdSet.addAll(usersA);
                        if (usersB != null) targetUserIdSet.addAll(usersB);
                        targetUserIdSet.add(sendGiftMQ.getUserId());
                        if (sendGiftMQ.getReceiveId() != null) {
                            targetUserIdSet.add(sendGiftMQ.getReceiveId());
                        }

                        // 3. 批量广播 PK 礼物特效到两个房间
                        List<ImMsgBody> broadcastMsgList = new ArrayList<>();
                        for (Long targetUserId : targetUserIdSet) {
                            ImMsgBody broadcastMsg = new ImMsgBody();
                            broadcastMsg.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                            broadcastMsg.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_PK_GIFT_EFFECT.getCode());
                            broadcastMsg.setUserId(targetUserId);
                            broadcastMsg.setMsgId("gift-effect:" + sendGiftMQ.getUuId() + ":" + targetUserId);
                            broadcastMsg.setData(giftEffectData.toJSONString());
                            broadcastMsgList.add(broadcastMsg);
                        }
                        imRouterRpc.batchSendMsg(broadcastMsgList);

                        // 4. 构造 PK_SCORE MQ 消息，由 PkScoreConsumer 消费完成计分
                        PkScoreMQ pkScoreMQ = new PkScoreMQ();
                        pkScoreMQ.setBizId(sendGiftMQ.getUuId());
                        pkScoreMQ.setPkId(pkState.getPkId());
                        pkScoreMQ.setSenderId(sendGiftMQ.getUserId());
                        pkScoreMQ.setReceiverId(sendGiftMQ.getReceiveId());
                        pkScoreMQ.setRoomId(roomId);
                        pkScoreMQ.setSide(side);
                        pkScoreMQ.setGiftId(sendGiftMQ.getGiftId());
                        pkScoreMQ.setGiftNum(1);
                        pkScoreMQ.setAddScore(addScore);
                        pkScoreMQ.setGiftCreateTime(System.currentTimeMillis());

                        // 发送 PK_SCORE 消息到 RocketMQ
                        Message pkScoreMsg = new Message();
                        pkScoreMsg.setTopic(GiftProviderTopicNames.PK_SCORE);
                        pkScoreMsg.setKeys(sendGiftMQ.getUuId());
                        pkScoreMsg.setBody(com.alibaba.fastjson2.JSON.toJSONBytes(pkScoreMQ));
                        try {
                            SendResult sendResult = mqProducer.send(pkScoreMsg);
                            if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
                                log.error("[SendGiftConsumer] PK_SCORE send failed, uuId={}", sendGiftMQ.getUuId());
                                // 不能确认当前 SEND_GIFT 消息；否则会出现扣币成功但 PK 永不计分。
                                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            }
                        } catch (Exception e) {
                            log.error("[SendGiftConsumer] PK_SCORE send error, uuId={}", sendGiftMQ.getUuId(), e);
                            // 资金扣减以 uuId 幂等，重试不会重复扣币；PK_SCORE 由 Consumer 再次去重。
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                        log.info("[SendGiftConsumer] PK gift — pkId={}, side={}, addScore={}, uuId={}",
                                pkState.getPkId(), side, addScore, sendGiftMQ.getUuId());

                    } else {
                        // === 普通房间：单房间广播（原有逻辑） ===
                        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
                        livingRoomReqDTO.setRoomId(roomId);
                        livingRoomReqDTO.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());

                        List<Long> onlineUserIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);

                        Set<Long> targetUserIdSet = new LinkedHashSet<>();
                        if (onlineUserIdList != null) {
                            targetUserIdSet.addAll(onlineUserIdList);
                        }
                        targetUserIdSet.add(sendGiftMQ.getUserId());
                        if (sendGiftMQ.getReceiveId() != null) {
                            targetUserIdSet.add(sendGiftMQ.getReceiveId());
                        }

                        List<ImMsgBody> broadcastMsgList = new ArrayList<>();
                        for (Long targetUserId : targetUserIdSet) {
                            ImMsgBody broadcastMsg = new ImMsgBody();
                            broadcastMsg.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                            broadcastMsg.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS.getCode());
                            broadcastMsg.setUserId(targetUserId);
                            broadcastMsg.setMsgId("gift-effect:" + sendGiftMQ.getUuId() + ":" + targetUserId);
                            broadcastMsg.setData(giftEffectData.toJSONString());
                            broadcastMsgList.add(broadcastMsg);
                        }
                        imRouterRpc.batchSendMsg(broadcastMsgList);
                    }

                    // IM 广播成功后标记已投递，后续 MQ 重试直接跳过
                    redisTemplate.opsForValue().set(effectSentKey, "1", 24, TimeUnit.HOURS);
                } else {
                    ImMsgBody imMsgBody = new ImMsgBody();
                    imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                    imMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode());
                    imMsgBody.setUserId(sendGiftMQ.getUserId());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msg", tradeRespDTO.getMsg());
                    imMsgBody.setData(jsonObject.toJSONString());

                    imRouterRpc.sendMsg(imMsgBody);
                }
                log.info("[SendGiftConsumer] msg is {}", msgExt);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        log.info("[SendGiftConsumer] mqPushConsumer is started, namesrvAddr is {}", rocketMQConsumerProperties.getNameSrv());
    }
}
