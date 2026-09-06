package org.qiyu.live.user.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "qiyu.rmq.consumer")
@Configuration
@Data
public class RocketMQConsumerProperties {

    //rocketmq的nameServer的地址
    private String nameServer;
    //分组名称
    private String groupName;

}
