package org.idea.qiyu.live.framework.redis.starter.config;

import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

public class FastJson2RedisSerializer extends GenericFastJsonRedisSerializer {

    @Override
    public byte[] serialize(Object source) throws SerializationException {
        if (source instanceof String || source instanceof Character) {
            return source.toString().getBytes(StandardCharsets.UTF_8);
        }
        return super.serialize(source);
    }

    @Override
    public Object deserialize(byte[] source) throws SerializationException {
        if (source == null || source.length == 0) {
            return null;
        }
        try {
            return super.deserialize(source);
        } catch (Exception e) {
            return new String(source, StandardCharsets.UTF_8);
        }
    }
}
