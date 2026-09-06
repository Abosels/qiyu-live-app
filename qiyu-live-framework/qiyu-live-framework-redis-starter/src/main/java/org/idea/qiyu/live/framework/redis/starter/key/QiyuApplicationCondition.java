package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import java.util.Map;

public class QiyuApplicationCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String applicationName = context.getEnvironment().getProperty("spring.application.name");
        if (!StringUtils.hasText(applicationName)) {
            return false;
        }

        Map<String, Object> attributes = metadata.getAnnotationAttributes(
            ConditionalOnQiyuApplication.class.getName()
        );
        if (attributes == null) {
            return false;
        }

        String expectedApplicationName = (String) attributes.get("value");
        return StringUtils.hasText(expectedApplicationName)
            && expectedApplicationName.equals(applicationName);
    }
}
