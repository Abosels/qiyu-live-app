package org.qiyu.live.living.provider.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.idea.qiyu.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.im.router.interfaces.contants.ImMsgBizCodeEnum;
import org.qiyu.live.living.interfaces.dto.PkStateDTO;
import org.qiyu.live.living.provider.dao.mapper.ILivingRoomMapper;
import org.qiyu.live.living.provider.dao.mapper.IPkBattleMapper;
import org.qiyu.live.living.provider.dao.mapper.IPkScoreRecordMapper;
import org.qiyu.live.living.provider.dao.po.LivingRoomPO;
import org.qiyu.live.living.provider.dao.po.PkBattlePO;
import org.qiyu.live.living.provider.dao.po.PkScoreRecordPO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.qiyu.live.living.provider.service.PkEventBroadcastService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Maintains PK lifecycle after matching: settlement, score reconciliation and waiting-pool cleanup. */
@Slf4j
@Component
public class PkLifecycleScheduler {

    private static final long FINISHED_STATE_TTL_SECONDS = 10 * 60;
    private static final DefaultRedisScript<List> PK_SETTLING_SCRIPT;
    private static final DefaultRedisScript<List> PK_STATE_SYNC_SCRIPT;

    static {
        DefaultRedisScript<List> settling = new DefaultRedisScript<>();
        settling.setLocation(new ClassPathResource("scripts/pk_settling.lua"));
        settling.setResultType(List.class);
        PK_SETTLING_SCRIPT = settling;
        DefaultRedisScript<List> sync = new DefaultRedisScript<>();
        sync.setLocation(new ClassPathResource("scripts/pk_state_sync.lua"));
        sync.setResultType(List.class);
        PK_STATE_SYNC_SCRIPT = sync;
    }

    @Resource private RedisTemplate<Object, Object> redisTemplate;
    @Resource private LivingProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource private IPkBattleMapper pkBattleMapper;
    @Resource private IPkScoreRecordMapper pkScoreRecordMapper;
    @Resource private ILivingRoomMapper livingRoomMapper;
    @Resource private ILivingRoomService livingRoomService;
    @Resource private PkEventBroadcastService pkEventBroadcastService;

    /** Claims and settles expired battles. A Redis lock makes this safe with multiple provider instances. */
    @Scheduled(fixedDelay = 5_000)
    public void settleExpiredPk() {
        List<PkBattlePO> battles = pkBattleMapper.selectList(new LambdaQueryWrapper<PkBattlePO>()
                .in(PkBattlePO::getStatus, List.of("RUNNING", "SETTLING"))
                .le(PkBattlePO::getEndTime, LocalDateTime.now())
                .last("limit 100"));
        for (PkBattlePO battle : battles) {
            settle(battle);
        }
    }

    /** Rebuilds Redis scores from durable records after a previous Redis sync failure. */
    @Scheduled(fixedDelay = 60_000)
    public void repairRedisScores() {
        List<PkScoreRecordPO> pending = pkScoreRecordMapper.selectList(new LambdaQueryWrapper<PkScoreRecordPO>()
                .in(PkScoreRecordPO::getRedisSyncStatus, List.of(0, 2))
                .eq(PkScoreRecordPO::getScoreStatus, 1)
                .last("limit 500"));
        pending.stream().map(PkScoreRecordPO::getPkId).distinct().forEach(pkId -> repairOnePk(pkId, pending));
    }

