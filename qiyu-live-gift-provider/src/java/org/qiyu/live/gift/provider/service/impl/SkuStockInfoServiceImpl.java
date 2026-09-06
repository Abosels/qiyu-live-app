package org.qiyu.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.ShopCacheKeyBuilder;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.gift.constants.SkuOrderInfoEnum;
import org.qiyu.live.gift.dto.RollBackStockDTO;
import org.qiyu.live.gift.dto.req.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.resp.SkuOrderInfoRespDTO;
import org.qiyu.live.gift.provider.dao.mapper.SkuStockInfoMapper;
import org.qiyu.live.gift.provider.dao.po.SkuStockInfoPO;
import org.qiyu.live.gift.provider.service.ISkuOrderInfoService;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuStockInfoServiceImpl implements ISkuStockInfoService {

    /** 在 Redis 内原子校验并扣减库存：失败返回 -1，成功返回剩余库存。 */
    private static final String DECR_STOCK_LUA = """
            local stock = redis.call('GET', KEYS[1])
            local decreaseNum = tonumber(ARGV[1])
            if not stock or not decreaseNum or decreaseNum <= 0 then
                return -1
            end
            if tonumber(stock) < decreaseNum then
                return -1
            end
            return redis.call('DECRBY', KEYS[1], decreaseNum)
            """;

    /** 先校验全部库存，再统一扣减，保证同一 Redis 实例内的多 SKU 原子预扣。 */
    private static final String DECR_STOCK_BATCH_LUA = """
            for index = 1, #KEYS do
                local stock = redis.call('GET', KEYS[index])
                local decreaseNum = tonumber(ARGV[index])
                if not stock or not decreaseNum or decreaseNum <= 0 or tonumber(stock) < decreaseNum then
                    return 0
                end
            end
            for index = 1, #KEYS do
                redis.call('DECRBY', KEYS[index], tonumber(ARGV[index]))
            end
            return 1
            """;

    @Resource
    private SkuStockInfoMapper skuStockInfoMapper;

    @Resource
    private ISkuOrderInfoService skuOrderInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ShopCacheKeyBuilder shopCacheKeyBuilder;

    @Override
    public boolean updateStockNum(Long skuId, Integer num) {
        SkuStockInfoPO skuStockInfoPO = new SkuStockInfoPO();
        skuStockInfoPO.setStockNum(num);
        LambdaUpdateWrapper<SkuStockInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SkuStockInfoPO::getSkuId,skuId);
        skuStockInfoMapper.update(skuStockInfoPO,updateWrapper);
        return true;
    }

    @Override
    public boolean decrStockNumBySkuId(Integer roomId, Long skuId, Integer num) {
        DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(DECR_STOCK_LUA);
        redisScript.setResultType(Integer.class);
        String stockCacheKey = shopCacheKeyBuilder.buildSkuStockKey(roomId, skuId);
        Integer remainingStock = redisTemplate.execute(redisScript, Collections.singletonList(stockCacheKey), num);
        return remainingStock != null && remainingStock >= 0;
    }

    @Override
    public boolean decrStockNumBatch(Integer roomId, Map<Long, Integer> skuCountMap) {
        if (roomId == null || skuCountMap == null || skuCountMap.isEmpty()) {
            return false;
        }
        List<String> stockCacheKeys = new ArrayList<>();
        List<Integer> decreaseNums = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : skuCountMap.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0) {
                return false;
            }
            stockCacheKeys.add(shopCacheKeyBuilder.buildSkuStockKey(roomId, entry.getKey()));
            decreaseNums.add(entry.getValue());
        }

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(DECR_STOCK_BATCH_LUA);
        redisScript.setResultType(Long.class);
        // Lua 在同一次执行中完成全部校验和扣减，不再需要 Java 侧逐项回补。
        Long executeResult = redisTemplate.execute(redisScript, stockCacheKeys, decreaseNums.toArray());
        return Long.valueOf(1L).equals(executeResult);
    }

    @Override
    public void incrStockNumBySkuId(Integer roomId, Long skuId, Integer num) {
        // 下单过程中后续 SKU 缺货时，回补此前已预扣成功的库存。
        String stockCacheKey = shopCacheKeyBuilder.buildSkuStockKey(roomId, skuId);
        redisTemplate.opsForValue().increment(stockCacheKey, num);
    }

    @Override
    public SkuStockInfoPO queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuStockInfoPO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SkuStockInfoPO::getSkuId, skuId);
        queryWrapper.eq(SkuStockInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return skuStockInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public List<SkuStockInfoPO> queryBySkuIds(List<Long> skuIds) {
        LambdaQueryWrapper<SkuStockInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuStockInfoPO::getSkuId, skuIds);
        queryWrapper.eq(SkuStockInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        return skuStockInfoMapper.selectList(queryWrapper);
    }

    @Override
    public void rollBackStockHandler(RollBackStockDTO rollBackStockDTO) {
        SkuOrderInfoRespDTO orderInfoRespDTO = skuOrderInfoService.queryByOrderId(rollBackStockDTO.getOrderId());
        if (orderInfoRespDTO == null || !SkuOrderInfoEnum.PREPARE_PAY.getCode().equals(orderInfoRespDTO.getStatus())) {
            return;
        }
        SkuOrderInfoReqDTO reqDTO = new SkuOrderInfoReqDTO();
        reqDTO.setStatus(SkuOrderInfoEnum.CANCELLED.getCode());
        reqDTO.setId(orderInfoRespDTO.getId());
        reqDTO.setUserId(orderInfoRespDTO.getUserId());
        reqDTO.setRoomId(orderInfoRespDTO.getRoomId());
        // 只有仍处于待支付状态才能取消；若支付已抢占为 PAYING，不回补库存。
        if (!skuOrderInfoService.updateOrderStatusIfMatch(
                orderInfoRespDTO.getId(), orderInfoRespDTO.getUserId(), orderInfoRespDTO.getRoomId(),
                SkuOrderInfoEnum.PREPARE_PAY.getCode(), SkuOrderInfoEnum.CANCELLED.getCode())) {
            return;
        }


        // 订单以重复 SKU 保存数量，例如 "101,101,102" 代表 SKU 101 两件、102 一件。
        Map<Long, Integer> skuCountMap = new LinkedHashMap<>();
        for (String skuIdValue : orderInfoRespDTO.getSkuIdList().split(",")) {
            Long skuId = Long.valueOf(skuIdValue);
            skuCountMap.merge(skuId, 1, Integer::sum);
        }
        skuCountMap.forEach((skuId, count) -> incrStockNumBySkuId(orderInfoRespDTO.getRoomId(), skuId, count));

    }
}
