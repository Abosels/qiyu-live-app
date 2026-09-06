package org.qiyu.live.im.provider.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.idea.qiyu.live.framework.redis.starter.key.IMProviderCacheKeyBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

class ImTokenServiceImplTest {

    @Test
    void shouldReadLongUserIdFromRedisTokenValue() {
        ImTokenServiceImpl imTokenService = new ImTokenServiceImpl();
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        IMProviderCacheKeyBuilder cacheKeyBuilder = new IMProviderCacheKeyBuilder();
        ReflectionTestUtils.setField(cacheKeyBuilder, "applicationName", "qiyu-live-im-provider");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("qiyu-live-im-provider:IM_LOGIN_TOKEN:token-1")).thenReturn(1L);

        ReflectionTestUtils.setField(imTokenService, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(imTokenService, "cacheKeyBuilder", cacheKeyBuilder);

        Long result = imTokenService.getUserIdByToken("token-1");

        assertThat(result).isEqualTo(1L);
    }
}
