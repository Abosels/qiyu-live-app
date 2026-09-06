package org.qiyu.live.account.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class AccountProviderApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AccountProviderApplication.class);
        // 使用 SERVLET 保证 Provider 进程持续运行。
        springApplication.setWebApplicationType(WebApplicationType.SERVLET);
        springApplication.run(args);

    }
}
