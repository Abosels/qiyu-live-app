package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.req.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.resp.RedPacketConfigRespDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;

public interface IRedPacketConfigRpc {
    /**
     * 支持根据主播id传是否有红包雨配置权限
     *
     * @param anchorId
     * @return
     */
    RedPacketConfigRespDTO queryByAnchorId(Long anchorId);

    /**
     * 新增红包配置
     *
     * @param redPacketConfigReqDTO
     * @return
     */
    boolean addOne(RedPacketConfigReqDTO redPacketConfigReqDTO);

    /**
     * 更新红包配置
     * @param redPacketConfigReqPO
     * @return
     */
    boolean updateById(RedPacketConfigReqDTO redPacketConfigReqPO);

    /**
     * 提前生成红包雨数据
     * @param anchorId
     * @return
     */
    boolean prepareRedPacket(Long anchorId);
    /**
     * 领取红包
     * @param reqDTO
     * @return
     */
    RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO);

    /**
     * 广播直播间用户，开始抢红包
     */
    Boolean startRedPacket(RedPacketConfigReqDTO reqDTO);

}
