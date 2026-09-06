package org.qiyu.live.api.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.account.interfaces.IAccountTokenRpc;
import org.qiyu.live.api.error.QiyuApiError;
import org.qiyu.live.api.service.IUserLoginService;
import org.qiyu.live.api.vo.WebResponseVO;
import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;
import org.qiyu.live.msg.interfaces.ISmsRpc;
import org.qiyu.live.user.interfaces.dto.UserLoginDTO;
import org.qiyu.live.user.interfaces.interfaces.IUserPhoneRpc;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserLoginServiceImpl implements IUserLoginService {

    // 这里必须用 DubboReference，不能用 @Resource。
    // @Resource 只会找本地 Spring Bean，而 IUserPhoneRpc 是远程 Dubbo 接口。
    @DubboReference
    private IUserPhoneRpc userPhoneRpc;

    // 账号 token 也是远程 Dubbo 服务，不能走本地 Spring 注入
    @DubboReference
    private IAccountTokenRpc accountTokenRpc;

    // 短信服务同样是 Dubbo 远程引用
    @DubboReference
    private ISmsRpc smsRpc;

    // 手机号正则：11位中国大陆手机号
    private static final String PHONE_RES = "^1[3-9]\\d{9}$";

    @Override
    public WebResponseVO sendLoginCode(String phone) {
        // 手机号非空校验
        ErrorAssert.isNotBlank(phone, QiyuApiError.PHONE_NOT_BLANK);
        // 手机号格式校验：11位中国大陆手机号
        ErrorAssert.isTrue(Pattern.matches(PHONE_RES, phone), QiyuApiError.PHONE_IN_VALID);

        // 调用短信服务发送验证码
        MsgSendResultEnum sendResultEnum = smsRpc.sendLoginCode(phone);
        /*
         * [修复] 原 if (sendResultEnum == null) return bizError(...) 改为 ErrorAssert 断言。
         * sendResultEnum 为 null 说明短信 RPC 调用异常，
         * 断言失败后由 GlobalExceptionHandler 统一返回 bizError。
         */
        ErrorAssert.isNotNull(sendResultEnum, QiyuApiError.SMS_SEND_FAIL);
        /*
         * [修复] 原 if (sendResultEnum != SEND_SUCCESS) return bizError(...) 改为 ErrorAssert 断言。
         * 仅当 sendResultEnum == SEND_SUCCESS 时校验通过，其他情况（余额不足、频率限制等）统一抛异常。
         */
        ErrorAssert.isTrue(sendResultEnum == MsgSendResultEnum.SEND_SUCCESS, QiyuApiError.SMS_SEND_FAIL);

        return WebResponseVO.success(sendResultEnum.getDesc());
    }

    @Override
    public WebResponseVO login(String phone, Integer code, HttpServletResponse response) {
        // 手机号非空校验
        ErrorAssert.isNotBlank(phone, QiyuApiError.PHONE_NOT_BLANK);
        // 手机号格式校验
        ErrorAssert.isTrue(Pattern.matches(PHONE_RES, phone), QiyuApiError.PHONE_IN_VALID);
        // 验证码格式校验
        ErrorAssert.isTrue(code != null && code >= 100000 && code <= 999999, QiyuApiError.LOGIN_CODE_IN_VALID);

        // 调用短信服务校验验证码
        MsgCheckDTO msgCheckDTO = smsRpc.checkLoginCode(phone, code);
        /*
         * [保留 if] 短信校验失败时需要返回 msgCheckDTO.getMessage() 中的动态错误信息
         * （如"验证码已过期"、"验证码错误"等），不适合用静态 ErrorAssert 替代。
         */
        if (!msgCheckDTO.isSuccess()) {
            return WebResponseVO.bizError(msgCheckDTO.getMessage());
        }

        // 验证码校验通过，调用手机号服务完成登录
        UserLoginDTO userLoginDTO = userPhoneRpc.login(phone);
        /*
         * [修复] 原 if (userLoginDTO == null) return bizError(...) 改为 ErrorAssert 断言。
         * userLoginDTO 为 null 说明手机号登录 RPC 失败，
         * 断言失败后由 GlobalExceptionHandler 统一返回 bizError。
         */
        ErrorAssert.isNotNull(userLoginDTO, QiyuApiError.LOGIN_FAIL);

        String token = accountTokenRpc.createAndSaveLoginToken(userLoginDTO.getUserId());
        // 把 cookie 用的 token 同步回 DTO，保证前端拿到的 token 与 zbtk cookie 一致
        userLoginDTO.setToken(token);
        Cookie cookie = new Cookie("zbtk", token);
        cookie.setDomain("qiyu.live.com");
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(cookie);

        return WebResponseVO.success(userLoginDTO);
    }
}
