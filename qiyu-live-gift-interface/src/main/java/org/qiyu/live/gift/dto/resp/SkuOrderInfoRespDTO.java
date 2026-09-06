package org.qiyu.live.gift.dto.resp;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SkuOrderInfoRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    /** sku编号列表，逗号分隔 */
    private String skuIdList;

    /** 下单时的虚拟币总价快照。 */
    private Integer totalPrice;

    /** 用户id */
    private Long userId;

    /** 直播id */
    private Integer roomId;

    /** 状态 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 创建时间戳 */
    private Integer createTime;

    /** 更新时间戳 */
    private Integer updateTime;
}
