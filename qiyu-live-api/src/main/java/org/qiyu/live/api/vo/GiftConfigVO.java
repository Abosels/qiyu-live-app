package org.qiyu.live.api.vo;

import lombok.Data;

/**
 * 礼物配置 VO（返回给前端展示礼物列表用）
 */
@Data
public class GiftConfigVO {

    /**
     * 礼物ID
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
}
