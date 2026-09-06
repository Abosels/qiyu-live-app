package org.qiyu.live.living.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * PK 计分明细 PO。
 * <p>每笔 PK 礼物一条记录，biz_id = SendGiftMQ.uuId，
 * 与资金流水、礼物记录共享同一业务号。</p>
 */
@TableName("t_pk_score_record")
@Data
public class PkScoreRecordPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** PK 对局 ID */
    private Long pkId;

    /** 业务唯一号 = SendGiftMQ.uuId */
    private String bizId;

    /** 送礼人用户 ID */
    private Long senderId;
    /** 收礼人用户 ID */
    private Long receiverId;
    /** 礼物所属直播间 ID */
    private Integer roomId;

    /** 属于哪一方：A / B */
    private String side;

    /** 礼物 ID */
    private Integer giftId;
    /** 礼物数量 */
    private Integer giftNum;
    /** 礼物单价（分） */
    private Integer giftPrice;
    /** 本次加分 */
    private Long addScore;

    /** 计分状态：1=有效 0=无效 */
    private Integer scoreStatus;

    /** Redis 同步状态：0=未同步 1=已同步 2=失败待补偿 */
    private Integer redisSyncStatus;
    /** 补偿重试次数 */
    private Integer retryCount;
    /** 最近一次失败原因 */
    private String lastError;

    /** 送礼时间（结算时以此判断是否超时） */
    private LocalDateTime giftCreateTime;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}