    /** Removes ZSet members whose waiting marker expired or whose room is no longer live. */
    @Scheduled(fixedDelay = 60_000)
    public void cleanPkWaitingPool() {
        String poolKey = cacheKeyBuilder.buildPkWaitingPool();
        long expiredBefore = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5);
        var rooms = redisTemplate.opsForZSet().rangeByScore(poolKey, 0, expiredBefore);
        if (rooms == null) {
            return;
        }
        for (Object value : rooms) {
            Integer roomId = Integer.valueOf(String.valueOf(value));
            boolean markerExists = Boolean.TRUE.equals(redisTemplate.hasKey(cacheKeyBuilder.buildPkWaitingRoom(roomId)));
            LivingRoomPO room = livingRoomMapper.selectById(roomId);
            boolean roomLive = room != null && Integer.valueOf(CommonStatusEnum.VALID_STATUS.getCode()).equals(room.getStatus());
            if (!markerExists || !roomLive) {
                redisTemplate.opsForZSet().remove(poolKey, value);
                redisTemplate.delete(cacheKeyBuilder.buildPkWaitingRoom(roomId));
                log.info("[PkLifecycleScheduler] removed stale waiting room, roomId={}", roomId);
            }
        }
    }

    private void settle(PkBattlePO battle) {
        String lockKey = cacheKeyBuilder.buildPkSettleLock(battle.getPkId());
        String owner = UUID.randomUUID().toString();
        if (!Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, owner, 30, TimeUnit.SECONDS))) {
            return;
        }
        try {
            String stateKey = cacheKeyBuilder.buildPkState(battle.getPkId());
            List<?> settleResult = redisTemplate.execute(PK_SETTLING_SCRIPT, List.of(stateKey), String.valueOf(System.currentTimeMillis()));
            if (settleResult == null || settleResult.isEmpty()) {
                log.warn("[PkLifecycleScheduler] PK state unavailable, pkId={}", battle.getPkId());
                return;
            }
            String currentStatus = String.valueOf(settleResult.getLast());
            if (!"SETTLING".equals(currentStatus)) {
                return;
            }

            ScoreSummary summary = summarizeScores(battle);
            String winnerSide = summary.scoreA == summary.scoreB ? "DRAW" : summary.scoreA > summary.scoreB ? "A" : "B";
            Long winnerAnchorId = "A".equals(winnerSide) ? battle.getAnchorIdA() : "B".equals(winnerSide) ? battle.getAnchorIdB() : null;
            long finishTime = System.currentTimeMillis();

            PkBattlePO update = new PkBattlePO();
            update.setId(battle.getId());
            update.setScoreA(summary.scoreA);
            update.setScoreB(summary.scoreB);
            update.setWinnerSide(winnerSide);
            update.setStatus("FINISHED");
            update.setSettleTime(LocalDateTime.now());
            update.setVersion((battle.getVersion() == null ? 0 : battle.getVersion()) + 1);
            pkBattleMapper.updateById(update);

            redisTemplate.opsForHash().put(stateKey, "scoreA", String.valueOf(summary.scoreA));
            redisTemplate.opsForHash().put(stateKey, "scoreB", String.valueOf(summary.scoreB));
            redisTemplate.opsForHash().put(stateKey, "status", "FINISHED");
            redisTemplate.opsForHash().put(stateKey, "winnerSide", winnerSide);
            redisTemplate.opsForHash().put(stateKey, "finishTime", String.valueOf(finishTime));
            if (winnerAnchorId != null) {
                redisTemplate.opsForHash().put(stateKey, "winnerAnchorId", String.valueOf(winnerAnchorId));
            }
            redisTemplate.opsForHash().increment(stateKey, "version", 1);
            redisTemplate.expire(stateKey, FINISHED_STATE_TTL_SECONDS, TimeUnit.SECONDS);
            redisTemplate.expire(cacheKeyBuilder.buildRoomToPk(battle.getRoomIdA()), FINISHED_STATE_TTL_SECONDS, TimeUnit.SECONDS);
            redisTemplate.expire(cacheKeyBuilder.buildRoomToPk(battle.getRoomIdB()), FINISHED_STATE_TTL_SECONDS, TimeUnit.SECONDS);

            PkStateDTO state = livingRoomService.queryCurrentPkState(battle.getRoomIdA());
            pkEventBroadcastService.broadcastState(state, ImMsgBizCodeEnum.LIVING_ROOM_PK_FINISH);
            log.info("[PkLifecycleScheduler] PK settled, pkId={}, scoreA={}, scoreB={}, winner={}",
                    battle.getPkId(), summary.scoreA, summary.scoreB, winnerSide);
        } catch (Exception e) {
            log.error("[PkLifecycleScheduler] PK settlement failed, pkId={}", battle.getPkId(), e);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    private void repairOnePk(Long pkId, List<PkScoreRecordPO> pending) {
        PkBattlePO battle = pkBattleMapper.selectOne(new LambdaQueryWrapper<PkBattlePO>()
                .eq(PkBattlePO::getPkId, pkId).last("limit 1"));
        if (battle == null || !"RUNNING".equals(battle.getStatus())) {
            markRecordsSynced(pending, pkId);
            return;
        }
        ScoreSummary summary = summarizeScores(battle);
        try {
            List<?> result = redisTemplate.execute(PK_STATE_SYNC_SCRIPT, List.of(cacheKeyBuilder.buildPkState(pkId)),
                    String.valueOf(summary.scoreA), String.valueOf(summary.scoreB));
            if (result == null || result.isEmpty() || "-1".equals(String.valueOf(result.getFirst()))) {
                return;
            }
            markRecordsSynced(pending, pkId);
            PkStateDTO state = livingRoomService.queryCurrentPkState(battle.getRoomIdA());
            pkEventBroadcastService.broadcastState(state, ImMsgBizCodeEnum.LIVING_ROOM_PK_STATE_SYNC);
        } catch (Exception e) {
            log.error("[PkLifecycleScheduler] Redis repair failed, pkId={}", pkId, e);
        }
    }

    private ScoreSummary summarizeScores(PkBattlePO battle) {
        List<PkScoreRecordPO> records = pkScoreRecordMapper.selectList(new LambdaQueryWrapper<PkScoreRecordPO>()
                .eq(PkScoreRecordPO::getPkId, battle.getPkId())
                .eq(PkScoreRecordPO::getScoreStatus, 1)
                .le(PkScoreRecordPO::getGiftCreateTime, battle.getEndTime()));
        long scoreA = records.stream().filter(record -> "A".equals(record.getSide())).mapToLong(PkScoreRecordPO::getAddScore).sum();
        long scoreB = records.stream().filter(record -> "B".equals(record.getSide())).mapToLong(PkScoreRecordPO::getAddScore).sum();
        return new ScoreSummary(scoreA, scoreB);
    }

    private void markRecordsSynced(List<PkScoreRecordPO> records, Long pkId) {
        records.stream().filter(record -> pkId.equals(record.getPkId())).forEach(record -> {
            PkScoreRecordPO update = new PkScoreRecordPO();
            update.setId(record.getId());
            update.setRedisSyncStatus(1);
            update.setLastError(null);
            pkScoreRecordMapper.updateById(update);
        });
    }

    private record ScoreSummary(long scoreA, long scoreB) { }
}
