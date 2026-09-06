package org.qiyu.live.gateway.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.account.interfaces.IAccountTokenRpc;
import org.qiyu.live.common.interfaces.enums.GatewayHeaderEnum;
import org.qiyu.live.gateway.properties.GatewayApplicationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AccountCheckFilter implements GlobalFilter, org.springframework.core.Ordered {

    @DubboReference(check = false)
    private IAccountTokenRpc accountTokenRpc;

    @Resource
    private GatewayApplicationProperties gatewayApplicationProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String reqUrl = request.getURI().getPath();
        if (StringUtils.isEmpty(reqUrl)) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().setComplete();
        }

        List<String> notCheckUrlList = gatewayApplicationProperties.getNotCheckUrlList();
        if (!CollectionUtils.isEmpty(notCheckUrlList)) {
            for (String notCheckUrl : notCheckUrlList) {
                if (reqUrl.startsWith(notCheckUrl)) {
                    // 不需要登录校验的地址，直接放行
                    log.info("不需要进行 token 校验，直接放行: {}", reqUrl);
                    return chain.filter(exchange);
                }
            }
        }

        HttpCookie tokenCookie = request.getCookies().getFirst("zbtk");
        if (tokenCookie == null || StringUtils.isEmpty(tokenCookie.getValue())) {
            log.error("没有检测到 zbtk cookie，被拦截: {}", reqUrl);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String tokenCookieValue = tokenCookie.getValue().trim();
        Long userId;
        try {
            // 通过 Dubbo 远程调用账号服务，解析 token 对应的用户 id
            userId = accountTokenRpc.getUserIdByToken(tokenCookieValue);
        } catch (Exception e) {
            // Dubbo 远程调用失败时，网关返回 503，避免把整个请求链路直接打挂
            log.error("Dubbo 调用 accountTokenRpc 失败，路径: {}", reqUrl, e);
            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            return exchange.getResponse().setComplete();
        }

        if (userId == null) {
            log.error("token 失效，被拦截: {}", reqUrl);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 将登录用户 id 放到请求头中，后续服务直接读取即可
        ServerHttpRequest.Builder builder = request.mutate();
        builder.header(GatewayHeaderEnum.USER_LOGIN_ID.getName(), String.valueOf(userId));
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}