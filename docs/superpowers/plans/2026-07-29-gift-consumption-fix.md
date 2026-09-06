# Gift Consumption Fix Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make one gift message deduct a user's currency at most once and publish an IM effect only after the deduction is committed.

**Architecture:** `SendGiftMQ.uuId` becomes the durable gift business ID. `consumeForSendGift` executes one short transaction in `bank-provider`: conditional balance deduction plus trade insertion. A unique database index makes RocketMQ redelivery idempotent; Redis is invalidated only after transaction completion.

**Tech Stack:** Java 21, Spring Boot, Dubbo, MyBatis-Plus, MySQL, Redis, RocketMQ.

## Global Constraints

- Backend only; do not change the request path `POST /gift/sendGift`.
- Do not modify Alipay sandbox or any other payment code in this plan.
- Keep existing asynchronous `increase`, `decrease`, `consumeIcreBHandler`, and `consumeDcreBHandler` unchanged; the repaired gift flow must not call them.
- MySQL is the account of record; Redis is a cache only.
- New Java code must include concise Chinese comments explaining non-obvious business reasons.
- Do not delete user-written code.

---

### Task 1: Add Durable Gift Idempotency Column

**Files:**
- Modify manually in MySQL: `qiyu_live_bank.t_qiyu_currency_trade`

**Produces:** A non-null `biz_id` column and a unique `(biz_id, type)` index.

- [ ] **Step 1: Inspect existing data before schema change**

```sql
SELECT id, user_id, number, type, status, create_time
FROM t_qiyu_currency_trade
ORDER BY id DESC
LIMIT 20;
```

- [ ] **Step 2: Add a nullable column first, so existing rows remain valid**

```sql
ALTER TABLE t_qiyu_currency_trade
    ADD COLUMN biz_id VARCHAR(64) NULL COMMENT '业务唯一号：送礼使用 SendGiftMQ.uuId，充值使用支付订单号';
```

- [ ] **Step 3: Add the idempotency index**

```sql
CREATE UNIQUE INDEX uk_qiyu_currency_trade_biz_type
    ON t_qiyu_currency_trade (biz_id, type);
```

- [ ] **Step 4: Verify the column and index**

```sql
SHOW COLUMNS FROM t_qiyu_currency_trade LIKE 'biz_id';
SHOW INDEX FROM t_qiyu_currency_trade WHERE Key_name = 'uk_qiyu_currency_trade_biz_type';
```

### Task 2: Extend the Gift Consumption Contract

**Files:**
- Modify: `qiyu-live-bank-interface/src/main/java/org/qiyu/live/bank/dto/AccountTradeReqDTO.java`
- Modify: `qiyu-live-bank-interface/src/main/java/org/qiyu/live/bank/dto/AccountTradeRespDTO.java`

**Consumes:** The `biz_id` database column from Task 1.

**Produces:** `AccountTradeReqDTO.bizId` and a correct success response.

- [ ] **Step 1: Add the business ID to the request DTO**

```java
/**
 * 业务唯一号：送礼时使用 SendGiftMQ.uuId，用于数据库级幂等。
 */
private String bizId;
```

- [ ] **Step 2: Correct the existing success factory**

Replace the incorrect line in `buildSuccess`:

```java
dto.setSuccess(false);
```

with:

```java
dto.setSuccess(true);
```

- [ ] **Step 3: Compile the contract and its dependent modules**

```powershell
mvn -s .mvn/settings.xml -pl qiyu-live-bank-interface -am -DskipTests compile
```

Expected: `BUILD SUCCESS`.

### Task 3: Persist the Business ID and Query Existing Gift Trades

**Files:**
- Modify: `qiyu-live-bank-provider/src/main/java/org/qiyu/live/bank/provider/dao/po/QiyuCurrencyTradePO.java`
- Modify: `qiyu-live-bank-provider/src/main/java/org/qiyu/live/bank/provider/service/IQiyuCurrencyTradeService.java`
- Modify: `qiyu-live-bank-provider/src/main/java/org/qiyu/live/bank/provider/service/impl/QiyuCurrencyTradeServiceImpl.java`

**Consumes:** `AccountTradeReqDTO.bizId`.

**Produces:** `existsByBizIdAndType(String bizId, Integer type)` and `insertOne(userId, number, type, bizId)`.

- [ ] **Step 1: Add the PO field**

```java
/**
 * 业务唯一号，配合唯一索引防止 RocketMQ 重复投递重复扣费。
 */
private String bizId;
```

- [ ] **Step 2: Declare the service methods**

```java
boolean existsByBizIdAndType(String bizId, Integer type);

boolean insertOne(long userId, int number, int type, String bizId);
```

- [ ] **Step 3: Implement the query with `LambdaQueryWrapper`**

```java
return qiyuCurrencyTradeMapper.selectCount(new LambdaQueryWrapper<QiyuCurrencyTradePO>()
        .eq(QiyuCurrencyTradePO::getBizId, bizId)
        .eq(QiyuCurrencyTradePO::getType, type)) > 0;
```

- [ ] **Step 4: Set `bizId` before insert and rethrow persistence errors**

```java
qiyuCurrencyTradePO.setBizId(bizId);
qiyuCurrencyTradeMapper.insert(qiyuCurrencyTradePO);
return true;
```

Do not catch and silently return `false` in this new transaction path; an exception must reach Spring so the balance update rolls back.

- [ ] **Step 5: Compile bank provider**

```powershell
mvn -s .mvn/settings.xml -pl qiyu-live-bank-provider -am -DskipTests compile
```

