package org.qiyu.live.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SkuOrderInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 订单主键。 */
    private Integer id;

    /** 下单 SKU 编号列表，逗号分隔。 */
    private String skuIdList;

    /** 下单时的虚拟币总价快照。 */
    private Integer totalPrice;

    /** 下单用户 ID。 */
    private Long userId;

    /** 订单所属直播间 ID。 */
    private Integer roomId;

    /** 订单状态。 */
    private Integer status;

    /** 订单备注。 */
    private String remark;

    /** 创建时间戳。 */
    private Integer createTime;

    /** 更新时间戳。 */
    private Integer updateTime;
}
