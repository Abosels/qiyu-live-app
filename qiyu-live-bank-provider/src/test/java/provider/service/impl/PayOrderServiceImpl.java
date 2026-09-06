package provider.service.impl;

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
import org.springframework.stereotype.Service;
import provider.dao.mapper.IPayOrderMapper;
import provider.dao.po.PayOrderPO;
import provider.dao.po.PayTopicPO;
import provider.service.*;

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

    @Override
    public String insertOne(PayOrderPO payOrderPO) {
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
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setStatus(status);

        LambdaUpdateWrapper<PayOrderPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayOrderPO::getOrderId, orderId)
                // 已支付订单不允许再次进入后续状态，避免重复回调反复处理。
                .in(PayOrderPO::getStatus,
                        OrderStatusEnum.WAITING_PAY.getCode(),
                        OrderStatusEnum.PAYING.getCode());

        return payOrderMapper.update(payOrderPO, updateWrapper) == 1;
    }

    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        PayOrderPO payOrderPO = this.queryByOrderId(payOrderDTO.getOrderId());
        if (payOrderPO == null) {
            return false;
        }
        payOrderDTO.getBizCode();
        PayTopicPO payTopicPO = payTopicService.getByCode(payOrderDTO.getBizCode());
        if (payTopicPO == null || StringUtils.isEmpty(payTopicPO.getTopic())) {
            return false;
        }
        this.payNotifyHandler(payOrderPO);
        //假设 支付成功后，要发送消息通知 -》 msg-provider
        //假设 支付成功后，要修改用户的 vip 经验值 -》 account-provider
        //为了避免上面强依赖其他api， 所以发mq
        //中台服务，支付的对接方 十几种服务 pay-notify-topic
        Message message = new Message() ;
        message.setTopic(payTopicPO.getTopic());
        message.setBody(JSON.toJSONBytes(payOrderPO));
        try {
            SendResult sendResult = mqProducer.send(message);
            log.info("sendResult is {}",sendResult);
        } catch (Exception e) {
            log.error("[payNotify] error is", e);
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 增加用户余额
     * @param payOrderPO
     */
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
