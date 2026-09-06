package org.qiyu.live.bank.interfaces;

import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.dto.QiyuCurrencyAccountDTO;

public interface IQiyuCurrencyAccountRpc {

    /**
     * 旧兼容入口：缺少业务唯一号，禁止新资金业务调用。
     * @param userId
     * @param number
     */
    @Deprecated
    void increase(long userId, int number);

    /**
     * 扣除虚拟币
     * @param userId
     * @param number
     */
    @Deprecated
    void decrease(long userId, int number);

    /** 正式入账入口：必须携带 tradeType 和 bizId。 */
    AccountTradeRespDTO increase(AccountTradeReqDTO accountTradeReqDTO);

    /** 正式扣款入口：必须携带 tradeType 和 bizId。 */
    AccountTradeRespDTO decrease(AccountTradeReqDTO accountTradeReqDTO);

    /**
     * 查询账户
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

    /**
     * 专门给送礼业务调用的扣减余额逻辑
     *
     * @param accountTradeReqDTO
     * @return
     */
    AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO);
}
