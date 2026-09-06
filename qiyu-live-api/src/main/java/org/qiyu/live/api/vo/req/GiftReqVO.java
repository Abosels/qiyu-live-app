package org.qiyu.live.api.vo.req;

import lombok.Data;

@Data
public class GiftReqVO {
    private int giftId;
    private Integer roomId;
    private Long senderUserId;
    private Long receiverUserId;
    private Long receiveId;

    /**
     * 送礼场景：
     * 0 普通直播间
     * 1 PK 直播间
     */
    private Integer type;

    /**
     * 前端在一次点击送礼时生成并在网络重试中复用的业务幂等键。
     */
    private String requestId;
}
