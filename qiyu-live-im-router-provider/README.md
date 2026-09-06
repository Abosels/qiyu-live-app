# qiyu-live-im-router-provider

## 1. 模块定位

`qiyu-live-im-router-provider` 是 IM 路由服务。

它不维护客户端连接，而是负责读取 Redis 中的在线绑定地址，并通过自定义 Dubbo Cluster 把请求精准打到目标 `qiyu-live-im-core-server` 实例。

一句话概括：这个模块解决“这条消息应该被路由到哪一台 Core Server”。

## 2. 主要职责

- 对上游暴露消息路由 RPC
- 读取 Redis 中的用户绑定地址
- 把目标地址写入 `RpcContext`
- 基于自定义 Dubbo Cluster 选择目标 Core Server
- 调用 `IRouterHandlerRpc` 将消息投递给目标 Core

## 3. 核心链路

### 3.1 路由消息链路

1. 上游调用路由 RPC
2. `ImRouterServiceImpl` 查询 Redis：

```text
qiyu:live:im:bind:ip:<appId>:<userId>
```

3. 取得目标地址，例如 `172.18.0.1:9092`
4. 把地址放入 `RpcContext`
5. `ImRouterClusterInvoker` 从当前可用 Invoker 中选出目标 Core
6. 由 `IRouterHandlerRpc` 完成实际消息投递

## 4. 关键类

- `ImRouterProviderApplication`
  - 模块启动入口
- `org.qiyu.live.im.router.provider.rpc.IRouterHandlerRpc`
  - 对外路由 RPC 入口实现
- `ImRouterServiceImpl`
  - 路由核心逻辑
- `ImRouterCluster`
  - 自定义 Dubbo Cluster
- `ImRouterClusterInvoker`
  - 按地址选择目标 Provider

## 5. 配置入口

运行时配置以这几个文件为准：

- `src/main/resources/bootstrap.yml`
- `src/main/resources/qiyu-live-im-router-provider.yml`

历史遗留说明：

- `src/main/resources/META-INF/maven/bootstrap.yml` 不是 Spring Boot 生效配置
- 该文件当前只保留“废弃说明”，用于防止后续再次误放运行配置

关键配置项：

- `spring.application.name=qiyu-live-im-router-provider`
- `server.port=8086`
- `dubbo.protocol.port=9095`
- `dubbo.consumer.cluster=imRouter`
- `QIYU_NACOS_ADDR`
- `DUBBO_IP_TO_REGISTRY`
- `DUBBO_PORT_TO_REGISTRY`

当前本地联调基线：

- HTTP：`8086`
- Dubbo：`9095`
- Router 消费的 Core Dubbo 地址：`172.18.0.1:9092`
- Nacos：`127.0.0.1:8848`
- Redis：`127.0.0.1:6379`

## 6. 启动命令

```powershell
$env:JAVA_TOOL_OPTIONS='-Duser.home=D:\myCode\qiyu-live-app\.runtime-home'
$env:QIYU_NACOS_ADDR='127.0.0.1:8848'
$env:QIYU_DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:QIYU_DUBBO_PORT_TO_REGISTRY='9095'
$env:DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:DUBBO_PORT_TO_REGISTRY='9095'
mvn --% -pl qiyu-live-im-router-provider -Dmaven.repo.local=.mvn-local-repo spring-boot:run -DskipTests
```

## 7. 当前已确认修复

- 修复 Dubbo SPI 文件路径，当前生效文件为：
  - `src/main/resources/META-INF/dubbo/org.apache.dubbo.rpc.cluster.Cluster`
- 启动类与 `main` 入口已恢复正常
- `RpcContext` 路由 key 已统一
- 路由 RPC 实现不再固定返回 `false`
- `pom.xml` 已补齐 Spring Boot、Dubbo、Nacos、测试依赖

## 8. 当前联调结论

当前已经确认：

- Router 正常启动
- Router 暴露 Dubbo 地址为 `172.18.0.1:9095`
- Router 已订阅到 Core 的 `172.18.0.1:9092`
- Router 与 Core 已建立实际 Dubbo 连接

路由能否真正命中目标实例，最终取决于两件事是否一致：

- Core 写入 Redis 的绑定地址
- Core 实际暴露的 Dubbo 地址

## 9. 手工验收命令

端口检查：

```powershell
netstat -ano | Select-String ':8086|:9095'
```

关注日志中的关键字：

- `Available Invokers`
- `172.18.0.1:9092`

如果要跑当前最小测试集：

```powershell
mvn --% -pl qiyu-live-im-router-provider -Dmaven.repo.local=.mvn-local-repo -Dtest=ImRouterServiceImplTest,IRouterHandlerRpcTest test
```

## 10. 阅读顺序建议

建议按下面顺序阅读：

1. `ImRouterProviderApplication`
2. `org.qiyu.live.im.router.provider.rpc.IRouterHandlerRpc`
3. `ImRouterServiceImpl`
4. `ImRouterCluster`
5. `ImRouterClusterInvoker`

## 11. 相关文档

完整联调手册见：

- `docs/im-local-startup-and-debug.md`
