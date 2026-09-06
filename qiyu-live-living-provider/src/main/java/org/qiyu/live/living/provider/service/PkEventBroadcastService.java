package org.qiyu.live.living.provider.service;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.qiyu.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.qiyu.live.im.contants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.interfaces.contants.ImMsgBizCodeEnum;
import org.qiyu.live.im.router.interfaces.rpc.ImRouterRpc;
import org.qiyu.live.living.interfaces.dto.PkStateDTO;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Publishes the same PK snapshot to both participating live rooms. */
@Component
public class PkEventBroadcastService {

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;
    @DubboReference(check = false)
    private ImRouterRpc imRouterRpc;

    /** Broadcasts one immutable PK state snapshot. */
    public void broadcastState(PkStateDTO state, ImMsgBizCodeEnum bizCode) {
        if (state == null || state.getPkId() == null || state.getRoomIdA() == null || state.getRoomIdB() == null) {
            return;
        }
        Set<Long> users = new LinkedHashSet<>();
        users.addAll(queryRoomUsers(state.getRoomIdA()));
        users.addAll(queryRoomUsers(state.getRoomIdB()));
        if (users.isEmpty()) {
            return;
        }

        List<ImMsgBody> messages = new ArrayList<>(users.size());
        String data = JSON.toJSONString(state);
        for (Long userId : users) {
            ImMsgBody message = new ImMsgBody();
            message.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
            message.setBizCode(bizCode.getCode());
            message.setUserId(userId);
            message.setMsgId("pk:" + bizCode.getCode() + ":" + state.getPkId() + ":" + state.getVersion() + ":" + userId);
            message.setData(data);
            messages.add(message);
        }
        imRouterRpc.batchSendMsg(messages);
    }

    private List<Long> queryRoomUsers(Integer roomId) {
        String key = cacheKeyBuilder.buildLivingRoomUserSet(roomId, AppIdEnum.QIYU_LIVE_BIZ.getCode());
        List<Long> users = new ArrayList<>();
        try (Cursor<Object> cursor = redisTemplate.opsForSet().scan(key,
                ScanOptions.scanOptions().count(100).build())) {
            while (cursor.hasNext()) {
                users.add(Long.valueOf(String.valueOf(cursor.next())));
            }
        }
        return users;
    }
}
