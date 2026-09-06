# Payment Settlement and Alipay Sandbox Implementation Plan

**Goal:** Make simulated payment settlement atomic, then replace only the Alipay mock channel with a real Alipay sandbox adapter while keeping WeChat mock-only.

**Architecture:** `qiyu-live-api` creates payment orders. `qiyu-live-bank-api` receives payment notifications. `qiyu-live-bank-provider` owns the payment state machine and one local MySQL transaction for order settlement, balance crediting, and one trade record.

**Tech Stack:** Spring Boot 3, Java 21, Dubbo, MyBatis-Plus, MySQL, Redis, Nacos, Alipay sandbox SDK.

## Global Constraints

- The user manually enters every code change in the IDE.
- Each teaching step changes one file only and includes concise Chinese comments.
- Only Alipay and WeChat payment channels are accepted.
- Alipay mock is replaced with sandbox SDK after atomic settlement passes.
- WeChat remains mock-only.
- Settlement never uses an async executor, Redis mutation, or RocketMQ before the database transaction commits.

### Task 1: Correct Conditional Order State Updates

**Files:**
- Modify: `qiyu-live-bank-provider/src/main/java/org/qiyu/live/bank/provider/service/impl/PayOrderServiceImpl.java`
- Modify: `qiyu-live-bank-provider/src/main/java/org/qiyu/live/bank/provider/service/IPayOrderService.java`
- Modify: `qiyu-live-bank-interface/src/main/java/org/qiyu/live/bank/interfaces/IPayOrderRpc.java`

**Outcome:** The provider can transition a named order only from a permitted source state and reports affected rows.

### Task 2: Add Atomic Payment Settlement

**Files:**
- Modify: `PayOrderServiceImpl.java`
- Modify: `QiyuCurrencyAccountServiceImpl.java`
- Modify: `IQiyuCurrencyAccountMapper.java`
- Modify: `QiyuCurrencyTradeServiceImpl.java`

**Outcome:** A single local transaction conditionally marks an order paid, synchronously credits balance, and writes exactly one recharge trade record.

### Task 3: Create Payment Contracts and Move Callback API

**Files:**
- Create/modify: `qiyu-live-bank-interface` payment DTOs and RPC contract
- Create: `qiyu-live-bank-api` Spring Boot application and callback controller
- Modify: `qiyu-live-bank-api/pom.xml`

**Outcome:** `qiyu-live-api` remains the order-creation API while `qiyu-live-bank-api` only receives payment callbacks and calls provider by Dubbo.

### Task 4: Add Mock Channels

**Files:**
- Create: payment channel interface and Alipay/WeChat mock channel implementations in provider
- Modify: `BankServiceImpl.java`

**Outcome:** Both mock notifications use the same atomic settlement service; only Alipay and WeChat are accepted.

### Task 5: Add Nacos Configuration

**Files:**
- Modify manually in Nacos: `qiyu-live-bank-provider.yml`
- Create manually in Nacos: `qiyu-live-bank-api.yml`
- Modify: bank-api bootstrap configuration

**Outcome:** Mock is configurable; Alipay sandbox credentials are placeholders and never committed.

### Task 6: Replace Alipay Mock with Sandbox SDK

**Files:**
- Modify: provider pom and Alipay channel implementation
- Modify: bank-api Alipay callback controller

**Outcome:** The Alipay channel creates sandbox Page Pay requests and verifies sandbox notifications while the settlement service remains unchanged.

### Task 7: Verify the End-to-End Flows

**Coverage:**
- First successful notification credits once.
- Duplicate notification credits zero additional balance.
- Invalid order state, amount, or signature does not credit balance.
- New order can still be recharged after a previous order is paid.
- WeChat mock uses the same settlement path.
