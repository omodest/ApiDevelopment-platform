//package api.development.platform.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.Exchange;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.HashMap;
//import java.util.Map;
//import org.springframework.amqp.core.Queue;
//
//import static api.development.platform.constant.CommonConstant.*;
//
//
//@Slf4j
//@Configuration
//public class RabbitMqConfig {
//    /**
//     * 普通队列
//     * @return
//     */
//    @Bean
//    public Queue smsQueue(){
//        Map<String, Object> arguments = new HashMap<>();
//        //声明死信队列和交换机消息，过期时间：1分钟
//        arguments.put("x-dead-letter-exchange", SMS_EXCHANGE_ROUTING_KEY);
//        arguments.put("x-dead-letter-routing-key", SMS_DELAY_EXCHANGE_ROUTING_KEY);
//        arguments.put("x-message-ttl", 60000);
//        return new Queue(SMS_QUEUE_NAME,true,false,false ,arguments);
//    }
//
//    /**
//     * 死信队列：消息重试三次后放入死信队列
//     * @return
//     */
//    @Bean
//    public Queue deadLetter(){
//        return new Queue(SMS_DELAY_QUEUE_NAME, true, false, false);
//    }
//
//    /**
//     * 主题交换机
//     * @return
//     */
//    @Bean
//    public Exchange smsExchange() {
//        return new TopicExchange(SMS_EXCHANGE_NAME, true, false);
//    }
//
//
//    /**
//     * 交换机和普通队列绑定
//     * @return
//     */
//    @Bean
//    public Binding smsBinding(){
//        return new Binding(SMS_QUEUE_NAME, Binding.DestinationType.QUEUE,SMS_EXCHANGE_NAME,SMS_EXCHANGE_ROUTING_KEY,null);
//    }
//
//    /**
//     * 交换机和死信队列绑定
//     * @return
//     */
//    @Bean
//    public Binding smsDelayBinding(){
//        return new Binding(SMS_DELAY_QUEUE_NAME, Binding.DestinationType.QUEUE,SMS_EXCHANGE_NAME,SMS_DELAY_EXCHANGE_ROUTING_KEY,null);
//    }
//
//
//}