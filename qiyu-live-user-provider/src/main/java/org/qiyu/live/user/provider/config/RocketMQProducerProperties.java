package org.qiyu.live.user.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 生产者的配置信息
 * 这个类负责创建并启动一个 RocketMQ 生产者 MQProducer，然后交给 Spring 容器管理。
 */
@ConfigurationProperties(prefix = "qiyu.rmq.producer")
@Configuration
@Data
public class RocketMQProducerProperties {
    //rocketmq的nameServer的地址
    private String nameServer;
    //分组名称
    private String groupName;
    //消息重发次数
    private Integer retryTimes;
    //消息发送超时时间
    private Integer sendMsgTimeout;
}
