package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.req.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.resp.SkuOrderInfoRespDTO;
import org.qiyu.live.gift.provider.dao.po.SkuOrderInfoPO;

public interface ISkuOrderInfoService {
    /**
     * 支持多直播间内用户下单的订单查询
     *
     * @param userId
     * @param roomId
     * @return
     */
    SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId);

    SkuOrderInfoRespDTO queryByOrderId(Integer orderId);

    /**
     * 插入一条订单信息
     *
     * @param skuOrderInfoReqDTO
     * @return
     */
    SkuOrderInfoPO insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 根据订单id修改状态
     *
     * @param skuOrderInfoReqDTO
     * @return
     */
    boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 按订单当前状态做条件更新，用于支付与超时回滚之间的并发状态抢占。
     */
    boolean updateOrderStatusIfMatch(Integer orderId, Long userId, Integer roomId,
                                     Integer expectedStatus, Integer targetStatus);


}
