package api.development.apiplatform_client_sdk;

import api.development.apiplatform_client_sdk.client.NameClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration // 配置文件注解
@ConfigurationProperties("name.client") // 后续使用该sdk，都需要再yml文件中以 name.client为前缀
@Data // 给字段添加get、set等方法
@ComponentScan // 扫描组件
public class NameClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean // 创建一个名为nameClient的bean
    public NameClient nameClient() {
        return new NameClient(accessKey, secretKey);
    }

}
