# IM 本地启动、联调与验收手册

## 1. 文档目标

本文档用于指导本地启动并联调以下 3 个 IM 运行模块：

- `qiyu-live-im-provider`
- `qiyu-live-im-core-server`
- `qiyu-live-im-router-provider`

同时覆盖：

- 启动前依赖准备
- 推荐启动顺序
- 本地环境变量约定
- 端口、Nacos、Dubbo、Redis 的检查命令
- `18080` 残留进程的定位与清理办法
- 后续你我继续联调时可直接复用的手工验收命令

## 2. 当前已确认的联调基线

截至本次排查，当前这套本地联调基线如下：

- Nacos 地址：`127.0.0.1:8848`
- Redis 地址：`127.0.0.1:6379`
- RocketMQ NameServer：`127.0.0.1:9876`
- IM Provider Dubbo 暴露地址：`172.18.0.1:9094`
- IM Core Server Dubbo 暴露地址：`172.18.0.1:9092`
- IM Router Provider Dubbo 暴露地址：`172.18.0.1:9095`

当前建议本地统一使用：

```text
DUBBO_IP_TO_REGISTRY=172.18.0.1
```

原因：

- 当前 Dubbo 会把本机可注册地址识别为 `172.18.0.1`
- 使用 `127.0.0.1` 时，`im-core-server` 在 Dubbo 导出阶段会报 `Specified invalid registry ip`
- Router 侧已经实际订阅到 `172.18.0.1:9092`

## 3. 启动前依赖准备

### 3.1 必须可用的依赖

启动 IM 三个模块前，至少保证以下依赖已经就绪：

1. Nacos
2. Redis
3. RocketMQ NameServer
4. RocketMQ Broker
5. MongoDB

### 3.2 基础连通性检查

在仓库根目录执行：

```powershell
Test-NetConnection 127.0.0.1 -Port 8848
Test-NetConnection 127.0.0.1 -Port 6379
Test-NetConnection 127.0.0.1 -Port 9876
Test-NetConnection 127.0.0.1 -Port 27017
```

期望结果：

- `TcpTestSucceeded : True`

### 3.3 Docker 中间件检查

