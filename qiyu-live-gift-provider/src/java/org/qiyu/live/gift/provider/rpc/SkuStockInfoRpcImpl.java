package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.idea.qiyu.live.framework.redis.starter.key.ShopCacheKeyBuilder;
import org.qiyu.live.gift.interfaces.ISkuStockInfoRpc;
import org.qiyu.live.gift.provider.dao.po.SkuStockInfoPO;
import org.qiyu.live.gift.provider.service.IAnchorShopInfoService;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@DubboService
public class SkuStockInfoRpcImpl implements ISkuStockInfoRpc {

    @Resource
    private ISkuStockInfoService stockInfoService;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ShopCacheKeyBuilder shopCacheKeyBuilder;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;

    @Override
    public boolean decrStockNumBySkuId(Integer roomId, Long skuId, Integer num) {
        return stockInfoService.decrStockNumBySkuId(roomId, skuId, num);
    }

    @Override
    public boolean prepareStockInfo(Long anchorId) {
        Integer roomId = queryRoomIdByAnchorId(anchorId);
        if (roomId == null) {
            return false;
        }
        List<Long> skuIdList = anchorShopInfoService.querySkuIdByAnchorId(anchorId);
        List<SkuStockInfoPO> skuStockInfoPOS = stockInfoService.queryBySkuIds(skuIdList);
        //带货主播，虽然带货的商品种类不多，但是还是可以用multiset
        Map<String, Integer> saveCacheMap = skuStockInfoPOS.stream().collect(Collectors.toMap(
                dto -> shopCacheKeyBuilder.buildSkuStockKey(roomId, dto.getSkuId()),
                dto -> dto.getStockNum()
        ));
        redisTemplate.opsForValue().multiSet(saveCacheMap);
        //对命令执行批量过期设置操作
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                for (String redisKey : saveCacheMap.keySet()) {
                    operations.expire((K) redisKey, 1, TimeUnit.MINUTES);
                }
                return null;
            }
        });
        return true;
    }

    @Override
    public Integer queryStockNum(Integer roomId, Long skuId) {
        String cacheKey = shopCacheKeyBuilder.buildSkuStockKey(roomId, skuId);
        Object stockNumObj = redisTemplate.opsForValue().get(cacheKey);
        return stockNumObj == null ? null : Integer.parseInt(stockNumObj.toString());
    }

    @Override
    public boolean syncStockNumToMySQL(Long anchorId) {
        Integer roomId = queryRoomIdByAnchorId(anchorId);
        if (roomId == null) {
            return false;
        }
        List<Long> skuIdList = anchorShopInfoService.querySkuIdByAnchorId(anchorId);
        for (Long skuId : skuIdList) {
            Integer stockNum = this.queryStockNum(roomId, skuId);
            if (stockNum == null){
                continue;
            }
            stockInfoService.updateStockNum(skuId, stockNum);
        }
        return true;
    }

    /** 库存 Key 以直播间 Hash Tag 分片，预热与同步必须先定位主播当前直播间。 */
    private Integer queryRoomIdByAnchorId(Long anchorId) {
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByAnchorId(anchorId);
        return livingRoomRespDTO == null ? null : livingRoomRespDTO.getId();
    }
}
