package org.qiyu.live.im.router.provider.service;

import org.qiyu.live.im.dto.ImMsgBody;

import java.util.List;

public interface ImRouterService {
    /**
     * 按照用户userid进行发送消息
     */
    boolean sendMsg(ImMsgBody imMsgBody);

    /**
     * 支持批量发送消息 , 群聊
     * @param imMsgBodyList
     */
    void batchSendMsg(List<ImMsgBody> imMsgBodyList);
}
