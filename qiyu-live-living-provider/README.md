# qiyu-live-living-provider

## 1. 模块定位

`qiyu-live-living-provider` 是直播间域的核心业务实现服务。

它对外提供直播间的开启、关闭、查询以及房间内用户管理能力，通过 Dubbo 暴露 RPC 服务。

## 2. 主要职责

- 实现 `ILivingRoomRpc` 接口，通过 Dubbo 注册到 Nacos
- 使用 MyBatis-Plus 将直播间数据持久化到 MySQL（`t_living_room`、`t_living_room_record`）
- 使用 Redis 缓存直播间详情和列表，防止数据库热点压力
- 通过 RocketMQ 消费 IM 在线/离线事件，实时维护每个直播间的用户集合
- 定时任务（`RefreshLivingRoomListJob`）每秒将直播间列表刷新到 Redis

## 3. 核心类

### RPC 层
- `LivingRoomRpcImpl` — `@DubboService`，实现 `ILivingRoomRpc`，委托给 `ILivingRoomService`

### 服务层
- `ILivingRoomService` — 服务接口（7 个方法：开播、关播、查询、用户上下线处理等）
- `LivingRoomServiceImpl` — 服务实现
  - 开播：插入 `t_living_room`，状态设为 VALID
  - 关播（事务）：验证主播所有权 → 写入 `t_living_room_record` 归档 → 删除当前记录 → 清除 Redis 缓存
  - 查询：缓存优先（Redis），未命中回源 MySQL，空值缓存防击穿
  - 列表分页：完全从 Redis List 读取（`LRANGE` + `LLEN`），不查数据库

### DAO 层
- `ILivingRoomMapper` — MyBatis-Plus `BaseMapper<LivingRoomPO>`，操作 `t_living_room`
- `ILivingRoomRecordMapper` — MyBatis-Plus `BaseMapper<LivingRoomRecordPO>`，操作 `t_living_room_record`
- `LivingRoomPO` — 当前直播间 PO（anchorId、type、roomName、status、watchNum、goodNum、startTime 等）
- `LivingRoomRecordPO` — 历史直播间记录 PO（额外包含 endTime 字段）

### 配置与定时任务
- `MybatisPageConfig` — MyBatis-Plus 分页插件（MySQL 适配，每页最大 500 条）
- `RefreshLivingRoomListJob` — 每秒执行，持 Redis 分布式锁，查库后原子替换 Redis List（先写临时 key 再 rename）

### 消息消费者
- `LivingRoomOnlineConsumer` — 订阅 `IM_ONLINE_TOPIC`，维护房间 Redis 用户 Set
- `LivingRoomOFFlineConsumer` — 订阅 `IM_OFFLINE_TOPIC`，移除离线用户

## 4. 运行依赖

- MySQL（`t_living_room`、`t_living_room_record` 表）
- Redis（直播间缓存、用户集合、分布式锁）
- Nacos（Dubbo 服务注册发现）
- Dubbo（RPC 通信）
- RocketMQ NameServer / Broker（消费 IM 在线/离线事件）

## 5. 配置入口

该模块当前无独立 `bootstrap.yml` / `application.yml`，运行时配置由聚合部署模块 `qiyu-live-api` 统一提供。

## 6. 当前作用

该模块通常被以下模块消费：

- `qiyu-live-api`（编译期聚合打包）
- `qiyu-live-msg-provider`（通过 Dubbo 调用 `ILivingRoomRpc.queryUserIdByRoomId()` 实现消息分发）

## 7. 阅读顺序建议

建议按这个顺序看：

1. `LivingRoomRpcImpl` — RPC 入口
2. `ILivingRoomService` + `LivingRoomServiceImpl` — 核心业务逻辑
3. `LivingRoomPO` + `LivingRoomRecordPO` — 数据模型
4. `ILivingRoomMapper` + `ILivingRoomRecordMapper` — 持久化
5. `RefreshLivingRoomListJob` — 定时缓存刷新
6. `LivingRoomOnlineConsumer` + `LivingRoomOFFlineConsumer` — 消息消费
7. `MybatisPageConfig` — 分页配置

## 8. 相关文档

- [项目总览](../README.md)
- [qiyu-live-living-interfaces](../qiyu-live-living-interfaces/README.md)
