package api.development.apiplatform_interface;

import api.development.apiplatform_client_sdk.client.NameClient;
import api.development.apiplatform_client_sdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ApiplatformInterfaceApplicationTests {

    @Resource
    private NameClient nameClient;

    @Test
    public void contextLoads() {
        System.out.println(nameClient); // 输出nameClient，检查是否为null
        String result = nameClient.getNameByGet("root");
        User user = new User();
        user.setName("root");
        String usernameByPost = nameClient.getUserNameByPost(user);
        System.out.println(result);
        System.out.println(usernameByPost);
    }
}

//        System.out.println(nameClient);
//        NameClient nameClient = new NameClient("poise.admin","admin.admin");