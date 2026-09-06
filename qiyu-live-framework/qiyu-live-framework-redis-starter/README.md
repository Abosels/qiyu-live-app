# qiyu-live-framework-redis-starter

## 1. 模块定位

这个 starter 提供项目统一的 Redis 自动配置、序列化和 Key 生成能力。

它是全项目 Redis 约定的基础，不同业务模块通过它共享同一套 key 命名规则和序列化方式。

## 2. 主要职责

- 提供 RedisTemplate / 序列化器自动配置
- 提供按业务模块区分的 KeyBuilder
- 通过条件装配确保只在 `qiyu-live-app` 场景下生效

## 3. 关键类

- `RedisKeyAutoConfiguration`
  - Redis 相关自动配置入口
- `RedisConfig`
  - Redis 基础配置
- `RedisSerializerFactory`
  - 序列化器工厂
- `FastJson2RedisSerializer`
  - Redis 序列化实现
- `AccountProviderCacheKeyBuilder`
- `IMProviderCacheKeyBuilder`
- `IMCoreServerProviderCacheKeyBuilder`
- `MsgProviderCacheKeyBuilder`
- `UserProviderCacheKeyBuilder`

## 4. 适用模块

这个 starter 常被以下模块使用：

- `qiyu-live-user-provider`
- `qiyu-live-account-provider`
- `qiyu-live-msg-provider`
- `qiyu-live-im-provider`
- `qiyu-live-im-core-server`

## 5. 约定

项目里所有 Redis key 都应该优先通过对应的 `KeyBuilder` 生成，而不是在业务代码里手写字符串。

这样做的好处是：

- key 前缀统一
- 类型转换一致
- 后续排查绑定关系更容易

## 6. 说明

这个模块没有独立启动入口。
它的职责是为业务服务提供 Redis 基础能力和统一约束。
