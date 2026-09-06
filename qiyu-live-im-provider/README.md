# qiyu-live-im-provider

## 1. 模块定位

`qiyu-live-im-provider` 是 IM 领域基础能力 Provider。

它本身不维护长连接，而是通过 Dubbo 对外提供登录 token 校验和在线状态查询能力，供 `qiyu-live-im-core-server` 及其他调用方使用。

一句话概括：这个模块解决“Core Server 登录鉴权时要问谁，以及如何判断用户是否在线”。

## 2. 主要职责

- 生成 IM 登录 token
- 根据 token 反查用户身份
- 查询用户是否已经绑定到某台 IM Core Server
- 通过 Dubbo 暴露 `ImTokenRpc` 和 `ImOnlineRpc`

## 3. 核心链路

### 3.1 Token 生成链路

1. 上游调用 `ImTokenRpc.createImLoginToken`
2. `ImTokenRpcImpl` 转到 `ImTokenServiceImpl`
3. 生成 token 并写入 Redis
4. 设置短期过期时间

### 3.2 Token 校验链路

1. Core Server 调用 `ImTokenRpc.getUserIdByToken`
2. Provider 从 Redis 读取 token 映射
3. 返回对应 `userId`

### 3.3 在线状态链路

1. 上游调用 `ImOnlineRpc.isOnline`
2. Provider 读取 Redis 绑定 key
3. 根据绑定地址是否存在判断在线状态

## 4. 关键类

- `ImProviderApplication`
  - 模块启动入口
- `ImTokenRpcImpl`
  - 登录 token Dubbo Provider
- `ImOnlineRpcImpl`
  - 在线状态查询 Dubbo Provider
- `ImTokenServiceImpl`
  - token 生成、缓存、解析逻辑
- `ImOnlineServiceImpl`
  - 在线状态读取逻辑

## 5. 配置入口

运行时配置以这几个文件为准：

- `src/main/resources/bootstrap.yml`
- `src/main/resources/qiyu-live-im-provider.yaml`

关键配置项：

- `spring.application.name=qiyu-live-im-provider`
- `server.port=8085`
- `dubbo.protocol.port=9094`
- `QIYU_NACOS_ADDR`
- `DUBBO_IP_TO_REGISTRY`
- `DUBBO_PORT_TO_REGISTRY`
- Redis 连接参数

当前本地联调基线：

- HTTP：`8085`
- Dubbo：`9094`
- Dubbo 注册 IP：`172.18.0.1`
- Nacos：`127.0.0.1:8848`
- Redis：`127.0.0.1:6379`

## 6. 启动命令

```powershell
$env:JAVA_TOOL_OPTIONS='-Duser.home=D:\myCode\qiyu-live-app\.runtime-home'
$env:QIYU_NACOS_ADDR='127.0.0.1:8848'
$env:QIYU_DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:QIYU_DUBBO_PORT_TO_REGISTRY='9094'
$env:DUBBO_IP_TO_REGISTRY='172.18.0.1'
$env:DUBBO_PORT_TO_REGISTRY='9094'
mvn --% -pl qiyu-live-im-provider -Dmaven.repo.local=.mvn-local-repo spring-boot:run -DskipTests
```

## 7. 当前已确认修复

- 启动类补齐 `@EnableDubbo`
- `pom.xml` 中错误和重复的接口依赖已修正
- token 反查逻辑不再错误强转为 `Integer`
- 测试和依赖注入问题已清理，当前模块可正常启动并被 Core 调用

## 8. 当前联调结论

当前已经确认：

- Provider 正常启动
- Dubbo 暴露地址为 `172.18.0.1:9094`
- Core 已与 Provider 建立 Dubbo 连接
- Core 登录处理链路使用的 `ImTokenRpc` 注入已恢复正常

## 9. 手工验收命令

端口检查：

```powershell
netstat -ano | Select-String ':8085|:9094'
```

日志关键点：

- 启动日志里应出现 Dubbo 服务导出成功
- Core 启动后，Provider 侧通常可看到来自 Core 的连接建立

如需重点验证 token 测试：

```powershell
mvn --% -pl qiyu-live-im-provider -Dmaven.repo.local=.mvn-local-repo -Dtest=ImTokenServiceImplTest test
```

## 10. 阅读顺序建议

建议按下面顺序阅读：

1. `ImProviderApplication`
2. `ImTokenRpcImpl`
3. `ImTokenServiceImpl`
4. `ImOnlineRpcImpl`
5. `ImOnlineServiceImpl`

## 11. 相关文档

完整联调手册见：

- `docs/im-local-startup-and-debug.md`
