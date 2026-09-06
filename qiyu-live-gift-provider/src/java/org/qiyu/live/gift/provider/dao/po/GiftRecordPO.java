package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 送礼记录表 t_gift_record PO实体
 */
@Data
@TableName("t_gift_record")
public class GiftRecordPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 157571663715754L;

    /**
     * 送礼流水记录自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 发起送礼行为的用户 ID
     */
    private Long userId;

    /**
     * 接收礼物的对象 ID（主播 / 直播间 ID）
     */
    private Long objectId;

    /**
     * 关联礼物配置表的gift_id，标记赠送的是哪款礼物
     */
    private Integer giftId;

    /**
     * 送礼行为的来源渠道编码（区分不同入口）
     */
    private Short source;

    /**
     * 用户实际送出礼物的时间
     */
    private LocalDateTime sendTime;

    /**
     * 本条送礼记录的更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 送礼金额
     */
    private Integer price;
    private Integer priceUnit;
}