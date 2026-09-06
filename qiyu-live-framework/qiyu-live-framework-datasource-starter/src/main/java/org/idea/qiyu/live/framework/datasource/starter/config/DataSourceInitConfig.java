package org.idea.qiyu.live.framework.datasource.starter.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(DataSource.class)
public class DataSourceInitConfig {

    @Bean
    public ApplicationRunner runner(DataSource dataSource) {

        // 新逻辑：保留启动自检，但把它变成“诊断型检查”。
        // 这样即使 Nacos 配置、ShardingSphere 路由、MySQL 端口有问题，也能在日志里看到原因，
        // 同时不会因为这个自检直接把整个服务启动流程打断。
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("DataSource init success:" + connection.getMetaData().getURL());
            } catch (Exception e) {
                System.err.println("DataSource init failed, please check Nacos datasource config, ShardingSphere yaml and MySQL connection.");
                e.printStackTrace();
            }
        };
    }
}
