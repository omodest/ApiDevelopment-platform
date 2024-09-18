package api.development.platform.job;

import api.development.platform.model.entity.ProductOrder;
import api.development.platform.service.OrderService;
import api.development.platform.service.ProductOrderService;
import api.development.platform.utils.RedissonLockUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.List;

import static api.development.platform.model.enums.PayTypeStatusEnum.ALIPAY;

@Slf4j
@Component
public class PayJob {

    @Resource
    private OrderService orderService;
    @Resource
    private RedissonLockUtils redissonLockUtils;

    /**
     * 分布式锁，每20s处理订单超时且未支付等情况
     * 1. 获取创建时间超过5分钟且未支付的订单。
     * 2. 获取支付宝支付类型的订单服务。
     * 3. 对每个超时订单进行处理：
     *    - 如果处理过程中发生异常，记录错误日志并中断处理当前订单。
     */
    @Scheduled(cron = "0/20 * * * * ?")
    public void aliPayConfirm(){
        // aliPayConfirm为锁名称，用于避免同一时间多个实例同时处理相同任务。
       redissonLockUtils.redissonDistributedLocks("aliPayConfirm", () -> {
           // 获取所有创建时间超过5分钟且未支付的订单
           List<ProductOrder> orderList = orderService.getNoPayOrderByDuration(5, false, ALIPAY.getValue());
           // 拿到支付宝支付类型的订单服务实例
           ProductOrderService productOrderService = orderService.getProductOrderServiceByPayType(ALIPAY.getValue());
           for (ProductOrder productOrder: orderList){
               try {
                   // 对创建超过5分钟的订单进行处理
                   productOrderService.processingTimedOutOrders(productOrder);
               }catch (Exception e){
                   String orderNo = productOrder.getOrderNo();
                   log.error("支付宝超时订单,{},确认异常：{}", orderNo, e.getMessage());
                   break;
               }
           }
       });
    }
}
