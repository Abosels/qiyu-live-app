# qiyu-live-framework-mq-starter

## 1. 模块定位

这个 starter 负责 RocketMQ 相关的基础自动配置。

它为需要使用消息驱动的业务模块提供统一的 Producer 和 Consumer 参数绑定能力。

## 2. 主要职责

- 绑定 RocketMQ Producer 配置
- 绑定 RocketMQ Consumer 配置
- 将 MQ 参数从配置文件或 Nacos 中读取后注入到业务模块

## 3. 关键类

- `RocketMQProducerProperties`
- `RocketMQConsumerProperties`
- `RocketMQProducerConfig`

## 4. 使用场景

当前项目中主要用于：

- `qiyu-live-im-core-server`
- `qiyu-live-user-provider`

后续如果其他业务模块也要发消息，可以直接复用这个 starter。

## 5. 说明

这个模块本身不启动业务服务，只负责把 RocketMQ 基础配置统一封装起来。

如果某个业务模块启动时找不到 NameServer，先检查：

- 本地 Docker 或宿主机上 RocketMQ 是否已启动
- `bootstrap.yml` 里的 `nameServer` 是否指向正确地址
- 环境变量兜底值是否和实际环境一致
