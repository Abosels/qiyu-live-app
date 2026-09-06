package org.qiyu.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.qiyu.live.bank.provider.dao.mapper.IQiyuCurrencyTradeMapper;
import org.qiyu.live.bank.provider.dao.po.QiyuCurrencyTradePO;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyTradeService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QiyuCurrencyTradeServiceImpl implements IQiyuCurrencyTradeService {

    @Resource
    private IQiyuCurrencyTradeMapper qiyuCurrencyTradeMapper;

    @Override
    public boolean insertOne(long userId, int number, int type) {
        try{
            QiyuCurrencyTradePO qiyuCurrencyTradePO = new QiyuCurrencyTradePO();
            qiyuCurrencyTradePO.setUserId(userId);
            qiyuCurrencyTradePO.setNumber(number);
            qiyuCurrencyTradePO.setType(type);
            qiyuCurrencyTradeMapper.insert(qiyuCurrencyTradePO);
            return true;
        }catch (Exception e){
            log.error("[QiyuCurrencyTradeServiceImpl] insert error , error is", e);
        }
        return false;
    }

    @Override
    public boolean existsByBizIdAndType(String bizId, int type) {
        return qiyuCurrencyTradeMapper.selectCount(new LambdaQueryWrapper<QiyuCurrencyTradePO>()
                .eq(QiyuCurrencyTradePO::getBizId, bizId)
                .eq(QiyuCurrencyTradePO::getType, type)) > 0;
    }

    @Override
    public void insertOneOrThrow(long userId, int number, int type, String bizId) {
        QiyuCurrencyTradePO tradePO = new QiyuCurrencyTradePO();
        tradePO.setUserId(userId);
        tradePO.setNumber(number);
        tradePO.setType(type);
        tradePO.setBizId(bizId);
        qiyuCurrencyTradeMapper.insert(tradePO);
    }
}
