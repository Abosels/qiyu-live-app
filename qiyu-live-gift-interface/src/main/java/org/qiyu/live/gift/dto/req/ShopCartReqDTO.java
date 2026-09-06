package org.qiyu.live.gift.dto.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ShopCartReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long skuId;
    private Integer roomId;

    /**
     * 约定
     * changeNum = 1   数量加一
     * changeNum = -1  数量减一
     */
    private Integer changeNum;
}
