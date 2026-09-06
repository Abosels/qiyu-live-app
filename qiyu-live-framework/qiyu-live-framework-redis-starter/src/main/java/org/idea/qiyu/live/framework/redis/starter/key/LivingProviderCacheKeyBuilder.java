package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class LivingProviderCacheKeyBuilder extends RedisKeyBuilder{

    private static String LIVING_ROOM_LIST = "living_room_list";
    private static String LIVING_ROOM_OBJ = "living_room_OBJ";
    private static String REFRESH_LIVING_ROOM_LIST_LOCK = "refresh_living_room_list_lock";
    private static String LIVING_ROOM_USER_SET = "living_room_user_set";

    private static String LIVING_ONLINE_PK = "living_online_pk";
    private static String PK_WAITING_POOL = "pk_waiting_pool";
    private static String PK_WAITING_ROOM = "pk_waiting_room";
    private static String PK_STATE = "pk_state";
    private static String ROOM_TO_PK = "room_to_pk";
    private static String PK_ID_GENERATOR = "pk_id_generator";
    private static String PK_SCORE_DEDUPE = "pk_score_dedupe";
    private static String PK_SETTLE_LOCK = "pk_settle_lock";

    /**
     * 直播间缓存
     * @param roomId
     * @return
     */
    public String buildLivingRoomObj(Integer roomId) {
        return super.getPrefix() + LIVING_ROOM_OBJ + super.getSplitItem() + roomId;
    }

    /**
     * 直播间列表
     * @param type
     * @return
     */
    public String buildLivingRoomList(Integer type) {
        return super.getPrefix() + LIVING_ROOM_LIST + super.getSplitItem() + type;
    }

    /**
     * 列表刷新锁
     * @return
     */
    public String buildLivingRoomListLock() {
        return super.getPrefix() + REFRESH_LIVING_ROOM_LIST_LOCK;
    }

    /**
     * 在线用户set
     * @param roomId
     * @param appId
     * @return
     */
    public String buildLivingRoomUserSet(Integer roomId, Integer appId) {
        return super.getPrefix() + LIVING_ROOM_USER_SET + super.getSplitItem() + appId + super.getSplitItem() + roomId;
    }
    public String buildLivingOnlinePk(Integer roomId){
        return super.getPrefix() + LIVING_ONLINE_PK + super.getSplitItem() + roomId;
    }

    /**
     * PK 等待池 ZSet Key（全局唯一）。
     * <p>member = roomId，score = 申请时间戳（毫秒）。
     * 匹配时按 score 升序取最早申请的 roomId。</p>
     */
    public String buildPkWaitingPool() {
        return super.getPrefix() + PK_WAITING_POOL;
    }

    /**
     * 单个房间的 PK 等待状态 Key。
     * <p>有 TTL（5 分钟），主播异常退出后自动清理，不会长期占坑。</p>
     *
     * @param roomId 直播间 ID
     */
    public String buildPkWaitingRoom(Integer roomId) {
        return super.getPrefix() + PK_WAITING_ROOM + super.getSplitItem() + roomId;
    }
    /**
     * PK 对局实时状态 Hash Key。
     * <p>Hash 字段：pkId, roomIdA, roomIdB, anchorIdA, anchorIdB,
     * scoreA, scoreB, version, status, startTime, endTime。</p>
     * <p>无论一场 PK 收到多少笔礼物，Hash
     始终只有固定字段，不会膨胀。</p>
     *
     * @param pkId PK 对局唯一 ID
     */
    public String buildPkState(Long pkId) {
        return super.getPrefix() + PK_STATE + super.getSplitItem() + pkId;
    }

    /**
     * 房间 → PK 对局 ID 映射 Key。
     * <p>两个直播间通过此映射连接到同一份 PK 状态。
     * 此后无论礼物流水来自哪个房间，都通过 roomId → pkId → PK 状态 Hash
     计分和广播。</p>
     * <p>PK 结束后此 Key 需同步清理。</p>
     *
     * @param roomId 直播间 ID
     */
    public String buildRoomToPk(Integer roomId) {
        return super.getPrefix() + ROOM_TO_PK + super.getSplitItem() + roomId;
    }
    /**
     * PK 对局 ID 生成器 Key（Redis INCR 自增）。
     * <p>每次匹配成功后调用 INCR 获取全局唯一的 pkId。</p>
     */
    public String buildPkIdGenerator() {
        return super.getPrefix() + PK_ID_GENERATOR;
    }

    /**
     * 单笔送礼在 PK 实时计分阶段的去重 Key。
     * MySQL 唯一索引保证持久化幂等，该 Key 保证 Redis 加分也只会执行一次。
     */
    public String buildPkScoreDedupe(Long pkId, String bizId) {
        return super.getPrefix() + PK_SCORE_DEDUPE + super.getSplitItem() + pkId
                + super.getSplitItem() + bizId;
    }

    /**
     * PK 结算分布式锁，避免多个 living-provider 实例同时结束同一场 PK。
     */
    public String buildPkSettleLock(Long pkId) {
        return super.getPrefix() + PK_SETTLE_LOCK + super.getSplitItem() + pkId;
    }
}
