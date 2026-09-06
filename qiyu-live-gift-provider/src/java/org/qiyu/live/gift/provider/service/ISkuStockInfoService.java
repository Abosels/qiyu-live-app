package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.RollBackStockDTO;
import org.qiyu.live.gift.provider.dao.po.SkuStockInfoPO;

import java.util.List;
import java.util.Map;

public interface ISkuStockInfoService {

    /**
     * 更新缓存
     * @param skuId
     * @param num
     * @return
     */
    boolean updateStockNum(Long skuId, Integer num);

    /**
     * 根据skuId扣减库存
     *
     * @param skuId
     * @param num
     * @return
     */
    boolean decrStockNumBySkuId(Integer roomId, Long skuId, Integer num);

    /**
     * 原子预扣多个 SKU 库存，任意一个 SKU 库存不足时全部不扣减。
     *
     * @param skuCountMap key 为 SKU，value 为购买数量
     * @return 全部扣减成功返回 true
     */
    boolean decrStockNumBatch(Integer roomId, Map<Long, Integer> skuCountMap);

    /**
     * 回补 Redis 中已预扣的库存。
     *
     * @param skuId 商品 SKU
     * @param num 回补数量
     */
    void incrStockNumBySkuId(Integer roomId, Long skuId, Integer num);

    /**
     * 根据skuId查询库存信息
     * @param skuId
     * @return
     */
    SkuStockInfoPO queryBySkuId(Long skuId);

    /**
     * 批量sku信息查询
     *
     * @param skuIds
     * @return
     */
    List<SkuStockInfoPO> queryBySkuIds(List<Long> skuIds);

    /**
     * 处理库存回滚逻辑
     *
     * @param rollBackStockDTO
     */
    void rollBackStockHandler(RollBackStockDTO rollBackStockDTO);
}
