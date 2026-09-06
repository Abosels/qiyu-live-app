package org.qiyu.live.user.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
@Data
public class UserPhoneDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 156456L;
    private Long id;

    private Long userId;

    private String phone;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
