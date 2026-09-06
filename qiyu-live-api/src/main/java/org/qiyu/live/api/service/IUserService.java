package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.WebResponseVO;
import org.qiyu.live.user.interfaces.dto.UserDTO;

/**
 * 用户信息服务接口
 */
public interface IUserService {

    /**
     * 根据请求上下文中的 userId 获取当前登录用户的完整信息
     *
     * @return WebResponseVO&lt;UserDTO&gt; 用户信息
     */
    WebResponseVO getUserInfo();

    /**
     * 更新用户信息
     *
     * @param userDTO 用户信息
     * @return WebResponseVO 更新结果
     */
    WebResponseVO updateUserInfo(UserDTO userDTO);
}
