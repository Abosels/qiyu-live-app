package org.qiyu.live.im.core.server.handler.ws;

import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandler;
import java.nio.charset.StandardCharsets;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.ImHandlerFactory;
import org.qiyu.live.im.core.server.interfaces.contants.ImCoreServerConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@ChannelHandler.Sharable
public class WslmServerCoreHandler extends SimpleChannelInboundHandler {

    @Resource
    private ImHandlerFactory imHandlerFactory;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame ) {
            wsMsgHandler(ctx,(WebSocketFrame) msg);
        };
    }

    /**
     * 正常或者意外断链，都会触发到这里
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId != null && appId != null){
            ChannelHandlerContextCache.remove(userId);
            //移除用户之前连接上的ip记录
            redisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);
        }
    }

    private void wsMsgHandler(ChannelHandlerContext ctx, WebSocketFrame msg) {
        //如果不是本文消息，统一后台不会处理
        if(!(msg instanceof TextWebSocketFrame)){
            log.error(String.format("[WebSocketCoreHandler] wsMsgHandler, %s msg types not supported", msg.getClass().getName()));
            return;
        }
        try{
            //返回应答信息
            String content = ((TextWebSocketFrame) msg).text();
            JSONObject jsonObject = JSONObject.parseObject(content, JSONObject.class);
            ImMsg imMsg = new ImMsg();
            imMsg.setMagic(jsonObject.getShort("magic"));
            imMsg.setCode(jsonObject.getInteger("code"));
            imMsg.setLen(jsonObject.getInteger("len"));
            // [修复] 显式指定 UTF-8 字符集，与整条 IM 链路保持一致
            imMsg.setBody(jsonObject.getString("body").getBytes(StandardCharsets.UTF_8));
            imHandlerFactory.doMsgHandler(ctx,imMsg);
        }catch(Exception e){
            log.error("[WebSocketCoreHandler] wsMsgHandler error is:", e);
        }
    }
}
