# sendGift 业务代码审查提示词

下面的内容可以整体复制到 ChatGPT 网页版。随后按提示上传或粘贴项目文件。请先让模型完成审查，不要一开始就让它直接改代码。

```text
你现在是一个熟悉 Java Spring Boot 微服务、Dubbo、Redis、RocketMQ、MySQL/MyBatis-Plus、Netty IM 的资深后端工程师。

我有一个直播项目 qiyu-live-app，需要你对“sendGift / 送礼物消费”业务做一次从 API 到最终 IM 通知的代码审查。目标不是马上重构，而是：
1. 还原真实调用链和数据流；
2. 找出确定存在的基本错误、资金一致性问题、并发问题、消息可靠性问题和 IM 通知问题；
3. 判断 bank 模块中的 consume() 和 consumeForSendGift() 是否还能使用，以及应该保留哪一个语义；
4. 给出最小修改方案，按优先级分批，不要大范围重写项目；
5. 如果信息不足，明确列出缺失证据，不要猜测。

项目约束：
- 这是 Maven 多模块 Java 微服务项目。
- 主要组件：Spring Boot、Dubbo、Nacos、Redis、RocketMQ、MySQL、MyBatis-Plus、Netty IM。
- 礼物请求入口：POST /gift/sendGift。
- 礼物消息 Topic：send_gift。
- 送礼消息 DTO：SendGiftMQ，字段包括 userId、giftId、price、roomId、receiveId、uuId。
- 当前设计意图是：API 校验礼物配置后发送 RocketMQ；gift-provider 消费消息；bank-provider 扣减用户余额；再经 IM Router 通知用户。
- API 返回成功时，必须区分“MQ 已接收”与“余额已经扣款成功”，不能把异步受理结果误称为最终交易成功。
- 所有结论都要引用文件路径和行号，并标注：确定问题 / 高风险问题 / 需要运行时或数据库证据确认。
- 不要因为代码中的中文注释乱码就忽略代码逻辑。

请按下面顺序工作：

第一阶段：建立调用链
请画出或用文字列出以下链路，并指出每一步的输入、输出、失败方式和边界：
HTTP Controller -> GiftServiceImpl -> GiftConfig Dubbo RPC -> SendGiftMQ -> RocketMQ send_gift -> SendGiftConsumer -> bank-provider RPC -> Redis / MySQL account -> trade record -> ImRouterRpc -> Redis 在线绑定 -> im-core-server -> Netty TCP/WS 连接 -> 前端。

第二阶段：逐文件审查
重点检查：
1. Controller 是否真的调用 giftService.sendGift，参数是否有校验，sender/receiver/roomId 是否可信；
2. GiftServiceImpl 是否使用当前登录用户作为扣款人，是否正确设置 receiveId，是否校验收礼人属于 room；
3. price 是否始终来自服务端有效礼物配置，是否可能被客户端篡改、溢出或传入非法值；
4. RocketMQ 生产端是否检查发送结果、是否有 key、tag、业务唯一号、失败重试和接口幂等策略；
5. SendGiftConsumer 是否正确解析 UTF-8，是否处理空消息、非法 JSON、空 UUID、非法 userId/price；
6. Redis 幂等键的生成、TTL、setIfAbsent 原子性、状态值含义，以及“先写幂等标记还是先扣款”的故障后果；
7. bank-provider 的 Redis 余额检查、Redis decrement、MySQL update、流水插入之间是否会超扣、重复扣款、少扣、缓存与数据库不一致；
8. consume() 与 consumeForSendGift() 的校验、并发控制、事务边界、返回值和缓存更新是否一致；
9. AccountTradeRespDTO 的成功/失败构造方法是否真的返回正确状态；
10. gift record 是否实际写入，是否有 sender、receiver、room、gift、price、业务唯一号等必要字段；
11. 扣款成功和失败时 IM 消息的 userId、appId、bizCode、data 是否正确，是否应该同时通知发送者和接收者；
12. ImRouterRpc 是否能处理用户不在线、Redis 没有绑定、绑定值格式错误、Dubbo 路由失败；
13. RocketMQ consumer 返回 CONSUME_SUCCESS 的时机是否正确。尤其分析：扣款成功但 IM 发送失败、Redis 已写幂等标记但 RPC 抛异常、批量消息中间一条失败、进程在扣款后崩溃等情况；
14. 礼物配置查询是否真的返回全部有效礼物，而不是被 limit 语句限制成一条；
15. 日志是否包含可串联的业务唯一号，是否会泄漏余额、敏感信息或打印过大的 MQ 对象。

第三阶段：重点回答 consume 是否还能用
请分别回答：
- consume() 当前是否比 consumeForSendGift() 更安全？理由是什么？
- 如果直接把 SendGiftConsumer 改成调用 consume()，会不会导致 Redis 余额读取结果过期或扣款状态不一致？
- 如果继续用 consumeForSendGift()，最少需要修复哪些问题？
- 最终应该采用哪一种“唯一扣款入口”：原子 SQL 扣款，还是 Redis 预扣后异步落库？请结合本项目当前代码，不要泛泛而谈。
- 账户扣款和交易流水是否必须处于同一个事务？当前异步线程池是否破坏了事务一致性？
- 如何保证 RocketMQ 至少一次投递下不会重复扣款？请给出可执行的幂等状态机，例如 INIT / PROCESSING / SUCCESS / FAIL，说明 Redis 或数据库各自负责什么。

第四阶段：故障场景推演
请逐项给出当前代码的实际结果、风险和最小修复方向：
- Controller 直接返回成功但没有发送 MQ；
- MQ 发送成功，消费者尚未处理，API 已返回；
- 消息重复投递；
- 消费者在 Redis setIfAbsent 后、扣款前崩溃；
- 扣款成功后、流水写入前崩溃；
- MySQL 扣款成功但 Redis decrement 失败；
- Redis decrement 成功但 MySQL update 失败；
- 余额读取成功后并发两次送礼；
- consumeForSendGift 收到 null、负数或 0 金额；
- 扣款失败但失败通知发送失败；
- 收礼人不在线；
- IM Router 找不到在线绑定；
- RocketMQ 消费批次中某一条 JSON 损坏；
- Redis 幂等键 TTL 到期后同一业务被再次投递。

第五阶段：输出格式
请严格按以下格式输出：

A. 一句话总结当前 sendGift 是否真正可用。

B. 真实调用链
用编号步骤列出每个模块、RPC、Topic、Redis key、数据库表和返回结果。

C. 问题清单
表格列：严重级别（P0/P1/P2/P3）、确定性、文件:行号、问题、触发条件、业务后果、最小修复建议。
P0 表示可能造成资金错误或所有请求假成功；P1 表示核心流程不可用或消息/扣款不一致；P2 表示可恢复的可靠性、校验或可观测性问题；P3 表示代码质量问题。

D. consume() 结论
明确说“保留、替换、拆分或暂不使用”，并解释为什么。

E. 最小修复批次
分成：
1. 立即修复：不改变整体架构，只修复确定的业务错误；
2. 第二批：补足幂等、异常重试、事务和流水；
3. 第三批：可选架构优化。
每批只列必要文件和最小改动，不要直接给大段重构代码。

F. 测试方案
至少覆盖：Controller 调用、礼物配置校验、MQ 消息字段、重复消息、并发扣款、余额不足、扣款后 IM 失败、消费者异常重试、流水唯一性、Redis 与 DB 对账。
优先给出可以在当前项目中落地的单元测试或集成测试名称、输入、预期结果。

G. 还需要我补充的证据
只列出真正影响结论的文件、SQL 表结构、Nacos 配置、RocketMQ 版本/消费日志和 Redis key 实际值。

审查时先阅读我提供的全部文件，再下结论。不要直接修改代码；如果我后续要求修改，先给出最小 diff 计划，确认只改相关模块。
```

