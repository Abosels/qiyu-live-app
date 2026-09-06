package org.idea.qiyu.live.framework.redis.starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//redis客户端操作对象

@AutoConfiguration
@ConditionalOnClass(RedisTemplate.class)
public class RedisConfig {

    //@ConditionalOnMissingBean 如果 Spring 容器里已经有 RedisTemplate，就不创建新的。

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        StringRedisSerializer keySerializer = RedisSerializerFactory.keySerializer();
        RedisSerializer<Object> valueSerializer = RedisSerializerFactory.valueSerializer();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        /**
         * Redis 内容	序列化方式
         * 普通 key	    字符串序列化
         * 普通 value	自定义 value 序列化
         * hash key	    字符串序列化
         * hash value	自定义 value 序列化
         */
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
