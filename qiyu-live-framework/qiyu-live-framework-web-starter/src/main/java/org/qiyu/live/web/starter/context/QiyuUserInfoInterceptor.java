package org.qiyu.live.web.starter.context;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.qiyu.live.common.interfaces.enums.GatewayHeaderEnum;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class QiyuUserInfoInterceptor implements HandlerInterceptor {

    //所有web请求来到这里时，都要被拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String userIdStr = request.getHeader(GatewayHeaderEnum.USER_LOGIN_ID.getName());
        //参数判断，userId为空, 可能是白名单url
        if(StringUtils.isEmpty(userIdStr)){
            return true;
        }
        //如果userId不为空，则把它放在线程本地变量里面去
        QiyuRequestContext.set(RequestConstants.QIYU_USER_ID,Long.valueOf(userIdStr));
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        QiyuRequestContext.clear();
    }
}
