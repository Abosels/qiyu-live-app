package provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_pay_order")
public class PayOrderPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 外部订单唯一编号
     */
    private String orderId;

    /**
     * 付费产品ID
     */
    private Integer productId;

    /**
     * 订单状态 0待支付,1支付中,2已支付,3撤销,4无效
     */
    private Integer status;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付渠道 0支付宝 1微信 2银联 3收银台
     */
    private Integer payChannel;

    /**
     * 来源
     */
    private Integer source;

    /**
     * 支付成功回调时间
     */
    private LocalDateTime payTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
