package org.qiyu.live.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SkuInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Long skuId;
    private Integer skuPrice;
    private String skuCode;
    private String name;
    private Integer status;
    private String iconUrl;
    private String originalIconUrl;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
