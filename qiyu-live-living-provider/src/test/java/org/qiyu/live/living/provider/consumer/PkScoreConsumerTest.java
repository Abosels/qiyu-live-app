package org.qiyu.live.living.provider.consumer;

import com.alibaba.fastjson2.JSON;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.qiyu.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.junit.jupiter.api.Test;
import org.qiyu.live.common.interfaces.dto.PkScoreMQ;
import org.qiyu.live.living.provider.dao.mapper.IPkScoreRecordMapper;
import org.qiyu.live.living.provider.dao.po.PkScoreRecordPO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PkScoreConsumerTest {

    @Test
    void duplicateMessageAlreadySyncedMustNotScoreRedisAgain() {
        IPkScoreRecordMapper mapper = mock(IPkScoreRecordMapper.class);
        @SuppressWarnings("unchecked")
        RedisTemplate<Object, Object> redisTemplate = mock(RedisTemplate.class);
        PkScoreRecordPO existing = new PkScoreRecordPO();
        existing.setId(1L);
        existing.setRedisSyncStatus(1);

        when(mapper.insert(any(PkScoreRecordPO.class))).thenThrow(new DuplicateKeyException("uk_pk_biz"));
        when(mapper.selectOne(any())).thenReturn(existing);

        PkScoreConsumer consumer = new PkScoreConsumer();
        ReflectionTestUtils.setField(consumer, "pkScoreRecordMapper", mapper);
        ReflectionTestUtils.setField(consumer, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(consumer, "cacheKeyBuilder", new LivingProviderCacheKeyBuilder());

        assertEquals(ConsumeConcurrentlyStatus.CONSUME_SUCCESS, consumer.consume(message("gift-uuid-1")));
        verifyNoInteractions(redisTemplate);
    }

    private MessageExt message(String bizId) {
        PkScoreMQ body = new PkScoreMQ();
        body.setPkId(10L);
        body.setBizId(bizId);
        body.setRoomId(1001);
        body.setSenderId(2001L);
        body.setSide("A");
        body.setAddScore(100L);
        MessageExt message = new MessageExt();
        message.setBody(JSON.toJSONBytes(body));
        return message;
    }
}
