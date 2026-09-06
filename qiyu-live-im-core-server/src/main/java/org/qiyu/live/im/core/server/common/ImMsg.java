package org.qiyu.live.im.core.server.common;

import lombok.Data;
import org.qiyu.live.im.contants.ImContants;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

@Data
public class ImMsg implements Serializable {

    @Serial
    private static final long serialVersionUID = -4716144154L;

    //魔数， 用于做基本校验
    private short magic;

    //用于记录body长度
    private int len;

    //用于表示当前消息的作用，后续会交给不同的handler去处理
    private int code;

    //存储消息体的内容，一般会按照字节数组的方式去存放
    private byte[] body;

    public static ImMsg build(int code, String data) {
        ImMsg msg = new ImMsg();
        msg.setMagic(ImContants.DEFAULT_MAGIC);
        msg.setCode(code);
        // [修复] 显式指定 UTF-8 字符集，避免跨环境乱码
        byte[] body = data.getBytes(StandardCharsets.UTF_8);
        msg.setBody(body);
        msg.setLen(body.length);
        return msg;
    }
}
