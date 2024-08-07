//package api.development.platform.utils;
//
//import api.development.platform.config.RedisTokenBucket;
//import api.development.platform.model.dto.user.SmsDTO;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.concurrent.TimeUnit;
//
//import static api.development.platform.constant.CommonConstant.SMS_CODE_PREFIX;
//
//@Component
//@Slf4j
//public class SmsUtils {
//    @Resource
//    private RedisTemplate<String, String> redisTemplate;
//    @Resource
//    private RedisTokenBucket redisTokenBucket;
//    @Resource
//    private RabbitMqUtils rabbitMqUtils;
//
//    public boolean sendSms(SmsDTO smsDTO) {
//        // 从令牌桶中取得令牌，未取得不允许发送短信
//        boolean acquire = redisTokenBucket.tryAcquire(smsDTO.getPhoneNum());
//        if (!acquire) {
//            log.info("phoneNum：{}，send SMS frequent", smsDTO.getPhoneNum());
//            return false;
//        }
//        log.info("发送短信：{}",smsDTO);
//        String phoneNum = smsDTO.getPhoneNum();
//        String code = smsDTO.getCode();
//
//        // 将手机号对应的验证码存入Redis，方便后续检验
//        redisTemplate.opsForValue().set(SMS_CODE_PREFIX + phoneNum, String.valueOf(code), 5, TimeUnit.MINUTES);
//
//        // 利用消息队列，异步发送短信
//        rabbitMqUtils.sendSmsAsync(smsDTO);
//        return true;
//    }
//
//    public boolean verifyCode(String phoneNum, String code) {
//        String key = SMS_CODE_PREFIX + phoneNum;
//        String checkCode = redisTemplate.opsForValue().get(key);
//        if (StringUtils.isNotBlank(code) && code.equals(checkCode)) {
//            redisTemplate.delete(key);
//            return true;
//        }
//        return false;
//
//    }
//}
