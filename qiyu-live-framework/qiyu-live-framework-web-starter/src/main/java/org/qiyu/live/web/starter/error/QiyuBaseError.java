package org.qiyu.live.web.starter.error;

public interface QiyuBaseError {

    //定义返回的错误代码
    int getErrorCode();
    //定义返回的错误提示语
    String getErrorMsg();
}
