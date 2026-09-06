package org.qiyu.live.web.starter.context;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.qiyu.live.web.starter.error.QiyuErrorException;
import org.qiyu.live.web.starter.limit.RequestLimit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RequestLimitInterceptor implements HandlerInterceptor {

    @Value("${spring.application.name}")
    private String applicationName;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMathod = (HandlerMethod) handler;
        boolean hasLimit = handlerMathod.getMethod().isAnnotationPresent(RequestLimit.class);
        //如果当前方法有限流注解
        if (hasLimit){
            RequestLimit requestLimit = handlerMathod.getMethod().getAnnotation(RequestLimit.class);
            Long userId = QiyuRequestContext.getUserId();
            //无userId 不限流
            if (userId == null){
                return true;
            }
            String cacheKey = applicationName + ":" + userId + ":" + request.getRequestURI();
            cacheKey = Base64.getEncoder().encodeToString(cacheKey.getBytes(StandardCharsets.UTF_8));
            int limit = requestLimit.limit();
            int second = requestLimit.second();
            Integer reqTime = (Integer) Optional.ofNullable(redisTemplate.opsForValue().get(cacheKey)).orElse(0);
            if (reqTime == 0){
                redisTemplate.opsForValue().set(cacheKey,1,second, TimeUnit.SECONDS);
                return true;
            } else if (reqTime < limit){
                redisTemplate.opsForValue().increment(cacheKey, 1);
                return true;
            }
            throw new QiyuErrorException(-1,requestLimit.msg());

            //(userId + url + requestValue) base64 -> string(key)
            //redis -> key -> set(1) 首次访问
            //redis -> key -> incr 不是首次访问
        }
        return true;
    }
}
