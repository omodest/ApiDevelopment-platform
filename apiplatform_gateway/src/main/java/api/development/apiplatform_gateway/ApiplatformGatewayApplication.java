package api.development.apiplatform_gateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class ApiplatformGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiplatformGatewayApplication.class, args);
    }

}
