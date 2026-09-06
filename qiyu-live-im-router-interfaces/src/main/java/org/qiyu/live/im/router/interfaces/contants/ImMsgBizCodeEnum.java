package org.qiyu.live.im.router.interfaces.contants;

public enum ImMsgBizCodeEnum {

    //不仅仅这一种用法，当前开发先这一种
    LIVING_ROOM_IM_CHAT_MSG_BIZ(5555,"直播间im聊天消息"),
    LIVING_ROOM_SEND_GIFT_SUCCESS(5556,"送礼成功"),
    LIVING_ROOM_SEND_GIFT_FAIL(5557,"送礼失败"),
    // === PK 公共事件消息 ===
    LIVING_ROOM_PK_START(5558, "PK开始"),
    LIVING_ROOM_PK_SCORE_UPDATE(5559, "PK比分更新"),
    LIVING_ROOM_PK_FINISH(5560, "PK结束"),
    LIVING_ROOM_PK_CANCEL(5561, "PK取消"),
    LIVING_ROOM_PK_STATE_SYNC(5562, "PK状态同步"),
    LIVING_ROOM_PK_GIFT_EFFECT(5563, "PK礼物特效"),
    START_RED_PACKET(5564, "开启红包雨");

    int code;
    String desc;

    ImMsgBizCodeEnum(int code, String desc) {
        this.desc = desc;
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
