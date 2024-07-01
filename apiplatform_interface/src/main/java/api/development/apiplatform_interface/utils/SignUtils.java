package api.development.apiplatform_interface.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;


import java.util.Map;

public class SignUtils {
    /**
     *
     * @param body 请求体内容
     * @param secretKey 密钥
     * @return
     */
    public static String getSignUtils(String body, String secretKey){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body.toString() + "." + secretKey;
        return md5.digestHex(content);
    }
}
