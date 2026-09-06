# qiyu-live-account-provider

## 1. 模块定位

`qiyu-live-account-provider` 是账号域业务实现模块。

它通过 Dubbo 对外提供账号 token 相关能力，并依赖 MongoDB、Redis 和 Nacos 完成本地和线上运行。

## 2. 主要职责

- 实现账号 token 的生成和校验
- 对外暴露 `IAccountTokenRpc`
- 连接数据库和配置中心
- 支持容器化打包与部署

## 3. 核心类

- `AccountProviderApplication`
- `AccountTokenRpcImpl`
- `AccountTokenServiceImpl`
- `IAccountTokenService`

## 4. 运行依赖

- MongoDB
- Redis
- Nacos
- Dubbo

## 5. 配置入口

主要配置位于：

- `src/main/resources/bootstrap.yml`

本地联调时关注：

- `server.port=8083`
- `dubbo.protocol.port=9090`
- `host.docker.internal:8848`
- `host.docker.internal:27017`

## 6. 本地启动方式

这个模块同时提供了：

- `docker/Dockerfile`
- `docker/docker-compose.yml`

建议先确认 MongoDB 和 Nacos 可达，再启动服务。

## 7. 阅读顺序建议

建议按这个顺序看：

1. `AccountProviderApplication`
2. `AccountTokenRpcImpl`
3. `AccountTokenServiceImpl`
