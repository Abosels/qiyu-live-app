package org.qiyu.live.living.interfaces.contants;

public enum LivingRoomTypeEnum {
    DEFAULT_LIVING_ROOM(1,""),
    RK_LIVING_ROOM(2,"");

    Integer code;
    String desc;

    LivingRoomTypeEnum(Integer code, String desc) {
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
