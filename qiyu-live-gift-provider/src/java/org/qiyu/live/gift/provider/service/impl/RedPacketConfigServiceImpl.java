package org.qiyu.live.gift.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.idea.qiyu.live.framework.redis.starter.key.RedPacketProviderCacheKeyBuilder;
import org.qiyu.live.bank.constants.TradeTypeEnum;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.common.interfaces.utils.ListUtils;
import org.qiyu.live.gift.constants.RedPacketStatusCodeEnum;
import org.qiyu.live.gift.dto.req.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.provider.dao.mapper.RedPacketConfigMapper;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;
import org.qiyu.live.gift.provider.service.IRedPacketConfigService;
import org.qiyu.live.gift.provider.service.bo.SendRedPacketBO;
import org.qiyu.live.im.contants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.interfaces.contants.ImMsgBizCodeEnum;
import org.qiyu.live.im.router.interfaces.rpc.ImRouterRpc;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Slf4j
@Service
public class RedPacketConfigServiceImpl implements IRedPacketConfigService {

    @Resource
    private RedPacketConfigMapper redPacketConfigMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RedPacketProviderCacheKeyBuilder cacheKeyBuilder;
    @DubboReference(check = false)
    private ImRouterRpc imRouterRpc;
    @DubboReference(check = false)
    private ILivingRoomRpc livingRoomRpc;
    @Resource
    private MQProducer mqProducer;
    @DubboReference(check = false)
    private IQiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;

