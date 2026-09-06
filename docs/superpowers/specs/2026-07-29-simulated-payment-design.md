# Simulated Payment Design

**Goal:** Implement a simulated payment flow that follows the Alipay sandbox lifecycle while guaranteeing local MySQL settlement atomicity.

## Architecture

`qiyu-live-api` keeps the user-facing payment initiation API. `qiyu-live-bank-api` is a separate callback-only HTTP service. `qiyu-live-bank-provider` owns payment order state transitions, account crediting, trade records, and the single local transaction that settles a successful payment.

## Module Boundaries

- `qiyu-live-api`: Create payment orders and return simulated payment launch data to the caller.
- `qiyu-live-bank-api`: Receive mock Alipay/WeChat notifications and forward them to the provider through Dubbo.
- `qiyu-live-bank-interface`: DTOs, enums, and Dubbo contracts only.
- `qiyu-live-bank-provider`: Payment state machine, amount checks, synchronous settlement, and post-commit cache invalidation.

## Settlement Invariant

One successful `orderId` performs exactly once, within one MySQL transaction:

1. Conditionally transition the order from `WAITING_PAY` or `PAYING` to `PAYED`.
2. Increase the user's currency balance synchronously.
3. Insert exactly one `LIVING_RECHARGE_TRADE` record.
4. Store the simulated third-party transaction id and payment time.

If any operation fails, the transaction rolls back. Redis and RocketMQ are outside the settlement transaction: Redis is invalidated or refreshed only after commit, and MQ is not part of the simulated-payment settlement path.

## Payment Channels

- Alipay: Mock the sandbox lifecycle now; the future real adapter replaces only channel integration and signature verification.
- WeChat: Mock only. No claim of real WeChat Pay integration.

Only Alipay and WeChat are accepted payment channels.

## Explicit Non-Goals

- Frontend implementation.
- Real funds.
- Real WeChat Pay integration.
- RocketMQ-based settlement.
