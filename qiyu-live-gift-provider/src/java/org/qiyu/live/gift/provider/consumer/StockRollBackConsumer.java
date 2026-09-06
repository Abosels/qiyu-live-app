package org.qiyu.live.gift.provider.consumer;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.qiyu.live.gift.dto.RollBackStockDTO;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

/** 消费延迟消息，关闭未支付订单并回补其预扣库存。 */
@Slf4j
@Configuration
public class StockRollBackConsumer implements InitializingBean {

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private ISkuStockInfoService skuStockInfoService;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setVipChannelEnabled(false);
        consumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        consumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName()
                + "_" + StockRollBackConsumer.class.getSimpleName());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(GiftProviderTopicNames.ROLL_BACK_STOCK, "");
        consumer.setMessageListener((MessageListenerConcurrently) (messages, context) -> {
            for (MessageExt message : messages) {
                RollBackStockDTO rollBackStockDTO;
                try {
                    rollBackStockDTO = JSON.parseObject(
                            new String(message.getBody(), StandardCharsets.UTF_8), RollBackStockDTO.class);
                } catch (Exception e) {
                    log.error("[StockRollBackConsumer] 非法库存回滚消息, msgId={}", message.getMsgId(), e);
                    continue;
                }
                if (rollBackStockDTO == null || rollBackStockDTO.getOrderId() == null
                        || rollBackStockDTO.getUserId() == null) {
                    log.error("[StockRollBackConsumer] 库存回滚消息缺少订单信息, msgId={}", message.getMsgId());
                    continue;
                }
                try {
                    skuStockInfoService.rollBackStockHandler(rollBackStockDTO);
                } catch (Exception e) {
                    log.error("[StockRollBackConsumer] 库存回滚失败，等待 RocketMQ 重试, orderId={}",
                            rollBackStockDTO.getOrderId(), e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        log.info("[StockRollBackConsumer] consumer has started");
    }


}
