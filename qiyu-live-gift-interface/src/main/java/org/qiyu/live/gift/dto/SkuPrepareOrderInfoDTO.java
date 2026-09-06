package org.qiyu.live.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class SkuPrepareOrderInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<SkuPrepareOrderItemInfoDTO> SkuPrepareOrderItemInfoDTOS;
    /** 预下单成功后返回给前端，支付时必须回传该订单号。 */
    private Integer orderId;
    private Integer totalPrice;
}
