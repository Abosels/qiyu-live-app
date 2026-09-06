package org.qiyu.live.im.core.server.handler.ws;

/*
 * [修复说明]
 * 1. Fastjson v1 → v2：统一使用 com.alibaba.fastjson2.JSON
 * 2. paramArr 未定义：用 QueryStringDecoder 按参数名解析
 * 3. @Sharable + 成员变量 webSocketServerHandshaker：改为方法局部变量
 * 4. 同步 isSuccess()：改用 addListener 异步回调
 */

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.im.core.server.handler.impl.LoginMsgHandlerImpl;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ws的握手连接处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WsSharkHandler extends ChannelInboundHandlerAdapter {

    //指定监听的端口
    @Value("${qiyu.im.ws.port}")
    private int port;
    // Nacos 未显式下发 IP 时使用本机地址，保证本地 WebSocket 可启动。
    @Value("${spring.cloud.nacos.discovery.ip:${QIYU_IM_SERVER_IP:127.0.0.1}}")
    private String serverIp;
    @DubboReference
    private ImTokenRpc imTokenRpc;
    @Resource
    private LoginMsgHandlerImpl loginMsgHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        //握手接入ws
        if (msg instanceof FullHttpMessage){
            handlerHttpRequest(ctx, ((FullHttpRequest)msg));
            return;
        }
        //将消息传递给下一个链路处理器去处理
        ctx.fireChannelRead(msg);
    }

    private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        String webSocketUrl = "ws://" + serverIp + ":" + port;

        /*
         * 使用 QueryStringDecoder 按参数名称解析 URL 参数。
         *
         * 按名称解析的参数：
         *   token  — 必填，IM 登录 token
         *   userId — 必填，客户端用户 ID
         *   code   — 可选，业务场景标识（如 1001=直播间登录）
         *   roomId — 可选，仅在直播间登录场景下携带
         */
        String uri = msg.uri();
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = decoder.parameters();

        // 按参数名称安全获取各参数值
        String token = getFirstParam(parameters, "token");
        String userIdStr = getFirstParam(parameters, "userId");
        String codeStr = getFirstParam(parameters, "code");
        String roomIdStr = getFirstParam(parameters, "roomId");

        // token 和 userId 为必填项，缺失时拒绝连接
        if (token == null || userIdStr == null) {
            log.error("[WsSharkHandler] 参数缺失，token={}, userId={}", token, userIdStr);
            ctx.close();
            return;
        }

        // userId 格式校验：必须为有效数字
        Long userId;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            log.error("[WsSharkHandler] userId 格式错误, userIdStr={}", userIdStr);
            ctx.close();
            return;
        }

        // 通过 token RPC 校验用户身份
        Long queryUserId;
        try {
            queryUserId = imTokenRpc.getUserIdByToken(token);
        } catch (Exception e) {
            log.error("[WsSharkHandler] token RPC 调用异常", e);
            ctx.close();
            return;
        }

        if (queryUserId == null || !queryUserId.equals(userId)) {
            log.error("[WsSharkHandler] token 校验不通过！tokenUserId={}, clientUserId={}", queryUserId, userId);
            ctx.close();
            return;
        }

        // 兼容现有 token 格式；token 没有 % 时使用 code 参数或默认值。
        Integer appId;
        try {
            int lastPercent = token.lastIndexOf("%");
            if (lastPercent >= 0 && lastPercent < token.length() - 1) {
                appId = Integer.valueOf(token.substring(lastPercent + 1));
            } else {
                // token 中无 % 分隔符时，尝试使用 code 参数，否则使用默认值
                appId = (codeStr != null) ? Integer.valueOf(codeStr) : 10000;
                log.warn("[WsSharkHandler] token 中无 % 分隔符，使用备用 appId={}", appId);
            }
        } catch (NumberFormatException e) {
            log.error("[WsSharkHandler] appId 解析失败", e);
            ctx.close();
            return;
        }

        // 解析 roomId，仅在直播间登录场景下携带，普通登录时为 null
        Integer roomId = null;
        if (roomIdStr != null) {
            try {
                roomId = Integer.valueOf(roomIdStr);
            } catch (NumberFormatException e) {
                log.error("[WsSharkHandler] roomId 格式错误, roomIdStr={}", roomIdStr);
                ctx.close();
                return;
            }
        }

        /*
         * [修复] WebSocketServerHandshaker 改为方法局部变量。
         * 原实现为成员变量，在 @Sharable Handler 中存在并发覆盖风险。
         * 同时改用 addListener 异步回调判断握手结果，替代同步 isSuccess()。
         */
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketUrl, null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(msg);

        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }

        // 使用 final 变量供 lambda 内部访问
        final Integer finalRoomId = roomId;
        final Integer finalAppId = appId;

        handshaker.handshake(ctx.channel(), msg).addListener((ChannelFuture future) -> {
            if (!future.isSuccess()) {
                log.error("[WsSharkHandler] WebSocket 握手失败", future.cause());
                ctx.close();
                return;
            }
            // 握手成功后绑定用户登录状态到 Channel
            loginMsgHandler.LoginSuccessHandler(ctx, userId, finalAppId, finalRoomId);
            log.info("[WsSharkHandler] channel is connected! userId={}, appId={}, roomId={}",
                    userId, finalAppId, finalRoomId);
        });
    }

    /**
     * 从 QueryStringDecoder 解析出的参数 Map 中安全获取第一个参数值。
     * 参数不存在时返回 null，避免 NullPointerException。
     *
     * @param parameters 查询参数 Map
     * @param name       参数名
     * @return 第一个参数值，不存在时返回 null
     */
    private String getFirstParam(Map<String, List<String>> parameters, String name) {
        List<String> values = parameters.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    enum ParamCodeEnum {
        LIVING_ROOM_LOGIN(1001,"直播间登录");
        private Integer code;
        private String desc;
        ParamCodeEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public Integer getCode() {
            return code;
        }
    }
}
