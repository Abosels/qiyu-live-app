# change1: IM 登录登出与 WebSocket 链路排错修复

> 日期：2026-07-21
> 依据文档：`Claude_Code_IM登录登出排错与修复方案.md`

---

## 一、根因清单（按严重程度排序）

| # | 文件 | 行号 | 根因 | 影响 | 修复方式 |
|---|------|------|------|------|----------|
| 1 | `WsSharkHandler.java` | 84, 87 | `paramArr` 变量未定义，直接使用 `paramArr[3]`、`paramArr[4]` | **编译失败** | 用 `QueryStringDecoder` 按参数名解析 URL，废弃 paramArr |
| 2 | `WsSharkHandler.java` | 27, 40, 75 | `@ChannelHandler.Sharable` + 成员变量 `webSocketServerHandshaker` | 并发连接互相覆盖，握手混乱 | 改为方法局部变量 |
| 3 | `WsSharkHandler.java` | 83 | 同步判断 `channelFuture.isSuccess()` | 可能在握手完成前绑定登录状态 | 改用 `addListener` 异步回调 |
| 4 | `WsSharkHandler.java` | 4 | `import com.alibaba.fastjson.JSON`（v1），其余文件均用 fastjson2 | JSON 库版本不一致 | 统一为 `com.alibaba.fastjson2.JSON` |
| 5 | `WsSharkHandler.java` | 62-67 | `substring`/`replaceAll` 链式解析 URI，依赖参数位置 | 参数顺序变化即 NPE/StringIndexOutOfBounds | 改用 `QueryStringDecoder` 按名称解析 |
| 6 | `WsSharkHandler.java` | 67 | Token 中截取 appId 依赖 `%` 分隔符 | Token 格式变化即崩溃 | 保留逻辑+增加容错（无%时用备用值） |
| 7 | `LogoutMsgHandlerImpl.java` | 70, 77 | 双重 `writeAndFlush(respMsg)`，第一次无 CLOSE 监听器 | 响应发送两次；可能在发送完成前关闭连接 | 删第一次，仅保留带 `CLOSE` 的第二次 |
| 8 | `LogoutMsgHandlerImpl.java` | 101 | `.getBytes()` 未指定字符集 | 跨环境乱码风险 | 改为 `.getBytes(StandardCharsets.UTF_8)` |
| 9 | `LoginMsgHandlerImpl.java` | 53 | `new String(body)` 未指定字符集 | 跨环境乱码风险 | 改为 `new String(body, StandardCharsets.UTF_8)` |
| 10 | `LoginMsgHandlerImpl.java` | 146 | MQ 发送失败后 `throw new RuntimeException(e)` | 已成功的登录连接被误关闭 | 仅记录日志，不重新抛出 |
| 11 | `LogoutMsgHandlerImpl.java` | 107 | 同上，MQ 发送失败后重新抛出 | 登出清理流程被中断 | 仅记录日志，不重新抛出 |
| 12 | `ImMsgBody.java` | 16-17 | `appId` 为 `int`、`userId` 为 `long`（基本类型） | `appId < 10000` 拆箱时空指针风险 | 改为 `Integer`/`Long` 包装类型 |
| 13 | `ImMsg.java` | 31 | `data.getBytes()` 未指定字符集 | 跨环境乱码风险 | 改为 `data.getBytes(StandardCharsets.UTF_8)` |
| 14 | `WslmServerCoreHandler.java` | 65 | `.getBytes()` 未指定字符集 | 跨环境乱码风险 | 改为 `.getBytes(StandardCharsets.UTF_8)` |
| 15 | `APILivingRoomServiceImpl.java` | 57 | `setAnchor(String)` 传入 String 给 boolean 参数 | 编译失败 | 旧代码注释保留，改为 `setAnchor(false)` |
| — | `LivingProviderCacheKeyBuilder.java` | 12, 28 | 预存语法错误：逗号代分号、`s+roomId` 拼写 | 编译失败（非本次引入） | 修复为 `;` 和 `+ roomId` |

---

