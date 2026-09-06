package org.qiyu.live.bank.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.bank.api.service.IPayNotifyService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 处理支付回调的逻辑
 *
 */
@RestController
@RequestMapping("/payNotify")
public class PayNotifyController {
    @Resource
    private IPayNotifyService payNotifyService;


    @PostMapping("/wx-notify")
    public String wxNotify(@RequestParam("param") String param){
        return payNotifyService.onNotifyHandler(param);
    }

    /** 浏览器打开该地址后，返回 SDK 生成的自动提交表单。 */
    @GetMapping(value = "/alipay-page-pay", produces = MediaType.TEXT_HTML_VALUE)
    public String alipayPagePay(@RequestParam("orderId") String orderId) {
        return payNotifyService.createAlipayPagePayForm(orderId);
    }

    /** 支付宝服务器通知入口，成功时必须返回纯文本 success。 */
    @PostMapping("/alipay-notify")
    public String alipayNotify(@RequestParam Map<String, String> notifyParams) {
        return payNotifyService.onAlipayNotify(notifyParams);
    }
}