    @Override
    public RedPacketConfigPO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getAnchorId, anchorId);
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public RedPacketConfigPO queryByConfigCode(String configCode) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getConfigCode, configCode);
        queryWrapper.eq(RedPacketConfigPO::getStatus, RedPacketStatusCodeEnum.IS_PREPARE.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean addOne(RedPacketConfigPO redPacketConfigPO) {
        redPacketConfigPO.setConfigCode(UUID.randomUUID().toString());
        return redPacketConfigMapper.insert(redPacketConfigPO) > 0;
    }

    @Override
    public boolean updateById(RedPacketConfigPO redPacketConfigPO) {

        return redPacketConfigMapper.updateById(redPacketConfigPO) > 0;
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {
        //防止重复生成，以及错误参数传递情况

        RedPacketConfigPO redPacketConfigPO = this.queryByAnchorId(anchorId);
        if (redPacketConfigPO == null) {
            return false;
        }
        Integer totalCount = redPacketConfigPO.getTotalCount();
        Integer totalPrice = redPacketConfigPO.getTotalPrice();
        if (totalCount == null || totalCount <= 0 || totalPrice == null || totalPrice < totalCount) {
            return false;
        }
        String configCode = redPacketConfigPO.getConfigCode();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKeyBuilder.buildRedPacketPrepareSuccess(configCode)))) {
            return true;
        }
        Boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(
                cacheKeyBuilder.buildRedPocketInitLock(configCode), 1, 3, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(lockStatus)) {
            return false;
        }
        List<Integer> priceList =this.createRedPacketPriceList(totalPrice,totalCount);
        String cacheKey = cacheKeyBuilder.buildRedPocketList(configCode);
        //redis 输入输出缓冲区，
        List<List<Integer>> splitPriceList = ListUtils.splistList(priceList,100);
        for (List<Integer> priceItemList : splitPriceList) {
            redisTemplate.opsForList().leftPushAll(cacheKey, priceItemList);
        }
        redisTemplate.expire(cacheKey, 1, TimeUnit.DAYS);
        redPacketConfigPO.setStatus(RedPacketStatusCodeEnum.IS_PREPARE.getCode());
        this.updateById(redPacketConfigPO);
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildRedPacketPrepareSuccess(configCode), 1, 1 ,TimeUnit.DAYS);
        return true;
    }

    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        if (reqDTO == null || reqDTO.getConfigCode() == null || reqDTO.getRoomId() == null) {
            return false;
        }
        String code = reqDTO.getConfigCode();
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(cacheKeyBuilder.buildRedPacketPrepareSuccess(code)))) {
            return false;
        }
        String notifySuccessCache = cacheKeyBuilder.buildRedPacketNotify(code);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(notifySuccessCache))) {
            return true;
        }
        RedPacketConfigPO configPO= this.queryByConfigCode(code);
        if (configPO == null) {
            return false;
        }
        //广播im事件
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("redPakcetConfigPOs", JSON.toJSONString(configPO));
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(reqDTO.getRoomId());
        livingRoomReqDTO.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
        if (CollectionUtils.isEmpty(userIdList)){
            return false;
        }
        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.START_RED_PACKET,jsonObject);
        configPO.setStatus(RedPacketStatusCodeEnum.HAS_SEND.getCode());
        configPO.setStartTime(LocalDateTime.now());
        this.updateById(configPO);
        redisTemplate.opsForValue().set(notifySuccessCache,1,1,TimeUnit.DAYS);
        return true;
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO) {
        if (reqDTO == null || reqDTO.getUserId() == null || reqDTO.getConfigCode() == null) {
            return null;
        }
        String configCode = reqDTO.getConfigCode();
        String userReceivedKey = cacheKeyBuilder.buildRedPacketUserReceived(configCode, reqDTO.getUserId());
        // 先占用用户领取资格，再从金额列表弹出，避免同一用户重复领取多个红包。
        if (!Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(userReceivedKey, 1, 1, TimeUnit.DAYS))) {
            return new RedPacketReceiveDTO(0, "您已领取过该红包");
        }
        String cacheKey = cacheKeyBuilder.buildRedPocketList(configCode);
        Object cacheObj = redisTemplate.opsForList().rightPop(cacheKey);
        if (cacheObj == null) {
            redisTemplate.delete(userReceivedKey);
            return null;
        }
        Integer price = ((Number) cacheObj).intValue();
        log.info("[receiveRedPacket] code is {}, price is {}", configCode, price);
        SendRedPacketBO sendRedPacketBO = new SendRedPacketBO();
        sendRedPacketBO.setPrice(price);
        sendRedPacketBO.setReqDTO(reqDTO);
        // 幂等号必须稳定，RocketMQ 重试时资金层才能识别同一次红包入账。
        sendRedPacketBO.setBizId("red-packet:" + configCode + ":" + reqDTO.getUserId());
        Message message = new Message();
        message.setTopic(GiftProviderTopicNames.RECEIVE_RED_PACKET);
        message.setBody(JSON.toJSONBytes(sendRedPacketBO));
        try{
            SendResult sendResult = mqProducer.send(message);
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                return new RedPacketReceiveDTO(price, "恭喜领取红包" + price + "币");
            }
            redisTemplate.opsForList().rightPush(cacheKey, price);
            redisTemplate.delete(userReceivedKey);
            return new RedPacketReceiveDTO(0, "红包领取失败，请重试");
        }catch(Exception e){
            redisTemplate.opsForList().rightPush(cacheKey, price);
            redisTemplate.delete(userReceivedKey);
            log.error("[receiveRedPacket] 红包领取消息发送失败, code={}", configCode, e);
        }
        return new RedPacketReceiveDTO(null,"很抱歉，红包被人抢走了");

    }

    @Override
    public void receiveRedPacketHandle(RedPacketConfigReqDTO reqDTO, Integer price, String bizId) {
        String configCode = reqDTO.getConfigCode();
        AccountTradeReqDTO accountTradeReqDTO = new AccountTradeReqDTO();
        accountTradeReqDTO.setUserId(reqDTO.getUserId());
        accountTradeReqDTO.setNumber(price);
        accountTradeReqDTO.setBizId(bizId);
        accountTradeReqDTO.setTradeType(TradeTypeEnum.RED_PACKET_REWARD_TRADE.getCode());
        AccountTradeRespDTO accountTradeRespDTO = qiyuCurrencyAccountRpc.increase(accountTradeReqDTO);
        if (accountTradeRespDTO == null || !accountTradeRespDTO.isSuccess()) {
            throw new IllegalStateException("红包入账失败");
        }
        if (!accountTradeRespDTO.isFirstProcessed()) {
            return;
        }
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPocketTotalGetPriceCache(configCode);
        String totalGetCacheKey = cacheKeyBuilder.buildRedPocketTotalGetCache(configCode);
        redisTemplate.opsForValue().increment(cacheKeyBuilder.buildUserTotalGetPriceCache(reqDTO.getUserId()), price);
        redisTemplate.opsForValue().increment(totalGetCacheKey);
        redisTemplate.expire(totalGetCacheKey, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().increment(totalGetPriceCacheKey, price);
        redisTemplate.expire(totalGetPriceCacheKey, 1, TimeUnit.DAYS);
        //对表t_red_packet_config中total_get_price进行增加
        redPacketConfigMapper.increaseTotalGetPrice(configCode,price);
        //对表t_red_packet_config中total_get进行增加
        redPacketConfigMapper.increaseTotalGet(configCode);
    }

    /**
     * 生成红包金额List集合数据
     * @param totalPrice
     * @param totalCount
     * @return
     */
    private List<Integer> createRedPacketPriceList(Integer totalPrice, Integer totalCount) {
        List<Integer> redPacketPriceList = new ArrayList<>(totalCount);
        int sum = 0;
        for (int i = 0; i < totalCount; i++) {
            //如果是最后一个红包
            if (totalCount == i+1){
                sum += totalPrice;
                redPacketPriceList.add(totalPrice);
                break;
            }
            int remainingCount = totalCount - i;
            int maxLimit = Math.min(totalPrice - remainingCount + 1,
                    Math.max(1, (totalPrice / remainingCount) * 2));
            int currentPrice = ThreadLocalRandom.current().nextInt(1, maxLimit + 1);
            totalPrice -= currentPrice;
            redPacketPriceList.add(currentPrice);
            sum += currentPrice;
        }
        System.out.println("总共生成金额" + sum);
        return redPacketPriceList;
    }

    private void batchSendImMsg(List<Long> userIdList, ImMsgBizCodeEnum imMsgBizCodeEnum, JSONObject jsonObject) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        List<ImMsgBody> imMsgBodies = userIdList.stream().map(userId ->{
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
            imMsgBody.setBizCode(imMsgBizCodeEnum.getCode());
            imMsgBody.setUserId(userId);
            imMsgBody.setData(jsonObject.toJSONString());
            return imMsgBody;
        }).collect(Collectors.toList());
        imRouterRpc.batchSendMsg(imMsgBodies);
    }
}
