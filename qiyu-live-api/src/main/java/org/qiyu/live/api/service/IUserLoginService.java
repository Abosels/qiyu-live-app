package org.qiyu.live.api.service;

import jakarta.servlet.http.HttpServletResponse;
import org.qiyu.live.api.vo.WebResponseVO;

public interface IUserLoginService {
    WebResponseVO sendLoginCode(String phone);
    WebResponseVO login(String phone, Integer code, HttpServletResponse response);
}
