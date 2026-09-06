package provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 虚拟货币货币账户 PO
 * 对应表：t_qiyu_currency_account
 *
 * @author qiyu
 */
@Data
@TableName("t_qiyu_currency_account")
public class QiyuCurrencyAccountPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id（主键，自增）
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 当前余额
     */
    @TableField("current_balance")
    private int currentBalance;

    /**
     * 累计充值
     */
    @TableField("total_charged")
    private int totalCharged;

    /**
     * 账户状态(0无效 1有效 2冻结)
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}