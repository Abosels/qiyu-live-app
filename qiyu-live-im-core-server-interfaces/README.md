# qiyu-live-im-core-server-interfaces

## 1. 模块定位

`qiyu-live-im-core-server-interfaces` 是 IM Core Server 对外暴露的接口契约模块，主要为路由层或其他服务调用 Core Server 提供统一的 RPC 契约与常量。

这个模块的核心目的，是把 `core-server` 的“远程可调用能力”从运行实现里拆出来，避免其他模块直接依赖 `qiyu-live-im-core-server` 实现包。

## 2. 模块职责

- 定义路由层调用 Core Server 的 RPC 接口
- 定义 Core Server 共享常量

## 3. 核心代码

### 3.1 RPC 接口

- `org.qiyu.live.im.core.server.interfaces.rpc.IRouterHandlerRpc`
  - 路由层调用 Core Server 时使用
  - 典型用途是把业务消息投递到指定用户所在的 Core Server 实例

### 3.2 常量

- `org.qiyu.live.im.core.server.interfaces.contants.ImCoreServerConstants`
  - 保存 Core Server 与其他模块共享的 Redis key 前缀等常量

## 4. 依赖关系

### 4.1 谁依赖它

- `qiyu-live-im-core-server`
- `qiyu-live-im-router-provider`
- 未来任何要远程调用 Core Server 的模块

### 4.2 为什么要单独拆模块

如果不拆接口模块，`router-provider` 想调用 `core-server` 时只能直接依赖 `core-server` 实现模块，这会带来两个问题：

- 模块边界不清晰
- 运行实现和接口契约耦合过重

所以这里采用“接口层单独下沉”的方式更合理。

## 5. 设计原则

- 只放契约，不放实现
- 常量尽量稳定，避免频繁变更
- 远程接口方法签名要明确，避免把 Provider 内部对象泄漏出去

## 6. 阅读顺序建议

1. `ImCoreServerConstants`
2. `IRouterHandlerRpc`

看完之后再去读 `qiyu-live-im-core-server` 的 `RouterHandlerRpcImpl`，会更容易理解路由层是怎么把消息转发进 Core Server 的。
