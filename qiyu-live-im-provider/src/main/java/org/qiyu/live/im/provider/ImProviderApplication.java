package org.qiyu.live.im.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ImProviderApplication {
    public static void main(String[] args) {
        // 不再强制 WebApplicationType.NONE：纯 Dubbo 应用在 Spring Boot 3.x 下启动后会因
        // 没有非守护线程而立即退出（exit 0），改回默认的 Servlet 类型由内嵌 Tomcat 维持进程存活。
        SpringApplication.run(ImProviderApplication.class, args);
    }
}
