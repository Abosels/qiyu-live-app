package org.qiyu.live.user.provider.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.apache.rocketmq.client.producer.MQProducer;
import org.idea.qiyu.live.framework.redis.starter.config.RedisKeyAutoConfiguration;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.user.interfaces.dto.UserDTO;
import org.qiyu.live.user.provider.dao.mapper.IUserMapper;
import org.idea.qiyu.live.framework.redis.starter.config.RedisConfig;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserServiceRedisTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(RedisConfig.class, RedisKeyAutoConfiguration.class))
        .withPropertyValues("spring.application.name=qiyu-live-user-provider")
        .withBean(RedisConnectionFactory.class, () -> mock(RedisConnectionFactory.class))
        .withBean(MQProducer.class, () -> mock(MQProducer.class))
        .withBean(IUserMapper.class, () -> mock(IUserMapper.class))
        .withBean(UserServiceImpl.class);

    @Test
    void shouldInjectRedisTemplateFromRedisStarterIntoUserService() {
        contextRunner.run(context -> {
            UserServiceImpl userService = context.getBean(UserServiceImpl.class);
            RedisTemplate<?, ?> redisTemplate = context.getBean("redisTemplate", RedisTemplate.class);

            assertThat(userService).isNotNull();
            assertThat(redisTemplate).isNotNull();
            assertThat(ReflectionTestUtils.getField(userService, "redisTemplate")).isSameAs(redisTemplate);
        });
    }

    @Test
    void shouldReturnCachedUserWhenRedisHasValue() {
        UserServiceImpl userService = new UserServiceImpl();
        @SuppressWarnings("unchecked")
        RedisTemplate<String, UserDTO> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, UserDTO> valueOperations = mock(ValueOperations.class);
        IUserMapper userMapper = Mockito.mock(IUserMapper.class);
        UserProviderCacheKeyBuilder userProviderCacheKeyBuilder = new UserProviderCacheKeyBuilder();
        ReflectionTestUtils.setField(userProviderCacheKeyBuilder, "applicationName", "qiyu-live-user-provider");

        UserDTO cachedUser = new UserDTO();
        cachedUser.setUserId(1L);
        cachedUser.setNickName("cache-user");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("qiyu-live-user-provider:userInfo:1")).thenReturn(cachedUser);

        ReflectionTestUtils.setField(userService, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
        ReflectionTestUtils.setField(userService, "userProviderCacheKeyBuilder", userProviderCacheKeyBuilder);

        UserDTO result = userService.getUserById(1L);

        assertThat(result).isSameAs(cachedUser);
        verifyNoInteractions(userMapper);
    }
}
