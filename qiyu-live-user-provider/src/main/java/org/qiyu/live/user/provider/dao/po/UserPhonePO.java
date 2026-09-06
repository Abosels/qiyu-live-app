package org.qiyu.live.user.provider.dao.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_user_phone")
public class UserPhonePO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String phone;

    private Integer status;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
