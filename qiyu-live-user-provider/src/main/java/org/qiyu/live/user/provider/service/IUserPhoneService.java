package org.qiyu.live.user.provider.service;

import org.qiyu.live.user.interfaces.dto.UserLoginDTO;
import org.qiyu.live.user.interfaces.dto.UserPhoneDTO;

import java.util.List;

public interface IUserPhoneService {
    /**
     * 登录+注册初始化(底层会进行手机号的注册)
     * userId、token
     */
    UserLoginDTO initLogin(String phone);

    /**
     * 根据手机号找到相关用户id
     */
    UserPhoneDTO queryByPhone(String phone);

    /**
     * 更具用户id，查询相关信息
     * @param userId
     * @return
     */
    List<UserPhoneDTO> queryByUserId(Long userId);
}
