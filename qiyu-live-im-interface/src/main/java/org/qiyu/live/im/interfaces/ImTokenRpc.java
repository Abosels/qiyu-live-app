package org.qiyu.live.im.interfaces;

public interface ImTokenRpc {
    /**
     * 创建用户登录im服务的token
     * @param userId
     * @param appId
     * @return
     */
    String createImLoginToken(long userId, int appId);

    /**
     * 根据token来查userId
     * @param token
     * @return
     */
    Long getUserIdByToken(String token);
}
