package org.qiyu.live.msg.provider.consumer.handler.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.interfaces.contants.ImMsgBizCodeEnum;
import org.qiyu.live.im.router.interfaces.rpc.ImRouterRpc;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.qiyu.live.msg.dto.MessageDTO;
import org.qiyu.live.msg.provider.consumer.handler.IMessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class SingleMessageHandlerImpl implements IMessageHandler {

    @DubboReference
    private ImRouterRpc imRouterRpc;

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;

    @Override
    public void onMsgReceive(ImMsgBody imMsgBody) {
        if (imMsgBody == null
                || StringUtils.isEmpty(imMsgBody.getBizCode())
                || imMsgBody.getData() == null) {
            return;
        }

        int bizCode = imMsgBody.getBizCode();

        if (ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode() != bizCode) {
            return;
        }

        MessageDTO messageDTO;
        try {
            messageDTO = JSON.parseObject(
                    imMsgBody.getData(),
                    MessageDTO.class
            );
        } catch (Exception e) {
            return;
        }

        if (messageDTO == null
                || messageDTO.getRoomId() == null
                || messageDTO.getRoomId() <= 0) {
            return;
        }

        LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
        reqDTO.setRoomId(messageDTO.getRoomId());
        reqDTO.setAppId(imMsgBody.getAppId());

        List<Long> roomUserIdList =
                livingRoomRpc.queryUserIdByRoomId(reqDTO);

        if (CollectionUtils.isEmpty(roomUserIdList)) {
            return;
        }

        Long senderUserId = imMsgBody.getUserId();

        List<ImMsgBody> targetMessages = roomUserIdList.stream()
                .filter(Objects::nonNull)
                .distinct()
                // 当前设计中发送者不再接收自己的消息
                .filter(userId -> !userId.equals(senderUserId))
                .map(userId -> {
                    ImMsgBody targetMsg = new ImMsgBody();
                    targetMsg.setUserId(userId);
                    targetMsg.setAppId(imMsgBody.getAppId());
                    targetMsg.setBizCode(
                            ImMsgBizCodeEnum
                                    .LIVING_ROOM_IM_CHAT_MSG_BIZ
                                    .getCode()
                    );
                    targetMsg.setMsgId(imMsgBody.getMsgId());
                    targetMsg.setData(JSON.toJSONString(messageDTO));
                    return targetMsg;
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(targetMessages)) {
            return;
        }

        imRouterRpc.batchSendMsg(targetMessages);
    }
}
