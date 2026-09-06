package org.qiyu.live.user.provider.config;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.user.interfaces.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import static org.qiyu.live.common.interfaces.topic.UserProviderTopicNames.USER_UPDATE_CACHE;

@Configuration
public class RocketMQConsumerConfig implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQConsumerConfig.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;

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

    @Resource
    private RedisTemplate<String, UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

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

        // 老写法：直接从配置对象读取，配置绑定失败时容易出现 null。
        // consumer.setNamesrvAddr(rocketMQConsumerProperties.getNameServer());
        // consumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName());

        // 新写法：直接使用兜底后的字符串配置。
        consumer.setNamesrvAddr(resolvedNameServer);
        consumer.setConsumerGroup(resolvedGroupName);
        consumer.setConsumeMessageBatchMaxSize(1);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        try {
            consumer.subscribe(USER_UPDATE_CACHE, "*");
            consumer.setMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                                ConsumeConcurrentlyContext context) {
                    String body = new String(msgs.get(0).getBody(), StandardCharsets.UTF_8);
                    UserDTO userDTO = JSON.parseObject(body, UserDTO.class);
                    if (userDTO != null && userDTO.getUserId() != null) {
                        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId());
                        redisTemplate.delete(key);
                        LOGGER.info("Delayed cache delete finished, userId={}, key={}", userDTO.getUserId(), key);
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();

            // 新写法：真正启动成功才打印 info。
            LOGGER.info("RocketMQ consumer started, nameServer={}, groupName={}", resolvedNameServer, resolvedGroupName);
        } catch (Exception e) {
            // 老写法：这里会直接抛异常把整个 Spring 容器打断。
            // throw new RuntimeException("Failed to start RocketMQ consumer", e);

            // 新写法：先记录警告，不阻断整个服务启动。
            LOGGER.warn("RocketMQ consumer start failed, service will continue. nameServer={}, groupName={}",
                    resolvedNameServer, resolvedGroupName, e);
        }
    }
}