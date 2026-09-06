package org.qiyu.live.common.interfaces.enums;

public enum CommonStatusEnum {

    // 有效状态：手机号已绑定、可正常查询使用
    VALID_STATUS(1 , "有效"),
    // 无效状态：手机号已失效、被逻辑删除或禁用
    INVALID_STATUS(0 , "无效");

    private final Integer code;
    private final String desc;

    CommonStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}