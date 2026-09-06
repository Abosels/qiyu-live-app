package org.qiyu.live.api.vo.req;

import lombok.Data;
import org.qiyu.live.bank.constants.PaySourceEnum;

@Data
public class PayProductReqVO {
    /**
     * 产品id
     */
    private Integer productId;
    /**
     * 支付来源。直播间支付，个人中心支付，聊天界面支付，第三方界面支付，广告弹窗支付
     * @see PaySourceEnum
     */
    private Integer paySource;

    /**
     * 支付渠道
     * @see org.qiyu.live.bank.constants.PayChannelEnum
     */
    private Integer payChannel;
}
