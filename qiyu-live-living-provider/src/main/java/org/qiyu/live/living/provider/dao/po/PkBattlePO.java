package org.qiyu.live.living.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * PK 对局记录 PO。
 */
@TableName("t_pk_battle")
@Data
public class PkBattlePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** PK 对局唯一 ID（雪花算法生成） */
    private Long pkId;

    /** A 方房间 ID */
    private Integer roomIdA;
    /** B 方房间 ID */
    private Integer roomIdB;

    /** A 方主播用户 ID */
    private Long anchorIdA;
    /** B 方主播用户 ID */
    private Long anchorIdB;

    /** A 方最终分数 */
    private Long scoreA;
    /** B 方最终分数 */
    private Long scoreB;

    /** 胜方：A / B / DRAW */
    private String winnerSide;

    /** RUNNING / SETTLING / FINISHED / CANCELLED */
    private String status;

    /** 乐观锁版本号 */
    private Integer version;

    /** PK 开始时间 */
    private LocalDateTime startTime;
    /** PK 预计结束时间 */
    private LocalDateTime endTime;
    /** 实际结算时间 */
    private LocalDateTime settleTime;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}