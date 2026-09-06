package provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import provider.dao.po.QiyuCurrencyAccountPO;

@Mapper
public interface IQiyuCurrencyAccountMapper extends BaseMapper<QiyuCurrencyAccountPO> {

    @Update("update t_qiyu_currency_account set current_balance = current_balance + #{number} " +
            "where user_id = #{userId}")
    /**
     * 支付到账时同步增加用户余额。
     *
     * @return 受影响行数；0 表示账户不存在或更新失败
     */
    int increase(@Param("userId") long userId, @Param("number") int number);

    @Select("select current_balance from t_qiyu_currency_account where user_id = #{userId} and status = 1 limit 1")
    Integer queryBalance(@Param("userId") long userId);
    /**
     * 扣减余额，只有余额充足才会执行更新
     * @return 影响行数，1=扣款成功，0=余额不足扣款失败
     */
    @Update("update t_qiyu_currency_account set current_balance = current_balance - #{number} " +
            "where user_id = #{userId} AND current_balance >= #{number}")
    int decrease(@Param("userId") long userId, @Param("number") int number);

}
