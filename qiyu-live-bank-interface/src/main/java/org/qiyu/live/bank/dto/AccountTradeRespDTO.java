package org.qiyu.live.bank.dto;

import lombok.Data;
import org.qiyu.live.bank.constants.TradeTypeEnum;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AccountTradeRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer type;
    private Integer status;
    private int code;
    private long userId;
    private int number;
    private boolean isSuccess;
    private String msg;
    /**
     * 本次业务是否首次完成资金变动。
     * false 表示相同 bizId + tradeType 已处理过。
     */
    private boolean firstProcessed;

    public static AccountTradeRespDTO buildTradeSuccess(
            long userId, int tradeType, String msg, boolean firstProcessed) {
        AccountTradeRespDTO dto = buildSuccess(userId, msg);
        dto.setType(tradeType);
        dto.setFirstProcessed(firstProcessed);
        return dto;
    }

    /**
     * 构建失败响应，失败请求不会被视为首次处理。
     */
    public static AccountTradeRespDTO buildFail(long userId, String msg, int code) {
        AccountTradeRespDTO dto = new AccountTradeRespDTO();
        dto.setUserId(userId);
        dto.setCode(code);
        dto.setMsg(msg);
        dto.setSuccess(false);
        dto.setFirstProcessed(false);
        return dto;
    }

    public static AccountTradeRespDTO buildSuccess(long userId, String msg) {
        AccountTradeRespDTO dto = new AccountTradeRespDTO();
        dto.setUserId(userId);
        dto.setMsg(msg);
        dto.setSuccess(true);
        return dto;
    }
    /**
     * 兼容旧送礼调用，新的业务应使用 buildTradeSuccess 并显式传入流水类型。
     */
    @Deprecated
    public static AccountTradeRespDTO buildGiftConsumeSuccess(
            long userId, String msg, boolean firstProcessed) {
        return buildTradeSuccess(userId, TradeTypeEnum.SEND_GIFT_TRADE.getCode(), msg, firstProcessed);
    }
}