## 二、修改文件清单

### 主修改（6 个文件）

| 文件 | 改动说明 |
|------|----------|
| `qiyu-live-im-core-server/.../ws/WsSharkHandler.java` | **完全重写 `handlerHttpRequest()`**：QueryStringDecoder 解析参数、handshaker 改为局部变量、addListener 异步回调、Fastjson v1→v2、旧代码完整注释保留 |
| `qiyu-live-im-core-server/.../impl/LoginMsgHandlerImpl.java` | UTF-8 修复（`new String(body)`）、MQ 异常不再传播、清理未使用的 `JSONObject` import |
| `qiyu-live-im-core-server/.../impl/LogoutMsgHandlerImpl.java` | 删除重复 `writeAndFlush`、UTF-8 修复（`.getBytes()`）、MQ 异常不再传播、清理 5 个未使用的 import |
| `qiyu-live-im-interface/.../dto/ImMsgBody.java` | `int appId` → `Integer appId`、`long userId` → `Long userId` |
| `qiyu-live-im-core-server/.../common/ImMsg.java` | `data.getBytes()` → `data.getBytes(StandardCharsets.UTF_8)`、添加 import |
| `qiyu-live-im-core-server/.../ws/WslmServerCoreHandler.java` | `.getBytes()` → `.getBytes(StandardCharsets.UTF_8)`、添加 import |

### 附带修复（2 个文件，预存编译错误）

| 文件 | 改动说明 |
|------|----------|
| `qiyu-live-framework/.../key/LivingProviderCacheKeyBuilder.java` | 修复预存语法错误：行12 逗号→分号、行28 `s+roomId` → `+ roomId` |
| `qiyu-live-api/.../impl/APILivingRoomServiceImpl.java` | 修复预存编译错误：行57 `setAnchor(String)` → `setAnchor(false)`，旧代码注释保留 |

---

## 三、保留的旧代码

### 注释保留

**WsSharkHandler.java** — 完整旧实现在文件末尾以多行注释保留：

```java
/*
 * [旧代码保留] ============================================================
 * 以下是原 handlerHttpRequest() 方法的完整实现，因存在以下问题而废弃：
 *
 * 1. paramArr 变量未定义（编译错误）
 * 2. 使用 substring/replaceAll 按位置解析 URI，参数顺序变化即崩溃
 * 3. 同步判断 channelFuture.isSuccess()，可能在握手完成前执行登录绑定
 * 4. 成员变量 webSocketServerHandshaker 在 @Sharable 下并发不安全
 * 5. 使用 fastjson v1 (com.alibaba.fastjson.JSON)
 * ...
 * =========================================================================
 */
```

**WsSharkHandler.java** — `webSocketServerHandshaker` 成员变量：
```java
/*
 * [旧代码保留] 原成员变量：
 *   private WebSocketServerHandshaker webSocketServerHandshaker;
 * 问题：该类标注 @ChannelHandler.Sharable，多个连接共享同一个 Handler 实例。
 * 现已改为 handlerHttpRequest() 方法内的局部变量。
 */
```

**WsSharkHandler.java** — `CloseWebSocketFrame` 处理：
```java
/*
 * [旧代码保留] 原关闭帧处理使用成员变量 webSocketServerHandshaker：
 *   if(msg instanceof CloseWebSocketFrame){
 *       webSocketServerHandshaker.close(...);
 *       return;
 *   }
 * CloseWebSocketFrame 本身携带关闭状态，Netty 会自动处理。
 */
```

**APILivingRoomServiceImpl.java** — `setAnchor` 编译错误行：
```java
/*
 * [旧代码保留] 原代码将 String 传给 boolean 参数，编译失败：
 *   respVO.setAnchor(StringUtils.isEmpty(userDTO.getAvatar())?"默认头像地址");
 * LivingRoomInitVO 中 anchor 字段为 boolean 类型，暂无 String 类型的 avatar 字段。
 */
```

### 兼容方法保留

无。`LoginSuccessHandler` 方法签名保持不变（四参数），两个调用方均一致，无需兼容重载。

