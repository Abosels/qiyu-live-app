package org.qiyu.live.bank.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付成功后的可靠 MQ 事件，避免数据库提交后 MQ 短暂不可用导致通知丢失。
 */
@Data
@TableName("t_pay_notify_outbox")
public class PayNotifyOutboxPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String bizKey;
    private String topic;
    private String messageBody;
    /** 0=待发送，1=发送中，2=已发送。 */
    private Integer status;
    private Integer retryCount;
    private LocalDateTime nextRetryTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
