package org.qiyu.live.framework.mq.starter.properties;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * RocketMQ 消费者配置。
 *
 * <p>这里刻意不用 {@code @ConfigurationProperties} / {@code @Value} 做字段绑定：该类被
 * {@code @Configuration} 注解并经由自动装配（AutoConfiguration.imports）注册时，字段级
 * 的 {@code @ConfigurationProperties} 与 {@code @Value} 注入在该场景下都不会生效，
 * {@code nameSrv} 始终为 null，导致消费者 {@code setNamesrvAddr(null)} 后
 * {@code connect to null} 崩溃（生产者前缀 qiyu.rmq.producer 却正常，属框架自身的
 * 绑定不对称问题）。</p>
 *
 * <p>改用构造器注入 {@link Environment}，在 getter 里实时读取配置并兜底，与 user-provider
 * 的 RocketMQConsumerConfig 思路一致。</p>
 */
@Configuration
public class RocketMQConsumerProperties {

    private final Environment environment;

    public RocketMQConsumerProperties(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        System.out.println("=======RocketMQConsumerProperties 解析结果 name-srv=[" + getNameSrv()
                + "] groupName=[" + getGroupName() + "]=======");
    }

    /**
     * NameServer 地址。容器内通过 rocketmq-namesrv 别名访问，兜底 rocketmq-namesrv:9876。
     */
    public String getNameSrv() {
        String v = resolveNameSrv();
        System.out.println("=======getNameSrv() 被调用 this@" + System.identityHashCode(this)
                + " 返回=[" + v + "]=======");
        return v;
    }

    /**
     * 消费分组名。兜底用应用名。
     */
    public String getGroupName() {
        String v = resolveGroupName();
        System.out.println("=======getGroupName() 被调用 this@" + System.identityHashCode(this)
                + " 返回=[" + v + "]=======");
        return v;
    }

    private String resolveNameSrv() {
        String value = environment.getProperty("qiyu.rmq.consumer.name-srv");
        if (value == null || value.isBlank()) {
            // 再兜底一次环境变量直读，避免 relax 绑定与占位符差异导致读不到。
            value = environment.getProperty("QIYU_RMQ_CONSUMER_NAME_SRV");
        }
        if (value == null || value.isBlank()) {
            value = "rocketmq-namesrv:9876";
        }
        return value;
    }

    private String resolveGroupName() {
        String value = environment.getProperty("qiyu.rmq.consumer.groupName");
        if (value == null || value.isBlank()) {
            value = environment.getProperty("spring.application.name");
        }
        if (value == null || value.isBlank()) {
            value = "qiyu-live-app";
        }
        return value;
    }
}
