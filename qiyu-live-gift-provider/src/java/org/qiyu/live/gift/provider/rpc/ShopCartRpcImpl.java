package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.gift.dto.req.ShopCartReqDTO;
import org.qiyu.live.gift.dto.resp.ShopCartRespDTO;
import org.qiyu.live.gift.interfaces.IShopCartRpc;
import org.qiyu.live.gift.provider.service.IShopCartService;

@DubboService
public class ShopCartRpcImpl implements IShopCartRpc {

    @Resource
    private IShopCartService shopCartService;

    @Override
    public Boolean addCart(ShopCartReqDTO shopCartReqDTO) {
        return shopCartService.addCart(shopCartReqDTO);
    }

    @Override
    public Boolean removeFromCart(ShopCartReqDTO shopCartReqDTO) {
        return shopCartService.removeFromCart(shopCartReqDTO);
    }

    @Override
    public ShopCartRespDTO getCartInfo(ShopCartReqDTO shopCartReqDTO) {
        return shopCartService.getCartInfo(shopCartReqDTO);
    }

    @Override
    public Boolean clearCart(ShopCartReqDTO shopCartReqDTO) {
        return shopCartService.clearCart(shopCartReqDTO);
    }

    @Override
    public Boolean updateCartItemNum(ShopCartReqDTO shopCartReqDTO) {
        return shopCartService.updateCartItemNum(shopCartReqDTO);
    }
}
