# Gift Settlement Design

## Goal

Repair the backend gift flow so that one gift request can deduct currency at most once, record one currency trade, and only publish the gift IM notification after the deduction succeeds.

## Scope

- Backend only; keep `POST /gift/sendGift` unchanged.
- Keep `consumeForSendGift` as the public Dubbo entry point for gift settlement.
- Do not implement frontend work, Alipay SDK work, or a distributed transaction across the bank and gift modules in this change.

## Data Model

Add `biz_id` to `t_qiyu_currency_trade`. For a gift settlement its value is `SendGiftMQ.uuId`.

Add a unique index on `(biz_id, type)`. The `SEND_GIFT_TRADE` type and a gift `biz_id` may be stored only once. The same pattern can later use `t_pay_order.order_id` for recharge settlement.

## Gift Flow

1. `GiftController.sendGift` calls `GiftService.sendGift`.
2. The API loads the configured gift price and sends a `SendGiftMQ` message with a generated `uuId`.
3. `SendGiftConsumer` calls `consumeForSendGift` and passes the sender ID, configured price, and `uuId` as `bizId`.
4. `consumeForSendGift` runs one local transaction in `bank-provider`:
   - Check whether a `SEND_GIFT_TRADE` row already exists for `bizId`.
   - If it exists, return an idempotent-success response without deducting again.
   - Execute the conditional balance-decrease SQL.
   - Insert a currency trade with `bizId` and `SEND_GIFT_TRADE`.
   - Commit. Any error rolls back the balance update and the trade insert together.
5. After the transaction commits, delete the sender balance Redis cache key.
6. Only a successful response triggers the IM gift-effect notification.

## Consistency Rules

- MySQL is the account of record. Redis is a read cache only.
- `increase`, `decrease`, and their thread-pool database handlers are not used for the repaired gift flow. They remain unchanged for now to avoid breaking unknown callers.
- The database transaction covers only balance deduction and currency-trade insertion. It must not send MQ or IM messages.
- RocketMQ and IM are at-least-once components. The durable `biz_id` guard prevents duplicate money deduction. A later outbox design is required if IM delivery itself must be exactly once.

## Known Defects Fixed

- `GiftController.sendGift` returns success without invoking the gift service.
- `AccountTradeRespDTO.buildSuccess` incorrectly sets `success` to `false`.
- `consumeForSendGift` currently reads Redis, decrements Redis, and schedules database deduction asynchronously before returning success.
- `SendGiftConsumer` uses a five-minute Redis key as its only idempotency guard; this is not durable and can discard retries.

## Verification

- An insufficient-balance gift does not add a trade row and does not send a success IM message.
- Two deliveries with the same `uuId` deduct only once and produce one trade row.
- A failure while inserting the trade rolls back the conditional balance deduction.
- Build `qiyu-live-bank-provider`, `qiyu-live-gift-provider`, and `qiyu-live-api` with required upstream modules.
