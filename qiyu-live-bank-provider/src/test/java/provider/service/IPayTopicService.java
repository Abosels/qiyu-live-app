package provider.service;

import provider.dao.po.PayTopicPO;

public interface IPayTopicService {
    /**
     * 根据code查询
     */
    PayTopicPO getPayTopicById(Integer code);

    PayTopicPO getByCode(Integer bizCode);
}
