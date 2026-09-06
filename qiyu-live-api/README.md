# qiyu-live-api

## 1. 模块定位

`qiyu-live-api` 是项目的业务 API 层。

它承接外部 HTTP 请求，并把请求转换为对后端 Dubbo 服务的调用，属于“面向前端/调用方的编排层”。

## 2. 主要职责

- 暴露 HTTP 接口
- 负责用户登录等聚合型业务编排
- 调用用户、账号、消息等 Dubbo 接口
- 复用 `qiyu-live-framework-web-starter` 提供的 Web 能力

## 3. 关键依赖

- `qiyu-live-user-interface`
- `qiyu-live-msg-interface`
- `qiyu-live-account-interface`
- `qiyu-live-common-interfacce`
- `qiyu-live-framework-web-starter`
- Nacos Discovery / Config
- Dubbo

## 4. 代码结构

从当前代码看，这个模块主要包含：

- `controller`
- `service`
- `vo`

它更像一个面向前端的 BFF 层，而不是纯业务实现层。

## 5. 运行依赖

- Nacos
- Dubbo
- 用户服务
- 账号服务
- 消息服务

## 6. 配置入口

主要配置位于：

- `src/main/resources/bootstrap.yml`
- `src/main/resources/dubbo.properties`

启动时重点检查：

- Nacos 地址
- 服务发现配置
- Dubbo 注册地址
- 对应业务服务是否已注册

## 7. 说明

如果前端接口要改，优先先看这个模块。
如果只是修改底层用户/账号/消息能力，优先在对应 provider 和 interface 模块处理。