```powershell
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

至少应该看到：

- `rmqnamesrv`
- `rmqbroker`

如果 RocketMQ 未启动，可执行：

```powershell
docker compose -f D:\ProgramData_s\IT\rocketmq\docker-compose.yml up -d
```

## 4. 推荐启动顺序

严格按以下顺序启动：

1. Nacos
2. Redis
3. RocketMQ NameServer
4. RocketMQ Broker
5. `qiyu-live-im-provider`
6. `qiyu-live-im-core-server`
7. `qiyu-live-im-router-provider`

原因：

- `im-core-server` 登录鉴权依赖 `ImTokenRpc`
- `im-core-server` ACK 逻辑依赖 RocketMQ
- `router-provider` 依赖 `core-server` 已注册且 Redis 已写入绑定地址

## 5. 启动前的统一环境变量

建议在每个新终端里先执行一次：

```powershell
$env:JAVA_TOOL_OPTIONS='-Duser.home=D:\myCode\qiyu-live-app\.runtime-home'
$env:QIYU_NACOS_ADDR='127.0.0.1:8848'
$env:QIYU_DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:QIYU_DUBBO_PORT_TO_REGISTRY='9092'
$env:DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:DUBBO_PORT_TO_REGISTRY='9092'
```

说明：

- `JAVA_TOOL_OPTIONS` 用于把运行时缓存收敛到仓库内目录
- `QIYU_NACOS_ADDR` 统一指向本机 Nacos
- `DUBBO_IP_TO_REGISTRY` 必须和实际 Dubbo 暴露地址保持一致

启动不同模块时，只需要按模块改 `DUBBO_PORT_TO_REGISTRY`

## 6. 模块启动命令

### 6.1 启动 IM Provider

```powershell
$env:JAVA_TOOL_OPTIONS='-Duser.home=D:\myCode\qiyu-live-app\.runtime-home'
$env:QIYU_NACOS_ADDR='127.0.0.1:8848'
$env:QIYU_DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:QIYU_DUBBO_PORT_TO_REGISTRY='9094'
$env:DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:DUBBO_PORT_TO_REGISTRY='9094'
mvn --% -pl qiyu-live-im-provider -Dmaven.repo.local=.mvn-local-repo spring-boot:run -DskipTests
```

期望端口：

- HTTP：`8085`
- Dubbo：`9094`

### 6.2 启动 IM Core Server

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

期望端口：

- HTTP：`8084`
- Dubbo：`9092`
- Netty IM：`18080`

### 6.3 启动 IM Router Provider

```powershell
$env:JAVA_TOOL_OPTIONS='-Duser.home=D:\myCode\qiyu-live-app\.runtime-home'
$env:QIYU_NACOS_ADDR='127.0.0.1:8848'
$env:QIYU_DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:QIYU_DUBBO_PORT_TO_REGISTRY='9095'
$env:DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:DUBBO_PORT_TO_REGISTRY='9095'
mvn --% -pl qiyu-live-im-router-provider -Dmaven.repo.local=.mvn-local-repo spring-boot:run -DskipTests
```

期望端口：

- HTTP：`8086`
- Dubbo：`9095`

## 7. 启动后的第一轮端口验收

### 7.1 一次性检查所有关键端口

```powershell
netstat -ano | Select-String ':8084|:8085|:8086|:9092|:9094|:9095|:18080'
```

期望至少看到：

- `8085` 对应 `im-provider`
- `8084`、`9092`、`18080` 对应 `im-core-server`
- `8086`、`9095` 对应 `im-router-provider`

### 7.2 推荐的人工判定规则

如果是一次“干净启动”，理想状态应满足：

- `8084` 和 `9092` 的 PID 相同
- `18080` 的 PID 也应与 `8084/9092` 相同
- `8086` 和 `9095` 的 PID 相同
- `8085` 和 `9094` 的 PID 相同

如果 `18080` 的 PID 单独不同，说明存在历史残留进程。

## 8. 18080 残留进程问题

### 8.1 本次排查结论

本次排查中，出现过如下现象：

- `8084/9092` 属于当前 `im-core-server`
- `18080` 单独挂在另一个 PID 上

这说明：

- 当前活跃的 Spring Boot `core-server` 已正常启动
- 但 Netty 端口 `18080` 可能被旧的 `im-core-server` 或旧的手动启动 JVM 占用
- 这种情况下，Dubbo 联通可能是新的，Netty 长连接却可能仍是旧进程

### 8.2 残留进程定位命令

先看端口与 PID：

```powershell
netstat -ano | Select-String ':18080|:8084|:9092'
```

如果你有系统权限，可继续查：

```powershell
tasklist /FI "PID eq <PID>" /V
```

例如：

```powershell
tasklist /FI "PID eq 14692" /V
tasklist /FI "PID eq 11124" /V
```

如果你有更高权限，也可以尝试：

```powershell
jcmd -l
jps -lv
```

### 8.3 残留进程清理办法

确认旧 PID 后，手工清理：

```powershell
taskkill /PID <旧PID> /F
```

例如：

```powershell
taskkill /PID 14692 /F
```

清理完成后重新确认：

```powershell
netstat -ano | Select-String ':18080|:8084|:9092'
```

然后重新启动 `qiyu-live-im-core-server`。

### 8.4 清理后的正确状态

重新启动成功后，应满足：

- `8084`
- `9092`
- `18080`

这 3 个端口对应同一个 PID。

## 9. Nacos 注册检查

### 9.1 当前本机 Nacos 的特殊情况

当前本机 `8848` 上的 Nacos 对旧接口：

```text
/nacos/v1/ns/instance/list
```

返回 `501 Not Implemented`。

因此本地检查不要依赖旧 REST 接口，优先使用：

1. Nacos 控制台
2. 服务启动日志
3. Dubbo 实际建连日志

### 9.2 控制台检查

浏览器打开：

- [http://127.0.0.1:8848/nacos](http://127.0.0.1:8848/nacos)

登录后重点检查这 3 个服务名：

- `qiyu-live-im-provider`
- `qiyu-live-im-core-server`
- `qiyu-live-im-router-provider`

### 9.3 日志侧关键字

#### IM Provider

日志中应出现：

```text
nacos registry, DEFAULT_GROUP qiyu-live-im-provider ... register finished
Register: dubbo://172.18.0.1:9094/org.qiyu.live.im.interfaces.ImTokenRpc
```

#### IM Core Server

日志中应出现：

```text
nacos registry, DEFAULT_GROUP qiyu-live-im-core-server ... register finished
Register: dubbo://172.18.0.1:9092/org.qiyu.live.im.core.server.interfaces.rpc.IRouterHandlerRpc
```

#### IM Router Provider

日志中应出现：

```text
nacos registry, DEFAULT_GROUP qiyu-live-im-router-provider ... register finished
Register: dubbo://172.18.0.1:9095/org.qiyu.live.im.router.interfaces.rpc.ImRouterRpc
```

## 10. Dubbo 暴露与消费检查

### 10.1 Core 暴露地址

当前确认的 Core 暴露地址应为：

```text
172.18.0.1:9092
```

日志关键字：

```text
Registered dubbo service ... IRouterHandlerRpc url dubbo://172.18.0.1:9092
```

### 10.2 Provider 暴露地址

当前确认的 IM Provider 暴露地址应为：

```text
172.18.0.1:9094
```

### 10.3 Router 暴露地址

当前确认的 Router 暴露地址应为：

```text
172.18.0.1:9095
```

### 10.4 Router 是否订阅到 Core

Router 日志中应出现：

```text
Available Invokers : 172.18.0.1:9092
Successfully connect to server /172.18.0.1:9092
```

这说明 Router 已经读到了 Core 的 Dubbo 暴露地址。

## 11. Redis 绑定地址检查

### 11.1 正确的绑定 key 规则

当前项目中已经统一为：

```text
qiyu:live:im:bind:ip:<appId>:<userId>
```

例如：

```text
qiyu:live:im:bind:ip:10001:10001
```

### 11.2 正确的 value 规则

Redis 中存放的 value 应为：

```text
<core Dubbo IP>:<core Dubbo Port>
```

在当前联调环境里应为：

```text
172.18.0.1:9092
```

### 11.3 手工查询命令

如果本机安装了 `redis-cli`：

```powershell
redis-cli -h 127.0.0.1 -p 6379 GET qiyu:live:im:bind:ip:10001:10001
```

如果 Redis 在 Docker 容器里且容器名明确，可用：

```powershell
docker exec -it <redis容器名> redis-cli GET qiyu:live:im:bind:ip:10001:10001
```

### 11.4 当前已做的真实验证

已通过 `LoginMsgHandlerImplIntegrationTest` 验证：

- 登录成功时会真实向 Redis 写入绑定 key
- 写入值为 `ChannelHandlerContextCache.getServerIpAddress()`
- 当前测试样本写入值为 `172.18.0.1:9092`

## 12. Router 读取地址与 Core 暴露地址一致性检查

### 12.1 一致性要求

以下 3 处必须完全一致：

1. `im-core-server` Dubbo 实际暴露地址
2. Redis `qiyu:live:im:bind:ip:<appId>:<userId>` 的 value
3. `router-provider` 实际读到并用于路由的地址

### 12.2 当前联调结论

当前已经确认：

- Core 暴露地址：`172.18.0.1:9092`
- Redis 绑定值写入目标：`172.18.0.1:9092`
- Router 订阅到的 Core Provider：`172.18.0.1:9092`

因此当前链路在地址一致性上是对齐的。

## 13. 推荐手工验收清单

### 13.1 第一阶段：依赖验收

依次执行：

```powershell
Test-NetConnection 127.0.0.1 -Port 8848
Test-NetConnection 127.0.0.1 -Port 6379
Test-NetConnection 127.0.0.1 -Port 9876
Test-NetConnection 127.0.0.1 -Port 27017
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

