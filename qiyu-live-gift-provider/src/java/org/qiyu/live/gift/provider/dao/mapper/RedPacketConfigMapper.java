package org.qiyu.live.gift.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;

@Mapper
public interface RedPacketConfigMapper extends BaseMapper<RedPacketConfigPO> {

    @Update("UPDATE t_red_packet_config "
            + "SET total_get_price = total_get_price + #{price}, "
            + "max_get_price = GREATEST(max_get_price, #{price}) "
            + "WHERE config_code = #{configCode}")
    int increaseTotalGetPrice(@Param("configCode") String configCode, @Param("price") Integer price);

    @Update("UPDATE t_red_packet_config SET total_get = total_get + 1 WHERE config_code = #{configCode}")
    int increaseTotalGet(@Param("configCode") String configCode);
}
