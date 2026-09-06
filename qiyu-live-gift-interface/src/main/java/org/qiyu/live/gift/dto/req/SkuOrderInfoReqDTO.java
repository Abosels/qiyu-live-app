package org.qiyu.live.gift.dto.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class SkuOrderInfoReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Long userId;
    private Integer roomId;
    private Integer status;
    private List<Long> skuIdList;
    /** 预下单时计算并写入的虚拟币总价快照。 */
    private Integer totalPrice;

}
