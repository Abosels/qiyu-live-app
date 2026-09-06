package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import java.util.Set;

public class RedisKeyLoadMatch implements Condition {

    /**
     * 允许加载 Redis Key 相关 Bean 的应用名白名单。
     * 这样 account-provider / msg-provider 可以自动装配自己的 key builder，
     * 其他模块不会误加载。
     */
    private static final Set<String> SUPPORTED_APPLICATION_NAMES = Set.of(
            "qiyu-live-account-provider",
            // gift-provider 同时使用礼物、红包和商品库存三类 Redis Key Builder。
            "qiyu-live-gift-provider",
            "qiyu-live-msg-provider",
            "qiyu-live-im-core-server"
    );

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String applicationName = context.getEnvironment().getProperty("spring.application.name");
        return StringUtils.hasText(applicationName) && SUPPORTED_APPLICATION_NAMES.contains(applicationName);
    }
}
