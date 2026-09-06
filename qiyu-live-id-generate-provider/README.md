# qiyu-live-id-generate-provider

## 1. 模块定位

`qiyu-live-id-generate-provider` 是全局 ID 生成服务实现模块。

它负责生成统一格式的 ID，并通过 Dubbo 暴露给其他业务模块使用。

## 2. 主要职责

- 实现 ID 生成 RPC
- 维护本地序列和非序列 ID 的生成逻辑
- 通过数据库表或本地对象承载号段信息
- 对外提供稳定的 ID 生成能力

## 3. 核心类

- `IdGenerateProviderApplication`
- `IdGenerateRpcImpl`
- `IdGenerateServiceImpl`
- `LocalSeqIdBO`
- `LocalUnSeqIdBO`
- `IdGenerateMapper`
- `IdGeneratePO`

## 4. 配置入口

主要配置位于：

- `src/main/resources/bootstrap.yml`
- `src/main/resources/application.yaml`

## 5. 运行依赖

- 数据库
- Nacos
- Dubbo

## 6. 使用场景

这个模块通常被以下业务模块依赖：

- `qiyu-live-user-provider`
- 其他需要统一主键的服务

## 7. 本地启动方式

建议先保证数据库和 Nacos 可达，再启动服务。
如果需要容器化运行，可参考模块内的 `docker/Dockerfile`。
