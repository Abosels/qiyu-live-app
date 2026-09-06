package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_category_info")
public class CategoryInfoPO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer level;
    private Integer parentId;
    private String categoryName;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
