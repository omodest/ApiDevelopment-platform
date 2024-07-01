package api.development.apiplatform_interface;

import api.development.apiplatform_interface.client.NameClient;
import api.development.apiplatform_interface.model.User;

public class Main {
    public static void main(String[] args) {
        NameClient nameClient = new NameClient();
        String nameByGet = nameClient.getNameByGet("测试get");
        String nameByPost = nameClient.getNameByPost("测试post");
        User user = new User();
        user.setName("测试post username");
        String userNameByPost = nameClient.getUserNameByPost(user);
        System.out.println(nameByGet);
        System.out.println(nameByPost);
        System.out.println(userNameByPost);
    }
}
