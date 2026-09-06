package org.qiyu.live.im.core.server.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ImOnlineDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4564187413754311L;
    private Long userId;
    private Integer appId;
    private Integer roomId;
    private Long loginTime;
}
