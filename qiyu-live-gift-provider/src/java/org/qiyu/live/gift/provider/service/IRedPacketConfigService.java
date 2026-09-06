package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.req.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;

public interface IRedPacketConfigService {
    /**
     * 支持根据主播id传是否有红包雨配置权限
     *
     * @param anchorId
     * @return
     */
    RedPacketConfigPO queryByAnchorId(Long anchorId);

    /**
     * 根据红包配置code检索信息
     * @param configCode
     * @return
     */
    RedPacketConfigPO queryByConfigCode(String configCode);

    /**
     * 新增红包配置
     *
     * @param redPacketConfigPO
     * @return
     */
    boolean addOne(RedPacketConfigPO redPacketConfigPO);

    /**
     * 更新红包配置
     * @param redPacketConfigPO
     * @return
     */
    boolean updateById(RedPacketConfigPO redPacketConfigPO);

    //红包怎么生成

    /**
     * 提前生成红包雨数据
     * @param anchorId
     * @return
     */
    boolean prepareRedPacket(Long anchorId);

    /**
     * 开始红包雨
     * @param reqDTO
     * @return
     */
    Boolean startRedPacket(RedPacketConfigReqDTO reqDTO);

    //红包怎么领取

    /**
     * 领取红包
     * @param reqDTO
     * @return
     */
    RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO);

    /**
     * 领红包之后的处理
     *
     * @param reqDTO
     */
    void receiveRedPacketHandle(RedPacketConfigReqDTO reqDTO, Integer price, String bizId);
}
