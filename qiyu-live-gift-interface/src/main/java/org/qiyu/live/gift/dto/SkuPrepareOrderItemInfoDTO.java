package org.qiyu.live.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SkuPrepareOrderItemInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private SkuInfoDTO skuInfo;
    /**
     * 买了多少件
     */
    private Integer count;
}
