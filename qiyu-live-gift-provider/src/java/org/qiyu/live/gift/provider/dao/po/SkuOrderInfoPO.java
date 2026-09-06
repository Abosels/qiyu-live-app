package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_sku_order_info")
public class SkuOrderInfoPO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** sku编号列表，逗号分隔 */
    private String skuIdList;

    /** 下单时固化的虚拟币总价，支付时以此字段扣款。 */
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
