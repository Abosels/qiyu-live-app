package org.qiyu.live.im.core.server.handler.ws;

import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 将内部二进制 IM 消息转换为浏览器可读取的 WebSocket 文本帧。
 */
@Component
@ChannelHandler.Sharable
public class WsImMsgEncoder extends MessageToMessageEncoder<ImMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ImMsg imMsg, List<Object> out) {
        JSONObject payload = new JSONObject();
        payload.put("magic", imMsg.getMagic());
        payload.put("code", imMsg.getCode());
        payload.put("len", imMsg.getLen());
        payload.put("body", new String(imMsg.getBody(), StandardCharsets.UTF_8));
        out.add(new TextWebSocketFrame(payload.toJSONString()));
    }
}
