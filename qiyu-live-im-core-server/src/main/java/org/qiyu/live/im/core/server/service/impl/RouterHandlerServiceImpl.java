package org.qiyu.live.im.core.server.service.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.qiyu.live.im.contants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.service.IMsgAckCheckService;
import org.qiyu.live.im.core.server.service.IRouterHandlerService;
import org.qiyu.live.im.dto.ImMsgBody;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RouterHandlerServiceImpl implements IRouterHandlerService {

    @Resource
    private IMsgAckCheckService ackCheckService;

    @Override
    public void onReceive(ImMsgBody imMsgBody) {

        if (sendMsgToClient(imMsgBody)) {
            //当im服务器推送了消息给到了客户端，然后我们需要记录下ack
            ackCheckService.recordMsgAck(imMsgBody,1);
            ackCheckService.sendDelayMsg(imMsgBody);
        }
    }

    @Override
    public boolean sendMsgToClient(ImMsgBody imMsgBody) {
        //需要进行消息通知的userid
        Long userId = imMsgBody.getUserId();
        ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);
        if (ctx != null) {
            if (imMsgBody.getMsgId() == null || imMsgBody.getMsgId().isBlank()) {
                imMsgBody.setMsgId(UUID.randomUUID().toString());
            }
            ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_MSG_BIZ.getCode(), JSON.toJSONString(imMsgBody));
            ctx.writeAndFlush(respMsg);
            return true;
        }
        return false;
    }
}
