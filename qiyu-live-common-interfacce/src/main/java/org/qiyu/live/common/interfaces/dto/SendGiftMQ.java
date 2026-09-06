package org.qiyu.live.common.interfaces.dto;

import lombok.Data;

@Data
public class SendGiftMQ {
    private long userId;
    private Integer giftId;
    private Integer price;
    private Integer roomId;
    private Long receiveId;
    private String uuId;
    private String url;
    private String msg;
    private String svgaUrl;
    private String giftName;
    private Integer type;
}
