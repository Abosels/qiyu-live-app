package org.qiyu.live.bank.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AccountTradeReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long userId;
    private int number;
    /** 送礼消息的唯一业务号，用于数据库级幂等。 */
    private String bizId;
    /**
     * 账户资金变动类型，对应 TradeTypeEnum。
     * 例如：充值入账、送礼扣币。
     */
    private Integer tradeType;
    private boolean isSuccess;
    private String msg;

}
