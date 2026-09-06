package org.qiyu.live.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.error.QiyuApiError;
import org.qiyu.live.api.service.IAPILivingRoomSerivce;
import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.req.OnlinePKReqVO;
import org.qiyu.live.api.vo.resp.LivingRoomPageRespVO;
import org.qiyu.live.api.vo.resp.LivingRoomRespVO;
import org.qiyu.live.api.vo.resp.RedPacketReceiveVO;
import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.req.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.resp.RedPacketConfigRespDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.interfaces.IRedPacketConfigRpc;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.dto.PkStateDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.qiyu.live.user.interfaces.dto.UserDTO;
import org.qiyu.live.user.interfaces.interfaces.IUserRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.BizBaseErrorEnum;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Service
public class APILivingRoomServiceImpl implements IAPILivingRoomSerivce {

    private static final String DEFAULT_AVATAR = "/static/images/default-avatar.png";
    private static final String DEFAULT_LIVING_BG = "/static/images/default-living-bg.png";

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private IUserRpc userRpc;
    @DubboReference
    private IRedPacketConfigRpc redPacketConfigRpc;

    @Override
    public Integer startLivingRoom(Integer type) {
        Long userId = QiyuRequestContext.getUserId();
        UserDTO userDTO = userRpc.getUserById(userId);
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(QiyuRequestContext.getUserId());
        livingRoomReqDTO.setRoomName("Anchor-" + QiyuRequestContext.getUserId() + " live room");
        livingRoomReqDTO.setCoverImg(userDTO.getAvatar());
        livingRoomReqDTO.setType(type);
        return livingRoomRpc.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean closeLivingRoom(Integer roomId) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(roomId);
        livingRoomReqDTO.setAnchorId(QiyuRequestContext.getUserId());
        return livingRoomRpc.closeLivingRoom(livingRoomReqDTO);
    }

    @Override
    public LivingRoomInitVO anchorConfig(Long userId, Integer roomId) {
        LivingRoomRespDTO room = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(room, QiyuApiError.LIVING_ROOM_END);
        ErrorAssert.isNotNull(room.getAnchorId(), QiyuApiError.LIVING_ROOM_END);
        ErrorAssert.isNotNull(userId, QiyuApiError.USER_NOT_FOUND);

        // One RPC provides both users. Duplicate IDs are handled by the Map returned from user-provider.
        Map<Long, UserDTO> users = userRpc.batchQueryUserInfo(Arrays.asList(room.getAnchorId(), userId));
        if (users == null) {
            users = Collections.emptyMap();
        }
        UserDTO anchor = users.get(room.getAnchorId());
        UserDTO watcher = users.get(userId);
        ErrorAssert.isNotNull(anchor, QiyuApiError.USER_NOT_FOUND);
        ErrorAssert.isNotNull(watcher, QiyuApiError.USER_NOT_FOUND);

        LivingRoomInitVO response = new LivingRoomInitVO();
        response.setUserId(userId);
        response.setNickName(watcher.getNickName());
        response.setWatcherAvatar(StringUtils.hasText(watcher.getAvatar()) ? watcher.getAvatar() : DEFAULT_AVATAR);
        response.setAnchorId(room.getAnchorId());
        response.setAnchorNickName(anchor.getNickName());
        response.setAvatar(StringUtils.hasText(anchor.getAvatar()) ? anchor.getAvatar() : DEFAULT_AVATAR);
        response.setRoomId(room.getId());
        response.setRoomName(room.getRoomName());
        response.setType(room.getType());
        response.setDefaultBgImg(StringUtils.hasText(room.getCoverImg()) ? room.getCoverImg() : DEFAULT_LIVING_BG);
        response.setAnchor(room.getAnchorId().equals(userId));
        if (response.isAnchor()){
            RedPacketConfigRespDTO redPacketConfigRespDTO= redPacketConfigRpc.queryByAnchorId(userId);
            if (redPacketConfigRespDTO != null){
                response.setRedPacketConfigCode(redPacketConfigRespDTO.getConfigCode());
            }
        }
        return response;
    }

    @Override
    public LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO) {
        PageWrapper<LivingRoomRespDTO> resultPage = livingRoomRpc.list(
                ConvertBeanUtils.convert(livingRoomReqVO, LivingRoomReqDTO.class));
        LivingRoomPageRespVO livingRoomPageRespVO = new LivingRoomPageRespVO();
        livingRoomPageRespVO.setList(ConvertBeanUtils.convertList(resultPage.getList(), LivingRoomRespVO.class));
        livingRoomPageRespVO.setHosNext(resultPage.isHasNext());
        return livingRoomPageRespVO;
    }

    @Override
    public boolean onlinePK(OnlinePKReqVO onlinePKReqVO) {
        LivingRoomReqDTO livingRoomReqDTO = ConvertBeanUtils.convert(onlinePKReqVO, LivingRoomReqDTO.class);
        livingRoomReqDTO.setAnchorId(QiyuRequestContext.getUserId());
        return livingRoomRpc.onlinePK(livingRoomReqDTO);
    }

    @Override
    public boolean offlinePK(OnlinePKReqVO onlinePKReqVO) {
        LivingRoomReqDTO livingRoomReqDTO = ConvertBeanUtils.convert(onlinePKReqVO, LivingRoomReqDTO.class);
        livingRoomReqDTO.setAnchorId(QiyuRequestContext.getUserId());
        return livingRoomRpc.offlinePK(livingRoomReqDTO);
    }

    @Override
    public PkStateDTO queryCurrentPkState(Integer roomId) {
        return livingRoomRpc.queryCurrentPkState(roomId);
    }

    @Override
    public Boolean prepareRedPacket(Long userId,Integer roomId) {
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(respDTO, BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isTrue(respDTO.getAnchorId().equals(userId), BizBaseErrorEnum.PARAM_ERROR);
        return redPacketConfigRpc.prepareRedPacket(userId);
    }

    @Override
    public Boolean startRedPacket(Long userId, String code) {
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setConfigCode(code);
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByAnchorId(userId);
        ErrorAssert.isNotNull(respDTO, BizBaseErrorEnum.PARAM_ERROR);
        reqDTO.setRoomId(respDTO.getRoomId());
        return redPacketConfigRpc.startRedPacket(reqDTO);
    }

    @Override
    public RedPacketReceiveVO getRedPackt(Long userId, String configCode) {
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setConfigCode(configCode);
        RedPacketReceiveDTO receiveDTO = redPacketConfigRpc.receiveRedPacket(reqDTO);
        RedPacketReceiveVO receiveVO = new RedPacketReceiveVO();
        if(receiveDTO == null){
            receiveVO.setMsg("红包领取活动已结束");
            receiveVO.setPrice(0);
        }else{
            receiveVO.setPrice(receiveDTO.getPrice());
            receiveVO.setMsg(receiveDTO.getNotifyMsg());
        }

        return receiveVO;
    }
}
