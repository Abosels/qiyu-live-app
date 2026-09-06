package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.contants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.core.server.interfaces.contants.ImCoreServerConstants;
import org.qiyu.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.qiyu.live.im.dto.ImMsgBody;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;


/**
 * 登陆消息的处理统一归到这个类中
 */
@Component
@Slf4j
public class LogoutMsgHandlerImpl implements SimpleHandler {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MQProducer mqProducer;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) throws Exception {

        // 从服务端保存的 Channel 属性中获取用户信息
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        Integer roomId = ImContextUtils.getRoomId(ctx);
        if (userId == null || appId == null) {
            log.error("[LogoutMsgHandlerImpl] channel attr error, userId={}, appId={}, imMsg={}", userId, appId, imMsg);
            //有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        //将im消息写回客户端
        logoutHandler(ctx, userId, appId,roomId);
    }

    /**
     * 执行用户登出
     */
    private void logoutHandler(ChannelHandlerContext ctx, Long userId, Integer appId,Integer roomId) {
        ImMsgBody respBody = new ImMsgBody();
        respBody.setUserId(userId);
        respBody.setAppId(appId);
        respBody.setRoomId(roomId);
        respBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_MSG_LOGOUT.getCode(), JSON.toJSONString(respBody));
        /*
         * [修复] 删除重复的 writeAndFlush。
         * 原代码先 writeAndFlush(respMsg) 再 writeAndFlush(respMsg).addListener(CLOSE)，
         * 导致登出响应被发送两次。现仅保留带 CLOSE 监听器的一次调用，
         * 确保响应发送完成后再关闭连接。
         */
        log.info("[LogoutMsgHandler] logout success,userId is {},appId is {}" , userId, appId);
        /*
         * 发送完成后再关闭连接。
         * 不能 writeAndFlush 后立刻 ctx.close()，
         * 否则登出响应可能还没发出去连接就被关闭了。
         */
        ctx.writeAndFlush(respMsg).addListener(ChannelFutureListener.CLOSE);
        // 2. 删除本机连接缓存,理想情况下，客户端断线的时候，会发送一个断线消息包
        ChannelHandlerContextCache.remove(userId);
        //3. 删除 Redis 中用户绑定的 IM 节点
        stringRedisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY +appId + ":" + userId);
        // 4. 发送用户离线 MQ
        sendLogoutMQ(ctx,userId, appId);
        // 5. 清理当前 Channel 保存的属性
        ImContextUtils.removeUserId(ctx);
        ImContextUtils.removeAppId(ctx);
        if (roomId != null) {
            ImContextUtils.removeRoomId(ctx);
        }
        log.info("[LogoutMsgHandlerImpl] logout success, userId={}, appId={}, roomId={}", userId, appId, roomId);
    }
    private void sendLogoutMQ(ChannelHandlerContext ctx,Long userId, Integer appId) {
        Integer roomId = ImContextUtils.getRoomId(ctx);
        ImOfflineDTO imOFFlineDTO = new ImOfflineDTO();
        imOFFlineDTO.setUserId(userId);
        imOFFlineDTO.setAppId(appId);
        imOFFlineDTO.setRoomId(roomId);
        imOFFlineDTO.setLogoutTime(System.currentTimeMillis());
        Message message = new Message();
        message.setTopic(ImCoreServerProviderTopicNames.IM_OFFLINE_TOPIC);
        // [修复] 显式指定 UTF-8 字符集，避免跨环境乱码
        message.setBody(JSON.toJSONString(imOFFlineDTO).getBytes(StandardCharsets.UTF_8));
        try {
            SendResult sendResult = mqProducer.send(message);
            log.info("[sendLogoutMQ] logout success,sendResult is {}" , sendResult);
        } catch (Exception e) {
            /*
             * [修复] 登出流程已执行完成（Channel 缓存已清理、Redis 已删除）。
             * MQ 发送失败只记录日志，不重新抛出异常。
             * 否则会中断后续的 Channel 属性清理逻辑。
             */
            log.error("[sendLogoutMQ] logout MQ send error" , e);
        }
    }
}
