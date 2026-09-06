package org.qiyu.live.gift.provider.rpc;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.bank.constants.TradeTypeEnum;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.constants.SkuOrderInfoEnum;
import org.qiyu.live.gift.dto.RollBackStockDTO;
import org.qiyu.live.gift.dto.SkuPrepareOrderInfoDTO;
import org.qiyu.live.gift.dto.SkuPrepareOrderItemInfoDTO;
import org.qiyu.live.gift.dto.req.PayNowReqDTO;
import org.qiyu.live.gift.dto.req.PrepareOrderReqDTO;
import org.qiyu.live.gift.dto.req.ShopCartReqDTO;
import org.qiyu.live.gift.dto.req.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.resp.ShopCartItemRespDTO;
import org.qiyu.live.gift.dto.resp.ShopCartRespDTO;
import org.qiyu.live.gift.dto.resp.SkuOrderInfoRespDTO;
import org.qiyu.live.gift.interfaces.ISkuOrderInfoRPC;
import org.qiyu.live.gift.provider.dao.po.SkuOrderInfoPO;
import org.qiyu.live.gift.provider.service.IShopCartService;
import org.qiyu.live.gift.provider.service.ISkuInfoService;
import org.qiyu.live.gift.provider.service.ISkuOrderInfoService;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class SkuOrderInfoRPCImpl implements ISkuOrderInfoRPC {

    @Resource
    private ISkuOrderInfoService skuOrderInfoService;
    @Autowired
    private IShopCartService shopCartService;
    @Resource
    private ISkuStockInfoService skuStockInfoService;
    @Resource
    private ISkuInfoService skuInfoService;
    @DubboReference
    private IQiyuCurrencyAccountRpc accountRpc;
    @Resource
    private MQProducer mqProducer;

    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        return skuOrderInfoService.queryByUserIdAndRoomId(userId, roomId);
    }

    @Override
    public boolean insertOne(SkuOrderInfoReqDTO SkuOrderInfoReqDTO) {
        return skuOrderInfoService.insertOne(SkuOrderInfoReqDTO) != null;
    }

    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        return skuOrderInfoService.updateOrderStatus(skuOrderInfoReqDTO);
    }

    @Override
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderReqDTO prepareOrderReqDTO) {
        ShopCartReqDTO shopCartReqDTO = ConvertBeanUtils.convert(prepareOrderReqDTO, ShopCartReqDTO.class);
        ShopCartRespDTO shopCartRespDTO = shopCartService.getCartInfo(shopCartReqDTO);
        List<ShopCartItemRespDTO> cartItemList = shopCartRespDTO.getShopCartItemRespDTOS();
        if (CollectionUtils.isEmpty(cartItemList)) {
            return null;
        }


        // 用重复 SKU 记录订单数量，沿用现有 sku_id_list 字段，无需修改数据库表结构。
        Map<Long, Integer> skuCountMap = new LinkedHashMap<>();
        List<Long> skuIdList = new ArrayList<>();
        for (ShopCartItemRespDTO cartItem : cartItemList) {
            Long skuId = cartItem.getSkuInfo().getSkuId();
            Integer count = cartItem.getCount();
            skuCountMap.put(skuId, count);
            for (int index = 0; index < count; index++) {
                skuIdList.add(skuId);
            }
        }

        int totalPrice = 0;
        for (ShopCartItemRespDTO cartItem : cartItemList) {
            Integer skuPrice = cartItem.getSkuInfo().getSkuPrice();
            Integer count = cartItem.getCount();
            if (skuPrice == null || skuPrice <= 0 || count == null || count <= 0) {
                return null;
            }
            // 下单时固定总价，支付阶段不再重新读取可能已变更的 SKU 价格。
            totalPrice += skuPrice * count;
        }

        // 单次 Lua 执行保证购物车中所有 SKU 要么全部预扣成功，要么全部保持原库存。
        if (!skuStockInfoService.decrStockNumBatch(shopCartReqDTO.getRoomId(), skuCountMap)) {
            return null;
        }
        SkuOrderInfoReqDTO skuOrderInfoReqDTO = new SkuOrderInfoReqDTO();
        skuOrderInfoReqDTO.setSkuIdList(skuIdList);
        skuOrderInfoReqDTO.setStatus(SkuOrderInfoEnum.PREPARE_PAY.getCode());
        skuOrderInfoReqDTO.setUserId(shopCartReqDTO.getUserId());
        skuOrderInfoReqDTO.setRoomId(shopCartReqDTO.getRoomId());
        skuOrderInfoReqDTO.setTotalPrice(totalPrice);
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoService.insertOne(skuOrderInfoReqDTO);

        //库存回滚mq消息发送
        this.stockRollBackHandler(skuOrderInfoPO.getUserId(), skuOrderInfoPO.getId());
        // 旧调用只会删除单个 skuId；预下单请求没有 skuId，因此不能清空本次已下单的购物车。
        // shopCartService.removeFromCart(shopCartReqDTO);
        shopCartService.clearCart(shopCartReqDTO);
        List<ShopCartItemRespDTO> shopCartItemRespDTOS = shopCartRespDTO.getShopCartItemRespDTOS();
        List<SkuPrepareOrderItemInfoDTO> itemList = new ArrayList<>();
        for (ShopCartItemRespDTO shopCartItemRespDTO : shopCartItemRespDTOS) {
            SkuPrepareOrderItemInfoDTO orderItemInfoDTO = new SkuPrepareOrderItemInfoDTO();
            orderItemInfoDTO.setSkuInfo(shopCartItemRespDTO.getSkuInfo());
            orderItemInfoDTO.setCount(shopCartItemRespDTO.getCount());
            itemList.add(orderItemInfoDTO);
        }
        SkuPrepareOrderInfoDTO skuPrepareOrderInfoDTO = new SkuPrepareOrderInfoDTO();
        skuPrepareOrderInfoDTO.setOrderId(skuOrderInfoPO.getId());
        skuPrepareOrderInfoDTO.setSkuPrepareOrderItemInfoDTOS(itemList);
        skuPrepareOrderInfoDTO.setTotalPrice(totalPrice);
        return skuPrepareOrderInfoDTO;
    }

    @Override
    public boolean payNow(PayNowReqDTO payNowReqDTO) {

        if (payNowReqDTO == null || payNowReqDTO.getUserId() == null || payNowReqDTO.getOrderId() == null) {
            return false;
        }

        SkuOrderInfoRespDTO orderInfoRespDTO = skuOrderInfoService.queryByOrderId(payNowReqDTO.getOrderId());
        if (orderInfoRespDTO == null || !payNowReqDTO.getUserId().equals(orderInfoRespDTO.getUserId())) {
            return false;
        }
        if (SkuOrderInfoEnum.HAS_PAY.getCode().equals(orderInfoRespDTO.getStatus())) {
            return true;
        }

        if (SkuOrderInfoEnum.PREPARE_PAY.getCode().equals(orderInfoRespDTO.getStatus())) {
            boolean statusChanged = skuOrderInfoService.updateOrderStatusIfMatch(
                    orderInfoRespDTO.getId(), orderInfoRespDTO.getUserId(), orderInfoRespDTO.getRoomId(),
                    SkuOrderInfoEnum.PREPARE_PAY.getCode(), SkuOrderInfoEnum.PAYING.getCode());
            if (!statusChanged) {
                orderInfoRespDTO = skuOrderInfoService.queryByOrderId(payNowReqDTO.getOrderId());
            } else {
                orderInfoRespDTO.setStatus(SkuOrderInfoEnum.PAYING.getCode());
            }
        }
        if (orderInfoRespDTO == null || !SkuOrderInfoEnum.PAYING.getCode().equals(orderInfoRespDTO.getStatus())) {
            return false;
        }

        Integer totalPrice = orderInfoRespDTO.getTotalPrice();
        if (totalPrice == null || totalPrice <= 0) {
            // 历史订单没有价格快照时拒绝支付，不能使用商品现价替代。
            return false;
        }

        AccountTradeReqDTO accountTradeReqDTO = new AccountTradeReqDTO();
        accountTradeReqDTO.setUserId(orderInfoRespDTO.getUserId());
        accountTradeReqDTO.setNumber(totalPrice);
        accountTradeReqDTO.setTradeType(TradeTypeEnum.SHOP_ORDER_PAY_TRADE.getCode());
        // 同一订单的重复点击、Dubbo 重试均使用同一流水业务号，只会扣款一次。
        accountTradeReqDTO.setBizId("shop-order:" + orderInfoRespDTO.getId());
        AccountTradeRespDTO accountTradeRespDTO = accountRpc.decrease(accountTradeReqDTO);
        if (accountTradeRespDTO == null || !accountTradeRespDTO.isSuccess()) {
            // 账户明确返回扣款失败时才释放支付锁，让用户充值后可再次支付。
            skuOrderInfoService.updateOrderStatusIfMatch(
                    orderInfoRespDTO.getId(), orderInfoRespDTO.getUserId(), orderInfoRespDTO.getRoomId(),
                    SkuOrderInfoEnum.PAYING.getCode(), SkuOrderInfoEnum.PREPARE_PAY.getCode());
            return false;
        }

        if (skuOrderInfoService.updateOrderStatusIfMatch(
                orderInfoRespDTO.getId(), orderInfoRespDTO.getUserId(), orderInfoRespDTO.getRoomId(),
                SkuOrderInfoEnum.PAYING.getCode(), SkuOrderInfoEnum.HAS_PAY.getCode())) {
            return true;
        }
        SkuOrderInfoRespDTO latestOrder = skuOrderInfoService.queryByOrderId(orderInfoRespDTO.getId());
        return latestOrder != null && SkuOrderInfoEnum.HAS_PAY.getCode().equals(latestOrder.getStatus());
    }

    private void stockRollBackHandler(Long userId, Integer orderId){
        //订单超时， 21:00 ，21:30分钟订单会自动关闭，在21:25的时候会有订单提醒
        //利用rocketMQ 延迟消息，实践论去做: 将扣减库存的信息, 利用rmq发送出去, 在延迟回调处进行校验。

        RollBackStockDTO rollBackStockDTO = new RollBackStockDTO();
        rollBackStockDTO.setUserId(userId);
        rollBackStockDTO.setOrderId(orderId);
        Message message = new Message();
        message.setTopic(GiftProviderTopicNames.ROLL_BACK_STOCK);
        message.setBody(JSON.toJSONBytes(rollBackStockDTO));
        //messageDelayLevel = 1s 5s 10s(3) 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h(这些都是低版本) 高版本可以自定义
        message.setDelayTimeLevel(16);
        try {
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
