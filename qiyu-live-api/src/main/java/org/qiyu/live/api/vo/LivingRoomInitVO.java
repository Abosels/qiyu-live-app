package org.qiyu.live.api.vo;

import lombok.Data;

@Data
public class LivingRoomInitVO {
    private Integer id;
    private String nickName;
    //观众头像
    private String watcherAvatar;
    private Long userId;
    private boolean anchor;
    /**
     * 当前直播的主播id
     */
    private Long anchorId;
    private Integer roomId;
    private String roomName;
    private Integer type;
    private Integer goodNum;
    private String anchorNickName;
    private String avatar;
    //默认背景图
    private String defaultBgImg;
    private String redPacketConfigCode;

}
