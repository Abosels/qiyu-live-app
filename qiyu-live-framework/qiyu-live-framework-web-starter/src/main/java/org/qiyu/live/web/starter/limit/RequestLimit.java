package org.qiyu.live.web.starter.limit;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {

    /**
     * 允许请求的量
     * @return
     */
    int limit();

    /**
     * 限流的时长
     * @return
     */
    int second();

    /**
     * 限流之后的提示内容
     * @return
     */
    String msg() default "请求过于频繁";
}
