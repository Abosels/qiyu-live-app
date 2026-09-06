package org.qiyu.live.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class RedPacketReceiveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer price;
    private String notifyMsg;

    public RedPacketReceiveDTO(Integer price) {
        this.price = price;
    }
    public RedPacketReceiveDTO(Integer price,String notifyMsg) {
        this.price = price;
        this.notifyMsg = notifyMsg;
    }
}
