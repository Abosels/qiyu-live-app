package org.qiyu.live.im.router.provider.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.qiyu.live.im.core.server.interfaces.contants.ImCoreServerConstants;
import org.qiyu.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.qiyu.live.im.dto.ImMsgBody;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

class ImRouterServiceImplTest {

    @AfterEach
    void clearRpcContext() {
        RpcContext.removeContext();
    }

    @Test
    void shouldUseSameRedisBindingKeyAndExposeBindAddressToDubboContext() {
        ImRouterServiceImpl routerService = new ImRouterServiceImpl();
        IRouterHandlerRpc routerHandlerRpc = mock(IRouterHandlerRpc.class);
        StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setAppId(10001);
        imMsgBody.setUserId(10086L);

        String expectedRedisKey = ImCoreServerConstants.IM_BIND_IP_KEY + "10001:10086";

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(expectedRedisKey)).thenReturn("127.0.0.1:9092%10086");

        ReflectionTestUtils.setField(routerService, "routerHandlerRpc", routerHandlerRpc);
        ReflectionTestUtils.setField(routerService, "stringRedisTemplate", stringRedisTemplate);

        boolean result = routerService.sendMsg(imMsgBody);

        assertThat(result).isTrue();
        assertThat(RpcContext.getContext().get("ip")).isEqualTo("127.0.0.1:9092");
        verify(valueOperations).get(expectedRedisKey);
        verify(routerHandlerRpc).sendMsg(imMsgBody);
    }
}
