package api.development.apiplatform_interface;

import api.development.apiplatform_client_sdk.client.NameClient;
import api.development.apiplatform_client_sdk.model.User;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiplatformInterfaceApplicationTests {

//    @Resource
//    private NameClient nameClient;

    @Test
    public void contextLoads() {
//        System.out.println(nameClient);
        NameClient nameClient = new NameClient("poise.adm1in","admin.admin");
        String result = nameClient.getNameByGet("root");
        User user = new User();
        user.setName("root");
        String usernameByPost = nameClient.getUserNameByPost(user);
        System.out.println(result);
        System.out.println(usernameByPost);
    }


}
