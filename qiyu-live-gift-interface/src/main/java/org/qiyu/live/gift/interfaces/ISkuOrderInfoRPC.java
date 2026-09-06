package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.SkuPrepareOrderInfoDTO;
import org.qiyu.live.gift.dto.req.PayNowReqDTO;
import org.qiyu.live.gift.dto.req.PrepareOrderReqDTO;
import org.qiyu.live.gift.dto.req.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.resp.SkuOrderInfoRespDTO;

public interface ISkuOrderInfoRPC {

    /**
     * 支持多直播间内用户下单的订单查询
     *
     * @param userId
     * @param roomId
     * @return
     */
    SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId);

    /**
     * 插入一条订单信息
     *
     * @param skuOrderInfoReqDTO
     * @return
     */
    boolean insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 根据订单id修改状态
     *
     * @param skuOrderInfoReqDTO
     * @return
     */
    boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 待支付订单生成
     * @param prepareOrderReqDTO
     * @return
     */
    SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderReqDTO prepareOrderReqDTO);

    /**
     * 立即支付
     * @param payNowReqDTO
     * @return
     */
    boolean payNow(PayNowReqDTO payNowReqDTO);
}
