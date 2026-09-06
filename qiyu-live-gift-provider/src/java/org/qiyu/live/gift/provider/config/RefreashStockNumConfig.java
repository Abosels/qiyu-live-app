package org.qiyu.live.gift.provider.config;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.ShopCacheKeyBuilder;
import org.qiyu.live.gift.interfaces.ISkuStockInfoRpc;
import org.qiyu.live.gift.provider.service.IAnchorShopInfoService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class RefreashStockNumConfig implements InitializingBean {

    @Resource
    private ISkuStockInfoRpc skuStockInfoRpc;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ShopCacheKeyBuilder shopCacheKeyBuilder;

    private ScheduledThreadPoolExecutor scheduledPool = new ScheduledThreadPoolExecutor(1);

    @Override
    public void afterPropertiesSet() throws Exception {
        //每15刷新一次库存
        scheduledPool.scheduleWithFixedDelay(new RefreshCacheListJob(), 3000, 15000, TimeUnit.MILLISECONDS);
    }

    private class RefreshCacheListJob implements Runnable {

        @Override
        public void run() {
            boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(shopCacheKeyBuilder.buildSkuStockSyncLock(),1,14,TimeUnit.SECONDS);
            if (lockStatus) {
                List<Long> anchorIdList = anchorShopInfoService.queryAllValidAnchorId();
                for (Long anchorId : anchorIdList) {
                    skuStockInfoRpc.syncStockNumToMySQL(anchorId);
                }
            }
        }
    }
}