Expected: `BUILD SUCCESS`.

### Task 4: Replace Asynchronous Gift Deduction with a Short Transaction

**Files:**
- Modify: `qiyu-live-bank-provider/src/main/java/org/qiyu/live/bank/provider/service/impl/QiyuCurrencyAccountServiceImpl.java`

**Consumes:** `bizId`, the durable trade lookup, and `IQiyuCurrencyAccountMapper.decrease` returning affected rows.

**Produces:** A synchronous and idempotent `consumeForSendGift`.

- [ ] **Step 1: Validate request data before opening the settlement flow**

```java
if (accountTradeReqDTO == null || accountTradeReqDTO.getUserId() <= 0
        || accountTradeReqDTO.getNumber() <= 0
        || accountTradeReqDTO.getBizId() == null
        || accountTradeReqDTO.getBizId().isBlank()) {
    return AccountTradeRespDTO.buildFail(0L, "送礼扣费参数不合法", 1);
}
```

- [ ] **Step 2: Add a local transaction annotation to `consumeForSendGift`**

```java
@Transactional(rollbackFor = Exception.class)
```

- [ ] **Step 3: Return idempotent success when the trade already exists**

```java
if (qiyuCurrencyTradeService.existsByBizIdAndType(
        accountTradeReqDTO.getBizId(), TradeTypeEnum.SEND_GIFT_TRADE.getCode())) {
    return AccountTradeRespDTO.buildSuccess(accountTradeReqDTO.getUserId(), "送礼已处理");
}
```

- [ ] **Step 4: Run the existing conditional balance-decrease SQL and reject insufficient balance**

```java
int affectedRows = qiyuCurrencyAccountMapper.decrease(
        accountTradeReqDTO.getUserId(), accountTradeReqDTO.getNumber());
if (affectedRows != 1) {
    return AccountTradeRespDTO.buildFail(accountTradeReqDTO.getUserId(), "余额不足", 4);
}
```

- [ ] **Step 5: Write the gift trade in the same transaction**

```java
qiyuCurrencyTradeService.insertOne(
        accountTradeReqDTO.getUserId(),
        accountTradeReqDTO.getNumber(),
        TradeTypeEnum.SEND_GIFT_TRADE.getCode(),
        accountTradeReqDTO.getBizId());
```

- [ ] **Step 6: Register Redis cache deletion for after transaction commit**

```java
String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(accountTradeReqDTO.getUserId());
TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
    @Override
    public void afterCommit() {
        redisTemplate.delete(cacheKey);
    }
});
return AccountTradeRespDTO.buildSuccess(accountTradeReqDTO.getUserId(), "消费成功");
```

Add these imports:

```java
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
```

- [ ] **Step 7: Compile bank provider**

```powershell
mvn -s .mvn/settings.xml -pl qiyu-live-bank-provider -am -DskipTests compile
```

Expected: `BUILD SUCCESS`.

### Task 5: Pass the Gift Business ID and Repair the API Entry

**Files:**
- Modify: `qiyu-live-gift-provider/src/java/org/qiyu/live/gift/provider/consumer/SendGiftConsumer.java`
- Modify: `qiyu-live-api/src/main/java/org/qiyu/live/api/controller/GiftController.java`

**Consumes:** Synchronous `consumeForSendGift` from Task 4.

**Produces:** One generated gift request reaches the bank transaction with its `uuId`.

- [ ] **Step 1: Pass `SendGiftMQ.uuId` to bank-provider**

```java
accountTradeReqDTO.setBizId(sendGiftMQ.getUuId());
```

- [ ] **Step 2: Comment out the pre-deduction Redis idempotency block from the consumer**

Comment out the five-minute `setIfAbsent` block and add a comment that it cannot be the money-deduction guard. The MySQL unique index is now the durable guard. Keep message exceptions unhandled so RocketMQ can redeliver; bank-provider will safely return idempotent success for an already-settled `bizId`.

- [ ] **Step 3: Invoke the gift service from the controller**

Replace the empty success return with:

```java
giftService.sendGift(giftReqVO);
return WebResponseVO.success();
```

- [ ] **Step 4: Compile all affected modules**

```powershell
mvn -s .mvn/settings.xml -pl qiyu-live-api,qiyu-live-gift-provider -am -DskipTests compile
```

Expected: `BUILD SUCCESS`.

### Task 6: Manually Verify the Two Critical Cases

**Files:**
- No source changes.

**Consumes:** Running MySQL, Redis, RocketMQ, Nacos, API, bank-provider, and gift-provider.

- [ ] **Step 1: Test an insufficient-balance gift**

Send one gift whose `price` exceeds `t_qiyu_currency_account.current_balance`.

Expected: no balance change, no `t_qiyu_currency_trade` row for the message `uuId`, and IM receives `LIVING_ROOM_SEND_GIFT_FAIL`.

- [ ] **Step 2: Test duplicate delivery with the same `uuId`**

Publish the same `SendGiftMQ` JSON twice, retaining the identical `uuId`.

Expected: balance decreases once and exactly one `t_qiyu_currency_trade` row exists for that `biz_id` and `SEND_GIFT_TRADE` type.

```sql
SELECT user_id, number, type, biz_id
FROM t_qiyu_currency_trade
WHERE biz_id = 'replace-with-the-same-uuid';
```

- [ ] **Step 3: Record the result before payment work begins**

Keep the test UUID and the matching database query result. Payment recharge will reuse the same durable-idempotency pattern with `t_pay_order.order_id`.
