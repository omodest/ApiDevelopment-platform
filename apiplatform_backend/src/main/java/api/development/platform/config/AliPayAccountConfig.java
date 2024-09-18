package api.development.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付账号配置
 */
@Data
@ConfigurationProperties(prefix = "alipay")
@Component
public class AliPayAccountConfig {
    /**
     * appId
     */
    private String appId;

    /**
     * 商户私钥
     */
    private String appPrivateKey;
    /**
     * 支付宝公钥
     */
    private String aliPayPublicKey;
    /**
     * 异步通知url；后端请求
     */
    private String notifyUrl;

    /**
     * 同步返回的url; 支付完后重定向到的前端路径
     */
    private String returnUrl;

    /**
     * 支付宝网关地址
     */
    private String gatewayUrl;

    /**
     * 加密类型
     */
    private String signType;

    /**
     * 字符格式
     */
    private String charset;
}
