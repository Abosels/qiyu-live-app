package org.qiyu.live.bank.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PayProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
    * 主键id
    */
    private Long id;

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
