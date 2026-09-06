package org.idea.qiyu.live.framework.redis.starter.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisSerializerFactory {
    //私有化构造工具，不让别人 new RedisSerializerFactory()
    private RedisSerializerFactory(){
    }
    //Redis 的 key 直接按字符串存
    public static StringRedisSerializer keySerializer(){
        return new StringRedisSerializer();
    }
    //Redis 的 value 用 FastJson2 转换
    public static RedisSerializer<Object> valueSerializer(){
        return new FastJson2RedisSerializer();
    }
}
