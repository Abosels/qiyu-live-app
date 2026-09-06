package org.qiyu.live.gift.provider.consumer;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.qiyu.live.common.interfaces.topic.LivingProviderTopicNames;
import org.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.qiyu.live.gift.interfaces.ISkuStockInfoRpc;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class StartingLivingRoomConsumer implements InitializingBean {
    @Resource
    private ISkuStockInfoRpc skuStockInfoRpc;
    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        // 开播后预热该房间的库存缓存。
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName()
                + "_" + StartingLivingRoomConsumer.class.getSimpleName());
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        mqPushConsumer.subscribe(LivingProviderTopicNames.START_LIVING_ROOM,"");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently)(msgs,context) -> {
            for (MessageExt msg : msgs) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(new String(msg.getBody(), StandardCharsets.UTF_8));
                    Long anchorId = jsonObject.getLong("anchorId");
                    if (anchorId == null) {
                        log.error("[StartingLivingRoomConsumer] 开播消息缺少 anchorId, msgId={}", msg.getMsgId());
                        continue;
                    }
                    if (!skuStockInfoRpc.prepareStockInfo(anchorId)) {
                        // 房间查询或缓存预热失败时让 RocketMQ 重试，避免新 Hash Tag 没有库存数据。
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                } catch (Exception e) {
                    log.error("[StartingLivingRoomConsumer] 开播库存预热失败, msgId={}", msg.getMsgId(), e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        log.info("[StartingLivingRoomConsumer] consumer has started, namesrv={}", mqPushConsumer.getNamesrvAddr());
    }
}
