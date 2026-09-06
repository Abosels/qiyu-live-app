package org.qiyu.live.bank.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.bank.provider.dao.mapper.IPayNotifyOutboxMapper;
import org.qiyu.live.bank.provider.dao.po.PayNotifyOutboxPO;
import org.qiyu.live.bank.provider.service.IPayNotifyOutboxService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采用数据库状态抢占的方式投递支付事件，多实例下同一记录只会有一个实例获得发送权。
 */
@Slf4j
@Service
public class PayNotifyOutboxServiceImpl implements IPayNotifyOutboxService {

    private static final int PENDING = 0;
    private static final int SENDING = 1;
    private static final int SENT = 2;
    private static final int BATCH_SIZE = 100;

    @Resource
    private IPayNotifyOutboxMapper payNotifyOutboxMapper;
    @Resource
    private MQProducer mqProducer;

    /** 在外层支付事务中保存待发送事件。 */
    @Override
    public void createPending(String topic, String messageBody, String bizKey) {
        PayNotifyOutboxPO outboxPO = new PayNotifyOutboxPO();
        outboxPO.setBizKey(bizKey);
        outboxPO.setTopic(topic);
        outboxPO.setMessageBody(messageBody);
        outboxPO.setStatus(PENDING);
        outboxPO.setRetryCount(0);
        outboxPO.setNextRetryTime(LocalDateTime.now());
        payNotifyOutboxMapper.insert(outboxPO);
    }

    /** 每次最多发送固定数量，避免消息积压时长期占用调度线程。 */
    @Override
    @Scheduled(fixedDelayString = "${qiyu.pay.outbox.dispatch-interval-ms:5000}")
    public void dispatchPendingEvents() {
        recoverTimedOutClaims();
        List<PayNotifyOutboxPO> pendingEvents = payNotifyOutboxMapper.selectList(
                new LambdaQueryWrapper<PayNotifyOutboxPO>()
                        .eq(PayNotifyOutboxPO::getStatus, PENDING)
                        .le(PayNotifyOutboxPO::getNextRetryTime, LocalDateTime.now())
                        .orderByAsc(PayNotifyOutboxPO::getId)
                        .last("limit " + BATCH_SIZE));
        for (PayNotifyOutboxPO event : pendingEvents) {
            if (!tryClaim(event.getId())) {
                continue;
            }
            sendClaimedEvent(event);
        }
    }

    /** 进程在发送中崩溃时，五分钟后重新开放该事件供其他调度周期投递。 */
    private void recoverTimedOutClaims() {
        payNotifyOutboxMapper.update(null, new LambdaUpdateWrapper<PayNotifyOutboxPO>()
                .eq(PayNotifyOutboxPO::getStatus, SENDING)
                .lt(PayNotifyOutboxPO::getUpdateTime, LocalDateTime.now().minusMinutes(5))
                .set(PayNotifyOutboxPO::getStatus, PENDING)
                .set(PayNotifyOutboxPO::getNextRetryTime, LocalDateTime.now()));
    }

    /** 通过条件更新抢占记录，防止多个 provider 重复发送同一条消息。 */
    private boolean tryClaim(Long id) {
        return payNotifyOutboxMapper.update(null, new LambdaUpdateWrapper<PayNotifyOutboxPO>()
                .eq(PayNotifyOutboxPO::getId, id)
                .eq(PayNotifyOutboxPO::getStatus, PENDING)
                .set(PayNotifyOutboxPO::getStatus, SENDING)) == 1;
    }

    /** 发送成功后结束事件；异常则恢复为待发送并采用分钟级退避重试。 */
    private void sendClaimedEvent(PayNotifyOutboxPO event) {
        try {
            Message message = new Message(event.getTopic(), event.getMessageBody().getBytes(StandardCharsets.UTF_8));
            message.setKeys(event.getBizKey());
            if (mqProducer.send(message).getSendStatus() != SendStatus.SEND_OK) {
                throw new IllegalStateException("支付通知消息未成功写入 RocketMQ");
            }
            payNotifyOutboxMapper.update(null, new LambdaUpdateWrapper<PayNotifyOutboxPO>()
                    .eq(PayNotifyOutboxPO::getId, event.getId())
                    .eq(PayNotifyOutboxPO::getStatus, SENDING)
                    .set(PayNotifyOutboxPO::getStatus, SENT));
        } catch (Exception e) {
            int nextRetryCount = event.getRetryCount() + 1;
            payNotifyOutboxMapper.update(null, new LambdaUpdateWrapper<PayNotifyOutboxPO>()
                    .eq(PayNotifyOutboxPO::getId, event.getId())
                    .eq(PayNotifyOutboxPO::getStatus, SENDING)
                    .set(PayNotifyOutboxPO::getStatus, PENDING)
                    .set(PayNotifyOutboxPO::getRetryCount, nextRetryCount)
                    .set(PayNotifyOutboxPO::getNextRetryTime,
                            LocalDateTime.now().plusMinutes(Math.min(nextRetryCount, 30))));
            log.error("[payOutbox] send event failed, id={}, bizKey={}", event.getId(), event.getBizKey(), e);
        }
    }
}
