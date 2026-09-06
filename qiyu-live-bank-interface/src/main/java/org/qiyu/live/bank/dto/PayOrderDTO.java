package org.qiyu.live.bank.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PayOrderDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 外部订单唯一编号
     */
    private String orderId;

    /**
     * 付费产品ID
     */
    private Integer productId;

    /**
     * 订单状态 0待支付,1支付中,2已支付,3撤销,4无效
     */
    private Integer status;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付渠道 0支付宝 1微信 2银联 3收银台
     */
    private Integer payChannel;

    /**
     * 来源
     */
    private Integer source;

    /**
     * 下单时固化的支付金额，单位为分，避免商品后续改价影响已创建订单。
     */
    private Integer payAmount;

    /**
     * 下单时固化的金币数量，避免产品配置变更导致到账数量漂移。
     */
    private Integer coinAmount;

    /**
     * 第三方支付平台返回的交易号，用于对账与重复回调审计。
     */
    private String thirdPartyTradeNo;

    /**
     * 支付成功回调时间
     */
    private LocalDateTime payTime;

    private Integer bizCode;

}
