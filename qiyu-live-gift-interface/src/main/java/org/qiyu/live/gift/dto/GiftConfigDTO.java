package org.qiyu.live.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 礼物配置 DTO（接口层交互使用）
 */
@Data
public class GiftConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 16445216423176L;

    /**
     * 礼物ID（主键）
     */
    private Integer giftId;

    /**
     * 礼物名称
     */
    private String giftName;

    /**
     * 礼物价格（单位由 priceUnit 决定）
     */
    private Integer price;

    /**
     * 价格单位（1=金币, 2=钻石 等）
     */
    private Integer priceUnit;

    /**
     * 礼物图标/动画资源地址
     */
    private String giftImage;

    /**
     * 礼物类型（1=普通礼物, 2=特效礼物 等）
     */
    private Integer giftType;

    /**
     * 状态（0=禁用, 1=启用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * svga特效地址
     */
    private String svgaUrl;
}
