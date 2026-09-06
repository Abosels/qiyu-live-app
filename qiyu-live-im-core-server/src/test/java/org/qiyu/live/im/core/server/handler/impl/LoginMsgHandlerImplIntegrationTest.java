package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.qiyu.live.im.contants.AppIdEnum;
import org.qiyu.live.im.contants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImContextAttr;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.interfaces.contants.ImCoreServerConstants;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginMsgHandlerImplIntegrationTest {

    private static final long USER_ID = 10001L;
    private static final int APP_ID = AppIdEnum.QIYU_LIVE_BIZ.getCode();
    private static final String BIND_ADDR = "172.18.0.1:9092";
    private static final String REDIS_KEY = ImCoreServerConstants.IM_BIND_IP_KEY + APP_ID + ":" + USER_ID;

    @AfterEach
    void cleanup() {
        LettuceConnectionFactory connectionFactory = createConnectionFactory();
        StringRedisTemplate stringRedisTemplate = createRedisTemplate(connectionFactory);
        stringRedisTemplate.delete(REDIS_KEY);
        connectionFactory.destroy();
        ChannelHandlerContextCache.remove(USER_ID);
        ChannelHandlerContextCache.setServerIpAddress(null);
    }

    @Test
    void shouldWriteCoreExposeAddressToRedisWhenLoginSucceeds() throws Exception {
        LettuceConnectionFactory connectionFactory = createConnectionFactory();
        StringRedisTemplate stringRedisTemplate = createRedisTemplate(connectionFactory);
        LoginMsgHandlerImpl handler = new LoginMsgHandlerImpl();
        ImTokenRpc imTokenRpc = mock(ImTokenRpc.class);
        ChannelHandlerContext channelHandlerContext = mock(ChannelHandlerContext.class);
        Attribute<Long> userIdAttr = mock(Attribute.class);
        Attribute<Integer> appIdAttr = mock(Attribute.class);

        when(imTokenRpc.getUserIdByToken("token-for-bind")).thenReturn(USER_ID);
        when(channelHandlerContext.writeAndFlush(any())).thenReturn(null);
        when(channelHandlerContext.attr(ImContextAttr.USER_ID)).thenReturn(userIdAttr);
        when(channelHandlerContext.attr(ImContextAttr.APP_ID)).thenReturn(appIdAttr);

        ReflectionTestUtils.setField(handler, "imTokenRpc", imTokenRpc);
        ReflectionTestUtils.setField(handler, "stringRedisTemplate", stringRedisTemplate);
        ChannelHandlerContextCache.setServerIpAddress(BIND_ADDR);

        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setAppId(APP_ID);
        imMsgBody.setUserId(USER_ID);
        imMsgBody.setToken("token-for-bind");

        ImMsg imMsg = new ImMsg();
        imMsg.setCode(ImMsgCodeEnum.IM_MSG_LOGIN.getCode());
        imMsg.setBody(JSON.toJSONString(imMsgBody).getBytes());
        imMsg.setLen(imMsg.getBody().length);

        handler.handler(channelHandlerContext, imMsg);

        assertEquals(BIND_ADDR, stringRedisTemplate.opsForValue().get(REDIS_KEY));
        connectionFactory.destroy();
    }

    private LettuceConnectionFactory createConnectionFactory() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory("127.0.0.1", 6379);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    private StringRedisTemplate createRedisTemplate(LettuceConnectionFactory connectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }
}
