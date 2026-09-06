package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_sku_info")
public class SkuInfoPO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long skuId;
    private Integer skuPrice;
    private String skuCode;
    private String name;
    private Integer status;
    private String iconUrl;
    private String originlIconUrl;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}