package org.qiyu.live.gift.dto.resp;

import lombok.Data;

@Data
public class RedPacketConfigRespDTO {

    private Integer id;
    private Integer status;
    private Integer totalPrice;
    private Integer totalCount;
    private String configCode;
    private String remark;
}
