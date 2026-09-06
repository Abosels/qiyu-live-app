package org.qiyu.live.im.core.server.service.impl;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.idea.qiyu.live.framework.redis.starter.key.IMCoreServerProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.service.IMsgAckCheckService;
import org.qiyu.live.im.dto.ImMsgBody;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MsgAckCheckServiceImpl implements IMsgAckCheckService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private IMCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private MQProducer mqProducer;
    @Override
    public void doMsgAck(ImMsgBody imMsgBody) {
        redisTemplate.opsForHash().delete(cacheKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(),imMsgBody.getAppId()),
                imMsgBody.getMsgId());
    }

    @Override
    public void recordMsgAck(ImMsgBody imMsgBody, int times) {
        redisTemplate.opsForHash().put(cacheKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(),imMsgBody.getAppId()),
                imMsgBody.getMsgId(),times);
    }

    @Override
    public void sendDelayMsg(ImMsgBody imMsgBody) {
        String json = JSON.toJSONString(imMsgBody);
        Message message = new Message();
        message.setBody(json.getBytes());
        message.setTopic(ImCoreServerProviderTopicNames.QIYU_LIVE_IM_ACK_MSG_TOPIC);
        //等级为1 -》 1s， 等级为2 -》 5秒
        message.setDelayTimeLevel(2);
        try {
            SendResult sendResult = mqProducer.send(message);
            log.info("[MsgAckCheckServiceImpl.java] msg is {},sendResult is {}", json , sendResult);
        } catch (Exception e) {
            log.error("[MsgAckCheckServiceImpl.java] send message error,erro is {}", e);
        }

    }

    @Override
    public int getMsgAckTimes(String msgId, long userId, int appId) {
        Object value = redisTemplate.opsForHash().get(cacheKeyBuilder.buildImAckMapKey(userId,appId),msgId );
        if (value == null) {
            return -1;
        }
        return (int) value;
    }
}
