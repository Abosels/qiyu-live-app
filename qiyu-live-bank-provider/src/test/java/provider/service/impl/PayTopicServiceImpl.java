package provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.springframework.stereotype.Service;
import provider.dao.mapper.IPayTopicMapper;
import provider.dao.po.PayTopicPO;
import provider.service.IPayTopicService;

@Service
public class PayTopicServiceImpl implements IPayTopicService {
    @Resource
    private IPayTopicMapper payTopicMapper;

    @Override
    public PayTopicPO getPayTopicById(Integer code) {
        LambdaQueryWrapper<PayTopicPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayTopicPO::getBizCode, code);
        queryWrapper.eq(PayTopicPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return payTopicMapper.selectOne(queryWrapper);
    }

    @Override
    public PayTopicPO getByCode(Integer bizCode) {

        return null;
    }
}
