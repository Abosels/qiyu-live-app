package org.qiyu.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.qiyu.live.im.contants.ImContants;

import java.util.List;

/**
 * 消息解码器
 *
 */
public class ImMsgDecoder extends ByteToMessageDecoder {

    private final int BASE_LEN = 2 + 4 + 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        //bytebuf内容的基本校验，长度校验，magic值校验, 剩余可读
        if(byteBuf.readableBytes() >= BASE_LEN) {
            if(byteBuf.readShort() != ImContants.DEFAULT_MAGIC){
                channelHandlerContext.close();
                return;
            }
            byteBuf.markReaderIndex();

            short magic = byteBuf.readShort();
            int len = byteBuf.readInt();
            int code = byteBuf.readInt();
            //确保bytebuf剩余长度足够,如果是半包则指针重置读到刚才读的地方
            if (byteBuf.readableBytes() < len) {
                byteBuf.resetReaderIndex();
                return;
            }
            byte[] body = new byte[len];
            byteBuf.readBytes(body);
            ImMsg imMsg = new ImMsg();
            imMsg.setLen(len);
            imMsg.setCode(code);
            imMsg.setBody(body);
            out.add(imMsg);
        }
        //bytebuf转换为immsg对象

    }
}
