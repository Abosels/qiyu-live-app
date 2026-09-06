package org.qiyu.live.bank.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_qiyu_currency_trade")
public class QiyuCurrencyTradePO {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 流水金额
     */
    private int number;

    /**
     * 流水类型（1充值、2送礼扣费、3主播收益入账等自行枚举定义）
     */
    private Integer type;

    /** 业务唯一号，和交易类型共同受数据库唯一索引保护。 */
    private String bizId;

    /**
     * 状态：0无效 1有效
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
