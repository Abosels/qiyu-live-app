package org.qiyu.live.web.starter.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, Object> errorHandle(HttpServletRequest request, Exception e) {
        LOGGER.log(Level.SEVERE, request.getRequestURI() + " error", e);
        return buildErrorResponse(502, "系统异常");
    }

    @ExceptionHandler(QiyuErrorException.class)
    @ResponseBody
    public Map<String, Object> sysErrorHandle(HttpServletRequest request, QiyuErrorException e) {
        LOGGER.log(Level.SEVERE, request.getRequestURI() + " business error", e);
        return buildErrorResponse(e.getErrorCode(), e.getErrorMsg());
    }

    // web-starter 不依赖具体业务 API 模块，避免 Maven 循环依赖。
    private Map<String, Object> buildErrorResponse(int code, String msg) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", code);
        response.put("msg", msg);
        response.put("data", null);
        return response;
    }
}