通过标准：

- `8848`、`6379`、`9876`、`27017` 都返回 `TcpTestSucceeded : True`
- Docker 中至少能看到可用的 RocketMQ NameServer 和 Broker

如需进一步确认 RocketMQ 容器状态，可补充：

```powershell
docker logs --tail 50 rmqnamesrv
docker logs --tail 50 rmqbroker
```

### 13.2 第二阶段：模块启动验收

启动 3 个模块后执行：

```powershell
netstat -ano | Select-String ':8084|:8085|:8086|:9092|:9094|:9095|:18080'
```

人工确认：

- `8085/9094` 存在
- `8084/9092/18080` 存在
- `8086/9095` 存在

进一步确认 PID 归属：

```powershell
netstat -ano | Select-String ':8084|:9092|:18080'
netstat -ano | Select-String ':8085|:9094'
netstat -ano | Select-String ':8086|:9095'
```

通过标准：

- `8085` 与 `9094` 的 PID 相同
- `8086` 与 `9095` 的 PID 相同
- 理想情况下 `8084`、`9092`、`18080` 的 PID 也应相同
- 如果 `18080` 单独挂在别的 PID，上一次残留 JVM 尚未清理干净

### 13.3 第三阶段：Nacos 与 Dubbo 验收

当前本机 Nacos 旧 REST 接口返回 `501`，因此这里以控制台和启动日志为准。

