package org.qiyu.live.living.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LivingRoomRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -9123784350395021L;
    private Integer id;
    private Long anchorId;
    private String roomName;
    private Integer roomId;
    private String coverImg;
    private Integer type;
    private int page;
    private int pageSize;
}
