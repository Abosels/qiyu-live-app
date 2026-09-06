package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.micrometer.common.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.contants.AppIdEnum;
import org.qiyu.live.im.contants.ImContants;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.contants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.core.server.interfaces.contants.ImCoreServerConstants;
import org.qiyu.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LoginMsgHandlerImpl implements SimpleHandler {

    @DubboReference
    private ImTokenRpc imTokenRpc;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MQProducer mqProducer;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) throws Exception {
        byte[] body = imMsg.getBody();
        if (body == null || body.length == 0) {
            ctx.close();
            log.error("boday error,imMsg is {}", imMsg);
            throw new IllegalArgumentException("body error");
        }
        // [修复] 显式指定 UTF-8 字符集，避免跨环境乱码
        ImMsgBody imMsgBody = JSON.parseObject(new String(body, StandardCharsets.UTF_8), ImMsgBody.class);
        Long userIdFromMsg = imMsgBody.getUserId();
        Integer appId = imMsgBody.getAppId();
        String token = imMsgBody.getToken();
        Integer roomId = imMsgBody.getRoomId();
        if (StringUtils.isEmpty(token) || userIdFromMsg < 10000 || appId < 10000 || (roomId != null && roomId <= 0)) {
            log.error(
                    "[LoginMsgHandlerImpl] param error, userId={}, appId={}, roomId={}",
                    userIdFromMsg,
                    appId,
                    roomId
            );
            ctx.close();
            throw new IllegalArgumentException("param error");
        }
        // 4. 使用 token 获取服务端记录的真实 userId
        Long userId;
        try {
            userId = imTokenRpc.getUserIdByToken(token);
        } catch (Exception e) {
            log.error("[LoginMsgHandlerImpl] token rpc error", e);
            ctx.close();
            return;
        }
        //如果userId不为空，token校验成功，而且和传递过来的userId是同一个，则允许建立连接
        if (userId != null && userId.equals(userIdFromMsg)) {
            LoginSuccessHandler(ctx,userId,appId,roomId);
            return;
        }
        // token 无效或者 token 对应的用户和客户端传入用户不一致
        log.error(
                "[LoginMsgHandlerImpl] token check error, tokenUserId={}, msgUserId={}",
                userId,
                userIdFromMsg
        );
        ctx.close();
        throw new IllegalArgumentException("token check error");
    }
    /**
     * 如果用户登陆成功则处理相关记录
     */
    public void LoginSuccessHandler(ChannelHandlerContext ctx, Long userId, Integer appId, Integer roomId) {
        //按照userId保存好相关的channel对象信息
        // userId -> ChannelHandlerContext
        ChannelHandlerContextCache.put(userId,ctx);
        ImContextUtils.setUserId(ctx,userId);
        ImContextUtils.setAppId(ctx,appId);
        if (roomId != null) {
            ImContextUtils.setRoomId(ctx,roomId);
        }
        //将im消息回写给客户端
        ImMsgBody respBody= new ImMsgBody();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setRoomId(roomId);
        respBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_MSG_LOGIN.getCode(), JSON.toJSONString(respBody));
        stringRedisTemplate.opsForValue().set(ImCoreServerConstants.IM_BIND_IP_KEY +appId + ":" + userId,
                ChannelHandlerContextCache.getServerIpAddress() + "%" + userId,
                ImContants.DEFAULT_HEART_BEAT_GAP * 2, TimeUnit.SECONDS);
        log.info("[LoginMsgHandlerImpl] login success, userId is {},appId is {}, roomId={}", userId,appId,roomId    );
        ctx.writeAndFlush(respMsg);
        sendLoginMQ(userId,appId,roomId);
    }

    /**
     * 用户上线的时候发送mq消息
     * @param userId
     * @param appId
     */
    private void sendLoginMQ(Long userId, Integer appId,Integer roomId) {
        ImOnlineDTO imOnlineDTO = new ImOnlineDTO();
        imOnlineDTO.setUserId(userId);
        imOnlineDTO.setAppId(appId);
        //只有是在直播间登陆的时候，才会携带roomId参数
        if (roomId != null) {
            imOnlineDTO.setRoomId(roomId);
        }
        imOnlineDTO.setLoginTime(System.currentTimeMillis());
        Message message = new Message();
        message.setTopic(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC);
        message.setBody(JSON.toJSONString(imOnlineDTO).getBytes(StandardCharsets.UTF_8));
        try {
            SendResult sendResult = mqProducer.send(message);
            log.info("[sendLoginMQ] send login MQ success, sendResult is{}]",sendResult);
        } catch (Exception e) {
            /*
             * [修复] 用户已经登录成功，MQ 发送失败只记录日志，不重新抛出异常。
             * 否则会导致已成功建立的连接被误关闭。
             */
            log.error(
                    "[LoginMsgHandlerImpl] send login MQ error, userId={}, appId={}, roomId={}", userId, appId, roomId, e
            );
        }

    }
}
