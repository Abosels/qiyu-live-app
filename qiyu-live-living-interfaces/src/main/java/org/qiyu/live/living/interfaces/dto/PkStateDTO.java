package org.qiyu.live.living.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * PK 状态完整快照 DTO。
 * <p>
 * 用于替代原来只返回单个 Long 的 queryOnlinePkUserId，
 * 将 pkId、双方房间/主播、比分、version、状态、时间窗口
 * 一次性返回给调用方（API 层、前端、其他 Consumer）。
 * </p>
 *
 * <p>
 * 两个直播间收到完全相同的数据，
 * scoreA 永远属于 anchorIdA / roomIdA，
 * scoreB 永远属于 anchorIdB / roomIdB。
 * </p>
 */
@Data
public class PkStateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** PK 对局唯一 ID */
    private Long pkId;

    /** A 方房间 ID */
    private Integer roomIdA;
    /** B 方房间 ID */
    private Integer roomIdB;

    /** A 方主播用户 ID */
    private Long anchorIdA;
    /** B 方主播用户 ID */
    private Long anchorIdB;

    /** A 方当前分数 */
    private Long scoreA;
    /** B 方当前分数 */
    private Long scoreB;

    /** 单调递增版本号，前端用于丢弃乱序到达的旧消息 */
    private Long version;

    /**
     * PK 状态：
     * WAITING  — 等待匹配中
     * RUNNING  — PK 进行中
     * SETTLING — 结算中（不再接受新礼物）
     * FINISHED — 已结束
     * CANCELLED — 已取消
     */
    private String status;

    /** PK 开始时间戳（毫秒） */
    private Long startTime;
    /** PK 预计结束时间戳（毫秒） */
    private Long endTime;

    /** A / B / DRAW，结算前为空。 */
    private String winnerSide;

    /** 获胜主播，平局或未结算时为空。 */
    private Long winnerAnchorId;

    /** 实际结算完成时间戳（毫秒）。 */
    private Long finishTime;
}