---

## 四、UTF-8 检查结果

以下位置已全部改为 `StandardCharsets.UTF_8`：

| # | 文件 | 行号 | 修改前 | 修改后 |
|---|------|------|--------|--------|
| 1 | `LoginMsgHandlerImpl.java` | 53 | `new String(body)` | `new String(body, StandardCharsets.UTF_8)` |
| 2 | `LogoutMsgHandlerImpl.java` | 101 | `.getBytes()` | `.getBytes(StandardCharsets.UTF_8)` |
| 3 | `ImMsg.java` | 31 | `data.getBytes()` | `data.getBytes(StandardCharsets.UTF_8)` |
| 4 | `WslmServerCoreHandler.java` | 65 | `.getBytes()` | `.getBytes(StandardCharsets.UTF_8)` |

以下位置此前已使用 UTF-8，无需修改：

| 文件 | 行号 | 代码 |
|------|------|------|
| `LoginMsgHandlerImpl.java` | 134 | `.getBytes(StandardCharsets.UTF_8)` ✅ |

未在本次修改范围但仍需关注（非核心 IM 链路）：

| 文件 | 行号 | 代码 |
|------|------|------|
| `ImAckConsumer.java` | 72 | `new String(msg.get(0).getBody())` — 无 UTF-8 |
| `MsgAckCheckServiceImpl.java` | 42 | `json.getBytes()` — 无 UTF-8 |

---

## 五、登录/登出状态说明

### 场景 A：普通页面登录
- **输入**：token 有效、userId 正确、appId 正确、roomId 不传
- **行为**：登录成功 → Channel 中保存 userId、appId → roomId 为 null → 上线 MQ 不携带 roomId
- **状态**：✅ 已正确实现

### 场景 B：直播间页面登录
- **输入**：token 有效、userId 正确、appId 正确、roomId 有值
- **行为**：登录成功 → Channel 中保存 userId、appId、roomId → 上线 MQ 携带 roomId
- **状态**：✅ 已正确实现（`roomId != null` 时设置到 Channel 和 MQ）

### 场景 C：普通登录后进入直播间
- **行为**：依赖客户端在进入直播间时通过独立逻辑调用 `ImContextUtils.setRoomId(ctx, roomId)`
- **状态**：⚠️ **项目尚未实现独立的"进入直播间"事件处理器**。当前依赖客户端在直播间页面建立 WebSocket 时携带 roomId

### 场景 D：离开直播间但不登出
- **行为**：需调用 `ImContextUtils.removeRoomId(ctx)` 清除 roomId，保留 userId 和连接
- **状态**：⚠️ **项目未实现独立的"离开直播间"事件处理器**。当前 roomId 可能代表最近一次绑定的直播间，不能保证用户当前仍在该页面

### 场景 E：仍在直播间时直接登出
- **输入**：登出消息（userId、appId、roomId 从 Channel 属性读取，不信任客户端传入）
- **行为**：
  1. 从 Channel 读取 userId、appId、roomId
  2. 构造登出响应 → writeAndFlush + CLOSE 监听器（响应发送完成后关闭连接）
  3. 删除本机 `ChannelHandlerContextCache` 缓存
  4. 删除 Redis 中 IM 节点绑定
  5. 发送离线 MQ（roomId 不为空时携带）
  6. 清理 Channel 属性（userId、appId、roomId）
- **状态**：✅ 已正确实现（修复了双重 writeAndFlush 和 MQ 异常传播问题）

---

## 六、验证结果

### 编译验证

```
命令：mvn -pl qiyu-live-im-core-server -am -DskipTests -pl '!qiyu-live-user-provider' compile
结果：BUILD SUCCESS
```

全部 16 个模块编译通过：

