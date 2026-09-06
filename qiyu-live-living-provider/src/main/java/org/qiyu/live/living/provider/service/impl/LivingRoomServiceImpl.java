package org.qiyu.live.living.provider.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.idea.qiyu.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.common.interfaces.topic.LivingProviderTopicNames;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.im.contants.AppIdEnum;
import org.qiyu.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.qiyu.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.interfaces.contants.ImMsgBizCodeEnum;
import org.qiyu.live.im.router.interfaces.rpc.ImRouterRpc;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.dto.PkStateDTO;
import org.qiyu.live.living.provider.dao.mapper.ILivingRoomMapper;
import org.qiyu.live.living.provider.dao.mapper.ILivingRoomRecordMapper;
import org.qiyu.live.living.provider.dao.mapper.IPkBattleMapper;
import org.qiyu.live.living.provider.dao.mapper.IPkScoreRecordMapper;
import org.qiyu.live.living.provider.dao.po.LivingRoomPO;
import org.qiyu.live.living.provider.dao.po.LivingRoomRecordPO;
import org.qiyu.live.living.provider.dao.po.PkBattlePO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LivingRoomServiceImpl implements ILivingRoomService {

    @Resource
    private ILivingRoomMapper livingRoomMapper;
    @Resource
    private ILivingRoomRecordMapper livingRoomRecordMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;
    // PK 相关 Mapper（第六步新增）
    @Resource
    private IPkBattleMapper pkBattleMapper;
    @Resource
    private IPkScoreRecordMapper pkScoreRecordMapper;
    // IM 路由 RPC，用于 PK 公共事件广播
    @DubboReference(check = false)
    private ImRouterRpc imRouterRpc;
    @Autowired
    private MQProducer mqProducer;

    /** PK 匹配 Lua 脚本，加载一次后复用。 */
    private static final DefaultRedisScript<java.util.List> PK_MATCH_SCRIPT;
    static {
        DefaultRedisScript<java.util.List> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new org.springframework.core.io.ClassPathResource("scripts/pk_match.lua"));
        redisScript.setResultType(java.util.List.class);
        PK_MATCH_SCRIPT = redisScript;
    }
    /** PK 默认时长（秒），后续可改为配置项。 */
    private static final long PK_DURATION_SECONDS = 180;


    @Override
    public Integer startingLiving(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomPO livingRoomPO = ConvertBeanUtils.convert(livingRoomReqDTO, LivingRoomPO.class);
        livingRoomPO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
        livingRoomPO.setStartTime(LocalDateTime.now());
        livingRoomMapper.insert(livingRoomPO);
        String cacheKey = cacheKeyBuilder.buildLivingRoomObj(livingRoomPO.getId());
        //防止之前有控制缓存。这里做移除操作
        redisTemplate.delete(cacheKey);
        this.sendStartingLivingRoomMQ(livingRoomPO);
        return livingRoomPO.getId();
    }

    private void sendStartingLivingRoomMQ(LivingRoomPO livingRoomPO) {
        Message message = new Message();
        message.setBody(JSON.toJSONBytes(livingRoomPO));
        message.setTopic(LivingProviderTopicNames.START_LIVING_ROOM);
        try {
            mqProducer.send(message);
        } catch (Exception e) {
            log.error("starting livingRoom and send livingRoomMQ error", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomPO livingRoomPO = livingRoomMapper.selectById(livingRoomReqDTO.getRoomId());
        if (livingRoomPO == null){
            return false;
        }
        // 只有当前请求人是该直播间主播时，才允许关闭直播间。
        if (!Objects.equals(livingRoomPO.getAnchorId(), livingRoomReqDTO.getAnchorId())) {
            return false;
        }
        LivingRoomRecordPO livingRoomRecordPO = ConvertBeanUtils.convert(livingRoomReqDTO, LivingRoomRecordPO.class);
        livingRoomRecordPO.setEndTime(LocalDateTime.now());
        livingRoomRecordPO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
        livingRoomRecordMapper.insert(livingRoomRecordPO);
        livingRoomMapper.deleteById(livingRoomPO.getId());
        //移除掉直播间cache
        String cacheKey = cacheKeyBuilder.buildLivingRoomObj(livingRoomReqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }

    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        String cacheKey = cacheKeyBuilder.buildLivingRoomObj(roomId);
        LivingRoomRespDTO queryResult = (LivingRoomRespDTO) redisTemplate.opsForValue().get(cacheKey);
        if (queryResult != null){
            //空值缓存
            if(queryResult.getId() == null){
                return null;
            }
            return queryResult;
        }
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LivingRoomPO::getId, roomId);
        queryWrapper.eq(LivingRoomPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        queryResult = ConvertBeanUtils.convert(livingRoomMapper.selectOne(queryWrapper), LivingRoomRespDTO.class);
        if(queryResult == null){
            //防止缓存击穿
            redisTemplate.opsForValue().set(cacheKey, new LivingRoomRespDTO(),1, TimeUnit.MINUTES);
            return null;
        }
        redisTemplate.opsForValue().set(cacheKey, new LivingRoomRespDTO(),1, TimeUnit.MINUTES);
        return queryResult;
    }

    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        String cacheKey = cacheKeyBuilder.buildLivingRoomList(livingRoomReqDTO.getType());
        int page = livingRoomReqDTO.getPage();
        int pageSize = livingRoomReqDTO.getPageSize();
        long total = redisTemplate.opsForList().size(cacheKey);
        List<Object> resultList = redisTemplate.opsForList().range(cacheKey,(page - 1) * pageSize,(page * pageSize));
        PageWrapper<LivingRoomRespDTO> pageWrapper = new PageWrapper<>();
        if (CollectionUtils.isEmpty(resultList)){
            pageWrapper.setList(Collections.emptyList());
            pageWrapper.setHasNext(false);
            return pageWrapper;
        }else{
            List<LivingRoomRespDTO> livingRoomRespDTOS = ConvertBeanUtils.convertList(resultList, LivingRoomRespDTO.class);
            pageWrapper.setList(livingRoomRespDTOS);
            pageWrapper.setHasNext(page * pageSize < total);
            return pageWrapper;
        }
    }

    @Override
   public List<LivingRoomRespDTO> listAllLivingRoomFromDB(Integer type){
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LivingRoomPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.eq(LivingRoomPO::getType, type);
        //按时间倒序展示
        queryWrapper.orderByDesc(LivingRoomPO::getId);
        queryWrapper.last("limit 1000");
        return ConvertBeanUtils.convertList(livingRoomMapper.selectList(queryWrapper), LivingRoomRespDTO.class);
    }

    @Override
    public void userOnlineHandler(ImOnlineDTO imOnlineDTO) {
        Long userId = imOnlineDTO.getUserId();
        Integer roomId = imOnlineDTO.getRoomId();
        Integer appId = imOnlineDTO.getAppId();
        String cacheKey = cacheKeyBuilder.buildLivingRoomUserSet(roomId,appId);
        //set集合中
        redisTemplate.opsForSet().add(cacheKey,userId);
        redisTemplate.expire(cacheKey,12 ,TimeUnit.HOURS);

    }

    @Override
    public void userOfflineHandler(ImOfflineDTO imOfflineDTO) {
        Long userId = imOfflineDTO.getUserId();
        Integer roomId = imOfflineDTO.getRoomId();
        Integer appId = imOfflineDTO.getAppId();
        String cacheKey = cacheKeyBuilder.buildLivingRoomUserSet(roomId,appId);
        redisTemplate.opsForSet().remove(cacheKey,userId);
    }

    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        Integer roomId = livingRoomReqDTO.getRoomId();
        Integer appId = livingRoomReqDTO.getAppId();
        String cacheKey = cacheKeyBuilder.buildLivingRoomUserSet(roomId,appId);
        //0-100 100-200 200-300
        Cursor<Object> cursor= redisTemplate.opsForSet().scan(cacheKey, ScanOptions.scanOptions().match("*").count(100).build());
        List<Long> userIdList = new ArrayList<>();
        while(cursor.hasNext()){
            Long userId = (Long) cursor.next();
            userIdList.add(userId);
        }
        return userIdList;
    }

    /**
     * 申请 PK 匹配（进入等待池 / 匹配成功直接建局）。
     *
     * <p>每一步都原子化：</p>
     * <ol>
     *   <li>校验直播间存在且请求人是主播</li>
     *   <li>调用 Redis Lua 原子匹配</li>
     *   <li>匹配成功 → 生成 pkId → 写 MySQL t_pk_battle → 建 Redis PK
     状态
     *       → 建 roomId→pkId 映射 → 返回 true</li>
     *   <li>无对手 → Lua 已将自己加入等待池 → 返回 true</li>
     *   <li>已在等待池 → 返回 false（防止重复申请）</li>
     * </ol>
     *
     * @param livingRoomReqDTO 包含 roomId 和服务端注入的 anchorId
     * @return true 已进入等待池或匹配成功；false 校验失败或重复申请
     */
    @Override
    public boolean onlinePK(LivingRoomReqDTO livingRoomReqDTO) {
        Integer roomId = livingRoomReqDTO.getRoomId();
        Long anchorId = livingRoomReqDTO.getAnchorId();

        //1.校验直播间存在，且请求人是该房间的主播
        LivingRoomPO livingRoomPO = livingRoomMapper.selectById(roomId);
        if (livingRoomPO == null) {
            log.warn("[onlinePK] room not found, roomId={}", roomId);
            return false;
        }
        if (!Objects.equals(livingRoomPO.getAnchorId(), anchorId)) {
            log.warn("[onlinePK] anchor mismatch, roomId={}, dbAnchorId={}, reqAnchorId={}",
                    roomId, livingRoomPO.getAnchorId(), anchorId);
            return false;
        }
        // 2. 调用 Redis Lua 原子匹配
        String poolKey = cacheKeyBuilder.buildPkWaitingPool();
        String myWaitKey = cacheKeyBuilder.buildPkWaitingRoom(roomId);
        long applyTime = System.currentTimeMillis();
        // 等待状态 Key 前缀，Lua 需要用于删除对手的等待记录
        String waitKeyPrefix = cacheKeyBuilder.getPrefix() + "pk_waiting_room" + cacheKeyBuilder.getSplitItem();

        List<?> luaResult = redisTemplate.execute(PK_MATCH_SCRIPT, Arrays.asList(poolKey, myWaitKey)
                ,String.valueOf(roomId),String.valueOf(applyTime),String.valueOf(300),waitKeyPrefix);

        if (luaResult == null || luaResult.size() < 2) {
            log.error("[onlinePK] Lua script returned unexpected result , roomId is {} ", roomId);
            return false;
        }

        String status = String.valueOf(luaResult.get(0));
        String opponentRoomId = String.valueOf(luaResult.get(1));
        // 3. 已在等待池子中
        if ("DUPLICATE".equals(status)) {
            log.warn("[onlinePK] room already in PK waiting pool, roomId={}", roomId);
            return false;
        }
        // 4. 无对手，已由 Lua 加入等待池
        if ("WAITING".equals(status)) {
            log.info("[onlinePK] no opponent, room entered PK waiting pool, roomId={}, anchorId={}", roomId, anchorId);
            return true;
        }
        // 5. 匹配成功 → 建局
        if("MATCHED".equals(status)){
            Integer oppRoomId = Integer.valueOf(opponentRoomId);
            // a. 查出对手房间的主播id
            LivingRoomPO oppRoom = livingRoomMapper.selectById(oppRoomId);
            if (oppRoom == null) {
                log.error("[onlinePK] opponent room disappeared after match, oppRoomId={}", oppRoomId);
                // 对手房间已不存在，把自己从等待池移除，返回失败
                redisTemplate.opsForZSet().remove(poolKey,roomId);
                redisTemplate.delete(myWaitKey);
                return false;
            }
            Long oppAnchorId = oppRoom.getAnchorId();

            // b.生成 pkId
            Long pkId = redisTemplate.opsForValue().increment(cacheKeyBuilder.buildPkIdGenerator());

            // c.创建MySQL pk 对局记录
            PkBattlePO battlePO = new PkBattlePO();
            battlePO.setPkId(pkId);
            // 先进入等待池子的为A方
            battlePO.setRoomIdA(oppRoomId);
            battlePO.setRoomIdB(roomId);
            battlePO.setAnchorIdA(oppAnchorId);
            battlePO.setAnchorIdB(anchorId);
            battlePO.setScoreA(0L);
            battlePO.setScoreB(0L);
            battlePO.setStatus("RUNNING");
            battlePO.setVersion(1);
            LocalDateTime now = LocalDateTime.now();
            battlePO.setStartTime(now);
            battlePO.setEndTime(now.plusSeconds(PK_DURATION_SECONDS));
            pkBattleMapper.insert(battlePO);

            // d. 创建 Redis PK 状态 Hash
            String pkStateKey = cacheKeyBuilder.buildPkState(pkId);
            Map<String, Object> pkState = new HashMap<>();
            pkState.put("pkId", pkId.toString());
            pkState.put("roomIdA", oppRoomId.toString());
            pkState.put("roomIdB", roomId.toString());
            pkState.put("anchorIdA", oppAnchorId.toString());
            pkState.put("anchorIdB", anchorId.toString());
            pkState.put("scoreA", "0");
            pkState.put("scoreB", "0");
            pkState.put("version", "1");
            pkState.put("status", "RUNNING");
            pkState.put("startTime", String.valueOf(now.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()));
            pkState.put("endTime", String.valueOf(now.plusSeconds(PK_DURATION_SECONDS).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()));
            redisTemplate.opsForHash().putAll(pkStateKey, pkState);
            // PK 状态 Hash 的 TTL 设为 PK 时长 + 1小时缓冲（结算后保留一段时间）
            redisTemplate.expire(pkStateKey, PK_DURATION_SECONDS + 3600, TimeUnit.SECONDS);

            // 5e. 建立 roomId → pkId 双向映射
            String roomToPkA = cacheKeyBuilder.buildRoomToPk(oppRoomId);
            String roomToPkB = cacheKeyBuilder.buildRoomToPk(roomId);
            redisTemplate.opsForValue().set(roomToPkA, pkId.toString(),
                    PK_DURATION_SECONDS + 3600, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(roomToPkB, pkId.toString(),
                    PK_DURATION_SECONDS + 3600, TimeUnit.SECONDS);

            // 5f. 广播 PK_START 给两个直播间所有在线用户
            JSONObject pkStartData = new JSONObject();
            pkStartData.put("pkId", pkId.toString());
            pkStartData.put("roomIdA", oppRoomId.toString());
            pkStartData.put("roomIdB", roomId.toString());
            pkStartData.put("anchorIdA", oppAnchorId.toString());
            pkStartData.put("anchorIdB", anchorId.toString());
            pkStartData.put("scoreA", "0");
            pkStartData.put("scoreB", "0");
            pkStartData.put("version", "1");
            pkStartData.put("status", "RUNNING");
            pkStartData.put("startTime", String.valueOf(now.atZone(java.time.ZoneId.systemDefault())
                            .toInstant().toEpochMilli()));
            pkStartData.put("endTime", String.valueOf(now.plusSeconds(PK_DURATION_SECONDS)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toInstant().toEpochMilli()));

            // 分别查两个房间的在线用户，合并去重后统一广播
            List<Long> userIdsA = queryUserIdByRoomIdHelper(oppRoomId, AppIdEnum.QIYU_LIVE_BIZ.getCode());
            List<Long> userIdsB = queryUserIdByRoomIdHelper(roomId, AppIdEnum.QIYU_LIVE_BIZ.getCode());
            Set<Long> allUserIds = new LinkedHashSet<>();
            if (userIdsA != null) allUserIds.addAll(userIdsA);
            if (userIdsB != null) allUserIds.addAll(userIdsB);

            List<ImMsgBody> msgList = new ArrayList<>();
            for (Long targetUserId : allUserIds) {
                ImMsgBody msg = new ImMsgBody();
                msg.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                msg.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_PK_START.getCode());
                msg.setUserId(targetUserId);
                msg.setMsgId("pk-start:" + pkId + ":" + targetUserId);
                msg.setData(pkStartData.toJSONString());
                msgList.add(msg);
            }
            if (!msgList.isEmpty()) {
                imRouterRpc.batchSendMsg(msgList);
                log.info("[onlinePK] PK_START broadcast, pkId={}, roomA={}, roomB={}, userCount={}", pkId, oppRoomId, roomId, msgList.size());
            }
        }

        log.error("[onlinePK] unknown Lua status={}, roomId={}", status, roomId);
        return false;
    }

    /**
     * 退出 PK 匹配或取消等待。
     *
     * <p>根据当前房间所处阶段，分两种情况处理：</p>
     * <ul>
     *   <li><b>仍在等待池中</b> → 从 ZSet 移除 roomId，删除等待状态 Key，
     *       返回 true（取消等待成功）</li>
     *   <li><b>已在 PK 进行中</b> → 不能简单取消，应走 PK_CANCEL
     或提前结算流程，
     *       当前版本返回 false 并记录日志（后续步骤补充结算逻辑）</li>
     * </ul>
     *
     * @param livingRoomReqDTO 包含 roomId 和服务端注入的 anchorId
     * @return true 取消等待成功；false 校验失败或已在 PK 中无法取消
     */
    @Override
    public boolean offlinePK(LivingRoomReqDTO livingRoomReqDTO) {
        Integer roomId = livingRoomReqDTO.getRoomId();
        Long anchorId = livingRoomReqDTO.getAnchorId();
        //1.校验直播间存在且请求人是该房间主播
        LivingRoomPO livingRoomPO = livingRoomMapper.selectById(roomId);
        if (livingRoomPO == null) {
            log.warn("[offlinePK] room not found, roomId={}", roomId);
            return false;
        }
        if (!Objects.equals(livingRoomPO.getAnchorId(), anchorId)) {
            log.warn("[offlinePK] anchor mismatch, roomId={}, dbAnchorId={}, reqAnchorId={}", roomId, livingRoomPO.getAnchorId(), anchorId);
            return false;
        }
        // 2. 检查是否在等待池中 → 取消等待
        String waitingRoomKey = cacheKeyBuilder.buildPkWaitingRoom(roomId);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(waitingRoomKey))) {
            // 从 ZSet 中移除
            String waitingPoolKey = cacheKeyBuilder.buildPkWaitingPool();
            redisTemplate.opsForZSet().remove(waitingPoolKey, roomId);
            // 删除等待状态 Key
            redisTemplate.delete(waitingRoomKey);
            log.info("[offlinePK] room exited PK waiting pool, roomId={}, anchorId={}", roomId, anchorId);
            return true;
        }
        // 3. 检查是否已在 PK 进行中 → 不能简单取消，需走提前结算
        String roomToPkKey = cacheKeyBuilder.buildRoomToPk(roomId);
        String pkIdStr = (String)
                redisTemplate.opsForValue().get(roomToPkKey);
        if (pkIdStr != null && !pkIdStr.isBlank()) {
            log.warn("[offlinePK] room is in active PK, must use settlement flow, roomId={}, pkId={}", roomId, pkIdStr);
            // TODO: 后续补充提前结算逻辑（将该 PK 置为CANCELLED，调用结算流程）
            return false;
        }

        // 4. 既不在等待池也不在 PK 中，幂等返回 true
        log.info("[offlinePK] room is not in waiting pool or PK, nothing to cancel, roomId={}", roomId);
        return true;
    }
    /**
     * 根据 roomId 查询当前 PK 对手的 userId。
     *
     * <p>查询逻辑：</p>
     * <ol>
     *   <li>如果房间在等待池中 → 还没有对手，返回 null</li>
     *   <li>如果房间存在 roomId → pkId 映射 → 查 PK 状态 Hash，
     *       返回对方主播的 userId（后续步骤实现）</li>
     *   <li>都不存在 → 当前未参与任何 PK，返回 null</li>
     * </ol>
     *
     * <p>注意：此方法为过渡方案。后续步骤将新增 {@code
    queryCurrentPkState(Integer roomId)}
     * 返回完整 {@code PkStateDTO}，包含 pkId、比分、version、status
     等全部信息。</p>
     *
     * @param roomId 当前直播间 ID
     * @return 对手主播的 userId，若不在 PK 中则返回 null
     */
    @Override
    public Long queryOnlinePkUserId(Integer roomId) {
        // 1. 检查是否在等待池中 → 还没对手
        String waitingRoomKey = cacheKeyBuilder.buildPkWaitingRoom(roomId);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(waitingRoomKey))) {
            log.info("[queryOnlinePkUserId] room is in waiting pool, no opponent yet, roomId={}", roomId);
            return null;
        }
        // 2. 查询 roomId -> pkId 映射，查 PK 状态 Hash 返回对手 userId
        String roomToPkKey = cacheKeyBuilder.buildRoomToPk(roomId);
        String pkIdStr = (String)
                redisTemplate.opsForValue().get(roomToPkKey);
        if (pkIdStr != null && !pkIdStr.isBlank()) {
            Long pkId = Long.valueOf(pkIdStr);
            String pkStateKey = cacheKeyBuilder.buildPkState(pkId);
            Object roomIdAObj = redisTemplate.opsForHash().get(pkStateKey, "roomIdA");
            Object roomIdBObj = redisTemplate.opsForHash().get(pkStateKey, "roomIdB");
            Object anchorIdAObj = redisTemplate.opsForHash().get(pkStateKey, "anchorIdA");
            Object anchorIdBObj = redisTemplate.opsForHash().get(pkStateKey, "anchorIdB");

            if (roomIdAObj == null || roomIdBObj == null) {
                log.warn("[queryOnlinePkUserId] PK state incomplete, pkId={}", pkId);
                return null;
            }

            // 判断本方是 A 还是 B，返回对方 anchorId
            String roomIdAStr = String.valueOf(roomIdAObj);
            if (roomIdAStr.equals(roomId.toString())) {
                // 本方是 A，对手是 B
                return anchorIdBObj != null ?
                        Long.valueOf(String.valueOf(anchorIdBObj)) : null;
            } else {
                // 本方是 B，对手是 A
                return anchorIdAObj != null ?
                        Long.valueOf(String.valueOf(anchorIdAObj)) : null;
            }
        }

        // 3. 不在等待池也不在 PK 中
        log.info("[queryOnlinePkUserId] room is not in any PK, roomId={}",
                roomId);
        return null;
    }

    /**
     * 根据 roomId 查询当前 PK 完整状态快照。
     *
     * <p>查询路径：roomId → roomToPk 映射 → pkId → PK 状态 Hash。
     * 将 Redis Hash 各字段组装为 {@link PkStateDTO} 返回。</p>
     */
    @Override
    public PkStateDTO queryCurrentPkState(Integer roomId) {
        // 1. roomId → pkId
        String roomToPkKey = cacheKeyBuilder.buildRoomToPk(roomId);
        String pkIdStr = (String) redisTemplate.opsForValue().get(roomToPkKey);
        if (pkIdStr == null || pkIdStr.isBlank()) {
            return null; // 不在 PK 中
        }

        // 2. pkId → PK 状态 Hash
        Long pkId = Long.valueOf(pkIdStr);
        String pkStateKey = cacheKeyBuilder.buildPkState(pkId);
        Map<Object, Object> stateMap = redisTemplate.opsForHash().entries(pkStateKey);
        if (stateMap == null || stateMap.isEmpty()) {
            log.warn("[queryCurrentPkState] PK state Hash empty, pkId={}", pkId);
            return null;
        }

        // 3. 组装 DTO
        PkStateDTO dto = new PkStateDTO();
        dto.setPkId(pkId);
        dto.setRoomIdA(toInt(stateMap.get("roomIdA")));
        dto.setRoomIdB(toInt(stateMap.get("roomIdB")));
        dto.setAnchorIdA(toLong(stateMap.get("anchorIdA")));
        dto.setAnchorIdB(toLong(stateMap.get("anchorIdB")));
        dto.setScoreA(toLong(stateMap.get("scoreA")));
        dto.setScoreB(toLong(stateMap.get("scoreB")));
        dto.setVersion(toLong(stateMap.get("version")));
        dto.setStatus(String.valueOf(stateMap.get("status")));
        dto.setStartTime(toLong(stateMap.get("startTime")));
        dto.setEndTime(toLong(stateMap.get("endTime")));
        dto.setWinnerSide((String) stateMap.get("winnerSide"));
        dto.setWinnerAnchorId(toLong(stateMap.get("winnerAnchorId")));
        dto.setFinishTime(toLong(stateMap.get("finishTime")));
        return dto;
    }

    @Override
    public LivingRoomRespDTO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LivingRoomPO::getAnchorId, anchorId);
        queryWrapper.eq(LivingRoomPO::getStatus,CommonStatusEnum.VALID_STATUS);
        queryWrapper.last("limit 1");
        return ConvertBeanUtils.convert(livingRoomMapper.selectOne(queryWrapper),LivingRoomRespDTO.class);
    }

    // ===== 辅助类型转换方法 =====

    private Integer toInt(Object obj) {
        return obj == null ? null : Integer.valueOf(String.valueOf(obj));
    }

    private Long toLong(Object obj) {
        return obj == null ? null : Long.valueOf(String.valueOf(obj));
    }

    /**
     * 查询指定房间在线用户列表（轻量辅助方法，不依赖 LivingRoomReqDTO）。
     *
     * @param roomId 直播间 ID
     * @param appId  应用 ID
     * @return 在线用户 userId 列表，不会返回 null
     */
    private List<Long> queryUserIdByRoomIdHelper(Integer roomId, Integer appId) {
        String cacheKey = cacheKeyBuilder.buildLivingRoomUserSet(roomId, appId);
        Cursor<Object> cursor = redisTemplate.opsForSet().scan(cacheKey,
                        ScanOptions.scanOptions().match("*").count(100).build());
        List<Long> userIdList = new ArrayList<>();
        while (cursor.hasNext()) {
            userIdList.add((Long) cursor.next());
        }
        return userIdList;
    }
}
