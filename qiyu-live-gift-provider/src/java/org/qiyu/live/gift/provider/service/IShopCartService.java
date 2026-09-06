package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.req.ShopCartReqDTO;
import org.qiyu.live.gift.dto.resp.ShopCartRespDTO;

public interface IShopCartService {

    Boolean addCart(ShopCartReqDTO shopCartReqDTO);

    Boolean removeFromCart(ShopCartReqDTO shopCartReqDTO);

    ShopCartRespDTO getCartInfo(ShopCartReqDTO shopCartReqDTO);

    Boolean clearCart(ShopCartReqDTO shopCartReqDTO);

    /**
     * 修改购物车内商品数量
     * @param shopCartReqDTO
     * @return true
     */
    Boolean updateCartItemNum (ShopCartReqDTO shopCartReqDTO);
}
