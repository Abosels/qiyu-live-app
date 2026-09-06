package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.req.ShopCartReqDTO;
import org.qiyu.live.gift.dto.resp.ShopCartRespDTO;

public interface IShopCartRpc {

    /**
     * 添加商品到购物车中
     * @param shopCartReqDTO
     * @return
     */
    Boolean addCart(ShopCartReqDTO shopCartReqDTO);

    /** 删除当前直播间购物车中的一个 SKU。 */
    Boolean removeFromCart(ShopCartReqDTO shopCartReqDTO);

    /** 查询当前购物车中的信息。 */
    ShopCartRespDTO getCartInfo(ShopCartReqDTO shopCartReqDTO);

    /** 清空当前直播间购物车。 */
    Boolean clearCart(ShopCartReqDTO shopCartReqDTO);

    /** 更改购物车中的商品数量 */
    Boolean updateCartItemNum(ShopCartReqDTO shopCartReqDTO);
}
