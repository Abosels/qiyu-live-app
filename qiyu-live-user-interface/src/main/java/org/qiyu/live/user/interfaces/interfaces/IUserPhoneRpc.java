package org.qiyu.live.user.interfaces.interfaces;

import org.qiyu.live.user.interfaces.dto.UserLoginDTO;
import org.qiyu.live.user.interfaces.dto.UserPhoneDTO;

import java.util.List;

public interface IUserPhoneRpc {
    /**
     * 登录+注册初始化
     * userId、token
     */
    UserLoginDTO login(String phone);
    //根据手机号找到相关用户id
    UserPhoneDTO queryByPhone(String phone);
    //根据用户id查询手机号
    List<UserPhoneDTO> queryByUserId(Long userId);
}
