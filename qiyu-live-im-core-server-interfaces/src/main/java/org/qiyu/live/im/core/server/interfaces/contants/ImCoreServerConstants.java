package org.qiyu.live.im.core.server.interfaces.contants;

public class ImCoreServerConstants {
    // 统一 Redis 绑定 key 前缀，尾部保留分隔符，避免 appId 直接贴在 ip 后面。
    public static String IM_BIND_IP_KEY = "qiyu:live:im:bind:ip:";
}
