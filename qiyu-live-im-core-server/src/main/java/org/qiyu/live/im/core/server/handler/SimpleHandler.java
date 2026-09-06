package org.qiyu.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;

public interface SimpleHandler {
    /**
     * 消息处理函数
     *
     * @param ctx
     * @param imMsg
     * @throws Exception
     */
    void handler(ChannelHandlerContext ctx, ImMsg imMsg) throws Exception;
}
