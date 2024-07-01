import api.development.apiplatform_interface.client.NameClient;
import api.development.apiplatform_interface.model.User;

public class Main {
    public static void main(String[] args) {
        String accessKey = "poise.admin";
        String secretKey = "admin.admin";
        NameClient nc = new NameClient(accessKey,secretKey);
        String name1 = nc.getNameByGet("GETPoise");
        String name2 = nc.getNameByPost("POSTPoise");
        User user = new User();
        user.setName("POSTJson");
        String name3 = nc.getUserNameByPost(user);
    }
}
