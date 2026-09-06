package org.qiyu.live.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.IUserService;
import org.qiyu.live.api.vo.WebResponseVO;
import org.qiyu.live.user.interfaces.dto.UserDTO;
import org.qiyu.live.user.interfaces.interfaces.IUserRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.springframework.stereotype.Service;

/**
 * 用户信息服务实现 — 通过 Dubbo RPC 调用 user-provider 获取用户数据
 */
@Service
public class UserServiceImpl implements IUserService {

    @DubboReference
    private IUserRpc userRpc;

    @Override
    public WebResponseVO getUserInfo() {
        Long userId = QiyuRequestContext.getUserId();
        if (userId == null) {
            return WebResponseVO.bizError("用户未登录");
        }

        UserDTO userDTO = userRpc.getUserById(userId);
        if (userDTO == null) {
            return WebResponseVO.bizError("用户不存在");
        }

        return WebResponseVO.success(userDTO);
    }

    @Override
    public WebResponseVO updateUserInfo(UserDTO userDTO) {
        Long userId = QiyuRequestContext.getUserId();
        if (userId == null) {
            return WebResponseVO.bizError("用户未登录");
        }
        if (userDTO == null) {
            return WebResponseVO.errorParam("更新信息不能为空");
        }

        // 确保只更新当前登录用户自己的信息
        userDTO.setUserId(userId);
        boolean result = userRpc.updateUserInfo(userDTO);
        if (!result) {
            return WebResponseVO.bizError("更新失败");
        }

        return WebResponseVO.success();
    }
}
