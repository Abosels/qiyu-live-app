package org.qiyu.live.gift.dto.resp;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ShopCartRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Integer roomId;
    private List<ShopCartItemRespDTO> shopCartItemRespDTOS;

}
