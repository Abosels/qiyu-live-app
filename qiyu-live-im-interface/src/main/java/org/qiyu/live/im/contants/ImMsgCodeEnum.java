package org.qiyu.live.im.contants;


public enum ImMsgCodeEnum {

    IM_MSG_LOGIN(1001,"登录im消息包"),
    IM_MSG_LOGOUT(1002,"登出im消息包"),
    IM_MSG_BIZ(1003,"常规业务消息包"),
    IM_MSG_HEARTBEAT(1004,"im服务器心跳包"),
    IM_MSG_ACK(1005,"im服务的ack消息包");

    private int code;
    private String desc;

    ImMsgCodeEnum(int code, String desc) {
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
