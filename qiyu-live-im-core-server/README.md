# qiyu-live-im-core-server

## 1. 模块定位

`qiyu-live-im-core-server` 是 IM 长连接核心服务。

它负责维护 Netty 连接、解析 IM 协议、执行登录鉴权、把用户连接绑定到当前实例，并对外暴露 Dubbo 服务，供路由层把消息定向投递到这台 Core Server。

一句话概括：这个模块解决“用户连上来之后，这条连接在当前 JVM 内如何被管理和使用”。

## 2. 主要职责

- 启动 Netty IM 服务端，监听长连接端口
- 解析 `ImMsg` 协议并分发到不同业务处理器
- 处理登录、心跳、ACK、登出、业务消息
- 维护 `userId -> ChannelHandlerContext` 本地缓存
- 将 `appId:userId -> ip:port` 的在线绑定写入 Redis
- 通过 Dubbo 暴露 `IRouterHandlerRpc`，接收路由层转发请求

## 3. 核心链路

### 3.1 登录链路

1. 客户端发送登录消息
2. `ImServerCoreHandler` 收到并交给 `ImHandlerFactory`
3. `LoginMsgHandlerImpl` 调用 `ImTokenRpc` 做 token 鉴权
4. 鉴权成功后，绑定 `userId`、`appId`、`ChannelHandlerContext`
5. 写入 Redis：

```text
qiyu:live:im:bind:ip:<appId>:<userId> -> <dubboIp>:<dubboPort>
```

### 3.2 路由投递链路

1. `qiyu-live-im-router-provider` 找到目标用户绑定地址
2. Router 通过 `IRouterHandlerRpc` 调用目标 Core Server
3. `RouterHandlerRpcImpl` 进入当前 Core 实例
4. `RouterHandlerServiceImpl` 定位本机连接并写回客户端

### 3.3 断连清理链路

1. Netty 触发连接关闭
2. 本地上下文缓存删除
3. Redis 绑定 key 删除

## 4. 关键类

- `ImCoreServerApplication`
  - Spring Boot 启动入口
- `NettyImServerStarter`
  - 启动 Netty 服务
- `ImServerCoreHandler`
  - Netty 消息统一入口
- `ImHandlerFactoryImpl`
  - 消息码到 Handler 的分发工厂
- `LoginMsgHandlerImpl`
  - 登录鉴权与在线绑定写入
- `RouterHandlerRpcImpl`
  - 对路由层暴露的 Dubbo 服务
- `RouterHandlerServiceImpl`
  - 将消息落到本机连接
- `ChannelHandlerContextCache`
  - 本机连接缓存

## 5. 配置入口

运行时配置以这几个文件为准：

- `src/main/resources/bootstrap.yml`
- `src/main/resources/qiyu-live-im-core-server.yml`

关键配置项：

- `spring.application.name=qiyu-live-im-core-server`
- `server.port=8084`
- `dubbo.protocol.port=9092`
- `qiyu.im.port=18080`
- `QIYU_NACOS_ADDR`
- `DUBBO_IP_TO_REGISTRY`
- `DUBBO_PORT_TO_REGISTRY`
- `QIYU_RMQ_PRODUCER_NAMESRV`
- `QIYU_RMQ_CONSUMER_NAMESRV`

当前本地联调基线：

- HTTP：`8084`
- Dubbo：`9092`
- Netty IM：`18080`
- Dubbo 注册 IP：`172.18.0.1`
- Nacos：`127.0.0.1:8848`
- Redis：`127.0.0.1:6379`
- RocketMQ NameServer：`127.0.0.1:9876`

## 6. 启动命令

建议在仓库根目录执行：

```powershell
$env:JAVA_TOOL_OPTIONS='-Duser.home=D:\myCode\qiyu-live-app\.runtime-home'
$env:QIYU_NACOS_ADDR='127.0.0.1:8848'
$env:QIYU_RMQ_PRODUCER_NAMESRV='127.0.0.1:9876'
$env:QIYU_RMQ_CONSUMER_NAMESRV='127.0.0.1:9876'
$env:QIYU_RMQ_CONSUMER_NAMESERVER='127.0.0.1:9876'
$env:QIYU_DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:QIYU_DUBBO_PORT_TO_REGISTRY='9092'
$env:DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:DUBBO_PORT_TO_REGISTRY='9092'
mvn --% -pl qiyu-live-im-core-server -Dmaven.repo.local=.mvn-local-repo spring-boot:run -DskipTests
```

## 7. 当前已确认修复

- 修复 `ImMsg.build(...)` 里 body 未初始化即取长度导致的空指针
- `LoginMsgHandlerImpl` 改为 `@DubboReference` 注入 `ImTokenRpc`
- 登录成功后按 `appId:userId` 维度写入 Redis 在线绑定
- 修复 `ImServerCoreHandler` 中重复和错误的 `RedisTemplate` 注入
- `pom.xml` 补齐测试依赖与 RocketMQ Starter 依赖
- 补充登录绑定集成测试，覆盖 Redis 写入行为

## 8. 当前联调结论

当前已经确认：

- `qiyu-live-im-provider` 正常暴露 `172.18.0.1:9094`
- `qiyu-live-im-core-server` 正常暴露 `172.18.0.1:9092`
- Router 侧已经能订阅到 Core 的 `172.18.0.1:9092`
- 登录处理测试已经验证 Redis 绑定值写入目标地址

需要特别注意：

- `18080` 目前仍可能被旧 JVM 残留进程占用
- 如果 `8084/9092` 与 `18080` 的 PID 不一致，说明当前 Dubbo 是新进程，但 Netty 端口仍可能属于旧会话

## 9. 手工验收命令

端口检查：

```powershell
netstat -ano | Select-String ':8084|:9092|:18080'
```

正确状态应满足：

- `8084` 与 `9092` PID 相同
- `18080` 也应与它们相同

如果 `18080` PID 不一致，先定位旧进程：

```powershell
tasklist /FI "PID eq <PID>" /V
jcmd -l
jps -lv
```

确认是旧进程后再清理：

```powershell
taskkill /PID <旧PID> /F
```

## 10. 阅读顺序建议

建议按下面顺序阅读：

1. `ImCoreServerApplication`
2. `NettyImServerStarter`
3. `ImServerCoreHandler`
4. `ImHandlerFactoryImpl`
5. `LoginMsgHandlerImpl`
6. `RouterHandlerRpcImpl`
7. `RouterHandlerServiceImpl`

## 11. 相关文档

完整联调手册见：

- `docs/im-local-startup-and-debug.md`
