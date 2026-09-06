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
import org.qiyu.live.gift.provider.service.IRedPacketConfigService;
import org.qiyu.live.gift.provider.service.bo.SendRedPacketBO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

/** 接收红包领取消息，并完成账户入账和统计。 */
@Slf4j
@Configuration
public class ReceiveRedPacketConsumer implements InitializingBean {

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private IRedPacketConfigService redPacketConfigService;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setVipChannelEnabled(false);
        consumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        consumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName()
                + "_" + ReceiveRedPacketConsumer.class.getSimpleName());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(GiftProviderTopicNames.RECEIVE_RED_PACKET, "");
        consumer.setMessageListener((MessageListenerConcurrently) (messages, context) -> {
            for (MessageExt message : messages) {
                SendRedPacketBO sendRedPacketBO;
                try {
                    sendRedPacketBO = JSON.parseObject(
                            new String(message.getBody(), StandardCharsets.UTF_8), SendRedPacketBO.class);
                } catch (Exception e) {
                    log.error("[ReceiveRedPacketConsumer] 非法红包领取消息, msgId={}", message.getMsgId(), e);
                    continue;
                }
                if (sendRedPacketBO == null || sendRedPacketBO.getReqDTO() == null
                        || sendRedPacketBO.getReqDTO().getUserId() == null
                        || sendRedPacketBO.getReqDTO().getConfigCode() == null
                        || sendRedPacketBO.getPrice() == null || sendRedPacketBO.getPrice() <= 0
                        || sendRedPacketBO.getBizId() == null || sendRedPacketBO.getBizId().isBlank()) {
                    log.error("[ReceiveRedPacketConsumer] 红包领取消息缺少必要字段, msgId={}", message.getMsgId());
                    continue;
                }
                try {
                    redPacketConfigService.receiveRedPacketHandle(
                            sendRedPacketBO.getReqDTO(), sendRedPacketBO.getPrice(), sendRedPacketBO.getBizId());
                } catch (Exception e) {
                    log.error("[ReceiveRedPacketConsumer] 红包入账失败，等待 RocketMQ 重试, msgId={}", message.getMsgId(), e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        // 启动日志用于本地排查消费者是否已订阅红包主题，不记录任何红包业务数据。
        log.info("[ReceiveRedPacketConsumer] consumer has started, namesrv={}",
                rocketMQConsumerProperties.getNameSrv());
    }
}
