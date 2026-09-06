package org.qiyu.live.bank.constants;

/**
 * 支付渠道类型
 */
public enum PaySourceEnum {

    QIYU_LIVING_ROOM(1,"旗鱼直播间内支付"),
    QIYU_USER_CENTER(2,"旗鱼用户中心内支付");

    private final Integer code;
    private final String desc;

    PaySourceEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PaySourceEnum find(Integer code) {
       for(PaySourceEnum value : PaySourceEnum.values()) {
            if(value.getCode().equals(code)) {
                return value;
            }
       }
       return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