建议在 ChatGPT 网页版第一轮同时上传这些文件：

1. `qiyu-live-api/src/main/java/org/qiyu/live/api/controller/GiftController.java`
2. `qiyu-live-api/src/main/java/org/qiyu/live/api/service/impl/GiftServiceImpl.java`
3. `qiyu-live-api/src/main/java/org/qiyu/live/api/vo/req/GiftReqVO.java`
4. `qiyu-live-gift-provider/src/java/org/qiyu/live/gift/provider/consumer/SendGiftConsumer.java`
5. `qiyu-live-common-interfacce/src/main/java/org/qiyu/live/common/interfaces/dto/SendGiftMQ.java`
6. `qiyu-live-common-interfacce/src/main/java/org/qiyu/live/common/interfaces/topic/GiftProviderTopicNames.java`
7. `qiyu-live-bank-provider/src/main/java/org/qiyu/live/bank/provider/service/impl/QiyuCurrencyAccountServiceImpl.java`
8. `qiyu-live-bank-provider/src/main/java/org/qiyu/live/bank/provider/dao/mapper/IQiyuCurrencyAccountMapper.java`
9. `qiyu-live-bank-provider/src/main/java/org/qiyu/live/bank/provider/service/impl/QiyuCurrencyTradeServiceImpl.java`
10. `qiyu-live-bank-interface/src/main/java/org/qiyu/live/bank/dto/AccountTradeReqDTO.java`
11. `qiyu-live-bank-interface/src/main/java/org/qiyu/live/bank/dto/AccountTradeRespDTO.java`
12. `qiyu-live-gift-provider/src/java/org/qiyu/live/gift/provider/service/impl/GiftConfigServiceImpl.java`
13. `qiyu-live-gift-provider/src/java/org/qiyu/live/gift/provider/service/impl/GiftRecordServiceImpl.java`
14. `qiyu-live-im-router-provider/src/main/java/org/qiyu/live/im/router/provider/service/impl/ImRouterServiceImpl.java`
15. `qiyu-live-im-core-server/src/main/java/org/qiyu/live/im/core/server/handler/impl/BizImMsgHandlerImpl.java`
16. `qiyu-live-msg-provider/src/main/java/org/qiyu/live/msg/provider/consumer/ImMsgConsumer.java`
17. 相关 Nacos YAML、RocketMQ producer/consumer 配置、Redis key builder 和数据库建表 SQL。

