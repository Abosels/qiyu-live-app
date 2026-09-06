package org.qiyu.live.common.interfaces.dto;

import lombok.Data;

/**
 * PK 计分 MQ 消息体。
 * <p>由 SendGiftConsumer 在确认资金扣款成功后发送到 PK_SCORE_TOPIC。
 * PkScoreConsumer 消费此消息完成 MySQL 计分记录 + Redis Lua
 原子加分。</p>
 */
@Data
public class PkScoreMQ {

    /** 业务唯一号 = SendGiftMQ.uuId，关联资金流水和计分明细 */
    private String bizId;

    /** PK 对局 ID */
    private Long pkId;

    /** 送礼人用户 ID */
    private Long senderId;

    /** 收礼人用户 ID */
    private Long receiverId;

    /** 礼物所属直播间 ID */
    private Integer roomId;

    /** 属于 PK 哪一方：A / B */
    private String side;

    /** 礼物 ID */
    private Integer giftId;

    /** 礼物数量 */
    private Integer giftNum;

    /** 本次加分（= giftPrice × giftNum 或自定义规则） */
    private Long addScore;

    /** 送礼时间戳（毫秒，结算时判断是否超时） */
    private Long giftCreateTime;
}