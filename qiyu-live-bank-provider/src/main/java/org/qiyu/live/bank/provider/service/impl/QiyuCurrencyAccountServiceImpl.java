package org.qiyu.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.idea.qiyu.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import org.qiyu.live.bank.constants.TradeTypeEnum;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.dto.QiyuCurrencyAccountDTO;
import org.qiyu.live.bank.provider.dao.mapper.IQiyuCurrencyAccountMapper;
import org.qiyu.live.bank.provider.dao.po.QiyuCurrencyAccountPO;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyAccountService;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyTradeService;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class QiyuCurrencyAccountServiceImpl implements IQiyuCurrencyAccountService {

    @Resource
    private IQiyuCurrencyAccountMapper qiyuCurrencyAccountMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private BankProviderCacheKeyBuilder bankProviderCacheKeyBuilder;
    @Resource
    private IQiyuCurrencyTradeService qiyuCurrencyTradeService;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,4,30,TimeUnit.MINUTES,new ArrayBlockingQueue<>(1000));

    @Override
    public boolean insertOne(long userId) {
        try{
            QiyuCurrencyAccountPO accountPO = new QiyuCurrencyAccountPO();
            accountPO.setUserId(userId);
            qiyuCurrencyAccountMapper.insert(accountPO);
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return false;
    }

    @Override
    public void increase(long userId, int number) {
        String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(userId);
        if(redisTemplate.hasKey(cacheKey)){
            redisTemplate.opsForValue().increment(cacheKey,number);
            redisTemplate.expire(cacheKey,5,TimeUnit.MINUTES);
        }
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //分布式架构下，cap理论，可用性和性能，柔弱的一致性
                //在一步线程池中完成数据库层的扣减和流水记录插入操作，带有事务
                consumeIcreBHandler(userId, number, TradeTypeEnum.LIVING_RECHARGE_TRADE);
            }
        });
    }

    @Override
    public void decrease(long userId, int number) {
        //扣减余额
        String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(userId);
        if(redisTemplate.hasKey(cacheKey)){
            //基于redis的扣减操作
            redisTemplate.opsForValue().decrement(cacheKey,number);
            redisTemplate.expire(cacheKey,5,TimeUnit.MINUTES);
        }
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //分布式架构下，cap理论，可用性和性能，柔弱的一致性
                //在一步线程池中完成数据库层的扣减和流水记录插入操作，带有事务
                consumeDcreBHandler(userId, number, TradeTypeEnum.SEND_GIFT_TRADE);
            }
        });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountTradeRespDTO increase(AccountTradeReqDTO accountTradeReqDTO) {
        AccountTradeRespDTO invalidResp = validateTradeRequest(accountTradeReqDTO);
        if (invalidResp != null) {
            return invalidResp;
        }
        int tradeType = accountTradeReqDTO.getTradeType();
        if (tradeType != TradeTypeEnum.LIVING_RECHARGE_TRADE.getCode()
                && tradeType != TradeTypeEnum.RED_PACKET_REWARD_TRADE.getCode()) {
            return AccountTradeRespDTO.buildFail(accountTradeReqDTO.getUserId(), "该流水类型不允许入账", 2);
        }
        long userId = accountTradeReqDTO.getUserId();
        int number = accountTradeReqDTO.getNumber();
        String bizId = accountTradeReqDTO.getBizId();
        if (qiyuCurrencyTradeService.existsByBizIdAndType(bizId, tradeType)) {
            return AccountTradeRespDTO.buildTradeSuccess(userId, tradeType, "充值已处理", false);
        }
        if (qiyuCurrencyAccountMapper.increase(userId, number) != 1) {
            throw new IllegalStateException("充值账户不存在，到账失败");
        }
        qiyuCurrencyTradeService.insertOneOrThrow(userId, number, tradeType, bizId);
        clearBalanceCacheAfterCommit(userId);
        return AccountTradeRespDTO.buildTradeSuccess(userId, tradeType, "充值成功", true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountTradeRespDTO decrease(AccountTradeReqDTO accountTradeReqDTO) {
        AccountTradeRespDTO invalidResp = validateTradeRequest(accountTradeReqDTO);
        if (invalidResp != null) {
            return invalidResp;
        }
        int tradeType = accountTradeReqDTO.getTradeType();
        if (tradeType != TradeTypeEnum.SEND_GIFT_TRADE.getCode()
                && tradeType != TradeTypeEnum.SHOP_ORDER_PAY_TRADE.getCode()) {
            return AccountTradeRespDTO.buildFail(accountTradeReqDTO.getUserId(), "该流水类型不允许扣款", 2);
        }
        long userId = accountTradeReqDTO.getUserId();
        int number = accountTradeReqDTO.getNumber();
        String bizId = accountTradeReqDTO.getBizId();
        if (qiyuCurrencyTradeService.existsByBizIdAndType(bizId, tradeType)) {
            return AccountTradeRespDTO.buildTradeSuccess(userId, tradeType, "扣款已处理", false);
        }
        if (qiyuCurrencyAccountMapper.decrease(userId, number) != 1) {
            return AccountTradeRespDTO.buildFail(userId, "余额不足，扣减失败", 4);
        }
        qiyuCurrencyTradeService.insertOneOrThrow(userId, number, tradeType, bizId);
        clearBalanceCacheAfterCommit(userId);
        return AccountTradeRespDTO.buildTradeSuccess(userId, tradeType, "扣款成功", true);
    }

    @Override
    public QiyuCurrencyAccountDTO getByUserId(long userId) {
        return ConvertBeanUtils.convert(qiyuCurrencyAccountMapper.selectById(userId), QiyuCurrencyAccountDTO.class);
    }

    @Override
    public Integer getBalance(long userId) {
        String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(userId);
        Object cacheBalance = redisTemplate.opsForValue().get(cacheKey);
        if (cacheBalance != null) {
            if ((Integer)cacheBalance == -1){
                return null;
            }
            return (Integer)cacheBalance;
        }
        Integer currentBalance = qiyuCurrencyAccountMapper.queryBalance(userId);
        if (currentBalance == null) {
            redisTemplate.opsForValue().set(cacheKey,-1);
            return null;
        }
        redisTemplate.opsForValue().set(cacheKey,currentBalance,30, TimeUnit.MINUTES);
        return currentBalance;
    }

    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO) {
        // Legacy implementation retained for reference; it used asynchronous trade persistence.
        if (false) {
        // 1. 请求对象非空校验
        if (accountTradeReqDTO == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        long userId = accountTradeReqDTO.getUserId();
        // 2. userId 合法性校验：必须大于 0（数据库主键约束）
        if (userId <= 0) {
            log.error("[consume] userId 不合法, userId={}", userId);
            return AccountTradeRespDTO.buildFail(userId, "userId不合法", 1);
        }
        int number = accountTradeReqDTO.getNumber();
        // 3. 扣减数量合法性校验：必须大于 0
        if (number <= 0) {
            log.error("[consume] 扣减数量不合法, userId={}, number={}", userId, number);
            return AccountTradeRespDTO.buildFail(userId, "扣减数量必须大于0", 2);
        }
        // 4. 查询账户是否存在（仅校验存在性，不用余额做并发判断）
        QiyuCurrencyAccountDTO accountDTO = this.getByUserId(userId);
        if (accountDTO == null) {
            return AccountTradeRespDTO.buildFail(userId, "账户未有初始化", 3);
        }
        /*
         * 5. 原子扣减：将余额判断和更新合并到一条 SQL 中执行。
         *    Mapper 的 decrease 方法使用：
         *      UPDATE t_qiyu_currency_account
         *      SET current_balance = current_balance - #{number}
         *      WHERE user_id = #{userId} AND current_balance >= #{number}
         *    多线程并发消费时，MySQL 行锁保证只有一个线程能执行成功，
         *    其他线程会因为 current_balance >= #{number} 条件不满足而返回 0。
         *    禁止采用"先查询余额 → Java 中比较 → 再更新"的方式，存在超扣风险。
         */
        int affectedRows = qiyuCurrencyAccountMapper.decrease(userId, number);
        if (affectedRows == 0) {
            // 返回 0 表示余额不足或账户不存在（行锁竞争失败）
            log.warn("[consume] 扣减失败，余额不足或账户异常, userId={}, number={}", userId, number);
            return AccountTradeRespDTO.buildFail(userId, "余额不足，扣减失败", 4);
        }
        // 6. 同步更新 Redis 缓存，避免下次 getBalance() 读到过期旧值
        String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(userId);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
            redisTemplate.opsForValue().decrement(cacheKey, number);
        }
        // 7. 异步插入交易流水记录（与 decrease() 策略一致，最终一致性）
        threadPoolExecutor.execute(() -> {
            qiyuCurrencyTradeService.insertOne(userId, number, TradeTypeEnum.SEND_GIFT_TRADE.getCode());
        });
        // 8. 扣减成功后重新查询最新账户余额，组装返回结果
        QiyuCurrencyAccountDTO latestAccount = this.getByUserId(userId);
        AccountTradeRespDTO respDTO = new AccountTradeRespDTO();
        respDTO.setUserId(userId);
        respDTO.setNumber(latestAccount != null ? latestAccount.getCurrentBalance() : 0);
        respDTO.setSuccess(true);
        respDTO.setMsg("消费成功");
        return respDTO;
        }
        if (accountTradeReqDTO != null) {
            accountTradeReqDTO.setTradeType(TradeTypeEnum.SEND_GIFT_TRADE.getCode());
        }
        return decrease(accountTradeReqDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {

        if (accountTradeReqDTO != null) {
            accountTradeReqDTO.setTradeType(TradeTypeEnum.SEND_GIFT_TRADE.getCode());
        }
        return decrease(accountTradeReqDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseForRecharge(long userId, int number, String bizId) {

        AccountTradeReqDTO accountTradeReqDTO = new AccountTradeReqDTO();
        accountTradeReqDTO.setUserId(userId);
        accountTradeReqDTO.setNumber(number);
        accountTradeReqDTO.setBizId(bizId);
        accountTradeReqDTO.setTradeType(TradeTypeEnum.LIVING_RECHARGE_TRADE.getCode());
        AccountTradeRespDTO tradeRespDTO = increase(accountTradeReqDTO);
        if (!tradeRespDTO.isSuccess()) {
            throw new IllegalStateException(tradeRespDTO.getMsg());
        }
    }

    /** 只在事务提交后清缓存，避免回滚时误删有效缓存。 */
    private void clearBalanceCacheAfterCommit(long userId) {
        String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(userId);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                redisTemplate.delete(cacheKey);
            }
        });
    }

    /** 校验正式资金变动必需的幂等键、金额和流水类型。 */
    private AccountTradeRespDTO validateTradeRequest(AccountTradeReqDTO accountTradeReqDTO) {
        if (accountTradeReqDTO == null || accountTradeReqDTO.getUserId() <= 0
                || accountTradeReqDTO.getNumber() <= 0 || accountTradeReqDTO.getTradeType() == null
                || accountTradeReqDTO.getBizId() == null || accountTradeReqDTO.getBizId().isBlank()) {
            return AccountTradeRespDTO.buildFail(0L, "资金变动参数不合法", 1);
        }
        return null;
    }

    /**
     * 异步处理充值入账的 DB 更新和流水记录
     *
     * @param userId   用户 ID
     * @param number   金额
     * @param tradeType 交易类型，区分充值/送礼等场景
     */
    private void consumeIcreBHandler(long userId, int number, TradeTypeEnum tradeType) {
        //更新db，插入db
        qiyuCurrencyAccountMapper.increase(userId, number);
        //流水记录
        qiyuCurrencyTradeService.insertOne(userId, number, tradeType.getCode());
    }

    /**
     * 异步处理余额扣减的 DB 更新和流水记录
     *
     * @param userId   用户 ID
     * @param number   金额
     * @param tradeType 交易类型，区分送礼/退款等场景
     */
    private void consumeDcreBHandler(long userId, int number, TradeTypeEnum tradeType) {
        //更新db，插入db
        qiyuCurrencyAccountMapper.decrease(userId, number);
        //流水记录
        qiyuCurrencyTradeService.insertOne(userId, number, tradeType.getCode());
    }
}
