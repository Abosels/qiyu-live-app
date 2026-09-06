# qiyu-live-id-generate-interface

## 1. 模块定位

`qiyu-live-id-generate-interface` 是 ID 生成域的 RPC 契约模块。

它定义了 ID 生成服务的接口、DTO 和枚举。

## 2. 内容范围

- ID 生成 Dubbo 接口
- ID 生成请求 / 响应 DTO
- ID 类型枚举

## 3. 关键类

- `IdGenerateRpc`
- `IdGenerateDTO`
- `IdTypeEnum`

## 4. 使用方式

通常被以下模块依赖：

- `qiyu-live-id-generate-provider`
- 任何需要统一生成全局 ID 的业务模块

## 5. 说明

这个模块只承载契约，不写任何生成算法和数据库逻辑。
