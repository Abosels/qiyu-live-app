package org.qiyu.live.account.interfaces;

public interface IAccountTokenRpc {

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
