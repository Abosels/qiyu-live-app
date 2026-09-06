package provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_pay_product")
public class PayProductPO {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品价格(单位分)
     */
    private Integer price;

    /**
     * 扩展字段
     */
    private String extra;

    /**
     * 类型(鲸鱼币类型)
     */
    private Integer type;

    /**
     * 状态(0无效,1有效)
     */
    private Integer validStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
