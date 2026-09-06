package org.qiyu.live.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 送礼记录 DTO（接口交互使用）
 */
@Data
public class GiftRecordDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 133453427174768L;

    /**
     * 流水主键id
     */
    private Integer id;

    /**
     * 送礼用户ID
     */
    private Long userId;

    /**
     * 收礼对象ID（主播/直播间）
     */
    private Long objectId;

    /**
     * 礼物id（关联t_gift_config）
     */
    private Integer giftId;

    /**
     * 送礼渠道编码
     */
    private Short source;

    /**
     * 送礼时间
     */
    private LocalDateTime sendTime;

    /**
     * 记录更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 送礼金额
     */
    private Integer price;
    private Integer priceUnit;
}
