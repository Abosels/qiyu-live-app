package org.qiyu.live.gift.dto.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class RedPacketConfigReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long anchorId;
    private Integer roomId;
    private Integer status;
    private String remark;
    private Long userId;
    private Integer totalPrice;
    private Integer totalCount;
    private String configCode;
}
