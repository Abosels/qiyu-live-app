package org.qiyu.live.msg.provider.dao.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Date;

@Data
@TableName("t_sms")
public class SmsPO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("phone")
    private String phone;
    @TableField("code")
    private Integer code;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
