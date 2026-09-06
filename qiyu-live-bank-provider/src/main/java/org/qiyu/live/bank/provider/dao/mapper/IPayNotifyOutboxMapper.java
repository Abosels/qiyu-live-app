package org.qiyu.live.bank.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.bank.provider.dao.po.PayNotifyOutboxPO;

/** 支付可靠消息表的数据访问接口。 */
@Mapper
public interface IPayNotifyOutboxMapper extends BaseMapper<PayNotifyOutboxPO> {
}
