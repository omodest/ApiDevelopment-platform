//package api.development.platform.utils;
//
//import api.development.platform.config.TencentClient;
//import com.tencentcloudapi.common.exception.TencentCloudSDKException;
//import com.tencentcloudapi.sms.v20190711.SmsClient;
//import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
//import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
//@Component
//public class SendSmsUtils {
//
//    @Resource
//    private TencentClient tencentClient;
//
//    @Value("${tencent.sdkAppId}")
//    private String sdkAppId;
//    @Value("${tencent.signName}")
//    private String signName;
//    @Value("${tencent.templateId}")
//    private String templateId;
//
//    /**
//     * 发送短信工具
//     * @param phone
//     * @return
//     * @throws TencentCloudSDKException
//     */
//    public SendSmsResponse sendSmsResponse (String phone, String code) throws TencentCloudSDKException {
//        SendSmsRequest req = new SendSmsRequest();
//
//        // 应用 ID 可前往 [短信控制台](https://console.cloud.tencent.com/smsv2/app-manage) 查看
//        req.setSmsSdkAppid(sdkAppId);
//
//        // 签名信息可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-sign) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-sign) 的签名管理查看
//        req.setSign(signName);
//
//        // 模板 ID 可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-template) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-template) 的正文模板管理查看
//        req.setTemplateID(templateId);
//
//        String[] templateParamSet = {code};
//        req.setTemplateParamSet(templateParamSet);
//
//        String[] phoneNumberSet = new String[]{"+86" + phone};
//        req.setPhoneNumberSet(phoneNumberSet);
//
//        SmsClient client = tencentClient.client();
//        return client.SendSms(req);
//    }
//}
//
//
