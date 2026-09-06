package org.qiyu.live.gift.dto.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PrepareOrderReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Integer roomId;
}
