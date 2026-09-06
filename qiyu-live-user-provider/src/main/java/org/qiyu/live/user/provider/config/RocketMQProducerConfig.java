package org.qiyu.live.user.provider.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class RocketMQProducerConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(RocketMQProducerConfig.class);

    @Resource
    private RocketMQProducerProperties rocketMQProducerProperties;

    /**
     * RocketMQ producer 的 NameServer 兜底值。
     */
    @Value("${qiyu.rmq.producer.nameServer:host.docker.internal:9876}")
    private String producerNameServer;

    /**
     * RocketMQ producer 的分组名兜底值。
     */
    @Value("${qiyu.rmq.producer.groupName:${spring.application.name:qiyu-live-user-provider}}")
    private String producerGroupName;

    /**
     * 发送失败重试次数兜底值。
     */
    @Value("${qiyu.rmq.producer.retryTimes:3}")
    private Integer producerRetryTimes;

    /**
     * 发送消息超时兜底值。
     */
    @Value("${qiyu.rmq.producer.sendMsgTimeout:3000}")
    private Integer producerSendMsgTimeout;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public MQProducer mqProducer() {
        /**
         * 这里创建一个线程池，专门给 RocketMQ 异步发送消息用。
         * 线程池参数保留为原来的思路，只把注释整理成 UTF-8 中文。
         */
        ThreadPoolExecutor asyncThreadPoolExecutor = new ThreadPoolExecutor(100, 150, 3, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(applicationName + ":rmq-producer" + ThreadLocalRandom.current().nextInt(1000));
                return thread;
            }
        });

        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
        try {
            // 老写法：直接从配置对象读取，配置绑定失败时容易拿到 null。
            // defaultMQProducer.setNamesrvAddr(rocketMQProducerProperties.getNameServer());
            // defaultMQProducer.setProducerGroup(rocketMQProducerProperties.getGroupName());
            // defaultMQProducer.setRetryTimesWhenSendFailed(rocketMQProducerProperties.getRetryTimes());
            // defaultMQProducer.setRetryTimesWhenSendAsyncFailed(rocketMQProducerProperties.getRetryTimes());
            // defaultMQProducer.setSendMsgTimeout(rocketMQProducerProperties.getSendMsgTimeout());

            // 新写法：直接使用兜底后的字符串配置。
            defaultMQProducer.setNamesrvAddr(producerNameServer);
            defaultMQProducer.setProducerGroup(producerGroupName);
            defaultMQProducer.setRetryTimesWhenSendFailed(producerRetryTimes);
            defaultMQProducer.setRetryTimesWhenSendAsyncFailed(producerRetryTimes);
            defaultMQProducer.setSendMsgTimeout(producerSendMsgTimeout);
            defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
            defaultMQProducer.setAsyncSenderExecutor(asyncThreadPoolExecutor);
            defaultMQProducer.start();

            // 老写法：直接打印配置对象中的 nameServer。
            // LOGGER.info("RocketMQ producer started, nameServer is {}", rocketMQProducerProperties.getNameServer());

            // 新写法：打印最终生效的 nameServer，方便你排查。
            LOGGER.info("RocketMQ producer started, nameServer is {}", producerNameServer);
        } catch (MQClientException e) {
            // 老写法：这里会直接抛异常把整个 Spring 容器打断。
            // throw new RuntimeException(e);

            // 新写法：先记录警告，不阻断整个服务启动。
            LOGGER.warn("RocketMQ producer start failed, service will continue. nameServer={}", producerNameServer, e);
        }
        return defaultMQProducer;
    }
}