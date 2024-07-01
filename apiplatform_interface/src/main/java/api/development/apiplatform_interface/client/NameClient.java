package api.development.apiplatform_interface.client;

import api.development.apiplatform_interface.model.User;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * 调用第三方接口的客户端
 */
public class NameClient {

    // 使用get方法从服务器获取客户信息
    public String getNameByGet(String name){
        HashMap<String, Object> hashMap = new HashMap<>();
        // 将"name"参数添加到映射中
        hashMap.put("name", name);
        // 使用Httputil 工具发起GET请求，并获取服务器返回的结果
        String result = HttpUtil.get("http://localhost:8123/api/name/",hashMap);
        System.out.println(result);
        return result;
    }
    // 使用POST方法从服务器获取名称信息
    public String getNameByPost(@RequestParam String name){
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("name",name);
        String result = HttpUtil.post("http://localhost:8123/api/name/", String.valueOf(hashMap));
        System.out.println(result);
        return result;
    }
    // 使用POST方法向服务器发送user对象，并获取服务器返回的结果
    public String getUserNameByPost(@RequestBody User user){
        String jsonStr = JSONUtil.toJsonStr(user);
        // 使用HttpRequest工具发起POST请求，并获取服务器的响应
        HttpResponse httpResponse = HttpRequest.post("http://localhost:8123/api/name/")
                .body(jsonStr)
                .execute();
        //
        System.out.println("post username:" + httpResponse.getStatus());
        String body = httpResponse.body();
        System.out.println(body);
        return body;
    }
}
