package org.qiyu.live.api.service.impl;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.api.error.QiyuApiError;
import org.qiyu.live.api.service.IGiftService;
import org.qiyu.live.api.vo.GiftConfigVO;
import org.qiyu.live.api.vo.req.GiftReqVO;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.dto.SendGiftMQ;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.gift.dto.GiftConfigDTO;
import org.qiyu.live.gift.interfaces.IGiftConfigRpc;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 礼物相关 API 服务实现
 * 负责将前端请求转换为对 gift-provider 的 Dubbo RPC 调用
 */
@Slf4j
@Service
public class GiftServiceImpl implements IGiftService {

    @DubboReference
    private IGiftConfigRpc giftConfigRpc;
    @DubboReference
    private IQiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @Resource
    private MQProducer mqProducer;

    @Override
    public List<GiftConfigVO> listGift() {
        // 通过 Dubbo RPC 查询所有礼物配置
        List<GiftConfigDTO> dtoList = giftConfigRpc.queryGiftList();
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        // DTO → VO 转换
        List<GiftConfigVO> voList = new ArrayList<>(dtoList.size());
        for (GiftConfigDTO dto : dtoList) {
            GiftConfigVO vo = new GiftConfigVO();
            vo.setGiftId(dto.getGiftId());
            vo.setGiftName(dto.getGiftName());
            vo.setPrice(dto.getPrice());
            vo.setPriceUnit(dto.getPriceUnit());
            vo.setGiftImage(dto.getGiftImage());
            vo.setGiftType(dto.getGiftType());
            vo.setStatus(dto.getStatus());
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public boolean sendGift(GiftReqVO giftReqVO) {
        if (giftReqVO == null || giftReqVO.getGiftId() <= 0) {
            throw new IllegalArgumentException("礼物参数不合法");
        }
        int giftId = giftReqVO.getGiftId();
        GiftConfigDTO giftConfigDTO = giftConfigRpc.getGiftId(giftId);
        ErrorAssert.isNotNull(giftConfigDTO, QiyuApiError.GIFT_CONFIG_ERROR);
        if (giftReqVO.getRoomId() == null || giftReqVO.getRoomId() <= 0) {
            throw new IllegalArgumentException("直播间 roomId 不合法");
        }
        if (giftReqVO.getRequestId() == null || giftReqVO.getRequestId().isBlank()
                || giftReqVO.getRequestId().length() > 64) {
            throw new IllegalArgumentException("送礼 requestId 不合法");
        }
        // 收礼人必须由服务端根据直播间查询，不能信任前端提交的用户 ID。
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(giftReqVO.getRoomId());
        if (livingRoomRespDTO == null || livingRoomRespDTO.getAnchorId() == null
                || livingRoomRespDTO.getAnchorId() <= 0) {
            throw new IllegalArgumentException("直播间不存在或主播信息不合法");
        }
        SendGiftMQ sendGiftMQ = new SendGiftMQ();
        sendGiftMQ.setUserId(QiyuRequestContext.getUserId());
        sendGiftMQ.setGiftId(giftId);
        sendGiftMQ.setRoomId(giftReqVO.getRoomId());
        sendGiftMQ.setReceiveId(livingRoomRespDTO.getAnchorId());
        sendGiftMQ.setUrl(giftConfigDTO.getSvgaUrl());
        sendGiftMQ.setType(giftReqVO.getType());
        sendGiftMQ.setSvgaUrl(giftConfigDTO.getSvgaUrl());
        sendGiftMQ.setGiftName(giftConfigDTO.getGiftName());
        sendGiftMQ.setPrice(giftConfigDTO.getPrice());
        // 使用前端一次点击复用的 requestId，并加 userId 前缀防止业务号碰撞。
        String bizId = QiyuRequestContext.getUserId() + ":" + giftReqVO.getRequestId();
        sendGiftMQ.setUuId(bizId);
        Message message = new Message();
        message.setTopic(GiftProviderTopicNames.SEND_GIFT);
        // RocketMQ 可按业务键检索和排查同一次送礼消息。
        message.setKeys(bizId);
        message.setBody(JSON.toJSONBytes(sendGiftMQ));
        try {
            SendResult sendResult = mqProducer.send(message);
            if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
                throw new IllegalStateException("送礼消息未成功写入 RocketMQ");
            }
            log.info("[sendGift] send result is {}", sendResult);
        } catch (Exception e) {
            log.error("[sendGift] send error is ", e);
            throw new RuntimeException(e);
        }
        return true;
    }
}
