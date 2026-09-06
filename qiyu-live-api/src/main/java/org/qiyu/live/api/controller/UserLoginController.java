package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.IUserLoginService;
import org.qiyu.live.api.vo.WebResponseVO;
import org.qiyu.live.user.interfaces.dto.UserDTO;
import org.qiyu.live.user.interfaces.interfaces.IUserRpc;
import org.qiyu.live.web.starter.limit.RequestLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userLogin")
public class UserLoginController {

    @Resource
    private IUserLoginService userLoginService;

    //发送验证码 — 严格限流，防止短信被刷
    @RequestLimit(limit = 1, second = 60, msg = "验证码发送过于频繁，请60秒后再试")
    @PostMapping("/sendLoginCode")
    public WebResponseVO sendLoginCode(String phone){
        return userLoginService.sendLoginCode(phone);
    }
    //登录请求 — 防止暴力破解验证码
    @RequestLimit(limit = 5, second = 60, msg = "登录请求过于频繁，请稍后再试")
    @PostMapping("/login")
    public WebResponseVO login(String phone, Integer code , HttpServletResponse response){
        return userLoginService.login(phone, code, response);
    }
    /**
     * @DubboReference 的意思是：这里注入的不是本地实现类，而是 Dubbo 生成的远程代理对象。
     * 当执行： return userRpc.getUserById(userId);
     * 实际上会远程调用用户服务提供者里的： UserRpcImpl.getUserById(userId)
     * 而 UserRpcImpl 里面又会调用： userService.getUserById(userId)
     *
     * TestController.getUserInfo()
     *   ↓ 调用
     * IUserRpc.getUserById()
     *   ↓ Dubbo 找服务提供者
     * UserRpcImpl.getUserById()
     *   ↓ 调用
     * IUserService.getUserById()
     *   ↓ 通常再调用
     * IUserMapper.selectById()
     *   ↓ 调用
     *  MySQL user 表
     */

}
