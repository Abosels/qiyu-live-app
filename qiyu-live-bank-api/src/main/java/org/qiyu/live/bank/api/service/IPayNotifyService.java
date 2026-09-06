package org.qiyu.live.bank.api.service;

import java.util.Map;

public interface IPayNotifyService {

    /** 微信仍保留本地 Mock 回调，供当前个人项目联调使用。 */
    String onNotifyHandler(String paramJson);

    /** 生成支付宝电脑网站支付表单，浏览器访问后会自动跳转到支付宝沙箱收银台。 */
    String createAlipayPagePayForm(String orderId);

    /** 验签并处理支付宝服务器发起的异步通知。 */
    String onAlipayNotify(Map<String, String> notifyParams);
}
