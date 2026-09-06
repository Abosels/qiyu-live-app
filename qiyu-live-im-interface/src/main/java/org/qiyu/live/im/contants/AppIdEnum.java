package org.qiyu.live.im.contants;

public enum AppIdEnum {
    QIYU_LIVE_BIZ(10001,"奇遇直播业务");

    int code;
    String desc;
    AppIdEnum(int code, String desc) {
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
