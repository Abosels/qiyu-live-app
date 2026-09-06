# 请求限流策略说明

> 日期：2026-07-21
> 涉及模块：`qiyu-live-framework-web-starter`（提供能力）、`qiyu-live-api`（消费）

---

## 一、限流机制

### 1.1 注解定义

`@RequestLimit` 注解（位于 `qiyu-live-framework-web-starter`）：

```java
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {
    int limit();      // 时间窗口内允许的最大请求次数
    int second();     // 时间窗口大小（秒）
    String msg() default "请求过于频繁";  // 触发限流后的提示语
}
```

### 1.2 拦截器实现

`RequestLimitInterceptor`（`preHandle` 拦截）：

1. 检查方法是否有 `@RequestLimit` 注解
2. 无 `userId`（未登录）则不限流，直接放行
3. 构造 Redis key：`Base64(applicationName + ":" + userId + ":" + requestURI)`
4. 首次访问：`SET key 1 EX second` → 放行
5. 再次访问：`INCR key` → 判断是否 < `limit`
   - 未达阈值 → 放行
   - 达到阈值 → 抛出 `QiyuErrorException(-1, limit.msg())` → `GlobalExceptionHandler` 捕获 → 返回 `bizError`

### 1.3 注解生效范围

目前仅 `qiyu-live-api`（HTTP 入口层）使用。Provider 层通过 Dubbo 暴露，面向内部服务，不在此层做调用方限流。

---

## 二、限流分级逻辑

按接口类型和风险等级分为三级：

| 级别 | 适用场景 | limit | second | 示例接口 |
|------|----------|-------|--------|----------|
| **L1 宽松** | 查询类接口，纯读，无副作用 | 5~10 | 10s | 直播间列表、用户信息、首页聚合、礼物列表、IM配置 |
| **L2 中等** | 写操作接口，有副作用但非敏感 | 1~5 | 10~60s | 开播、关播、用户信息更新、anchor配置 |
| **L3 严格** | 涉及资损或安全的接口 | 1 | 60s | 发送短信验证码、登录 |

### 分级决策原则

```
是否涉及资金/短信成本？
  ├── 是 → L3（1次/60s，尽量防刷）
  └── 否 → 是否写操作？
            ├── 是 → L2（1~5次/10~60s，防误操作/滥用）
            └── 否 → L1（5~10次/10s，防爬虫/恶意高频请求）
```

---

## 三、各接口限流配置一览

### LivingRoomController (`/living`)

| 方法 | 端点 | 级别 | limit | second | 原因 |
|------|------|------|-------|--------|------|
| `starting_living` | `/startingLiving` | L2 | 1 | 10s | 写操作，防重复开播 |
| `closeLiving` | `/closeLiving` | L2 | 1 | 10s | 写操作，防重复关播 |
| `anchorConfig` | `/anchorConfig` | L1 | 5 | 10s | 查询主播配置 |
| `list` | `POST /living` | L1 | 10 | 10s | 分页列表查询 |

### UserLoginController (`/userLogin`)

| 方法 | 端点 | 级别 | limit | second | 原因 |
|------|------|------|-------|--------|------|
| `sendLoginCode` | `/sendLoginCode` | ==L3== | 1 | ==60s== | 短信有成本，防资损 |
| `login` | `/login` | L3 | 5 | 60s | 防暴力破解验证码（6位码有100万种可能，60s内5次足够） |

### UserController (`/user`)

| 方法 | 端点 | 级别 | limit | second | 原因 |
|------|------|------|-------|--------|------|
| `getUserInfo` | `GET /info` | L1 | 10 | 10s | 读操作 |
| `updateUserInfo` | `PUT /update` | L2 | 5 | 60s | 写操作 |

### HomePageController (`/home`)

| 方法 | 端点 | 级别 | limit | second | 原因 |
|------|------|------|-------|--------|------|
| `initPage` | `/initPage` | L1 | 5 | 10s | 聚合查询 |

### ImController (`/im`)

| 方法 | 端点 | 级别 | limit | second | 原因 |
|------|------|------|-------|--------|------|
| `getImConfig` | `/getImConfig` | L1 | 3 | 10s | IM连接配置，每次连接前调用 |

### GiftController (`/gift`)

| 方法 | 端点 | 级别 | limit | second | 原因 |
|------|------|------|-------|--------|------|
| `listGift` | `/listGift` | L1 | 10 | 10s | 读操作 |
| `sendGift` | `/sendGift` | L2 | 3 | 10s | 写操作，涉及消费 |

---

## 四、Redis Key 设计

```
原始 key：{applicationName}:{userId}:{requestURI}
Base64 编码后：{base64(原始key)}

示例：
  原始：qiyu-live-api:10001:/userLogin/sendLoginCode
  Base64：cWl5dS1saXZlLWFwaToxMDAwMTovdXNlckxvZ2luL3NlbmRMb2dpbkNvZGU=
```

使用 Base64 的原因：
- 避免原始 key 中特殊字符（`:`、`/` 等）在 Redis 中产生意外行为
- 统一 key 格式，便于排查
- key 中包含 URI 路径和 userId，天然做到"按用户 + 按接口"粒度的限流

---

## 五、异常处理链路

```
Controller 方法标注 @RequestLimit
        ↓
RequestLimitInterceptor.preHandle() 拦截
        ↓
Redis INCR 判断是否超限
        ↓ (超限)
抛出 QiyuErrorException(-1, msg)
        ↓
GlobalExceptionHandler.sysErrorHandle() 捕获
        ↓
返回 WebResponseVO.bizError(msg, -1)
```

---

## 六、后续建议

1. **接入监控**：限流触发时可上报 Metrics（如 Prometheus），方便观察各接口被限流的频率
2. **IP 级别限流**：当前仅按 userId 限流，未登录用户无法限制。可增加 IP 维度兜底
3. **动态配置**：当前 `limit` 和 `second` 硬编码在注解中，未来可接入 Nacos 配置中心实现热更新
4. **未登录限流**：当前 `userId == null` 直接放行，可增加基于 IP 的宽松限流作为兜底
