# qiyu-live-gift-provider

## 1. 模块定位

`qiyu-live-gift-provider` 是礼物域的核心业务实现服务。

它对外提供礼物配置管理和送礼记录能力，通过 Dubbo 暴露 RPC 服务。

## 2. 主要职责

- 实现 `IGiftConfigRpc` 和 `IGiftRecordRpc` 接口
- 管理礼物配置的增删改查
- 记录送礼流水
- 通过 Dubbo 注册到 Nacos

## 3. 运行依赖

- MySQL（礼物配置表、送礼记录表）
- Redis（缓存）
- Nacos（Dubbo 服务注册发现）
- Dubbo（RPC 通信）

## 4. 配置入口

运行时配置由聚合部署模块 `qiyu-live-api` 统一提供。独立调试时需自行配置 `bootstrap.yml`。

## 5. 当前作用

该模块通常被以下模块消费：

- `qiyu-live-api`（编译期聚合打包，通过 Dubbo 调用）

## 6. 相关文档

- [项目总览](../README.md)
- [qiyu-live-gift-interface](../qiyu-live-gift-interface/README.md)
