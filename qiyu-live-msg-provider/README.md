# qiyu-live-msg-provider

## 1. 模块定位

`qiyu-live-msg-provider` 是短信 / 消息发送域的业务实现模块。

它负责短信发送、消息消费和相关数据落库，是一个典型的消息驱动 provider。

## 2. 主要职责

- 实现短信发送 RPC
- 处理消息消费
- 维护消息发送相关数据库记录
- 配合 RocketMQ 完成异步消息处理

## 3. 核心类

- `MsgProviderApplication`
- `SmsRpcImpl`
- `SmsServiceImpl`
- `ImMsgConsumer`
- `MessageHandlerImpl`
- `ThreadPoolManager`
- `ApplicationProperties`
- `SmsTemplateIDEnum`
- `MyMetaObjectHandler`

## 4. 配置入口

主要配置位于：

- `src/main/resources/bootstrap.yml`

运行时重点关注：

- Nacos 配置中心
- Nacos 服务发现
- RocketMQ 消费者配置

## 5. 运行依赖

- Nacos
- RocketMQ
- 数据库
- Dubbo

## 6. 本地启动方式

建议先确认：

- RocketMQ NameServer 可达
- Nacos 可达
- 数据库可达

然后再启动 provider。

## 7. 使用场景

这个模块通常被业务系统用来做：

- 短信验证码发送
- 消息异步消费
- 消息结果记录