| 模块 | 状态 |
|------|------|
| qiyu-live-app | SUCCESS |
| qiyu-live-user-interface | SUCCESS |
| qiyu-live-common-interfacce | SUCCESS |
| qiyu-live-framework-datasource-starter | SUCCESS |
| qiyu-live-framework-redis-starter | SUCCESS |
| qiyu-live-id-generate-interface | SUCCESS |
| qiyu-live-msg-interface | SUCCESS |
| qiyu-live-account-interface | SUCCESS |
| qiyu-live-framework-web-starter | SUCCESS |
| qiyu-live-living-interfaces | SUCCESS |
| qiyu-live-framework-mq-starter | SUCCESS |
| qiyu-live-im-interface | SUCCESS |
| qiyu-live-im-core-server-interfaces | SUCCESS |
| qiyu-live-living-provider | SUCCESS |
| qiyu-live-api | SUCCESS |
| **qiyu-live-im-core-server** | **SUCCESS** |

> **注意**：`qiyu-live-user-provider` 因预存编译错误（`UserDTO.setNickname()` 方法不存在）被排除，该错误与本次修改无关。

### git diff --check

无本次修改引入的空白错误（预存的 `docs/tag-bit-governance.md` trailing whitespace 问题与本次无关）。

---

## 七、剩余风险

| # | 风险 | 严重程度 | 说明 |
|---|------|----------|------|
| 1 | **项目未实现独立的"离开直播间"事件** | 中 | 当前 roomId 可能代表最近一次绑定的直播间，不能保证用户当前仍在该页面。`removeRoomId` 仅在登出时调用 |
| 2 | **`channelInactive` 未发送离线 MQ** | 中 | 被动断线时不通知下游（直播间在线人数不更新），与主动登出的清理逻辑不一致 |
| 3 | **MQ 同步发送阻塞 Netty EventLoop** | 低 | `sendLoginMQ()` 和 `sendLogoutMQ()` 中使用同步 `mqProducer.send()`，可能阻塞 Netty I/O 线程。当前仅标注风险，未做大改 |
| 4 | **`ChannelHandlerContextCache` 非线程安全** | 中 | 底层使用 `HashMap`，无并发保护，多连接并发读写可能出问题 |
| 5 | **WebSocket token 在 URL 中传输** | 低 | Token 作为 URL 查询参数会出现在服务器访问日志中，有泄漏风险 |
| 6 | **`WsSharkHandler` 中 appId 解析仍依赖 token 格式** | 低 | 当前保留从 token 尾部 `%` 分隔符截取 appId 的逻辑，增加了无 `%` 时的容错，但仍不够健壮 |
| 7 | **`ImAckConsumer.java` 和 `MsgAckCheckServiceImpl.java` 的 UTF-8 未修复** | 低 | 这两个文件不在本次核心链路修改范围，但使用了无字符集的 `new String()`/`.getBytes()` |
| 8 | **`LivingRoomInitVO` 缺少 avatar 字段** | 低 | `APILivingRoomServiceImpl.anchorConfig()` 中原代码尝试设置头像地址但 VO 中无对应 String 字段 |

---

## 八、Docker & Nacos 启动说明

> 以下由用户手动执行。

### 1. Docker 基础设施

```bash
# 启动 MySQL 主从（项目根目录）
cd D:\myCode\qiyu-live-app
docker-compose up -d

# 或启动仅 MySQL 主从
docker-compose -f docker/mysql-master-slave/docker-compose.yml up -d
```

### 2. Nacos

```bash
# 独立 Nacos 实例（根据你的环境调整）
# 典型启动命令（需在 Nacos 安装目录下）：
bin\startup.cmd -m standalone
```

### 3. 验证端口

执行以下命令确认依赖服务已就绪：

```bash
# Nacos 控制台
curl http://localhost:8848/nacos

# MySQL 主库
mysql -h 127.0.0.1 -P 3307 -u root -p${QIYU_MYSQL_PASSWORD} -e "SELECT 1"

# MySQL 从库
mysql -h 127.0.0.1 -P 3308 -u root -p${QIYU_MYSQL_PASSWORD} -e "SELECT 1"
```

### 4. IM Core Server 启动（Maven）

```bash
cd D:\myCode\qiyu-live-app
mvn -pl qiyu-live-im-core-server spring-boot:run
```
