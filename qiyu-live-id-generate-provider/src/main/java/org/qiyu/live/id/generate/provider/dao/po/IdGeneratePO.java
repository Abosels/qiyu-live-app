package org.qiyu.live.id.generate.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_id_generate_config")
public class IdGeneratePO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 配置备注说明
     */
    private String remark;

    /**
     * 初始值
     */
    @TableField("init_num")
    private Long initNum;

    /**
     * 当前号段阈值
     */
    @TableField("next_threshold")
    private Long nextThreshold;

    /**
     * 当前号段起始值
     */
    @TableField("current_start")
    private Long currentStart;

    /**
     * 步长（每次递增区间）
     */
    private Integer step;

    /**
     * 是否有序：0-无序，1-有序
     */
    @TableField("is_seq")
    private Integer isSeq;

    /**
     * 业务前缀，若为空则返回时不携带
     */
    @TableField("id_prefix")
    private String idPrefix;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