先打开：

- [http://127.0.0.1:8848/nacos](http://127.0.0.1:8848/nacos)

再人工查看日志或控制台，确认：

1. `qiyu-live-im-provider` 已注册
2. `qiyu-live-im-core-server` 已注册
3. `qiyu-live-im-router-provider` 已注册
4. Router 已能连到 `172.18.0.1:9092`
5. Core 已能连到 `172.18.0.1:9094`

建议在各自启动窗口里重点搜索这些关键字：

```text
Started
Dubbo protocol
Register
Available Invokers
172.18.0.1:9092
172.18.0.1:9094
```

通过标准：

- Provider 日志中能看到 `9094` 暴露
- Core 日志中能看到 `9092` 暴露
- Router 日志中能看到 `Available Invokers : 172.18.0.1:9092`

### 13.4 第四阶段：Redis 绑定验收

先获取一个 IM 登录 token，再触发一次 IM 登录，随后执行：

```powershell
redis-cli -h 127.0.0.1 -p 6379 GET qiyu:live:im:bind:ip:<appId>:<userId>
```

如果本机没有 `redis-cli`，改用 Docker 容器执行：

```powershell
docker exec -it <redis容器名> redis-cli GET qiyu:live:im:bind:ip:<appId>:<userId>
```

期望结果：

```text
172.18.0.1:9092
```

### 13.5 第五阶段：最终一致性验收

人工对比以下三项：

1. Core 日志中的 Dubbo 暴露地址
2. Redis 绑定 value
3. Router 日志中的 `Available Invokers`

三者必须一致。

附加检查建议：

1. 如果 Redis 值是 `127.0.0.1:9092`，而 Core 实际暴露是 `172.18.0.1:9092`，路由会失败
2. 如果 Router 日志里能看到 Core Provider，但 Redis 读到空值，说明登录绑定未写入
3. 如果 Redis 值正确但 Router 仍未命中，多半是自定义 Cluster 路由匹配条件不一致

## 14. 推荐回归测试命令

### 14.1 本次补的最小必要测试

```powershell
mvn --% -pl qiyu-live-im-core-server,qiyu-live-im-router-provider -Dmaven.repo.local=.mvn-local-repo -Dtest=LoginMsgHandlerImplIntegrationTest,ImRouterServiceImplTest,IRouterHandlerRpcTest test
```

### 14.2 IM 三模块联动测试

```powershell
mvn --% -pl qiyu-live-im-core-server,qiyu-live-im-provider,qiyu-live-im-router-provider -am test
```

## 15. 后续你我联调时的建议操作方式

以后继续联调时，建议固定按下面节奏走：

1. 先执行第 3 章依赖检查命令
2. 再执行第 6 章模块启动命令
3. 再执行第 7 章端口检查命令
4. 再执行第 9、10、11、12 章的注册与地址一致性检查
5. 如果 `18080` PID 异常，先按第 8 章清残留，再重启 `core-server`

这样做的好处是：

- 每次都能快速判断问题是在依赖层、注册层、路由层还是长连接层
- 避免再出现“Dubbo 是新进程，18080 是旧进程”的混合状态
- 便于你我后续直接复用同一套命令继续推进项目未完成部分

## 16. 下一轮手工验收可直接复制的命令清单

### 16.1 依赖可达性

```powershell
Test-NetConnection 127.0.0.1 -Port 8848
Test-NetConnection 127.0.0.1 -Port 6379
Test-NetConnection 127.0.0.1 -Port 9876
Test-NetConnection 127.0.0.1 -Port 27017
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

### 16.2 启动 IM Provider

```powershell
$env:JAVA_TOOL_OPTIONS='-Duser.home=D:\myCode\qiyu-live-app\.runtime-home'
$env:QIYU_NACOS_ADDR='127.0.0.1:8848'
$env:QIYU_DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:QIYU_DUBBO_PORT_TO_REGISTRY='9094'
$env:DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:DUBBO_PORT_TO_REGISTRY='9094'
mvn --% -pl qiyu-live-im-provider -Dmaven.repo.local=.mvn-local-repo spring-boot:run -DskipTests
```

### 16.3 启动 IM Core Server

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

### 16.4 启动 IM Router Provider

```powershell
$env:JAVA_TOOL_OPTIONS='-Duser.home=D:\myCode\qiyu-live-app\.runtime-home'
$env:QIYU_NACOS_ADDR='127.0.0.1:8848'
$env:QIYU_DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:QIYU_DUBBO_PORT_TO_REGISTRY='9095'
$env:DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:DUBBO_PORT_TO_REGISTRY='9095'
mvn --% -pl qiyu-live-im-router-provider -Dmaven.repo.local=.mvn-local-repo spring-boot:run -DskipTests
```

### 16.5 启动后统一检查端口

```powershell
netstat -ano | Select-String ':8084|:8085|:8086|:9092|:9094|:9095|:18080'
```

### 16.6 单独定位 18080 残留进程

```powershell
netstat -ano | Select-String ':18080|:8084|:9092'
tasklist /FI "PID eq <PID>" /V
jcmd -l
jps -lv
```

### 16.7 查询 Redis 在线绑定

```powershell
redis-cli -h 127.0.0.1 -p 6379 GET qiyu:live:im:bind:ip:<appId>:<userId>
```

或：

```powershell
docker exec -it <redis容器名> redis-cli GET qiyu:live:im:bind:ip:<appId>:<userId>
```

### 16.8 运行当前最小回归测试

```powershell
mvn --% -pl qiyu-live-im-core-server,qiyu-live-im-router-provider -Dmaven.repo.local=.mvn-local-repo -Dtest=LoginMsgHandlerImplIntegrationTest,ImRouterServiceImplTest,IRouterHandlerRpcTest test
```

### 16.9 运行 IM 三模块全量测试

```powershell
mvn --% -pl qiyu-live-im-core-server,qiyu-live-im-provider,qiyu-live-im-router-provider -am test
```
