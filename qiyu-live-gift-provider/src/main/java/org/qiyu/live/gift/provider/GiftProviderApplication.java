package org.qiyu.live.gift.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 礼物服务启动入口
 * 提供礼物配置查询、送礼记录写入、送礼消费 MQ 监听能力
 */
@EnableDubbo
@SpringBootApplication
public class GiftProviderApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(GiftProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
