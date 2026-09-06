package org.qiyu.live.account.provider.service;

public interface IAccountTokenService {
    /**
     *  创建一个登录token
     * @param userId
     * @return
     */
    String createAndSaveLoginToken(Long userId);

    /**
     * 校验用户token
     * @param token
     * @return
     */
    Long getUserIdByToken(String token);
}
