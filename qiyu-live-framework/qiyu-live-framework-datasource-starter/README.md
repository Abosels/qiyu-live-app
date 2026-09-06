# qiyu-live-framework-datasource-starter

## 1. 模块定位

这个 starter 负责数据源初始化和 ShardingSphere 相关接入。

它是基础设施层，不是独立运行服务，通常被 user-provider、account-provider、id-generate-provider 等模块依赖。

## 2. 主要职责

- 配置和初始化数据库连接
- 支持基于 Nacos 的 JDBC URL 解析
- 为分库分表场景提供统一入口

## 3. 核心类

- `DataSourceInitConfig`
  - 数据源初始化配置入口
- `ShardingSphereNacosURLDriver`
  - Nacos URL 驱动支持类

## 4. 使用场景

适合被以下模块依赖：

- 用户服务
- 账号服务
- ID 生成服务
- 其他需要数据库访问的业务模块

## 5. 说明

这个模块没有独立启动入口。
它的价值在于把数据库初始化逻辑收敛到一个地方，避免每个业务模块重复写一套数据源配置。
