package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.idea.qiyu.live.framework.redis.starter.key.IMCoreServerProviderCacheKeyBuilder;
import org.qiyu.live.im.contants.ImContants;
import org.qiyu.live.im.contants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ImContextAttr;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.core.server.interfaces.contants.ImCoreServerConstants;
import org.qiyu.live.im.dto.ImMsgBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 心跳消息处理器，检测用户是否在线
 *
 */
@Component
@Slf4j
public class HeartBeatMsgHandlerImpl implements SimpleHandler {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private IMCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) throws Exception {
        //心跳包基本校验
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            log.info("attr error, immsg is {}", imMsg);
            //有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        //心跳包record记录，redis存储心跳记录
        String redisKey = cacheKeyBuilder.buildImLoginTokenKey(userId,appId);
        //qiyu-live-im-core-server:heartbeat:999:zset -> 把不同的用户ID分到不同的表
        this.recordOnlineTime(userId, redisKey);
        this.removeExpireRecord(userId, redisKey);
        redisTemplate.expire(redisKey, 5 , TimeUnit.MINUTES);
        //延长用户之前保存的ip绑定记录时间
        stringRedisTemplate.expire(
                ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId,
                ImContants.DEFAULT_HEART_BEAT_GAP * 2,
                TimeUnit.MINUTES
        );
        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setUserId(userId);
        imMsgBody.setAppId(appId);
        imMsgBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_MSG_HEARTBEAT.getCode(), JSON.toJSONString(imMsgBody));
        log.debug("[HeartBeatMsgHandlerImpl] imMsg is {}",imMsg);
        ctx.writeAndFlush(respMsg);
    }

    /**
     * 记录用户最近一次心跳时间到zSet上
     *
     * @param userId
     * @param redisKey
     */
    private void recordOnlineTime(Long userId, String redisKey) {
        redisTemplate.opsForZSet().add(redisKey, userId ,System.currentTimeMillis());
    }

    /**
     * 清理掉过期不在线的用户留下的心跳记录(在两次心跳的发送间隔中，如果没有重新更新score值，就会导致被删除)
     *
     * @param userId
     * @param redisKey
     */
    private void removeExpireRecord(Long userId, String redisKey) {
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, System.currentTimeMillis() - ImContants.DEFAULT_HEART_BEAT_GAP * 1000 * 2);
    }
}
