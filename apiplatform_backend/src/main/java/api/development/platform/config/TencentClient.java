//package api.development.platform.config;
//
//import com.tencentcloudapi.common.Credential;
//import com.tencentcloudapi.sms.v20190711.SmsClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TencentClient {
//
//    @Value("${tencent.secretId}")
//    private String secretId;
//
//    @Value("${tencent.secretKey}")
//    private String secretKey;
//
//    /**
//     * Tencent应用客户端
//     * @return
//     */
//    @Bean
//    public SmsClient client(){
//        Credential cred = new Credential(secretId, secretKey);
//        SmsClient smsClient = new SmsClient(cred, "ap-guangzhou");
//        return smsClient;
//    }
//}