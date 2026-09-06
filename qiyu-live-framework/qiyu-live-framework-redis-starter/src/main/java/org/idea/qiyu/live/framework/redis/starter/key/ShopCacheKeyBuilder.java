package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class ShopCacheKeyBuilder extends RedisKeyBuilder {

    private static final String SKU_DETAIL_KEY = "sku_detail_key";
    private static final String SHOP_CART_KEY = "shop_cart_key";
    private static final String SKU_STOCK_KEY = "sku_stock_key";
    private static final String SKU_STOCK_SYNC_LOCK = "sku_stock_sync_lock";
    private static final String SKU_ORDER_KEY = "sku_order_key";
    private static final String SKU_ORDER_INFO_KEY = "sku_order_info_key";


    public String buildSkuDetailKey(Long skuId){
        return super.getPrefix() + SKU_DETAIL_KEY + super.getSplitItem() + skuId;
    }

    public String buildShopCartKey(Long userId, Integer roomId){
        return super.getPrefix() + SHOP_CART_KEY + super.getSplitItem() + userId + super.getSplitItem() +roomId;
    }

    /*
     * 旧库存 Key 未包含 Hash Tag，Redis Cluster 下多 SKU Lua 可能跨 Slot 失败。
     * public String buildSkuStockKey(Long skuId){
     *     return super.getPrefix() + SKU_STOCK_KEY + super.getSplitItem() + skuId;
     * }
     */
    public String buildSkuStockKey(Integer roomId, Long skuId){
        // 同一直播间所有 SKU 使用同一 Hash Tag，保证 Redis Cluster 可执行多 Key Lua。
        return super.getPrefix() + SKU_STOCK_KEY + super.getSplitItem()
                + "{room-" + roomId + "}" + super.getSplitItem() + skuId;
    }

    public String buildSkuStockSyncLock(){
        return super.getPrefix() + SKU_STOCK_SYNC_LOCK + super.getSplitItem();
    }

    public String buildSkuOrderKey(Long userId, Integer roomId){
        return super.getPrefix() + SKU_ORDER_KEY + super.getSplitItem() + userId + super.getSplitItem() +roomId;
    }

    public String buildSkuOrderInfoKey(Integer roomId){
        return super.getPrefix() + SKU_ORDER_INFO_KEY + super.getSplitItem() + roomId;
    }
}
