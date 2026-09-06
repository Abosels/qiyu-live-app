package org.qiyu.live.api.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.IShopInfoService;
import org.qiyu.live.api.vo.PrepareOrderVO;
import org.qiyu.live.api.error.QiyuApiError;
import org.qiyu.live.api.vo.req.ShopCartReqVO;
import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.api.vo.resp.SkuDetailInfoVO;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.SkuInfoDTO;
import org.qiyu.live.gift.dto.SkuPrepareOrderInfoDTO;
import org.qiyu.live.gift.dto.req.PrepareOrderReqDTO;
import org.qiyu.live.gift.dto.req.PayNowReqDTO;
import org.qiyu.live.gift.dto.req.ShopCartReqDTO;
import org.qiyu.live.gift.dto.resp.ShopCartRespDTO;
import org.qiyu.live.gift.interfaces.IShopCartRpc;
import org.qiyu.live.gift.interfaces.ISkuInfoRpc;
import org.qiyu.live.gift.interfaces.ISkuOrderInfoRPC;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.BizBaseErrorEnum;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopInfoServiceImpl implements IShopInfoService {

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private ISkuInfoRpc skuInfoRpc;
    @DubboReference
    private IShopCartRpc shopCartRpc;
    @DubboReference
    private ISkuOrderInfoRPC skuOrderInfoRPC;

    @Override
    public List<SkuInfoDTO> queryByRoomId(Integer roomId) {
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(livingRoomRespDTO, BizBaseErrorEnum.PARAM_ERROR);
        Long anchorId = livingRoomRespDTO.getAnchorId();
        List<SkuInfoDTO> skuInfoDTOS = skuInfoRpc.queryByAnchorId(anchorId);
        ErrorAssert.isTrue(CollectionUtils.isNotEmpty(skuInfoDTOS), BizBaseErrorEnum.PARAM_ERROR);
        return ConvertBeanUtils.convertList(skuInfoDTOS, SkuInfoDTO.class);
    }

    @Override
    public SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO) {
        return ConvertBeanUtils.convert(skuInfoRpc.queryBySkuId(skuInfoReqVO.getSkuId()), SkuDetailInfoVO.class);
    }

    @Override
    public Boolean addCart(ShopCartReqVO shopCartReqVO) {
        ShopCartReqDTO shopCartReqDTO = ConvertBeanUtils.convert(shopCartReqVO, ShopCartReqDTO.class);
        shopCartReqDTO.setUserId(QiyuRequestContext.getUserId());
        return shopCartRpc.addCart(shopCartReqDTO);
    }

    @Override
    public Boolean removeFromCart(ShopCartReqVO shopCartReqVO) {
        ShopCartReqDTO shopCartReqDTO = ConvertBeanUtils.convert(shopCartReqVO, ShopCartReqDTO.class);
        shopCartReqDTO.setUserId(QiyuRequestContext.getUserId());
        return shopCartRpc.removeFromCart(shopCartReqDTO);
    }

    @Override
    public ShopCartRespDTO getCartInfo(Integer roomId) {
        // 购物车 RPC 统一通过请求 DTO 传递用户与直播间信息。
        ShopCartReqDTO shopCartReqDTO = new ShopCartReqDTO();
        shopCartReqDTO.setUserId(QiyuRequestContext.getUserId());
        shopCartReqDTO.setRoomId(roomId);
        return shopCartRpc.getCartInfo(shopCartReqDTO);
    }

    @Override
    public Boolean clearCart(Integer roomId) {
        // 清理范围限定为当前登录用户在指定直播间的购物车。
        ShopCartReqDTO shopCartReqDTO = new ShopCartReqDTO();
        shopCartReqDTO.setUserId(QiyuRequestContext.getUserId());
        shopCartReqDTO.setRoomId(roomId);
        return shopCartRpc.clearCart(shopCartReqDTO);
    }

    @Override
    public Boolean updateCartItemNum(ShopCartReqVO shopCartReqVO) {
        ShopCartReqDTO shopCartReqDTO = ConvertBeanUtils.convert(shopCartReqVO, ShopCartReqDTO.class);
        shopCartReqDTO.setUserId(QiyuRequestContext.getUserId());
        return shopCartRpc.updateCartItemNum(shopCartReqDTO);
    }

    @Override
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO prepareOrderVO) {
        PrepareOrderReqDTO prepareOrderReqDTO = new PrepareOrderReqDTO();
        prepareOrderReqDTO.setUserId(QiyuRequestContext.getUserId());
        prepareOrderReqDTO.setRoomId(prepareOrderVO.getRoomId());
        SkuPrepareOrderInfoDTO prepareOrderInfoDTO = skuOrderInfoRPC.prepareOrder(prepareOrderReqDTO);
        ErrorAssert.isNotNull(prepareOrderInfoDTO, QiyuApiError.SKU_IS_NOT_ENOUGH);
        return prepareOrderInfoDTO;

    }

    @Override
    public Boolean payNow(Integer orderId) {
        ErrorAssert.isNotNull(orderId, QiyuApiError.PAY_ERROR);
        PayNowReqDTO payNowReqDTO = new PayNowReqDTO();
        payNowReqDTO.setOrderId(orderId);
        // 不能使用前端提交的 userId，避免用户尝试支付他人的订单。
        payNowReqDTO.setUserId(QiyuRequestContext.getUserId());
        boolean paySuccess = skuOrderInfoRPC.payNow(payNowReqDTO);
        ErrorAssert.isTrue(paySuccess, QiyuApiError.PAY_ERROR);
        return true;
    }
}
