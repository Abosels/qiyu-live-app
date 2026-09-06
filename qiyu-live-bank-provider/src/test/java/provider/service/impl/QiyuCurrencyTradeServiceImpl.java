package provider.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import provider.dao.mapper.IQiyuCurrencyTradeMapper;
import provider.dao.po.QiyuCurrencyTradePO;
import provider.service.IQiyuCurrencyTradeService;

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
}
