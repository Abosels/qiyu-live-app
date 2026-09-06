package org.idea.qiyu.live.framework.redis.starter.config;

import org.idea.qiyu.live.framework.redis.starter.key.ConditionalOnQiyuApplication;
import org.idea.qiyu.live.framework.redis.starter.key.AccountProviderCacheKeyBuilder;
import org.idea.qiyu.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.idea.qiyu.live.framework.redis.starter.key.IMCoreServerProviderCacheKeyBuilder;
import org.idea.qiyu.live.framework.redis.starter.key.IMProviderCacheKeyBuilder;
import org.idea.qiyu.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.idea.qiyu.live.framework.redis.starter.key.MsgProviderCacheKeyBuilder;
import org.idea.qiyu.live.framework.redis.starter.key.RedPacketProviderCacheKeyBuilder;
import org.idea.qiyu.live.framework.redis.starter.key.ShopCacheKeyBuilder;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class RedisKeyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(BankProviderCacheKeyBuilder.class)
    @ConditionalOnQiyuApplication("qiyu-live-bank-provider")
    public BankProviderCacheKeyBuilder bankProviderCacheKeyBuilder() {
        return new BankProviderCacheKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(GiftProviderCacheKeyBuilder.class)
    @ConditionalOnQiyuApplication("qiyu-live-gift-provider")
    public GiftProviderCacheKeyBuilder giftProviderCacheKeyBuilder() {
        return new GiftProviderCacheKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(ShopCacheKeyBuilder.class)
    @ConditionalOnQiyuApplication("qiyu-live-gift-provider")
    public ShopCacheKeyBuilder shopCacheKeyBuilder() {
        // 商品库存、购物车与订单服务共享该 Key Builder。
        return new ShopCacheKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(RedPacketProviderCacheKeyBuilder.class)
    @ConditionalOnQiyuApplication("qiyu-live-gift-provider")
    public RedPacketProviderCacheKeyBuilder redPacketProviderCacheKeyBuilder() {
        // 红包初始化、领取幂等和统计均依赖该 Key Builder。
        return new RedPacketProviderCacheKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(LivingProviderCacheKeyBuilder.class)
    @ConditionalOnQiyuApplication("qiyu-live-living-provider")
    public LivingProviderCacheKeyBuilder livingProviderCacheKeyBuilder() {
        return new LivingProviderCacheKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(UserProviderCacheKeyBuilder.class)
    @ConditionalOnQiyuApplication("qiyu-live-user-provider")
    public UserProviderCacheKeyBuilder userProviderCacheKeyBuilder() {
        return new UserProviderCacheKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(AccountProviderCacheKeyBuilder.class)
    @ConditionalOnQiyuApplication("qiyu-live-account-provider")
    public AccountProviderCacheKeyBuilder accountProviderCacheKeyBuilder() {
        return new AccountProviderCacheKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(IMProviderCacheKeyBuilder.class)
    @ConditionalOnQiyuApplication("qiyu-live-im-provider")
    public IMProviderCacheKeyBuilder imProviderCacheKeyBuilder() {
        return new IMProviderCacheKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(IMCoreServerProviderCacheKeyBuilder.class)
    @ConditionalOnQiyuApplication("qiyu-live-im-core-server")
    public IMCoreServerProviderCacheKeyBuilder imCoreServerProviderCacheKeyBuilder() {
        return new IMCoreServerProviderCacheKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(MsgProviderCacheKeyBuilder.class)
    @ConditionalOnQiyuApplication("qiyu-live-msg-provider")
    public MsgProviderCacheKeyBuilder msgProviderCacheKeyBuilder() {
        return new MsgProviderCacheKeyBuilder();
    }
}
