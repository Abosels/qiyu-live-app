package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.IShopInfoService;
import org.qiyu.live.api.vo.PrepareOrderVO;
import org.qiyu.live.api.vo.WebResponseVO;
import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.api.vo.req.ShopCartReqVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 带货相关控制器
 */
@RestController
@RequestMapping("/shop")
public class ShopInfoController {
    @Resource
    private IShopInfoService shopInfoService;
    @PostMapping("/listSkuInfo")
    public WebResponseVO listSkuInfo(Integer roomId){
        return WebResponseVO.success(shopInfoService.queryByRoomId(roomId));
    }

    @PostMapping("/detail")
    public WebResponseVO listSkuInfo(SkuInfoReqVO reqVO){
        return WebResponseVO.success(shopInfoService.detail(reqVO));
    }

    /**
     * 往购物车添加商品
     * @return
     */
    @PostMapping("/addCart")
    public WebResponseVO addCart(@RequestBody ShopCartReqVO shopCartReqVO){
        return WebResponseVO.success(shopInfoService.addCart(shopCartReqVO));
    }

    /**
     * 网购物车减商品
     * @return
     */
    @PostMapping("/removeFromCart")
    public WebResponseVO removeFromCart(@RequestBody ShopCartReqVO shopCartReqVO){
        return WebResponseVO.success(shopInfoService.removeFromCart(shopCartReqVO));
    }

    /**
     * 查看购物车中商品信息
     * @return
     */
    @PostMapping("/getCartInfo")
    public WebResponseVO getCartInfo(@RequestBody ShopCartReqVO shopCartReqVO){
        return WebResponseVO.success(shopInfoService.getCartInfo(shopCartReqVO.getRoomId()));
    }

    /**
     * 清空购物车中的商品
     * @return
     */
    @PostMapping("/clearCart")
    public WebResponseVO clearCart(@RequestBody ShopCartReqVO shopCartReqVO){
        return WebResponseVO.success(shopInfoService.clearCart(shopCartReqVO.getRoomId()));
    }

    /** 修改购物车中指定 SKU 的数量，changeNum 由前端传 1 或 -1。 */
    @PostMapping("/updateCartItemNum")
    public WebResponseVO updateCartItemNum(@RequestBody ShopCartReqVO shopCartReqVO) {
        return WebResponseVO.success(shopInfoService.updateCartItemNum(shopCartReqVO));
    }
    @PostMapping("/prepareOrder")
    public WebResponseVO prepareOrder(@RequestBody PrepareOrderVO prepareOrderVO){
        return WebResponseVO.success(shopInfoService.prepareOrder(prepareOrderVO));
    }

    @PostMapping("/payNow")
    public WebResponseVO payNow(@RequestBody PrepareOrderVO prepareOrderVO){
        return WebResponseVO.success(shopInfoService.payNow(prepareOrderVO.getOrderId()));
    }
}
