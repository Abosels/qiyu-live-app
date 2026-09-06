package org.qiyu.live.api.vo.resp;

import lombok.Data;

/** 购物车中单个 SKU 的展示信息。 */
@Data
public class ShopCartItemVO {

    private Long skuId;
    private Integer skuPrice;
    private String skuCode;
    private String name;
    private String iconUrl;
    private String originalIconUrl;
    private String remark;
    private Integer quantity;
}
