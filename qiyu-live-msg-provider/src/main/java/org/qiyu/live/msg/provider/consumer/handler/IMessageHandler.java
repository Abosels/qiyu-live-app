package org.qiyu.live.msg.provider.consumer.handler;

import org.qiyu.live.im.dto.ImMsgBody;

public interface IMessageHandler {
    /**
     * 处理im服务投递过来的消息内容
     *
     * @param imMsgBody
     */
    void onMsgReceive(ImMsgBody imMsgBody);
}
