package org.qiyu.live.bank.provider.service;

import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.dto.QiyuCurrencyAccountDTO;

public interface IQiyuCurrencyAccountService {
    /**
     * 新增用户
     * @param userId
     */
    boolean insertOne(long userId);

    /**
     * 旧兼容入口：缺少业务唯一号，禁止新资金业务调用。
     * @param userId
     * @param number
     */
    @Deprecated
    void increase(long userId, int number);

    @Deprecated
    void decrease(long userId, int number);

    /** 正式入账方法：要求携带 tradeType 和 bizId。 */
    AccountTradeRespDTO increase(AccountTradeReqDTO accountTradeReqDTO);

    /** 正式扣款方法：要求携带 tradeType 和 bizId。 */
    AccountTradeRespDTO decrease(AccountTradeReqDTO accountTradeReqDTO);

    /**
     * 查询账户
     *
     * @param userId
     * @return
     */
    QiyuCurrencyAccountDTO getByUserId(long userId);

    /**
     * 查询余额
     * @param userId
     * @return
     */
    Integer getBalance(long userId);

    /** 暂停使用：送礼必须使用包含业务唯一号的专用入口。 */
    @Deprecated
    AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO);

    void increaseForRecharge(long userId, int number, String bizId);

    /**
     * 消费中的送礼业务
     * @param accountTradeReqDTO
     * @return
     */
    AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO);
}
