package org.qiyu.live.gift.constants;

public enum SendGiftTypeEnum {

    DEFAULT_SEND_GIFT_TYPE(0, "普通直播间送礼"),
    PK_SEND_GIFT_TYPE(1, "PK直播间送礼");

    private final Integer code;
    private final String desc;

    SendGiftTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static SendGiftTypeEnum findByCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (SendGiftTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}