package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.PrepareOrderVO;
import org.qiyu.live.api.vo.req.ShopCartReqVO;
import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.api.vo.resp.SkuDetailInfoVO;
import org.qiyu.live.gift.dto.SkuPrepareOrderInfoDTO;
import org.qiyu.live.gift.dto.resp.ShopCartRespDTO;
import org.qiyu.live.gift.dto.SkuInfoDTO;

import java.util.List;

public interface IShopInfoService {
    /**
     * 根据直播间id查询商品信息
     * @param roomId
     * @return
     */
    List<SkuInfoDTO> queryByRoomId(Integer roomId);

    SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO);

    /**
     * 添加商品到购物车中
     * @return
     */
    Boolean addCart(ShopCartReqVO shopCartReqVO);

    Boolean removeFromCart(ShopCartReqVO shopCartReqVO);

    ShopCartRespDTO getCartInfo(Integer roomId);

    Boolean clearCart(Integer roomId);

    Boolean updateCartItemNum(ShopCartReqVO shopCartReqVO);

    SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO prepareOrderVO);

    /** 使用虚拟币支付已预扣库存的带货订单。 */
    Boolean payNow(Integer orderId);
}
