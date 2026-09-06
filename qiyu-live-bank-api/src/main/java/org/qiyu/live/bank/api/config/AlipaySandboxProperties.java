package org.qiyu.live.bank.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝沙箱密钥仅从 Nacos 或部署环境读取，禁止提交到代码仓库。
 */
@Data
@Component
@ConfigurationProperties(prefix = "qiyu.pay.alipay")
public class AlipaySandboxProperties {

    private boolean enabled = false;
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String returnUrl;
}
