package org.qiyu.live.bank.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.bank.constants.OrderStatusEnum;
import org.qiyu.live.bank.constants.PayProductTypeEnum;
import org.qiyu.live.bank.constants.TradeTypeEnum;
import org.qiyu.live.bank.dto.PayOrderDTO;
import org.qiyu.live.bank.dto.PayProductDTO;
import org.qiyu.live.bank.provider.dao.mapper.IPayOrderMapper;
import org.qiyu.live.bank.provider.dao.po.PayOrderPO;
import org.qiyu.live.bank.provider.dao.po.PayTopicPO;
import org.qiyu.live.bank.provider.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Slf4j
@Service
public class PayOrderServiceImpl implements IPayOrderService {
    @Resource
    private IPayOrderMapper payOrderMapper;
    @Resource
    private IPayTopicService payTopicService;
    @Resource
    private MQProducer mqProducer;
    @Resource
    private IQiyuCurrencyTradeService qiyuCurrencyTradeService;
    @Resource
    private IQiyuCurrencyAccountService qiyuCurrencyAccountService;
    @Resource
    private IPayProductService payProductService;
    @Resource
    private IPayNotifyOutboxService payNotifyOutboxService;

    @Override
    public String insertOne(PayOrderPO payOrderPO) {
        PayProductDTO payProductDTO = payProductService.getByProductId(payOrderPO.getProductId());
        if (payProductDTO == null
                || !PayProductTypeEnum.QIYU_COIN.getCode().equals(payProductDTO.getType())
                || payProductDTO.getPrice() == null
                || payProductDTO.getPrice() <= 0) {
            throw new IllegalArgumentException("支付产品不存在或配置不合法");
        }
        Integer coinAmount = JSON.parseObject(payProductDTO.getExtra()).getInteger("coin");
        if (coinAmount == null || coinAmount <= 0) {
            throw new IllegalArgumentException("支付产品金币数量不合法");
        }

        // 创建订单时固定金额和金币数量，后续支付回调不再读取可变的产品配置。
        payOrderPO.setPayAmount(payProductDTO.getPrice());
        payOrderPO.setCoinAmount(coinAmount);
        String orderId = UUID.randomUUID().toString();
        payOrderPO.setOrderId(orderId);
        payOrderMapper.insert(payOrderPO);
        return orderId;
    }

    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setId(id);
        payOrderPO.setStatus(status);
        payOrderMapper.updateById(payOrderPO);
        return true;
    }

    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        if (StringUtils.isEmpty(orderId) || status == null) {
            return false;
        }
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setStatus(status);

        if (status == OrderStatusEnum.PAYING.getCode()) {
            // 创建订单后只允许从待支付进入支付中。
            LambdaUpdateWrapper<PayOrderPO> payingWrapper = new LambdaUpdateWrapper<>();
            payingWrapper.eq(PayOrderPO::getOrderId, orderId)
                    .eq(PayOrderPO::getStatus, OrderStatusEnum.WAITING_PAY.getCode());
            return payOrderMapper.update(payOrderPO, payingWrapper) == 1;
        }
        if (status != OrderStatusEnum.PAYED.getCode()) {
            return false;
        }

        LambdaUpdateWrapper<PayOrderPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayOrderPO::getOrderId, orderId)
                // 已支付订单不允许再次进入后续状态，避免重复回调反复处理。
                .in(PayOrderPO::getStatus,
                        OrderStatusEnum.WAITING_PAY.getCode(),
                        OrderStatusEnum.PAYING.getCode());

        return payOrderMapper.update(payOrderPO, updateWrapper) == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        if (payOrderDTO == null || StringUtils.isEmpty(payOrderDTO.getOrderId())) {
            return false;
        }
        PayOrderPO payOrderPO = this.queryByOrderId(payOrderDTO.getOrderId());
        if (payOrderPO == null) {
            return false;
        }
        if (payOrderDTO.getUserId() != null && !payOrderDTO.getUserId().equals(payOrderPO.getUserId())) {
            return false;
        }

        if (payOrderDTO.getPayChannel() != null && !payOrderDTO.getPayChannel().equals(payOrderPO.getPayChannel())) {
            return false;
        }
        if (payOrderDTO.getPayAmount() != null && !payOrderDTO.getPayAmount().equals(payOrderPO.getPayAmount())) {
            return false;
        }
        if (payOrderPO.getCoinAmount() == null || payOrderPO.getCoinAmount() <= 0) {
            throw new IllegalStateException("支付订单金币快照不合法");
        }

        if (OrderStatusEnum.PAYED.getCode().equals(payOrderPO.getStatus())) {
            // 已完成订单只接受同一笔第三方交易的重复通知，禁止不同交易号覆盖审计记录。
            return StringUtils.isEmpty(payOrderPO.getThirdPartyTradeNo())
                    || StringUtils.isEmpty(payOrderDTO.getThirdPartyTradeNo())
                    || payOrderDTO.getThirdPartyTradeNo().equals(payOrderPO.getThirdPartyTradeNo());
        }

        // 只有首次状态流转成功才入账；重复回调无需再次扣款或加款。
        PayOrderPO payedOrder = new PayOrderPO();
        payedOrder.setStatus(OrderStatusEnum.PAYED.getCode());
        payedOrder.setPayTime(java.time.LocalDateTime.now());
        payedOrder.setThirdPartyTradeNo(payOrderDTO.getThirdPartyTradeNo());
        LambdaUpdateWrapper<PayOrderPO> payedWrapper = new LambdaUpdateWrapper<>();
        payedWrapper.eq(PayOrderPO::getOrderId, payOrderPO.getOrderId())
                .in(PayOrderPO::getStatus,
                        OrderStatusEnum.WAITING_PAY.getCode(),
                        OrderStatusEnum.PAYING.getCode());
        if (payOrderMapper.update(payedOrder, payedWrapper) != 1) {
            PayOrderPO latestOrder = this.queryByOrderId(payOrderPO.getOrderId());
            return latestOrder != null && OrderStatusEnum.PAYED.getCode().equals(latestOrder.getStatus());
        }
        qiyuCurrencyAccountService.increaseForRecharge(
                payOrderPO.getUserId(), payOrderPO.getCoinAmount(), payOrderPO.getOrderId());

        PayTopicPO payTopicPO = payOrderDTO.getBizCode() == null
                ? null : payTopicService.getByCode(payOrderDTO.getBizCode());
        if (payTopicPO != null && !StringUtils.isEmpty(payTopicPO.getTopic())) {
            // 与订单状态和到账流水同事务保存，提交后由本地消息表异步可靠投递。
            payNotifyOutboxService.createPending(
                    payTopicPO.getTopic(), JSON.toJSONString(payOrderPO), payOrderPO.getOrderId());
        }
        return true;
    }

    /** 事务提交后发送 MQ，避免数据库回滚却向下游发送成功事件。 */
    @Deprecated
    private void sendPayNotifyMessageAfterCommit(String topic, PayOrderPO payOrderPO) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Message message = new Message();
                message.setTopic(topic);
                message.setBody(JSON.toJSONBytes(payOrderPO));
                try {
                    SendResult sendResult = mqProducer.send(message);
                    log.info("[payNotify] sendResult is {}", sendResult);
                } catch (Exception e) {
                    log.error("[payNotify] MQ send error", e);
                }
            }
        });
    }

    /**
     * 增加用户余额
     * @param payOrderPO
     */
    @Deprecated
    private void payNotifyHandler(PayOrderPO payOrderPO) {
        this.updateOrderStatus(payOrderPO.getOrderId(), OrderStatusEnum.PAYED.getCode());
        Integer productId= payOrderPO.getProductId();
        PayProductDTO payProductDTO = payProductService.getByProductId(productId);
        if (payProductDTO != null
                && PayProductTypeEnum.QIYU_COIN.getCode().equals(payProductDTO.getType())){
            Long userId = payOrderPO.getUserId();
            JSONObject jsonObject= JSON.parseObject(payProductDTO.getExtra());
            Integer number = jsonObject.getInteger("coin");
            qiyuCurrencyAccountService.increase(userId,number);
            qiyuCurrencyTradeService.insertOne(userId,number, TradeTypeEnum.LIVING_RECHARGE_TRADE.getCode());
        }

    }

    @Override
    public PayOrderPO queryByOrderId(String orderId) {
        LambdaQueryWrapper<PayOrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayOrderPO::getOrderId,orderId);
        queryWrapper.last("limit 1");
        return payOrderMapper.selectOne(queryWrapper);
    }
}
