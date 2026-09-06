# qiyu-live-gift-interface

## 1. 模块定位

`qiyu-live-gift-interface` 是礼物域的 RPC 契约模块。

它只放接口、DTO、枚举和常量，不承载运行时服务。

## 2. 内容范围

- 礼物配置 Dubbo 接口
- 送礼记录 Dubbo 接口
- 礼物相关 DTO

## 3. 关键接口

- `IGiftConfigRpc`
  - `getGiftId(Integer giftId)` — 按礼物 ID 查询配置
  - `queryGiftList()` — 查询所有礼物配置列表
  - `insertGift(GiftConfigDTO)` — 新增礼物配置
  - `updateGift(GiftConfigDTO)` — 更新礼物配置

- `IGiftRecordRpc`
  - `insertOne(GiftRecordDTO)` — 插入单条送礼记录

## 4. 关键 DTO

- `GiftConfigDTO` — 礼物配置 DTO（字段待 gift-provider 补充）
- `GiftRecordDTO` — 送礼记录 DTO（userId、objectId、giftId、source、price、priceUnit、sendTime 等）

## 5. 使用方式

该模块一般被以下模块依赖：

- `qiyu-live-gift-provider`（实现 RPC 接口）
- `qiyu-live-api`（通过 Dubbo 调用礼物服务）
- 其他需要调用礼物服务的业务模块

## 6. 说明

接口模块的原则是只做契约，不写业务逻辑。
如果接口变更，优先同步消费方和 provider 的兼容性。
