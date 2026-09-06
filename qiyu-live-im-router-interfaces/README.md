# qiyu-live-im-router-interfaces

## 1. 模块定位

`qiyu-live-im-router-interfaces` 是 IM 路由层的对外契约模块，用来定义“发送 IM 消息”这一类路由能力的统一接口。

它的作用和其他 `*-interfaces` 模块一致：只负责协议，不负责实现。

## 2. 模块职责

- 定义路由层 RPC 接口
- 为上层业务服务提供统一的消息投递入口

## 3. 核心接口

- `org.qiyu.live.im.router.interfaces.rpc.ImRouterRpc`
  - 接收 `ImMsgBody`
  - 返回消息是否已被成功投递到目标路由流程

这里的“成功”更偏向路由链路是否走通，不一定等价于客户端已经成功消费消息。

## 4. 调用关系

典型链路如下：

1. 上游业务服务构造 `ImMsgBody`
2. 调用 `ImRouterRpc.sendMsg`
3. 路由层根据 Redis 中的用户在线绑定信息定位目标 Core Server
4. 再通过 `qiyu-live-im-core-server-interfaces` 中定义的 RPC 把消息打到目标实例

## 5. 设计原则

- 接口签名保持简洁
- 路由层自身的实现细节不要泄漏到接口层
- 只暴露对外必要能力

## 6. 阅读顺序建议

1. `ImRouterRpc`
2. `qiyu-live-im-router-provider` 中的 `IRouterHandlerRpc`
3. `qiyu-live-im-router-provider` 中的 `ImRouterServiceImpl`

这样更容易理解从“路由接口”到“落到指定 Core Server”的完整链路。
