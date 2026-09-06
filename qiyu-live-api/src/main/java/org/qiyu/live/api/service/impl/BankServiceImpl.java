package org.qiyu.live.api.service.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.IBankService;
import org.qiyu.live.api.vo.req.PayProductReqVO;
import org.qiyu.live.api.vo.resp.PayProductItemVO;
import org.qiyu.live.api.vo.resp.PayProductRespVO;
import org.qiyu.live.api.vo.resp.PayProductVO;
import org.qiyu.live.bank.constants.OrderStatusEnum;
import org.qiyu.live.bank.constants.PayChannelEnum;
import org.qiyu.live.bank.dto.PayOrderDTO;
import org.qiyu.live.bank.dto.PayProductDTO;
import org.qiyu.live.bank.interfaces.IPayOrderRpc;
import org.qiyu.live.bank.interfaces.IPayProductRpc;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import org.qiyu.live.bank.constants.PaySourceEnum;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.BizBaseErrorEnum;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankServiceImpl implements IBankService {
    @Value("${qiyu.pay.bank-api-base-url:http://localhost:8093}")
    private String bankApiBaseUrl;
    @DubboReference
    private IPayProductRpc payProductRpc;
    @DubboReference
    private IQiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;
    @DubboReference
    private IPayOrderRpc payOrderRpc;

    @Override
    public PayProductVO products(Integer type) {
        List<PayProductDTO> products = payProductRpc.products(type);
        PayProductVO vo = new PayProductVO();
        List<PayProductItemVO> voList = new ArrayList<>();
        for (PayProductDTO product : products) {
            PayProductItemVO itemVO = new PayProductItemVO();
            itemVO.setName(product.getName());
            itemVO.setId(product.getId());
            itemVO.setCoinNumber(JSON.parseObject(product.getExtra()).getInteger("coin"));
            voList.add(itemVO);
        }
        vo.setPayProductItemVOList(voList);
        vo.setCurrentBalance(qiyuCurrencyAccountRpc.getBalance(QiyuRequestContext.getUserId()));
        return vo;
    }

    @Override
    public PayProductRespVO payProduct(PayProductReqVO payProductReqVO) {
        //参数校验
        ErrorAssert.isTrue(payProductReqVO!=null && payProductReqVO.getProductId()!=null
                && payProductReqVO.getPaySource()!=null && payProductReqVO.getPayChannel()!=null, BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isNotNull(PaySourceEnum.find(payProductReqVO.getPaySource()),BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isTrue(PayChannelEnum.ZHI_FU_BAO.getCode() == payProductReqVO.getPayChannel()
                        || PayChannelEnum.WEI_XIN.getCode() == payProductReqVO.getPayChannel(),
                BizBaseErrorEnum.PARAM_ERROR);
        PayProductDTO payProductDTO = payProductRpc.getByProductId(payProductReqVO.getProductId());
        ErrorAssert.isNotNull(payProductDTO, BizBaseErrorEnum.PARAM_ERROR);

        //插入一条订单,待支付状态
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setProductId(payProductReqVO.getProductId());
        payOrderDTO.setUserId(QiyuRequestContext.getUserId());
        payOrderDTO.setSource(payProductReqVO.getPaySource());
        payOrderDTO.setPayChannel(payProductReqVO.getPayChannel());
        String orderId = payOrderRpc.insertOne(payOrderDTO);
        //更新订单为支付中状态
        payOrderRpc.updateOrderStatus(orderId, OrderStatusEnum.PAYING.getCode());
        PayProductRespVO respVO = new PayProductRespVO();
        respVO.setOrderId(orderId);
        if (PayChannelEnum.ZHI_FU_BAO.getCode() == payProductReqVO.getPayChannel()) {
            // 前端直接跳转 bank-api，bank-api 再生成支付宝 SDK 的自动提交表单。
            respVO.setPayUrl(bankApiBaseUrl + "/payNotify/alipay-page-pay?orderId=" + orderId);
        }

        return respVO;
    }
}
