package org.qiyu.live.im.core.server.consumer;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.service.IMsgAckCheckService;
import org.qiyu.live.im.core.server.service.IRouterHandlerService;
import org.qiyu.live.im.dto.ImMsgBody;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ImAckConsumer implements InitializingBean {

    @Resource
    private IMsgAckCheckService msgAckCheckService;
    @Resource
    private IRouterHandlerService routerHandlerService;
    /**
     * RocketMQ consumer 的 NameServer 兜底值。
     * 如果 Nacos 或配置绑定暂时没起来，这里也能给一个默认地址。
     */
    @Value("${qiyu.rmq.consumer.nameServer:host.docker.internal:9876}")
    private String consumerNameServer;

    /**
     * RocketMQ consumer 的分组名兜底值。
     */
    @Value("${qiyu.rmq.consumer.groupName:${spring.application.name:qiyu-live-user-provider}}")
    private String consumerGroupName;

    @Override
    public void afterPropertiesSet() {
        initConsumer();
    }

    public void initConsumer() {
        // 先手动兜底，避免注入值为空。
        String resolvedNameServer = consumerNameServer;
        if (resolvedNameServer == null || resolvedNameServer.isBlank()) {
            resolvedNameServer = "host.docker.internal:9876";
        }

        String resolvedGroupName = consumerGroupName;
        if (resolvedGroupName == null || resolvedGroupName.isBlank()) {
            resolvedGroupName = "qiyu-live-user-provider";
        }

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setVipChannelEnabled(false);
        //设置我们的namesrv地址
        consumer.setNamesrvAddr(resolvedNameServer);
        //声明消费组
        consumer.setConsumerGroup(resolvedGroupName);
        //每次只拉取一条消息
        consumer.setConsumeMessageBatchMaxSize(1);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        try {
            consumer.subscribe(ImCoreServerProviderTopicNames.QIYU_LIVE_IM_ACK_MSG_TOPIC , "*");
            consumer.setMessageListener((MessageListenerConcurrently)(msg, context) -> {
                String json = new String(msg.get(0).getBody());
                ImMsgBody imMsgBody = JSON.parseObject(json, ImMsgBody.class);
                int retryTime = msgAckCheckService.getMsgAckTimes(imMsgBody.getMsgId(),imMsgBody.getUserId(), imMsgBody.getAppId());
                log.info("retryTime is {}", retryTime);
                if(retryTime < 0){
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                //只支持一次重发
                if (retryTime < 2){
                    msgAckCheckService.recordMsgAck(imMsgBody, retryTime + 1);
                    msgAckCheckService.sendDelayMsg(imMsgBody);
                    routerHandlerService.sendMsgToClient(imMsgBody);
                }else{
                    msgAckCheckService.doMsgAck(imMsgBody);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                });
            consumer.start();

            // 新写法：真正启动成功才打印 info。
            log.info("RocketMQ consumer started, nameServer={}, groupName={}", resolvedNameServer, resolvedGroupName);
        } catch (Exception e) {
            // 新写法：先记录警告，不阻断整个服务启动。
            log.warn("RocketMQ consumer start failed, service will continue. nameServer={}, groupName={}",
                    resolvedNameServer, resolvedGroupName, e);
        }
    }
}