当前本地阅读时已经看到、但仍应让 ChatGPT 根据完整文件再次确认的重点疑点：

- `GiftController.sendGift()` 当前只返回成功，未调用 `giftService.sendGift()`。
- `GiftServiceImpl` 中 `sendGiftMQ.setReceiveId(sendGiftMQ.getReceiveId())` 是自赋值，收礼人可能一直为空。
- `GiftServiceImpl` 注入并构造了 `AccountTradeReqDTO`，但没有使用它进行扣款。
- `AccountTradeRespDTO.buildSuccess()` 当前把 `success` 设置为 `false`，会让扣款成功被判断为失败。
- `consumeForSendGift()` 是“先查 Redis 余额，再调用 decrease”；需要检查并发超扣、参数校验、缓存与数据库一致性。
- `decrease()` 当前既立即执行一次数据库扣减，又在线程池中再次执行数据库扣减，存在重复扣款风险；同时流水写入是异步的。
- `SendGiftConsumer` 在 Redis 幂等标记写入后才扣款，异常时如何重试需要重新设计；当前批次最终无条件返回 `CONSUME_SUCCESS`。
- 送礼消息使用随机 UUID 作为幂等号，客户端重试会生成新 UUID，不能天然防止同一业务请求重复扣款。
- `GiftConfigServiceImpl.queryGiftList()` 中有 `last("limit 1")`，需要确认是否会导致只返回一条礼物。
- IM 路由依赖 Redis 在线绑定；不在线时 `sendMsg` 返回 false，但消费者当前没有明确的补偿或业务状态处理。
- 送礼成功通知只设置了接收方 userId，是否还需要通知发送方、roomId、giftId、price 等数据，要结合前端协议确认。

不要把上述疑点直接当作最终结论，必须结合完整代码、SQL、配置和运行日志逐条验证。

如果需要分多轮提问，建议第二轮只问：
“请先只处理 P0/P1 问题，给出最小 diff，不要重构；先解释每个修改如何避免重复扣款和消息丢失。”

第三轮再问：
“请为修复后的 sendGift 链路设计测试和故障演练，重点验证 RocketMQ 至少一次投递、Redis 幂等、MySQL 账户扣减、流水和 IM 通知的一致性。”
