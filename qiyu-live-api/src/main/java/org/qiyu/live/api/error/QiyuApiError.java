package org.qiyu.live.api.error;

import org.qiyu.live.web.starter.error.QiyuBaseError;

/**
 * API 层统一错误枚举
 * 每个枚举值定义业务错误码和错误描述，
 * 配合 {@link org.qiyu.live.web.starter.error.ErrorAssert} 使用，
 * 断言失败后由 {@link org.qiyu.live.web.starter.error.GlobalExceptionHandler} 统一处理。
 */
public enum QiyuApiError implements QiyuBaseError {

    LIVING_ROOM_TYPE_MISSING(10001, "需要给定直播间类型"),
    PHONE_NOT_BLANK(10002, "手机号不能为空"),
    PHONE_IN_VALID(10003, "手机号格式异常"),
    LOGIN_CODE_IN_VALID(10004, "验证码格式异常"),
    SMS_SEND_FAIL(10005, "短信发送失败，请稍后重试"),
    SMS_CODE_ERROR(10006, "验证码校验失败"),
    LOGIN_FAIL(10007, "用户登录失败"),
    GIFT_CONFIG_ERROR(10008, "礼物配置异常"),
    GIFT_SEND_ERROR(10009, "送礼失败"),
    LIVING_ROOM_END(10010, "Living room has ended or does not exist"),
    USER_NOT_FOUND(10011, "User does not exist"),
    SKU_IS_NOT_ENOUGH(10012, "商品库存不足,请重写下单"),
    PAY_ERROR(10013, "支付异常,请重新下单"),;

    private final int code;
    private final String desc;

    QiyuApiError(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /*
     * [修复] 原实现 getErrorCode() 返回 0、getErrorMsg() 返回 ""，
     * 导致 ErrorAssert 抛出的 QiyuErrorException 携带空错误信息，
     * GlobalExceptionHandler 捕获后返回 bizError("", 0)，前端无法感知真实错误。
     * 现改为返回枚举实例中定义的 code 和 desc。
     */
    @Override
    public int getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.desc;
    }
}
