package api.development.apiplatform_client_sdk.config;

import api.development.apiplatform_client_sdk.model.UserSignature;
import api.development.apiplatform_client_sdk.services.ApiServices;
import api.development.apiplatform_client_sdk.services.ApiServicesImpl;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 统一所有Client，这样后端只需要创建指定参数就可以调用不同接口
 */
@Data
@Configuration
@ConfigurationProperties("sdk.api.client")
@ComponentScan
public class SDKClient {
    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 秘密密钥
     */
    private String secretKey;

    /**
     * 网关
     */
    private String host;

    @Bean
    public UserSignature qiApiClient() {
        return new UserSignature(accessKey, secretKey);
    }

    @Bean
    public ApiServices apiService() {
        ApiServicesImpl apiService = new ApiServicesImpl();
        apiService.setUserSignature(new UserSignature(accessKey, secretKey));
        if (StringUtils.isNotBlank(host)) {
            apiService.setGatewayHost(host);
        }
        return apiService;
    }
}

