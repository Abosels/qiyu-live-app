package org.qiyu.live.living.provider.consumer;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.qiyu.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class LivingRoomOFFlineConsumer implements InitializingBean {
    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private ILivingRoomService livingRoomService;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setVipChannelEnabled(false);
        consumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        consumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + LivingRoomOnlineConsumer.class.getSimpleName());
        //一次从broker中拉取10条消息到本地内存中进行消费
        consumer.setConsumeMessageBatchMaxSize(10);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听im发送过来的业务消息topic
        consumer.subscribe(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC,"");
        consumer.setMessageListener((MessageListenerConcurrently)(msgs, context) -> {
            for(MessageExt msg : msgs){
                ImOfflineDTO imOfflineDTO = JSON.parseObject(new String(msg.getBody()), ImOfflineDTO.class);
                livingRoomService.userOfflineHandler(imOfflineDTO);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        log.info("Consumer Started, namesrvAddr:{}", rocketMQConsumerProperties.getNameSrv());
    }
}
