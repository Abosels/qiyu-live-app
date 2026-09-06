package org.qiyu.live.living.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LivingRoomReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4565619837561L;
    private Integer id;
    /**
     * 当前直播的主播id
     */
    private Long anchorId;
    private String roomName;
    private Integer roomId;
    private Integer appId;
    private String coverImg;
    private Integer type;
    private int page;
    private int pageSize;

}
