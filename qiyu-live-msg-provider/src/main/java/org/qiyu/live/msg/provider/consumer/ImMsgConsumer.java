package org.qiyu.live.msg.provider.consumer;

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
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.msg.provider.consumer.handler.IMessageHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ImMsgConsumer implements InitializingBean {

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private IMessageHandler messageHandler;

    //记录每个用户连接的im服务器地址，然后根据im服务器的链接地址去做具体的机器调用
    //基于mq广播思路去做，可能会有消息风暴，100台im机器，99%的mq消息都是无效的，
    //加入一个路由层 router中专的设计，router就是一个dubbo的rpc层
    //A--> B  ：  im-core-server -> msg-provider（持久化）-> im-core-server -> 通知到b
    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setVipChannelEnabled(false);
        consumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        consumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + ImMsgConsumer.class.getSimpleName());
        //一次从broker中拉取10条消息到本地内存中进行消费
        consumer.setConsumeMessageBatchMaxSize(10);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听im发送过来的业务消息topic
        consumer.subscribe(ImCoreServerProviderTopicNames.QIYU_LIVE_IM_BIZ_MSG_TOPIC,"");
        consumer.setMessageListener((MessageListenerConcurrently)(msgs,context) -> {
            for(MessageExt msg : msgs){
                ImMsgBody imMsgBody = JSON.parseObject(new String(msg.getBody(), StandardCharsets.UTF_8), ImMsgBody.class);
                messageHandler.onMsgReceive(imMsgBody);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        log.info("Consumer Started, namesrvAddr:{}", rocketMQConsumerProperties.getNameSrv());
    }
}
