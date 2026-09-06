package org.qiyu.live.im.router.provider.rpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.provider.service.ImRouterService;
import org.springframework.test.util.ReflectionTestUtils;

class IRouterHandlerRpcTest {

    @Test
    void shouldDelegateSendMsgToRouterService() {
        IRouterHandlerRpc routerHandlerRpc = new IRouterHandlerRpc();
        ImRouterService routerService = mock(ImRouterService.class);
        ImMsgBody imMsgBody = new ImMsgBody();
        when(routerService.sendMsg(imMsgBody)).thenReturn(true);
        ReflectionTestUtils.setField(routerHandlerRpc, "routerService", routerService);

        boolean result = routerHandlerRpc.sendMsg(imMsgBody);

        assertThat(result).isTrue();
        verify(routerService).sendMsg(imMsgBody);
    }
}
