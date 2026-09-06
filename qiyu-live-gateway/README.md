# qiyu-live-gateway

## 1. 模块定位

`qiyu-live-gateway` 是整个项目的统一入口网关。

它负责对外接收 HTTP 请求，再按路由规则把请求转发到后端服务。

## 2. 主要职责

- 承接外部请求
- 做统一路由和负载均衡
- 接入 Nacos 做服务发现
- 接入 Nacos 做配置中心
- 预留 Dubbo 消费能力

## 3. 关键依赖

- Spring Cloud Gateway
- Spring Cloud LoadBalancer
- Nacos Discovery
- Nacos Config
- Spring Cloud Bootstrap

## 4. 适用场景

这个模块适合承接：

- 用户侧 HTTP 请求
- 认证前置校验
- 统一鉴权和转发

## 5. 配置入口

通常通过 `bootstrap.yml` 和 Nacos 配置中心管理。

## 6. 说明

Gateway 不应该直接堆业务逻辑。
它更适合作为“入口层 + 转发层 + 基础过滤层”。
