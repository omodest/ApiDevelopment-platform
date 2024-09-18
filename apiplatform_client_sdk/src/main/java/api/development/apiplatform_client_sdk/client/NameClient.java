package api.development.apiplatform_client_sdk.client;


import api.development.apiplatform_client_sdk.model.params.NameParams;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;


import java.util.HashMap;
import java.util.Map;

import static api.development.apiplatform_client_sdk.utils.SignUtils.getSignUtils;


/**
 * 调用第三方接口的客户端
 */
public class NameClient {

    private String accessKey;

    private String secretKey;

    public NameClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }



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
    public String getNameByPost(String name){
        HashMap<String, Object> hashMap = new HashMap<>(); // 虽然可以使用object转string，但这里的键不使用object
        hashMap.put("name",name);
        String result = HttpUtil.post("http://localhost:8123/api/name/", hashMap);
        System.out.println(result);
        return result;
    }
    // 使用POST方法向服务器发送user对象，并获取服务器返回的结果
    public String getUserNameByPost(NameParams user){
        String jsonStr = JSONUtil.toJsonStr(user);
        // 使用HttpRequest工具发起POST请求，并获取服务器的响应
        HttpResponse httpResponse = HttpRequest.post("http://localhost:8111/api/name/user")
                .body(jsonStr)
                .addHeaders(getHeader(jsonStr)) // 设置操作
                .execute();
        System.out.println("post username:" + httpResponse.getStatus());
        String body = httpResponse.body();
        System.out.println(body);
        return body;
    }

    /**
     * 设置请求头
     * @return
     */
    private Map<String, String> getHeader(String body){
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey",accessKey); // 标识
//        hashMap.put("secretKey",secretKey); // 密钥
        hashMap.put("sign", getSignUtils(body,secretKey)); // 签名

        hashMap.put("body", body); // 请求体内容
        hashMap.put("nonce", RandomUtil.randomNumbers(4)); // 4为随机数字字符串
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 时间戳

        return hashMap;
    }

}
