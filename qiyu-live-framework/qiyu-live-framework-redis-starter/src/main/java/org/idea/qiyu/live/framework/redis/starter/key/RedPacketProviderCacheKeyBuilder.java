package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class RedPacketProviderCacheKeyBuilder extends RedisKeyBuilder{
    private static String RED_PACKET_List = "red_packet_list";
    private static String RED_PACKET_INIT_LOCK = "red_packet_init_lock";
    private static String RED_PACKET_TOTAL_GET_CACHE = "red_packet_total_get_cache";
    private static String RED_PACKET_TOTAL_GET_PRICE_CACHE = "red_packet_total_get_price_cache";
    private static String MAX_GET_PRICE_CACHE = "max_get_price_cache";
    private static String USER_TOTAL_GET_PRICE_CACHE = "user_total_get_price_cache";
    private static String RED_PACKET_PREPARE_SUCCESS = "red_packet_prepare_success";
    private static String RED_PACKET_NOTIFY = "red_packet_notify";
    private static String RED_PACKET_USER_RECEIVED = "red_packet_user_received";

    public String buildRedPocketList(String code) {
        return super.getPrefix() + RED_PACKET_List + super.getSplitItem() + code;
    }

    public String buildRedPocketInitLock(String code) {
        return super.getPrefix() + RED_PACKET_INIT_LOCK + super.getSplitItem() + code;
    }

    public String buildRedPocketTotalGetCache(String code) {
        return super.getPrefix() + RED_PACKET_TOTAL_GET_CACHE + super.getSplitItem() + code;
    }

    public String buildRedPocketTotalGetPriceCache(String code) {
        return super.getPrefix() + RED_PACKET_TOTAL_GET_PRICE_CACHE + super.getSplitItem() + code;
    }

    public String buildMaxGetPriceCache(String code) {
        return super.getPrefix() + MAX_GET_PRICE_CACHE + super.getSplitItem() + code;
    }

    public String buildUserTotalGetPriceCache(Long userId) {
        return super.getPrefix() + USER_TOTAL_GET_PRICE_CACHE + super.getSplitItem() + userId;
    }

    public String buildRedPacketPrepareSuccess(String code) {
        return super.getPrefix() + RED_PACKET_PREPARE_SUCCESS + super.getSplitItem() + code;
    }

    public  String buildRedPacketNotify(String code) {
        return super.getPrefix() + RED_PACKET_NOTIFY + super.getSplitItem() + code;
    }

    /** 同一用户在同一红包活动中的领取标识，用于阻止重复弹出金额。 */
    public String buildRedPacketUserReceived(String code, Long userId) {
        return super.getPrefix() + RED_PACKET_USER_RECEIVED + super.getSplitItem() + code
                + super.getSplitItem() + userId;
    }

}
