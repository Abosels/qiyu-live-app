package org.qiyu.live.bank.constants;

/**
 * 支付渠道 0支付宝 1微信 2银联 3收银台
 */
public enum PayChannelEnum {

    ZHI_FU_BAO(0,"支付宝"),
    WEI_XIN(1,"微信"),
    YIN_LIAN(2,"银联"),
    SHOU_YIN_TAI(3,"收银台");
    int code;
    String desc;
    PayChannelEnum(int code, String desc) {
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
