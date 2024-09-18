package api.development.platform.service.impl;

import api.development.apiplatform_interface.model.vo.UserVO;
import api.development.platform.common.ErrorCode;
import api.development.platform.exception.BusinessException;
import api.development.platform.model.entity.ProductOrder;
import api.development.platform.model.enums.PaymentStatusEnum;
import api.development.platform.model.vo.ProductOrderVo;
import api.development.platform.service.OrderService;
import api.development.platform.service.ProductOrderService;
import api.development.platform.utils.RedissonLockUtils;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static api.development.platform.model.enums.PayTypeStatusEnum.ALIPAY;
import static api.development.platform.model.enums.PayTypeStatusEnum.WX;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private ProductOrderService productOrderService;

    /**
     * 支付方式的productOrderServices
     */
    @Resource
    private List<ProductOrderService> productOrderServices;

    /**
     *  分布式锁
     */
    @Resource
    private RedissonLockUtils redissonLockUtil;

    /**
     * 按付费类型获取产品订单服务
     */
    @Override
    public ProductOrderService getProductOrderServiceByPayType(String payType) {
        // productOrderServices 是以List的方式依赖注入进来的，所以这里需要根据指定的支付方式进行过滤。
        return productOrderServices.stream()
                .filter(s -> {
                    // 以ProductOrderService的子类中找到带有@Qualifier注解，拿到其中的参数value，判断与payType是否相等；
                    Qualifier qualifierAnnotation = s.getClass().getAnnotation(Qualifier.class);
                    return qualifierAnnotation != null && qualifierAnnotation.value().equals(payType);
                })
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "暂无该支付方式"));
    }

    /**
     * 根据支付类型创建订单
     * @param productId 产品id
     * @param payType   付款类型
     * @param loginUser 登录用户
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务注解
    public ProductOrderVo createOrderByPayType(Long productId, String payType, UserVO loginUser) {
        // 按付费类型获取产品订单服务Bean
        ProductOrderService productOrderService = getProductOrderServiceByPayType(payType);
        // 创建用于获取订单的锁名称
        String redissonLock = ("getOrder_" + loginUser.getUserAccount()).intern();
        // 尝试获取分布式锁，执行获取订单的操作
        ProductOrderVo getProductOrderVo = redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
            // getProductOrder查询订单是否存在,订单存在就返回不再新创建
            return productOrderService.getProductOrder(productId, loginUser, payType);
        });
        // 如果订单已经存在，直接返回
        if (getProductOrderVo != null) {
            return getProductOrderVo;
        }
        // 创建用于保存订单的锁名称
        redissonLock = ("createOrder_" + loginUser.getUserAccount()).intern();
        // 分布式锁工具
        return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
            // 保存订单,返回vo信息
            try {
                return productOrderService.saveProductOrder(productId, loginUser);
            } catch (AlipayApiException e) {
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * 查找超过minutes分钟并且未支付的的订单
     *
     * @param minutes 分钟
     * @param remove 指示是否包括状态为 CLOSED 的订单
     * @return {@link List}<{@link ProductOrder}>
     */
    @Override
    public List<ProductOrder> getNoPayOrderByDuration(int minutes, Boolean remove, String payType) {
        // 当前时间减去指定分钟数，拿到订单截至时间。
        Instant instant = Instant.now().minus(Duration.ofMinutes(minutes));
        LambdaQueryWrapper<ProductOrder> productOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 拼接查询条件：未支付
        productOrderLambdaQueryWrapper.eq(ProductOrder::getStatus, PaymentStatusEnum.NOTPAY.getValue());
        if (StringUtils.isNotBlank(payType)) {
            productOrderLambdaQueryWrapper.eq(ProductOrder::getPayType, payType);
        }
        // 如果 remove 为 true，则查询状态为 CLOSED 的订单。这里使用了 or()，表示在现有条件的基础上，增加一个或的条件。
        if (remove) {
            productOrderLambdaQueryWrapper.or().eq(ProductOrder::getStatus, PaymentStatusEnum.CLOSED.getValue());
        }
        // 添加一个 and 条件，要求订单的创建时间小于等于计算出的截止时间 instant。
        productOrderLambdaQueryWrapper.and(p -> p.le(ProductOrder::getCreateTime, instant));
        return productOrderService.list(productOrderLambdaQueryWrapper);
    }

    /**
     * 做订单通知
     * 支票支付类型
     * 通过检查通知数据的特征来确定支付平台，并将通知数据传递给适当的服务进行处理。
     * @param notifyData 通知数据
     * @param request    要求
     * @return {@link String}
     */
    @Override
    public String doOrderNotify(String notifyData, HttpServletRequest request) {
        String payType;
        // 确认支付类型
        if (notifyData.startsWith("gmt_create=") && notifyData.contains("gmt_create") && notifyData.contains("sign_type") && notifyData.contains("notify_type")) {
            payType = ALIPAY.getValue();
        } else {
            payType = WX.getValue();
        }
        return this.getProductOrderServiceByPayType(payType).doPaymentNotify(notifyData, request);
    }
}

