package provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.dto.QiyuCurrencyAccountDTO;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import provider.service.IQiyuCurrencyAccountService;

@DubboService
public class QiyuCurrencyAccountRpcImpl implements IQiyuCurrencyAccountRpc {

    @Resource
    private IQiyuCurrencyAccountService qiyuCurrencyAccountService;

    @Override
    public void increase(long userId, int number) {
        qiyuCurrencyAccountService.increase(userId, number);
    }

    @Override
    public void decrease(long userId, int number) {
        qiyuCurrencyAccountService.decrease(userId, number);
    }

    @Override
    public AccountTradeRespDTO increase(AccountTradeReqDTO accountTradeReqDTO) {
        return qiyuCurrencyAccountService.increase(accountTradeReqDTO);
    }

    @Override
    public AccountTradeRespDTO decrease(AccountTradeReqDTO accountTradeReqDTO) {
        return qiyuCurrencyAccountService.decrease(accountTradeReqDTO);
    }

    @Override
    public QiyuCurrencyAccountDTO getByUserId(long userId) {
        return qiyuCurrencyAccountService.getByUserId(userId);
    }

    /**
     * 查询余额
     *
     * @param userId
     * @return
     */
    @Override
    public Integer getBalance(long userId) {
        return qiyuCurrencyAccountService.getBalance(userId);
    }

    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        return qiyuCurrencyAccountService.consumeForSendGift(accountTradeReqDTO);
    }

}
