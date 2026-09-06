package org.qiyu.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.ImHandlerFactory;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.contants.ImMsgCodeEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
@Slf4j
public class ImHandlerFactoryImpl implements ImHandlerFactory, InitializingBean {

    @Resource
    private ApplicationContext applicationContext;

    private static Map<Integer, SimpleHandler> simpleHandlerMap = new HashMap<>();

    @Override
    public void doMsgHandler(ChannelHandlerContext ctx, ImMsg imMsg) {
        SimpleHandler simpleHandler = simpleHandlerMap.get(imMsg.getCode());
        if (simpleHandler == null) {
            throw new IllegalArgumentException("msg code is null, code is" + imMsg.getCode());
        }
        try {
            simpleHandler.handler(ctx,imMsg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //登陆消息包，登录token认证，channel 和 userId关联
        //登出消息包，正常断开Im连接的时候发送的。
        //业务消息包，最常用的消息类型，例如我们im发送数据，或者接收数据的时候会用到
        //心跳消息包，定时会给im发送，汇报功能 （对长连接来说）
        simpleHandlerMap.put(ImMsgCodeEnum.IM_MSG_LOGIN.getCode(), applicationContext.getBean(LoginMsgHandlerImpl.class));
        simpleHandlerMap.put(ImMsgCodeEnum.IM_MSG_LOGOUT.getCode(), applicationContext.getBean(LogoutMsgHandlerImpl.class));
        simpleHandlerMap.put(ImMsgCodeEnum.IM_MSG_BIZ.getCode(), applicationContext.getBean(BizImMsgHandlerImpl.class));
        simpleHandlerMap.put(ImMsgCodeEnum.IM_MSG_HEARTBEAT.getCode(), applicationContext.getBean(HeartBeatMsgHandlerImpl.class));
        simpleHandlerMap.put(ImMsgCodeEnum.IM_MSG_ACK.getCode(), applicationContext.getBean(AckMsgHandlerImpl.class));
    }
}
