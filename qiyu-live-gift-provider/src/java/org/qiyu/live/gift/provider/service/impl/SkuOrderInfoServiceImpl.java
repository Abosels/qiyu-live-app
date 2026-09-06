package org.qiyu.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.ShopCacheKeyBuilder;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.SkuOrderInfoDTO;
import org.qiyu.live.gift.dto.req.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.resp.SkuOrderInfoRespDTO;
import org.qiyu.live.gift.provider.dao.mapper.SkuOrderInfoMapper;
import org.qiyu.live.gift.provider.dao.po.SkuOrderInfoPO;
import org.qiyu.live.gift.provider.service.ISkuOrderInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SkuOrderInfoServiceImpl implements ISkuOrderInfoService {

    @Resource
    private SkuOrderInfoMapper skuOrderInfoMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ShopCacheKeyBuilder shopCacheKeyBuilder;

    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        String cacheKey = shopCacheKeyBuilder.buildSkuOrderKey(userId,roomId);
        Object cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (cacheValue != null) {
            return ConvertBeanUtils.convert(cacheValue, SkuOrderInfoRespDTO.class);
        }
        LambdaQueryWrapper<SkuOrderInfoPO> qw = new LambdaQueryWrapper<>();
        qw.eq(SkuOrderInfoPO::getUserId, userId);
        qw.eq(SkuOrderInfoPO::getRoomId, roomId);
        qw.orderByDesc(SkuOrderInfoPO::getId);
        qw.last("limit 1");
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoMapper.selectOne(qw);
        if (skuOrderInfoPO != null) {
            // 查询方法声明的是 RespDTO，缓存与返回值必须保持同一类型。
            SkuOrderInfoRespDTO skuOrderInfoRespDTO = ConvertBeanUtils.convert(
                    skuOrderInfoPO, SkuOrderInfoRespDTO.class);
            redisTemplate.opsForValue().set(cacheKey, skuOrderInfoRespDTO,1, TimeUnit.HOURS);
            return skuOrderInfoRespDTO;
        }
        return null;
    }

    @Override
    public SkuOrderInfoRespDTO queryByOrderId(Integer orderId) {
        String cacheKey = shopCacheKeyBuilder.buildSkuOrderInfoKey(orderId);
        Object cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (cacheValue != null) {
            return ConvertBeanUtils.convert(cacheValue, SkuOrderInfoRespDTO.class);
        }
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoMapper.selectById(orderId);
        if (skuOrderInfoPO != null) {
            SkuOrderInfoRespDTO skuOrderInfoRespDTO = ConvertBeanUtils.convert(skuOrderInfoPO, SkuOrderInfoRespDTO.class);
            redisTemplate.opsForValue().set(cacheKey, skuOrderInfoRespDTO,1, TimeUnit.HOURS);
            return skuOrderInfoRespDTO;
        }
        return null;
    }

    @Override
    public SkuOrderInfoPO insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        String skuIdListStr = String.join(",", skuOrderInfoReqDTO.getSkuIdList().stream()
                .map(String::valueOf)
                .toList());
        SkuOrderInfoPO skuOrderInfoPO = ConvertBeanUtils.convert(skuOrderInfoReqDTO, SkuOrderInfoPO.class);
        skuOrderInfoPO.setSkuIdList(skuIdListStr);
        skuOrderInfoMapper.insert(skuOrderInfoPO);
        return skuOrderInfoPO;
    }

    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        SkuOrderInfoPO skuOrderInfoPO = new SkuOrderInfoPO();
        skuOrderInfoPO.setStatus(skuOrderInfoReqDTO.getStatus());
        skuOrderInfoPO.setId(skuOrderInfoReqDTO.getId());
        skuOrderInfoMapper.updateById(skuOrderInfoPO);
        String cacheKey = shopCacheKeyBuilder.buildSkuOrderKey(skuOrderInfoReqDTO.getUserId(),skuOrderInfoReqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }

    @Override
    public boolean updateOrderStatusIfMatch(Integer orderId, Long userId, Integer roomId,
                                            Integer expectedStatus, Integer targetStatus) {
        LambdaUpdateWrapper<SkuOrderInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SkuOrderInfoPO::getId, orderId)
                .eq(SkuOrderInfoPO::getUserId, userId)
                .eq(SkuOrderInfoPO::getStatus, expectedStatus)
                .set(SkuOrderInfoPO::getStatus, targetStatus);
        if (skuOrderInfoMapper.update(null, updateWrapper) != 1) {
            return false;
        }
        // 状态变化后必须清理按订单与按用户直播间查询的两类缓存。
        redisTemplate.delete(shopCacheKeyBuilder.buildSkuOrderInfoKey(orderId));
        redisTemplate.delete(shopCacheKeyBuilder.buildSkuOrderKey(userId, roomId));
        return true;
    }

}
