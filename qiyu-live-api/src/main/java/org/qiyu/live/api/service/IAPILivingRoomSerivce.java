package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.req.OnlinePKReqVO;
import org.qiyu.live.api.vo.resp.LivingRoomPageRespVO;
import org.qiyu.live.api.vo.resp.RedPacketReceiveVO;
import org.qiyu.live.living.interfaces.dto.PkStateDTO;

public interface IAPILivingRoomSerivce {
    /**
     * 开播
     * @param type
     * @return
     */
    Integer startLivingRoom(Integer type);

    /**
     * 关播
     * @param roomId
     * @return
     */
    boolean closeLivingRoom(Integer roomId);

    /**
     * 获取主播相关配置信息(只有主播才有的权限)
     */

    LivingRoomInitVO anchorConfig(Long userId, Integer roomId);

    /**
     * 直播间列表展示
     *
     * @param livingRoomReqVO
     * @return
     */
    LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO);

    /**
     * 点击我要开播，有两个选项：1.默认直播间，2.pk直播间，此方法便是开始pk直播间。
     * @param onlinePKReqVO
     * @return
     */
    boolean onlinePK(OnlinePKReqVO onlinePKReqVO);
    /**
     * 主播下拨
     */
    boolean offlinePK(OnlinePKReqVO onlinePKReqVO);

    /**
     * 查询当前房间的 PK 完整状态（进入房间、重连、回到前台时调用）。
     *
     * @param roomId 直播间 ID
     * @return PK 状态快照，若不在 PK 中则返回 null
     */
    PkStateDTO queryCurrentPkState(Integer roomId);

    /**
     * 初始化红包数据
     * @param userId
     * @return
     */
    Boolean prepareRedPacket(Long userId,Integer roomId);

    /**
     * 开始红包雨活动
     * @param userId
     * @param code
     * @return
     */
    Boolean startRedPacket(Long userId,String code);

    /**
     * 领取红包
     * @param userId
     * @param configCode
     * @return
     */
    RedPacketReceiveVO getRedPackt(Long userId, String configCode);
}
