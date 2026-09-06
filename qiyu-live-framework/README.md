# qiyu-live-framework

## 1. 模块定位

`qiyu-live-framework` 是公共基础设施聚合模块。

它本身不承载业务运行时能力，主要用于组织和复用以下 starter：

- `qiyu-live-framework-datasource-starter`
- `qiyu-live-framework-redis-starter`
- `qiyu-live-framework-mq-starter`
- `qiyu-live-framework-web-starter`

## 2. 作用

- 统一承载基础设施自动配置
- 避免各业务模块重复写 Redis、数据源、RocketMQ、Web 通用配置
- 作为业务模块的依赖中枢

## 3. 子模块关系

### 3.1 数据源 Starter

- [qiyu-live-framework-datasource-starter/README.md](./qiyu-live-framework-datasource-starter/README.md)
- 负责 ShardingSphere、数据源和 Nacos URL 驱动相关配置

### 3.2 Redis Starter

- [qiyu-live-framework-redis-starter/README.md](./qiyu-live-framework-redis-starter/README.md)
- 负责 Redis 序列化、KeyBuilder、条件装配

### 3.3 MQ Starter

- [qiyu-live-framework-mq-starter/README.md](./qiyu-live-framework-mq-starter/README.md)
- 负责 RocketMQ Producer / Consumer 自动配置

### 3.4 Web Starter

- [qiyu-live-framework-web-starter/README.md](./qiyu-live-framework-web-starter/README.md)
- 负责 Web 侧上下文和拦截器支持

## 4. 使用方式

业务模块通常只需要依赖对应 starter，不需要直接引用底层第三方组件。

例如：

- 需要 Redis Key 约定时，引入 Redis Starter
- 需要数据库连接和分库分表支持时，引入 Datasource Starter
- 需要 RocketMQ 时，引入 MQ Starter
- 需要统一 Web 上下文和请求头处理时，引入 Web Starter
