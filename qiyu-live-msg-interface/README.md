# qiyu-live-msg-interface

## 1. 模块定位

`qiyu-live-msg-interface` 是短信 / 消息发送域的 RPC 契约模块。

它定义短信发送相关接口、请求对象和结果枚举。

## 2. 内容范围

- 短信发送 Dubbo 接口
- 消息发送 DTO
- 消息发送结果枚举
- IM 消息业务码枚举

## 3. 关键类

- `ISmsRpc`
- `MessageDTO`
- `MsgCheckDTO`
- `MsgSendResultEnum`
- `ImMsgBizCodeEnum`

## 4. 使用方式

通常被以下模块依赖：

- `qiyu-live-msg-provider`
- 需要发送短信或消息通知的业务模块

## 5. 说明

这个模块只放契约，不承载短信发送的具体实现。
