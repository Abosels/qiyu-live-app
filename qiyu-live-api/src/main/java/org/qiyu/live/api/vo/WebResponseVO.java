package org.qiyu.live.api.vo;

import lombok.Data;

/**
 * Web 层统一返回对象
 * 负责包装参数错误、业务错误和成功结果，方便前端统一处理。
 */
@Data
public class WebResponseVO<T> {

    /**
     * 返回码：0 表示成功，非 0 表示失败
     */
    private Integer code;

    /**
     * 返回描述
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 参数错误
     */
    public static <T> WebResponseVO<T> errorParam(String msg) {
        WebResponseVO<T> responseVO = new WebResponseVO<>();
        responseVO.setCode(400);
        responseVO.setMsg(msg);
        return responseVO;
    }

    /**
     * 业务错误
     */
    public static <T> WebResponseVO<T> bizError(String msg) {
        WebResponseVO<T> responseVO = new WebResponseVO<>();
        responseVO.setCode(500);
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static <T> WebResponseVO<T> bizError(String msg,Integer code) {
        WebResponseVO<T> responseVO = new WebResponseVO<>();
        responseVO.setCode(code);
        responseVO.setMsg(msg);
        return responseVO;
    }

    /**
     * 成功返回
     */
    public static <T> WebResponseVO<T> success(T data) {
        WebResponseVO<T> responseVO = new WebResponseVO<>();
        responseVO.setCode(0);
        responseVO.setMsg("success");
        responseVO.setData(data);
        return responseVO;
    }

    /**
     * 成功返回，不需要携带数据时直接调用这个重载。
     */
    public static WebResponseVO<Void> success() {
        WebResponseVO<Void> responseVO = new WebResponseVO<>();
        responseVO.setCode(0);
        responseVO.setMsg("success");
        return responseVO;
    }

    /**
     * 系统异常（未预期的运行时错误、中间件故障等）
     * 与 {@link #bizError} 的区别：bizError 是预期内的业务失败（code=500），
     * sysError 是未预期的系统级异常（code=502），方便运维和监控区分。
     *
     * @param msg 异常描述
     * @param <T> 数据类型
     * @return 统一错误响应
     */
    public static <T> WebResponseVO<T> sysError(String msg) {
        WebResponseVO<T> responseVO = new WebResponseVO<>();
        responseVO.setCode(502);
        responseVO.setMsg(msg);
        return responseVO;
    }
}
