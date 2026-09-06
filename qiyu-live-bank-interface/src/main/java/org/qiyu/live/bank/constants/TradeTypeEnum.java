package org.qiyu.live.bank.constants;

public enum TradeTypeEnum {
    SEND_GIFT_TRADE(0,"送礼物交易"),
    LIVING_RECHARGE_TRADE(1,"直播间充值"),
    RED_PACKET_REWARD_TRADE(2, "红包奖励入账"),
    /** 用户使用虚拟币支付直播带货订单。 */
    SHOP_ORDER_PAY_TRADE(3, "直播间商品订单支付");

    int code;
    String desc;
    TradeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
