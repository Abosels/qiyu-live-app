package org.qiyu.live.bank.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 货币账户 DTO
 * 用于 Dubbo / Service / Controller 层参数传递
 *
 * @author qiyu
 */
@Data
public class QiyuCurrencyAccountDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 当前余额
     */
    private int currentBalance;

    /**
     * 累计充值
     */
    private int totalCharged;

    /**
     * 账户状态(0无效 1有效 2冻结)
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