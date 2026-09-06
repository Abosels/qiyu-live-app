package org.qiyu.live.gift.interfaces;

public interface ISkuStockInfoRpc {

    /**
     * 根据skuId扣减库存
     *
     * @param skuId
     * @param num
     * @return
     */
    boolean decrStockNumBySkuId(Integer roomId, Long skuId, Integer num);

    //1. 库存值同步到redis中
    /**
     * 预热库存信息
     * @param anchorId
     * @return
     */
    boolean prepareStockInfo(Long anchorId);

    //2. 提供基础的缓存查询接口

    /**
     * 基础的缓存查询接口
     * @param skuId
     * @return
     */
    Integer queryStockNum(Integer roomId, Long skuId);
    /**
     * 缓存破解 设计到lua
     */

    /**
     * 同步库存数据到MySQL
     * @param anchorId
     * @return
     */
    boolean syncStockNumToMySQL(Long anchorId);

    //3. 设计一个接口用于同步redis值到mysql中，(定时任务执行，本地定时任务去完成同步行为)


    //4. 库存扣减设计lua脚本

    //5. 库存扣减成功后，生成待支付订单(MQ)
}
