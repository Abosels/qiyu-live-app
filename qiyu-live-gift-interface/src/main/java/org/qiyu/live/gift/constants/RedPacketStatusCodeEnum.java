package org.qiyu.live.gift.constants;

public enum RedPacketStatusCodeEnum {

    INVALID(0, "无效"),
    IS_PREPARE(1, "已准备"),
    HAS_SEND(2, "已发送");

    private final Integer code;
    private final String desc;

    RedPacketStatusCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
