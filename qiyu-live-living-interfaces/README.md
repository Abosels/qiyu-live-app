# qiyu-live-living-interfaces

## 1. 模块定位

`qiyu-live-living-interfaces` 是直播间域的 RPC 契约模块。

它只放接口、DTO、枚举和常量，不承载运行时服务。

## 2. 内容范围

- 直播间相关 Dubbo 接口
- 直播间请求/响应 DTO
- 直播间类型枚举

## 3. 关键接口

- `ILivingRoomRpc`
  - `startLivingRoom(LivingRoomReqDTO)` — 开启直播间
  - `closeLivingRoom(LivingRoomReqDTO)` — 关闭直播间（仅主播本人可关）
  - `queryByRoomId(Integer roomId)` — 按 roomId 查询直播间详情
  - `list(LivingRoomReqDTO)` — 直播间列表分页查询
  - `queryUserIdByRoomId(LivingRoomReqDTO)` — 查询直播间内所有用户 ID

## 4. 关键 DTO

- `LivingRoomReqDTO` — 请求 DTO（anchorId、roomName、roomId、coverImg、type、page 等）
- `LivingRoomRespDTO` — 响应 DTO

## 5. 关键枚举

- `LivingRoomTypeEnum` — 直播间类型（`DEFAULT_LIVING_ROOM`、`RK_LIVING_ROOM`）

## 6. 使用方式

该模块一般被以下模块依赖：

- `qiyu-live-living-provider`（实现 `ILivingRoomRpc` 接口）
- `qiyu-live-msg-provider`（通过 RPC 查询直播间内用户列表，用于消息分发）
- 其他需要调用直播间服务的业务模块

## 7. 说明

接口模块的原则是只做契约，不写业务逻辑。
如果接口变更，优先同步消费方和 provider 的兼容性。
