package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.error.QiyuApiError;
import org.qiyu.live.api.service.IAPILivingRoomSerivce;
import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.WebResponseVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.req.OnlinePKReqVO;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.BizBaseErrorEnum;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.qiyu.live.web.starter.limit.RequestLimit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/living")
public class LivingRoomController {
    @Resource
    private IAPILivingRoomSerivce livingRoomService;

    @RequestLimit(limit = 1,second = 10, msg = "开播请求过于频繁，请稍后再试")
    @PostMapping("/startingLiving")
    public WebResponseVO starting_living(Integer type) {
        //调用rpc，往我们的开播表t_living_room写入一条记录
        ErrorAssert.isNotNull(type,BizBaseErrorEnum.PARAM_ERROR);
        Integer roomId = livingRoomService.startLivingRoom(type);
        LivingRoomInitVO initVO = new LivingRoomInitVO();
        initVO.setRoomId(roomId);
        return WebResponseVO.success(initVO);
    }

    @RequestLimit(limit = 1,second = 10, msg = "关播请求过于频繁，请稍后再试")
    @PostMapping("/closeLiving")
    public WebResponseVO closeLiving(Integer roomId) {
        ErrorAssert.isNotNull(roomId,BizBaseErrorEnum.PARAM_ERROR);
        boolean closeStatus = livingRoomService.closeLivingRoom(roomId);
        if (closeStatus) {
            return WebResponseVO.success();
        }
        return WebResponseVO.bizError("关播异常");
    }

    @RequestLimit(limit = 5, second = 10, msg = "请求过于频繁，请稍后再试")
    @PostMapping("/anchorConfig")
    public WebResponseVO anchorConfig(Integer roomId){
        long userId = QiyuRequestContext.getUserId();
        return WebResponseVO.success(livingRoomService.anchorConfig(userId,roomId));
    }

    @RequestLimit(limit = 10, second = 10, msg = "请求过于频繁，请稍后再试")
    @PostMapping
    public WebResponseVO list(@RequestBody LivingRoomReqVO livingRoomReqVO){
        ErrorAssert.isNotNull(livingRoomReqVO, BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isNotNull(livingRoomReqVO.getType(), QiyuApiError.LIVING_ROOM_TYPE_MISSING);
        ErrorAssert.isTrue(livingRoomReqVO.getPage() > 0 && livingRoomReqVO.getPage() <= 100, BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(livingRoomService.list(livingRoomReqVO));
    }

    /**
     * 点击我要开播，有两个选项：1.默认直播间，2.pk直播间，此便是开启pk直播间开播。
     * @param onlinePKReqVO
     * @return
     */
    @PostMapping("/onlinePK")
    public WebResponseVO onlinePK(OnlinePKReqVO onlinePKReqVO){
        ErrorAssert.isNotNull(onlinePKReqVO.getRoomId(),BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(livingRoomService.onlinePK(onlinePKReqVO));
    }

    @PostMapping("/offlinePK")
    public WebResponseVO offlinePK(OnlinePKReqVO onlinePKReqVO){
        return WebResponseVO.success(livingRoomService.offlinePK(onlinePKReqVO));
    }

    /**
     * 查询当前房间的 PK 完整状态。
     * 前端在进入房间、IM 重连、App 回到前台时调用此接口校准 PK 进度条。
     */
    @PostMapping("/queryCurrentPkState")
    public WebResponseVO queryCurrentPkState(Integer roomId){
        ErrorAssert.isNotNull(roomId, BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(livingRoomService.queryCurrentPkState(roomId));
    }

    @PostMapping("/prepareRedPacket")
    @RequestLimit(limit = 1,second = 10, msg = "正在初始化中，请稍等")
    public WebResponseVO prepareRedPacket(LivingRoomReqVO livingRoomReqVO){
        return WebResponseVO.success(livingRoomService.prepareRedPacket(QiyuRequestContext.getUserId(),livingRoomReqVO.getRoomId()));
    }

    @PostMapping("/startRedPacket")
    @RequestLimit(limit = 1,second = 10, msg = "正在广播直播间中，请稍等")
    public WebResponseVO startRedPacket(LivingRoomReqVO livingRoomReqVO){
        return WebResponseVO.success(livingRoomService.startRedPacket(QiyuRequestContext.getUserId(),livingRoomReqVO.getRedPacketConfigCode()));
    }

    @PostMapping("/getRedPackt")
    @RequestLimit(limit = 1,second = 5, msg = "请稍等")
    public WebResponseVO getRedPackt(LivingRoomReqVO livingRoomReqVO){
        return WebResponseVO.success(livingRoomService.getRedPackt(QiyuRequestContext.getUserId(),livingRoomReqVO.getRedPacketConfigCode()));
    }
}
