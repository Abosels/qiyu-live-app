package org.qiyu.live.bank.provider.service;

public interface IQiyuCurrencyTradeService {

    boolean insertOne(long userId, int number, int type);

    boolean existsByBizIdAndType(String bizId, int type);

    /** 插入失败必须抛出，让外层资产事务回滚。 */
    void insertOneOrThrow(long userId, int number, int type, String bizId);
}
