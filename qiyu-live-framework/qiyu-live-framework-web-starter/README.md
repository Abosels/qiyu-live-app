# qiyu-live-framework-web-starter

## 1. 模块定位

这个 starter 提供 Web 层通用能力：请求上下文、拦截器、统一异常处理、请求限流和基础 Web 配置。

它通常被需要统一处理请求头、用户上下文、限流或 Web 约定的服务引用。

## 2. 主要职责

- 提供 Web 通用配置
- 维护请求上下文对象（`QiyuRequestContext`）
- 提供请求拦截器（用户信息、限流）
- 全局异常处理（`GlobalExceptionHandler`）
- 断言工具类（`ErrorAssert` + `QiyuBaseError`）
- 请求限流注解与拦截器（`@RequestLimit`）

## 3. 关键类

### 上下文与拦截器

- `WebConfig` — Web 基础配置，注册拦截器
- `QiyuUserInfoInterceptor` — 用户信息拦截器，解析请求头中的 userId 并存入 `QiyuRequestContext`
- `QiyuRequestContext` — 基于 `ThreadLocal` 的请求上下文，存取当前请求的 userId
- `RequestConstants` — 请求相关常量（如 header 名称）

### 限流

- `RequestLimit` — `@RequestLimit(limit, second, msg)` 注解，标注在 Controller 方法上启用限流
- `RequestLimitInterceptor` — 限流拦截器，基于 Redis 实现"按用户 + 按接口"粒度的计数限流

### 异常处理

- `QiyuBaseError` — 错误码接口，定义 `getErrorCode()` 和 `getErrorMsg()`
- `BizBaseErrorEnum` — 通用业务错误枚举（`PARAM_ERROR`、`TOKEN_ERROR`）
- `ErrorAssert` — 断言工具类：`isNotNull`、`isNotBlank`、`isTrue`，断言失败抛 `QiyuErrorException`
- `QiyuErrorException` — 自定义运行时异常，携带 errorCode 和 errorMsg
- `GlobalExceptionHandler` — `@ControllerAdvice` 全局异常处理，将 `QiyuErrorException` 转为 `WebResponseVO.bizError`

## 4. 适用场景

通常被以下模块使用：

- `qiyu-live-api`（消费全部能力：限流、上下文、异常处理）

## 5. 说明

这个模块没有独立业务入口。
它的价值是把 Web 横切能力（上下文、限流、异常处理）集中在一起，减少每个服务重复写拦截器和异常处理代码。

## 6. 相关文档

- [请求限流策略说明](../../docs/request-limit-strategy.md)
