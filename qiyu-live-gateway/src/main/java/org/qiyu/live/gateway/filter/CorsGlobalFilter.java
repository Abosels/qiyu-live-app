package org.qiyu.live.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.cookie.CookieHeaderNames.MAX_AGE;

/**
 * 统一处理网关跨域问题
 * 这里独立出来，避免和登录鉴权逻辑耦合。
 */
@Component
@Slf4j
public class CorsGlobalFilter implements GlobalFilter, org.springframework.core.Ordered {

    /**
     * 前端页面的固定来源，按你的项目学习阶段先写死。
     * 后面如果需要多环境，再改成配置项。
     */
    private static final String ALLOW_ORIGIN = "http://web.qiyu.live.com:5500";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 统一加跨域响应头
        HttpHeaders headers = response.getHeaders();
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOW_ORIGIN);
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,OPTIONS");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "zbtk");
        headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);

        // 预检请求直接放行，避免浏览器先发 OPTIONS 被拦住
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            log.debug("CORS preflight request handled: {}", request.getURI());
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 先处理跨域，再做登录鉴权
        return -1;
    }
}
