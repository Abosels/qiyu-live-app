package org.qiyu.live.msg.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@Data
public class MessageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -564136813574646164L;

    private Long userId;
    private Integer roomId;
    //发送人姓名和头像
    private String senderName;
    private String senderAvatar;
    /**
     * 消息类型
     */
    private Integer msgType;
    /**
     * 消息内容
     */
    private String content;
    private Date createTime;
    private Date updateTime;
}
