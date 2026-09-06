# qiyu-live-im-interface

## 1. 模块定位

`qiyu-live-im-interface` 是 IM 领域的公共接口模块，负责沉淀 IM 业务对外暴露的 RPC 契约、消息 DTO 和通用常量。

这个模块本身不承载运行逻辑，不依赖具体的 Spring Boot Provider 启动过程，主要作用是给以下模块提供稳定协议：

- `qiyu-live-im-provider`
- `qiyu-live-im-core-server`
- `qiyu-live-im-router-provider`
- 未来需要调用 IM 能力的其他服务

## 2. 模块职责

- 定义 IM 登录 token 相关 RPC 接口
- 定义在线状态查询 RPC 接口
- 定义 IM 消息体 DTO
- 定义 IM 消息码、应用标识、协议常量

这个模块的边界非常清晰：只放“协议层”内容，不放业务实现，不放缓存逻辑，不放启动配置。

## 3. 核心代码

### 3.1 RPC 接口

- `org.qiyu.live.im.interfaces.ImTokenRpc`
  - 创建 IM 登录 token
  - 根据 token 反查用户 ID

- `org.qiyu.live.im.interfaces.ImOnlineRpc`
  - 查询某个用户在指定 `appId` 下是否在线

### 3.2 DTO

- `org.qiyu.live.im.dto.ImMsgBody`
  - IM 业务消息载体
  - 会在 `router-provider`、`core-server` 之间传递

### 3.3 常量

- `org.qiyu.live.im.contants.ImMsgCodeEnum`
  - IM 协议消息类型
- `org.qiyu.live.im.contants.ImContants`
  - IM 相关公共常量
- `org.qiyu.live.im.contants.AppIdEnum`
  - 业务应用标识枚举

## 4. 依赖关系

### 4.1 被哪些模块依赖

- `qiyu-live-im-provider`
- `qiyu-live-im-core-server`
- `qiyu-live-im-router-provider`

### 4.2 自身设计原则

- 不引入 Provider 级别的实现依赖
- 不依赖 Redis、Nacos、RocketMQ
- 尽量保持 DTO 和接口签名稳定

## 5. 设计建议

后续如果要继续扩展 IM 领域能力，建议遵守下面的拆分规则：

- 只要是“跨服务调用契约”，放到这个模块
- 只要是“服务内部实现”，不要放到这个模块
- 只要接口一旦发布就可能被多个模块依赖，优先考虑向后兼容

## 6. 阅读顺序建议

建议按下面顺序阅读：

1. `ImMsgCodeEnum`
2. `ImMsgBody`
3. `ImTokenRpc`
4. `ImOnlineRpc`

看完这四部分，基本就能理解 IM 上层 Provider 和 Core Server 之间交换的最小协议集合。
