package org.qiyu.live.api.vo.resp;

import lombok.Data;

@Data
public class PayProductRespVO {
    private String orderId;

    /** 支付宝渠道返回 bank-api 的收银台入口；微信 Mock 场景保持为空。 */
    private String payUrl;
}
