package org.qiyu.live.api.vo;

import lombok.Data;

@Data
public class PrepareOrderVO {
    /**
     * 支付时回传预下单响应中的订单号；预下单请求不需要传该字段。
     */
    private Integer orderId;
    /** 保留旧字段兼容请求体，实际用户身份始终以登录上下文为准。 */
    private Long userId;
    private Integer roomId;
}
