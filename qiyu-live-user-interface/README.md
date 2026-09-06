# qiyu-live-user-interface

## 1. 模块定位

`qiyu-live-user-interface` 是用户域的 RPC 契约模块。

它只放接口、DTO、枚举和常量，不承载运行时服务。

## 2. 内容范围

- 用户相关 Dubbo 接口
- 用户登录和标签相关 DTO
- 用户标签枚举
- 用户手机号相关 DTO

## 3. 关键接口

- `IUserRpc`
- `IUserTagRpc`
- `IUserPhoneRpc`

## 4. 关键 DTO

- `UserDTO`
- `UserLoginDTO`
- `UserPhoneDTO`
- `UserTagDTO`
- `UserCacheAsyncDesc`
- `MsgCheckDTO`

## 5. 使用方式

该模块一般被以下模块依赖：

- `qiyu-live-user-provider`
- `qiyu-live-api`
- 其他需要调用用户服务的业务模块

## 6. 说明

接口模块的原则是只做契约，不写业务逻辑。
如果接口变更，优先同步消费方和 provider 的兼容性。
