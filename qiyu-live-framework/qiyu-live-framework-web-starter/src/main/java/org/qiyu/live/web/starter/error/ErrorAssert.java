package org.qiyu.live.web.starter.error;

public class ErrorAssert {

    /**
     * 判断参数不能为空
     *
     * @param object
     * @param qiyuBaseError
     */
    public static void isNotNull(Object object, QiyuBaseError qiyuBaseError) {
        if (object == null) {
            throw  new QiyuErrorException(qiyuBaseError.getErrorCode(), qiyuBaseError.getErrorMsg());
        }
    }

    /**
     * 判断字符串不为空
     *
     * @param str
     * @param qiyuBaseError
     */
    public static void isNotBlank(String str, QiyuBaseError qiyuBaseError) {
        if(str == null || str.trim().length() == 0){
            throw  new QiyuErrorException(qiyuBaseError.getErrorCode(), qiyuBaseError.getErrorMsg());
        }
    }

    /**
     * flag == true
     * @param flag
     * @param qiyuBaseError
     */
    public static void isTrue(boolean flag, QiyuBaseError qiyuBaseError) {
        if(!flag){
            throw  new QiyuErrorException(qiyuBaseError.getErrorCode(), qiyuBaseError.getErrorMsg());
        }
    }
}
