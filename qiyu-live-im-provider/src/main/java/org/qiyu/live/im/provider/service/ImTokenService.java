package org.qiyu.live.im.provider.service;

/**
 * 用户登录token service
 */
public interface ImTokenService {
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
