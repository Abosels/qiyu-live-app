package org.qiyu.live.bank.api.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.bank.api.config.AlipaySandboxProperties;
import org.qiyu.live.bank.api.service.IPayNotifyService;
import org.qiyu.live.bank.api.vo.WxPayNotifyVO;
import org.qiyu.live.bank.constants.OrderStatusEnum;
import org.qiyu.live.bank.constants.PayChannelEnum;
import org.qiyu.live.bank.dto.PayOrderDTO;
import org.qiyu.live.bank.interfaces.IPayOrderRpc;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@Slf4j
public class PayNotifyServiceImpl implements IPayNotifyService {

    @DubboReference
    private IPayOrderRpc payOrderRpc;
    @Resource
    private AlipaySandboxProperties alipaySandboxProperties;

    @Override
    public String onNotifyHandler(String paramJson) {
        WxPayNotifyVO wxPayNotifyVO = JSON.parseObject(paramJson, WxPayNotifyVO.class);
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setOrderId(wxPayNotifyVO.getOrderId());
        payOrderDTO.setUserId(wxPayNotifyVO.getUserId());
        payOrderDTO.setBizCode(wxPayNotifyVO.getBizCode());
        boolean notifySuccess = payOrderRpc.payNotify(payOrderDTO);
        return notifySuccess ? "success" : "fail";
    }

    @Override
    public String createAlipayPagePayForm(String orderId) {
        PayOrderDTO payOrderDTO = payOrderRpc.queryByOrderId(orderId);
        if (payOrderDTO == null
                || !Integer.valueOf(PayChannelEnum.ZHI_FU_BAO.getCode()).equals(payOrderDTO.getPayChannel())
                || !isPayable(payOrderDTO.getStatus())
                || payOrderDTO.getPayAmount() == null
                || payOrderDTO.getPayAmount() <= 0) {
            throw new IllegalArgumentException("支付宝支付订单不存在或状态不允许支付");
        }
        validateAlipayConfig();

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(payOrderDTO.getOrderId());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setSubject("七遇直播金币充值");
        model.setTotalAmount(toYuan(payOrderDTO.getPayAmount()));

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(alipaySandboxProperties.getReturnUrl());
        request.setNotifyUrl(alipaySandboxProperties.getNotifyUrl());
        request.setBizModel(model);
        try {
            AlipayTradePagePayResponse response = createAlipayClient().pageExecute(request);
            if (!response.isSuccess()) {
                throw new IllegalStateException("支付宝下单失败：" + response.getSubMsg());
            }
            return response.getBody();
        } catch (Exception e) {
            throw new IllegalStateException("支付宝下单请求异常", e);
        }
    }

    @Override
    public String onAlipayNotify(Map<String, String> notifyParams) {
        try {
            validateAlipayConfig();
            if (!AlipaySignature.rsaCheckV1(notifyParams, alipaySandboxProperties.getAlipayPublicKey(), "UTF-8", "RSA2")) {
                log.warn("[alipayNotify] signature verification failed, outTradeNo={}", notifyParams.get("out_trade_no"));
                return "failure";
            }
            if (!alipaySandboxProperties.getAppId().equals(notifyParams.get("app_id"))
                    || !isSuccessTradeStatus(notifyParams.get("trade_status"))) {
                log.warn("[alipayNotify] callback parameters rejected, outTradeNo={}, tradeStatus={}",
                        notifyParams.get("out_trade_no"), notifyParams.get("trade_status"));
                return "failure";
            }

            PayOrderDTO payOrderDTO = new PayOrderDTO();
            payOrderDTO.setOrderId(notifyParams.get("out_trade_no"));
            payOrderDTO.setPayChannel(PayChannelEnum.ZHI_FU_BAO.getCode());
            payOrderDTO.setPayAmount(toCent(notifyParams.get("total_amount")));
            payOrderDTO.setThirdPartyTradeNo(notifyParams.get("trade_no"));
            return payOrderRpc.payNotify(payOrderDTO) ? "success" : "failure";
        } catch (Exception e) {
            // 回调失败时返回 failure，让支付宝按其通知策略重试；日志不输出密钥和完整签名参数。
            log.error("[alipayNotify] callback processing failed, outTradeNo={}", notifyParams.get("out_trade_no"), e);
            return "failure";
        }
    }

    /** 支付宝沙箱与正式环境仅网关和密钥不同，支付业务代码完全复用。 */
    private AlipayClient createAlipayClient() throws Exception {
        AlipayConfig config = new AlipayConfig();
        config.setServerUrl(alipaySandboxProperties.getGatewayUrl());
        config.setAppId(alipaySandboxProperties.getAppId());
        config.setPrivateKey(alipaySandboxProperties.getAppPrivateKey());
        config.setFormat("json");
        config.setCharset("UTF-8");
        config.setAlipayPublicKey(alipaySandboxProperties.getAlipayPublicKey());
        config.setSignType("RSA2");
        return new DefaultAlipayClient(config);
    }

    private void validateAlipayConfig() {
        if (!alipaySandboxProperties.isEnabled()
                || !StringUtils.hasText(alipaySandboxProperties.getAppId())
                || !StringUtils.hasText(alipaySandboxProperties.getAppPrivateKey())
                || !StringUtils.hasText(alipaySandboxProperties.getAlipayPublicKey())
                || !StringUtils.hasText(alipaySandboxProperties.getNotifyUrl())) {
            throw new IllegalStateException("支付宝沙箱配置不完整");
        }
    }

    private boolean isPayable(Integer status) {
        return OrderStatusEnum.WAITING_PAY.getCode().equals(status)
                || OrderStatusEnum.PAYING.getCode().equals(status);
    }

    private boolean isSuccessTradeStatus(String tradeStatus) {
        return "TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus);
    }

    private String toYuan(Integer amountInCent) {
        return BigDecimal.valueOf(amountInCent, 2).setScale(2, RoundingMode.UNNECESSARY).toPlainString();
    }

    private Integer toCent(String amountInYuan) {
        return new BigDecimal(amountInYuan).movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).intValueExact();
    }
}
