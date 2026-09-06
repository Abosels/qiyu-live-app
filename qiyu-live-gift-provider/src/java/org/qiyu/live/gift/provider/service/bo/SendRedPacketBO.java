package org.qiyu.live.gift.provider.service.bo;

import lombok.Data;
import org.qiyu.live.gift.dto.req.RedPacketConfigReqDTO;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SendRedPacketBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private RedPacketConfigReqDTO reqDTO;
    private Integer price;

    /** 领取事件唯一号，用于账户入账幂等。 */
    private String bizId;
}
