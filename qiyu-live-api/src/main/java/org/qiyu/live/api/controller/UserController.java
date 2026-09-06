package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.IUserService;
import org.qiyu.live.api.vo.WebResponseVO;
import org.qiyu.live.user.interfaces.dto.UserDTO;
import org.qiyu.live.web.starter.limit.RequestLimit;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息 HTTP 接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 获取当前登录用户的完整信息（昵称、头像等）
     *
     * @return WebResponseVO&lt;UserDTO&gt;
     */
    @RequestLimit(limit = 10, second = 10, msg = "请求过于频繁，请稍后再试")
    @GetMapping("/info")
    public WebResponseVO getUserInfo() {
        return userService.getUserInfo();
    }

    /**
     * 更新当前登录用户信息
     *
     * @param userDTO 要更新的字段
     * @return WebResponseVO
     */
    @RequestLimit(limit = 5, second = 60, msg = "更新请求过于频繁，请稍后再试")
    @PutMapping("/update")
    public WebResponseVO updateUserInfo(@RequestBody UserDTO userDTO) {
        return userService.updateUserInfo(userDTO);
    }
}
