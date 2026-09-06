package provider.service;

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
     * 增加虚拟币
     * @param userId
     * @param number
     */
    void increase(long userId, int number);

    /**
     * 扣除虚拟币
     * @param userId
     * @param number
     */
    void decrease(long userId, int number);

    AccountTradeRespDTO increase(AccountTradeReqDTO accountTradeReqDTO);
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

    AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO);

    /**
     * 消费中的送礼业务
     * @param accountTradeReqDTO
     * @return
     */
    AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO);
}
