package org.qiyu.live.gift.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.ShopCacheKeyBuilder;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.resp.ShopCartItemRespDTO;
import org.qiyu.live.gift.dto.req.ShopCartReqDTO;
import org.qiyu.live.gift.dto.resp.ShopCartRespDTO;
import org.qiyu.live.gift.dto.SkuInfoDTO;
import org.qiyu.live.gift.provider.dao.po.SkuInfoPO;
import org.qiyu.live.gift.provider.service.IShopCartService;
import org.qiyu.live.gift.provider.service.ISkuInfoService;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ShopCartServiceImpl implements IShopCartService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ShopCacheKeyBuilder shopCacheKeyBuilder;
    @Resource
    private ISkuInfoService skuInfoService;

    @Override
    public Boolean addCart(ShopCartReqDTO shopCartReqDTO) {
        String cacheKey = shopCacheKeyBuilder.buildShopCartKey(shopCartReqDTO.getUserId(),shopCartReqDTO.getRoomId());
        //1. 一个用户对应多个商品
        //2. 读取所有的商品数据
        //3. 每个商品都有数量
        //map(k,v) key是skuId, value是商品的数量
        redisTemplate.opsForHash().increment(cacheKey, shopCartReqDTO.getSkuId(), 1);
        return true;
    }

    @Override
    public Boolean removeFromCart(ShopCartReqDTO shopCartReqDTO) {
        String cacheKey = shopCacheKeyBuilder.buildShopCartKey(shopCartReqDTO.getUserId(), shopCartReqDTO.getRoomId());
        return redisTemplate.opsForHash().delete(cacheKey, shopCartReqDTO.getSkuId()) > 0;
    }

    @Override
    public ShopCartRespDTO getCartInfo(ShopCartReqDTO shopCartReqDTO) {
        String cacheKey = shopCacheKeyBuilder.buildShopCartKey(shopCartReqDTO.getUserId(), shopCartReqDTO.getRoomId());
        List<ShopCartItemRespDTO> shopCartItemRespDTOS = new ArrayList<>();
        Map<Long, Integer> skuCountMap = new HashMap<>();
        try (Cursor<Map.Entry<Object, Object>> allCartData = redisTemplate.opsForHash()
                .scan(cacheKey, ScanOptions.scanOptions().match("*").build())) {
            while (allCartData.hasNext()) {
                Map.Entry<Object, Object> entry = allCartData.next();
                // increment 的返回值可能是 Long，统一按字符串转换避免序列化类型差异。
                skuCountMap.put(Long.valueOf(String.valueOf(entry.getKey())),
                        Integer.valueOf(String.valueOf(entry.getValue())));
            }
        }
        if (skuCountMap.isEmpty()) {
            ShopCartRespDTO emptyCart = new ShopCartRespDTO();
            emptyCart.setRoomId(shopCartReqDTO.getRoomId());
            emptyCart.setUserId(shopCartReqDTO.getUserId());
            emptyCart.setShopCartItemRespDTOS(new ArrayList<>());
            return emptyCart;
        }
        List<SkuInfoPO> skuInfoDTOList = skuInfoService.queryBySkuIds(new ArrayList<>(skuCountMap.keySet()));
        for (SkuInfoPO skuInfoPO : skuInfoDTOList) {
            SkuInfoDTO skuInfoDTO = ConvertBeanUtils.convert(skuInfoPO, SkuInfoDTO.class);
            Integer count = skuCountMap.get(skuInfoPO.getSkuId());
            shopCartItemRespDTOS.add(new ShopCartItemRespDTO(count,skuInfoDTO));
        }
        ShopCartRespDTO shopCartRespDTO = new ShopCartRespDTO();
        shopCartRespDTO.setRoomId(shopCartReqDTO.getRoomId());
        shopCartRespDTO.setUserId(shopCartReqDTO.getUserId());
        shopCartRespDTO.setShopCartItemRespDTOS(shopCartItemRespDTOS);
        return shopCartRespDTO;
    }

    @Override
    public Boolean clearCart(ShopCartReqDTO shopCartReqDTO) {
        String cacheKey = shopCacheKeyBuilder.buildShopCartKey(shopCartReqDTO.getUserId(), shopCartReqDTO.getRoomId());
        return Boolean.TRUE.equals(redisTemplate.delete(cacheKey));
    }

    @Override
    public Boolean updateCartItemNum(ShopCartReqDTO shopCartReqDTO) {
        String cacheKey = shopCacheKeyBuilder.buildShopCartKey(shopCartReqDTO.getUserId(), shopCartReqDTO.getRoomId());
        Long latestNum = redisTemplate.opsForHash().increment(cacheKey, shopCartReqDTO.getSkuId(), shopCartReqDTO.getChangeNum());
        // 数量减到 0 时，购物车不再保留该 SKU。
        if (latestNum != null && latestNum <= 0) {
            redisTemplate.opsForHash().delete(cacheKey, shopCartReqDTO.getSkuId());
        }
        return true;
    }
}
