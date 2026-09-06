# qiyu-live-common-interfacce

## 1. 模块定位

`qiyu-live-common-interfacce` 是项目级公共常量和工具模块。

它为多个业务域提供可复用的枚举、Topic 名称和通用工具类。

## 2. 内容范围

- 通用状态枚举
- Gateway 请求头相关枚举
- MQ Topic 常量
- 字节、IP、Bean 转换工具

## 3. 关键类

- `CommonStatusEnum`
- `GatewayHeaderEnum`
- `ImCoreServerProviderTopicNames`
- `UserProviderTopicNames`
- `ConvertBeanUtils`
- `AESUtils`
- `IpLogConversionRule`

## 4. 使用方式

这个模块通常被以下模块依赖：

- `qiyu-live-api`
- `qiyu-live-gateway`
- `qiyu-live-user-provider`
- `qiyu-live-account-provider`
- `qiyu-live-msg-provider`
- `qiyu-live-im-provider`
- `qiyu-live-im-core-server`

## 5. 说明

公共模块的原则是：

- 只放跨模块会重复使用的内容
- 不放具体业务实现
- 不把和单一服务强相关的东西混进来
