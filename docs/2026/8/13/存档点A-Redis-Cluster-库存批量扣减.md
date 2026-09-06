# 存档点 A：Redis Cluster 库存批量扣减

记录日期：2026-08-13

## 当前状态

直播带货下单已使用 `decrStockNumBatch(Map<Long, Integer>)`。该方法通过单次 Lua 脚本完成多个 SKU 的库存校验与扣减：任意 SKU 库存不足时，所有 SKU 均不扣减。

当前 Redis 为单机部署，现有库存 Key 可以直接执行多 Key Lua。

## 未来迁移 Redis Cluster 的约束

Redis Cluster 中，一次 Lua 脚本涉及的全部 Key 必须位于同一个 Hash Slot，否则会报 `CROSSSLOT Keys in request don't hash to the same slot`。

直播间购物车订单中的商品属于同一个 `roomId`，迁移时库存 Key 应使用 `roomId` 作为 Hash Tag：

```text
qiyu:shop:stock:{room-1001}:10001
qiyu:shop:stock:{room-1001}:10002
```

花括号中的 `room-1001` 是 Redis Cluster 的分片依据。同一直播间的全部商品库存 Key 会落在同一个 Slot，因此可继续执行批量 Lua；不同直播间则可分散到不同节点。

## 迁移时的改造范围

1. 修改 `ShopCacheKeyBuilder` 的库存 Key 构造方法，使其接收 `roomId` 并生成带 `{roomId}` Hash Tag 的 Key。
2. 修改库存预热、查询、单 SKU 扣减、批量扣减、超时回滚的调用点，使其均传入相同的 `roomId`。
3. 检查 `SkuStockInfoRpcImpl`、`SkuStockInfoServiceImpl`、`SkuOrderInfoRPCImpl` 与订单回滚链路，确保同一订单使用完全一致的库存 Key。
4. 在 Redis Cluster 环境执行多 SKU 下单测试，验证成功订单全部扣减、库存不足订单全部不扣减。

## 当前不处理

当前单机 Redis 环境不修改 Key 格式，也不改动现有业务接口；迁移 Redis Cluster 时再按本存档执行。
