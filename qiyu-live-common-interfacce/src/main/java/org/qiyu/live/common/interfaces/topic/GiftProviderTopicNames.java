package org.qiyu.live.common.interfaces.topic;

public class GiftProviderTopicNames {
    /**
     * 移除礼物信息的缓存
     */
    public static final String REMOVE_GIFT_TOPIC = "remove_gift_topic";

    public static final String SEND_GIFT = "send_gift";
    /**
     * PK 计分消息 Topic，由 SendGiftConsumer 发送，PkScoreConsumer 消费
     */
    public static final String PK_SCORE = "pk_score";

    public static final String RECEIVE_RED_PACKET  = "receive_red_packet";

    /**
     * 延迟回调处理库存回滚问题
     */
    public static final String ROLL_BACK_STOCK = "roll_back_stock";
}
