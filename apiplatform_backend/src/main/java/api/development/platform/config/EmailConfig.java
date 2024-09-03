package api.development.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 关于发送电子邮件的配置文件
 */
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class EmailConfig {

    private String emailFrom = "2500822924@qq.com";
}
