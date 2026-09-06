# 2026-07-21 ~ 07-22 开发记录

> 本文件记录本次会话中所有改动，按时间顺序排列。

---

## 一、模块 README 补充（07-21）

### 1.1 新建 README（2 个缺失模块）

| 模块 | 文件 |
|------|------|
| `qiyu-live-living-interfaces` | `qiyu-live-living-interfaces/README.md` |
| `qiyu-live-living-provider` | `qiyu-live-living-provider/README.md` |

### 1.2 更新根 README

- Mermaid 依赖图新增 `LivingI` / `LivingP` 节点及 8 条依赖边
- 模块索引新增"直播间"分类

---

## 二、IM 登录登出与 WebSocket 链路排错修复（07-21）

> 依据：`Claude_Code_IM登录登出排错与修复方案.md`
> 详细报告：`docs/change1.md`

### 2.1 根因修复（14 项）

| # | 文件 | 问题 | 修复 |
|---|------|------|------|
| 1 | `WsSharkHandler.java` | `paramArr` 未定义（编译失败） | `QueryStringDecoder` 按参数名解析 |
| 2 | `WsSharkHandler.java` | `@Sharable` + 成员变量 `webSocketServerHandshaker` | 改为方法局部变量 |
| 3 | `WsSharkHandler.java` | 同步 `channelFuture.isSuccess()` | 改用 `addListener` 异步回调 |
| 4 | `WsSharkHandler.java` | Fastjson v1 与 v2 混用 | 统一为 fastjson2 |
| 5 | `WsSharkHandler.java` | `substring`/`replaceAll` 链式解析 URI | `QueryStringDecoder` 按名称取值 |
| 6 | `LogoutMsgHandlerImpl.java` | 双重 `writeAndFlush` | 删第一次，仅保留带 CLOSE 的 |
| 7 | `LogoutMsgHandlerImpl.java` | `.getBytes()` 无 UTF-8 | → `StandardCharsets.UTF_8` |
| 8 | `LogoutMsgHandlerImpl.java` | MQ 异常重新抛出 | 仅记录日志 |
| 9 | `LoginMsgHandlerImpl.java` | `new String(body)` 无 UTF-8 | → `new String(body, UTF_8)` |
| 10 | `LoginMsgHandlerImpl.java` | MQ 异常重新抛出 | 仅记录日志 |
| 11 | `ImMsgBody.java` | `int appId` / `long userId` | → `Integer` / `Long` |
| 12 | `ImMsg.java` | `data.getBytes()` 无 UTF-8 | → `StandardCharsets.UTF_8` |
| 13 | `WslmServerCoreHandler.java` | `.getBytes()` 无 UTF-8 | → `StandardCharsets.UTF_8` |
| 14 | `APILivingRoomServiceImpl.java` | `setAnchor(String)` → boolean 参数 | 旧代码注释，改为 `setAnchor(false)` |

### 2.2 附带修复（预存编译错误）

| 文件 | 问题 |
|------|------|
| `LivingProviderCacheKeyBuilder.java` | `,` → `;`、`s+roomId` → `+ roomId` |

### 2.3 编译验证

```
mvn -pl qiyu-live-im-core-server -am -DskipTests -pl '!qiyu-live-user-provider' compile
→ BUILD SUCCESS（16/16 模块）
```

---

## 三、gift-provider pom.xml 修复（07-21）

| 改前 | 改后 | 原因 |
|------|------|------|
| `org.idea` | `com.bei` | 统一 groupId |
| `mysql-connector-java` | `mysql-connector-j` | Spring Boot 3.x 驱动 |
| `mybatis-plus-boot-starter` | `mybatis-plus-spring-boot3-starter` | Spring Boot 3.x 适配 |
| 引用不存在变量 | 显式 `1.0-SNAPSHOT` / 去掉 version | 避免变量解析失败 |
| 无 build 段 | docker + spring-boot 插件 | 与其他 provider 一致 |

---

## 四、WebResponseVO.sysError 补全（07-21）

- `sysError(String msg)` → `sysError(String msg)` + 泛型 `<T>`
- code=502（区别于 bizError 的 500）
- 同时发现并修复 `QiyuApiError.getErrorCode()`/`getErrorMsg()` 返回硬编码 0/"" 的 bug

---

## 五、礼物 API 层搭建（07-21）

### 5.1 新建文件

| 文件 | 说明 |
|------|------|
| `GiftController.java` | `/gift/listGift`、`/gift/sendGift` |
| `IGiftService.java` | 服务接口 |
| `GiftServiceImpl.java` | 服务实现（Dubbo 调用 gift/bank RPC） |
| `GiftConfigVO.java` | 礼物配置展示 VO |

### 5.2 pom.xml

- `qiyu-live-api/pom.xml` 新增 `qiyu-live-gift-interface`、`qiyu-live-bank-interface` 依赖

---

## 六、UserLoginServiceImpl ErrorAssert 重构（07-21）

### 6.1 QiyuApiError 修复

- `getErrorCode()` 返回 `this.code`（原来返回 0）
- `getErrorMsg()` 返回 `this.desc`（原来返回 ""）
- 新增：`SMS_SEND_FAIL(10005)`、`SMS_CODE_ERROR(10006)`、`LOGIN_FAIL(10007)`、`GIFT_CONFIG_ERROR(10008)`

### 6.2 UserLoginServiceImpl

