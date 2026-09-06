package org.qiyu.live.msg.interfaces;

import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;

public interface ISmsRpc{
    /**
     * 发送短信登录验证码接口
     */
    MsgSendResultEnum sendLoginCode(String phone);
    /**
     * 校验短信登录验证码接口
     */
    MsgCheckDTO checkLoginCode(String phone, Integer code);
}
