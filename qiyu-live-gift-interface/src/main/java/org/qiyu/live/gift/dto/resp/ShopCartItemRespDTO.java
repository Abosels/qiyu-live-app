package org.qiyu.live.gift.dto.resp;

import lombok.Data;
import org.qiyu.live.gift.dto.SkuInfoDTO;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ShopCartItemRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer count;
    private SkuInfoDTO skuInfo;

    public ShopCartItemRespDTO(Integer count, SkuInfoDTO skuInfoDTO) {
        this.count = count;
        this.skuInfo = skuInfoDTO;
    }
}