| 方法 | 原 if 判断 | 改为 ErrorAssert |
|------|-----------|-----------------|
| `sendLoginCode` | `if (sendResultEnum == null)` | `ErrorAssert.isNotNull` |
| `sendLoginCode` | `if (sendResultEnum != SEND_SUCCESS)` | `ErrorAssert.isTrue(== SEND_SUCCESS)` |
| `login` | `isTrue((code无效条件), ...)` — 逻辑反了 | `isTrue(code有效条件, ...)` — 修正 |
| `login` | `if (userLoginDTO == null)` | `ErrorAssert.isNotNull` |
| `login` | `if (!msgCheckDTO.isSuccess())` | 保留 if（需动态错误信息） |

---

## 七、全局限流策略（07-21）

### 7.1 限流文档

- 新建：`docs/request-limit-strategy.md`
- 三级分级逻辑：L1 宽松（5~10/10s）、L2 中等（1~5/10~60s）、L3 严格（1/60s）

### 7.2 各 Controller 限流配置

| Controller | 方法 | limit | second |
|------------|------|-------|--------|
| LivingRoom | `anchorConfig` | 5 | 10s |
| LivingRoom | `list` | 10 | 10s |
| UserLogin | `sendLoginCode` | 1 | 60s |
| UserLogin | `login` | 5 | 60s |
| User | `getUserInfo` | 10 | 10s |
| User | `updateUserInfo` | 5 | 60s |
| HomePage | `initPage` | 5 | 10s |
| Im | `getImConfig` | 3 | 10s |
| Gift | `listGift` | 10 | 10s |
| Gift | `sendGift` | 3 | 10s |

### 7.3 RequestLimitInterceptor Base64 修复

- 原代码 `Base64.getEncoder().encodeToString(cacheKey.getBytes())` 未赋值 → 改为 `cacheKey = Base64.getEncoder().encodeToString(cacheKey.getBytes(StandardCharsets.UTF_8))`

---

## 八、模块 README 更新（07-21）

### 8.1 新建

| 模块 | 文件 |
|------|------|
| `qiyu-live-gift-interface` | `README.md` |
| `qiyu-live-gift-provider` | `README.md` |

### 8.2 更新

- `qiyu-live-framework-web-starter/README.md`：补齐限流、异常处理、断言工具类
- 根 `README.md`：新增"礼物"分类、限流文档链接

---

## 九、配置文件批量补充（07-22）

### 9.1 bootstrap.yml（3 个新建，含 DB + Redis + RocketMQ）

| 模块 | HTTP | Dubbo | 数据库 |
|------|------|-------|--------|
| `qiyu-live-living-provider` | 8087 | 9096 | qiyu-live-living |
| `qiyu-live-gift-provider` | 8088 | 9097 | qiyu-live-gift |
| `qiyu-live-bank-provider` | 8089 | 9098 | qiyu-live-bank |

### 9.2 logback-spring.xml（7 个新建）

api, living-provider, gift-provider, bank-provider, id-generate-provider, im-router-provider, msg-provider

### 9.3 dubbo.properties（9 个新建）

living, gift, bank, account, im-core-server, im-provider, im-router, msg, gateway

### 9.4 Nacos 配置（3 个新建）

`docs/nacos-configs/` 下：living, gift, bank 的 yml 文件

---

## 十、账户消费方法实现（07-22）

### 10.1 QiyuCurrencyAccountServiceImpl.consume()

6 步逻辑：
1. 请求非空校验
2. userId > 0 校验
3. number > 0 校验
4. 查账户是否存在（仅校验存在性）
5. Mapper.decrease() 原子扣减（SQL: `WHERE current_balance >= #{number}`）
6. 成功 → 重查最新余额 → 返回 `AccountTradeRespDTO(success=true)`

关键：第 5 步使用 MySQL 行锁保护，避免"先查后更"的并发超扣。

### 10.2 GiftConfigDTO 补全

空壳 → 10 个字段：giftId, giftName, price, priceUnit, giftImage, giftType, status, createTime, updateTime + `@Data`

### 10.3 GiftServiceImpl DTO→VO 映射

TODO 注释 → 激活 7 个 setter 映射。

---

## 当前项目端口一览

| 模块 | HTTP | Dubbo |
|------|------|-------|
| qiyu-live-api | 8081 | 9090 |
| qiyu-live-user-provider | 8082 | 9090 |
| qiyu-live-account-provider | 8083 | 9090 |
| qiyu-live-im-core-server | 8084 | 9092 |
| qiyu-live-im-provider | 8085 | 9094 |
| qiyu-live-im-router-provider | 8086 | 9095 |
| qiyu-live-living-provider | 8087 | 9096 |
| qiyu-live-gift-provider | 8088 | 9097 |
| qiyu-live-bank-provider | 8089 | 9098 |
| qiyu-live-gateway | 80 | — |
| qiyu-live-msg-provider | — | 9090 |
| qiyu-live-id-generate-provider | — | 9090 |

---

## 当前文档索引

| 文档 | 说明 |
|------|------|
| `README.md` | 项目总览 + 模块索引 + 依赖图 |
| `docs/change1.md` | IM 登录登出排错修复完整报告 |
| `docs/request-limit-strategy.md` | 请求限流策略 |
| `docs/im-local-startup-and-debug.md` | IM 本地联调手册 |
| `docs/nacos-configs/` | Nacos 配置文件（待上传） |
| 各模块 `README.md` | 24 个模块全部覆盖 |
