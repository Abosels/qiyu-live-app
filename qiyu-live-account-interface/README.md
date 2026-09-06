# qiyu-live-account-interface

## 1. 模块定位

`qiyu-live-account-interface` 是账号域的 RPC 契约模块。

它只定义账号相关接口，不实现业务逻辑。

## 2. 内容范围

- 账号 token 相关 Dubbo 接口
- 账号登录和鉴权契约

## 3. 关键接口

- `IAccountTokenRpc`

## 4. 依赖关系

典型消费方包括：

- `qiyu-live-account-provider`
- `qiyu-live-gateway`
- `qiyu-live-api`

## 5. 说明

该模块适合保持尽可能薄。
所有账号业务逻辑应放在 provider 模块中，接口模块只负责契约稳定。
