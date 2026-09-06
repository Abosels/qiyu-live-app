# qiyu-live-user-provider

## 1. 模块定位

`qiyu-live-user-provider` 是用户域的核心业务实现服务。

它对外提供用户信息、标签、手机号和登录相关能力，是整个项目里最典型的业务 provider 之一。

## 2. 主要职责

- 实现用户域 Dubbo 接口
- 操作 MongoDB 中的用户数据
- 操作 Redis 中的缓存和标签位信息
- 通过 RocketMQ 处理异步任务和延迟消息
- 通过 Docker 方式支持本地联调和部署

## 3. 核心类

- `UserProviderApplication`
- `UserRpcImpl`
- `UserTagRpcImpl`
- `UserPhoneRpcImpl`
- `UserServiceImpl`
- `UserTagServiceImpl`
- `UserPhoneServiceImpl`
- `TagBitRegistryServiceImpl`

## 4. 运行依赖

- MongoDB
- Redis
- Nacos
- Dubbo
- RocketMQ NameServer / Broker

## 5. 配置入口

主要运行配置位于：

- `src/main/resources/bootstrap.yml`
- `src/main/resources/tag-bit-registry.yaml`

本地联调时重点看：

- `server.port=8082`
- `dubbo.protocol.port=9090`
- `host.docker.internal:8848`
- `host.docker.internal:9876`
- MongoDB 连接串

## 6. 本地启动方式

项目同时提供了容器化相关资源：

- `docker/Dockerfile`
- `docker/docker-compose.yml`

如果你在宿主机直接跑，也可以按 `bootstrap.yml` 中的本地地址启动。

## 7. 当前作用

这个模块通常被以下模块消费：

- `qiyu-live-api`
- `qiyu-live-gateway`
- 其他需要用户信息的服务

## 8. 阅读顺序建议

建议按这个顺序看：

1. `UserProviderApplication`
2. `UserRpcImpl`
3. `UserServiceImpl`
4. `UserTagServiceImpl`
5. `UserPhoneServiceImpl`
6. `TagBitRegistryServiceImpl`
