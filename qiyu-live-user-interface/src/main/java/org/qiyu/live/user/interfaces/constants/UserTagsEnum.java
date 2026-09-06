package org.qiyu.live.user.interfaces.constants;

public enum UserTagsEnum {

    IS_VIP(1L << 0, "是否会员", "tag_info1"),
    IS_OLD_USER(1L << 1, "是否老用户", "tag_info1"),
    IS_GOLD_FATHER(1L << 2, "是否金主", "tag_info1"),
    FINISH_TASK(1L << 3, "是否完成任务", "tag_info1"),
    SHOW_IN_LIVE_RECOMMEND(1L << 4, "是否直播推荐曝光", "tag_info1"),
    ALWAYS_WATCH_LIVE(1L << 5, "是否常看直播", "tag_info1");

    private final long tag;
    private final String desc;
    private final String fieldName;

    UserTagsEnum(long tag, String desc, String fieldName) {
        this.tag = tag;
        this.desc = desc;
        this.fieldName = fieldName;
    }

    public long getTag() {
        return tag;
    }

    public String getDesc() {
        return desc;
    }

    public String getFieldName() {
        return fieldName;
    }
}
