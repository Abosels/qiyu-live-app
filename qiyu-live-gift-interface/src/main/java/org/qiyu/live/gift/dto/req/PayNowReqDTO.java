package org.qiyu.live.gift.dto.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PayNowReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    /** 待支付订单主键，由预下单响应返回。 */
    private Integer orderId;
    /**
     * 保留旧字段兼容已有调用；支付时以 orderId 定位订单，不再按 roomId 查找最新订单。
     */
    private Integer roomId;
}
