package org.qiyu.live.api.vo.req;

import lombok.Data;

@Data
public class ShopCartReqVO {

    private Long skuId;
    private Integer roomId;
    private Integer changeNum;
}
