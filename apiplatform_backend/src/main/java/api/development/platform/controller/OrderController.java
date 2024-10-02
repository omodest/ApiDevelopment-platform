package api.development.platform.controller;

import api.development.apiplatform_interface.model.entity.User;
import api.development.apiplatform_interface.model.vo.UserVO;
import api.development.platform.common.BaseResponse;
import api.development.platform.common.ErrorCode;
import api.development.platform.common.ResultUtils;
import api.development.platform.exception.BusinessException;
import api.development.platform.model.dto.pay.PayCreateRequest;
import api.development.platform.model.dto.productinfo.ProductInfoQueryRequest;
import api.development.platform.model.dto.productorder.ProductOrderQueryRequest;
import api.development.platform.model.entity.ProductInfo;
import api.development.platform.model.entity.ProductOrder;
import api.development.platform.model.enums.PaymentStatusEnum;
import api.development.platform.model.enums.ProductInfoStatusEnum;
import api.development.platform.model.vo.OrderVo;
import api.development.platform.model.vo.ProductOrderVo;
import api.development.platform.service.OrderService;
import api.development.platform.service.ProductInfoService;
import api.development.platform.service.ProductOrderService;
import api.development.platform.service.UserService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static api.development.platform.constant.PayConstant.QUERY_ORDER_STATUS;
import static api.development.platform.model.enums.PaymentStatusEnum.SUCCESS;

/**
 * 充值记录
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Resource
    private ProductInfoService productInfoService;

    @Resource
    private ProductOrderService productOrderService;

    @Resource
    private UserService userService;

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 分页获取商品列表
     * 充值页面商品展示
     * @param productInfoQueryRequest 接口信息查询请求
     * @param request                 请求
     * @return {@link BaseResponse}<{@link Page}<{@link ProductInfo}>>
     */
    @GetMapping("/product/list/page")
    public BaseResponse<Page<ProductInfo>> listProductInfoByPage(ProductInfoQueryRequest productInfoQueryRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (productInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ProductInfo productInfoQuery = new ProductInfo();
        BeanUtils.copyProperties(productInfoQueryRequest, productInfoQuery);
        long size = productInfoQueryRequest.getPageSize();
        String name = productInfoQueryRequest.getName();
        long current = productInfoQueryRequest.getCurrent();
        String description = productInfoQueryRequest.getDescription();
        String productType = productInfoQueryRequest.getProductType();
        Integer addPoints = productInfoQueryRequest.getAddPoints();
        Integer total = productInfoQueryRequest.getTotal();
        // 2. 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name)
                .like(StringUtils.isNotBlank(description), "description", description)
                .eq(StringUtils.isNotBlank(productType), "productType", productType)
                .eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints)
                .eq(ObjectUtils.isNotEmpty(total), "total", total);
        // 3. 根据金额升序排列
        queryWrapper.orderByAsc("total");
        Page<ProductInfo> productInfoPage = productInfoService.page(new Page<>(current, size), queryWrapper);
        // 4. 不是管理员只能查看已经上线的
        if (!userService.isAdmin(request)) {
            List<ProductInfo> productInfoList = productInfoPage.getRecords().stream()
                    .filter(productInfo -> productInfo.getStatus().equals(ProductInfoStatusEnum.ONLINE.getValue())).collect(Collectors.toList());
            productInfoPage.setRecords(productInfoList);
        }
        return ResultUtils.success(productInfoPage);
    }

    /**
     * 分页获取列表
     * 获取订单列表
     * @param productOrderQueryRequest 接口信息查询请求
     * @param request                  请求
     */
    @GetMapping("/order/list/page")
    public BaseResponse<OrderVo> listProductOrderByPage(ProductOrderQueryRequest productOrderQueryRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (productOrderQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductOrder productOrder = new ProductOrder();
        BeanUtils.copyProperties(productOrderQueryRequest, productOrder);
        long size = productOrderQueryRequest.getPageSize();
        String orderName = productOrderQueryRequest.getOrderName();
        String orderNo = productOrderQueryRequest.getOrderNo();
        Integer total = productOrderQueryRequest.getTotal();
        String status = productOrderQueryRequest.getStatus();
        String productInfo = productOrderQueryRequest.getProductInfo();
        String payType = productOrderQueryRequest.getPayType();
        Integer addPoints = productOrderQueryRequest.getAddPoints();
        long current = productOrderQueryRequest.getCurrent();

        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 联表查询
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<ProductOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(orderName), "orderName", orderName)
                .like(StringUtils.isNotBlank(productInfo), "productInfo", productInfo)
                .eq("userId", userId)
                .eq(StringUtils.isNotBlank(orderNo), "orderNo", orderNo)
                .eq(StringUtils.isNotBlank(status), "status", status)
                .eq(StringUtils.isNotBlank(payType), "payType", payType)
                .eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints)
                .eq(ObjectUtils.isNotEmpty(total), "total", total);
        // 未支付的订单前置
        queryWrapper.last("ORDER BY CASE WHEN status = 'NOTPAY' THEN 0 ELSE 1 END, status");
        Page<ProductOrder> productOrderPage = productOrderService.page(new Page<>(current, size), queryWrapper);
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(productOrderPage, orderVo);
        // 处理订单信息
        List<ProductOrderVo> productOrders = productOrderPage.getRecords().stream().map(this::formatProductOrderVo).collect(Collectors.toList());
        // 处理过期订单
//        LocalDateTime now = LocalDateTime.now();
//        for (ProductOrderVo productOrderVo: productOrders){
//            Date expirationTime = productOrderVo.getExpirationTime();
//            if (expirationTime != null) {
//                // 转换 Date 到 LocalDateTime
//                LocalDateTime expirationDateTime = LocalDateTime.ofInstant(
//                        expirationTime.toInstant(), ZoneId.systemDefault());
//                // 检查过期时间是否在当前时间之前
//                if (expirationDateTime.isBefore(now)) {
//                    closedProductOrder(productOrderVo.getOrderNo());
//                }
//            }
//        }
        orderVo.setRecords(productOrders);
        return ResultUtils.success(orderVo);
    }

    /**
     * 取消订单
     * @param orderNo 订单号
     */
    @PostMapping("/closed")
    public BaseResponse<Boolean> closedProductOrder(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        ProductOrder productOrder = productOrderService.getProductOrderByOutTradeNo(orderNo);
        if (productOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        ProductOrderService orderServiceByPayType = orderService.getProductOrderServiceByPayType(productOrder.getPayType());
        boolean closedResult = orderServiceByPayType.updateOrderStatusByOrderNo(orderNo, PaymentStatusEnum.CLOSED.getValue());
        return ResultUtils.success(closedResult);
    }

    /**
     * 整合需要的ProductOrderVo 对象
     */
    private ProductOrderVo formatProductOrderVo(ProductOrder productOrder) {
        ProductOrderVo productOrderVo = new ProductOrderVo();
        BeanUtils.copyProperties(productOrder, productOrderVo);
        ProductInfo prodInfo = JSONUtil.toBean(productOrder.getProductInfo(), ProductInfo.class);
        productOrderVo.setDescription(prodInfo.getDescription());
        productOrderVo.setProductType(prodInfo.getProductType());
        String voTotal = String.valueOf(prodInfo.getTotal());
        BigDecimal total = new BigDecimal(voTotal).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        productOrderVo.setTotal(total.toString());
        productOrderVo.setAddPoints(Math.toIntExact(productOrder.getAddPoints()));
        return productOrderVo;
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public BaseResponse<ProductOrderVo> createOrder(@RequestBody PayCreateRequest payCreateRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (ObjectUtils.anyNull(payCreateRequest) || StringUtils.isBlank(payCreateRequest.getProductId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long productId = Long.valueOf(payCreateRequest.getProductId());
        String payType = payCreateRequest.getPayType();
        if (StringUtils.isBlank(payType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂无该支付方式");
        }
        // 2. 找到当前用户
        User user = userService.getLoginUser(request);
        UserVO loginUser = new UserVO();
        BeanUtils.copyProperties(user,loginUser);
        // 根据支付方式创建订单
        ProductOrderVo productOrderVo = orderService.createOrderByPayType(productId, payType, loginUser);
        if (productOrderVo == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单创建失败，请稍后再试");
        }
        return ResultUtils.success(productOrderVo);
    }

    /**
     * 按id获取产品订单
     *
     * @param id id
     * @return {@link BaseResponse}<{@link ProductOrderVo}>
     */
    @GetMapping("/get")
    public BaseResponse<ProductOrderVo> getProductOrderById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductOrder productOrder = productOrderService.getById(id);
        if (productOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        ProductOrderVo productOrderVo = formatProductOrderVo(productOrder);
        return ResultUtils.success(productOrderVo);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteProductOrder(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 校验数据是否存在
        ProductOrder productOrder = productOrderService.getById(id);
        if (productOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!productOrder.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(productOrderService.removeById(id));
    }


    /**
     * 解析订单通知结果，支付成功后的回调信息，修改用户钱包和修改订单状态等。。。。。
     * 通知频率为15s/15s/30s/3m/10m/20m/30m/30m/30m/60m/3h/3h/3h/6h/6h - 总计 24h4m
     *
     * @param notifyData 通知数据
     * @param request    请求
     * @return {@link String}
     */
    @PostMapping("/notify/order")
    public String parseOrderNotifyResult(@RequestBody String notifyData, HttpServletRequest request) {
        return orderService.doOrderNotify(notifyData, request);
    }

    /**
     * 查询订单状态
     *
     * @param productOrderQueryRequest 接口订单查询请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/query/status")
    public BaseResponse<Boolean> queryOrderStatus(@RequestBody ProductOrderQueryRequest productOrderQueryRequest) {
        if (ObjectUtils.isEmpty(productOrderQueryRequest) || StringUtils.isBlank(productOrderQueryRequest.getOrderNo())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String orderNo = productOrderQueryRequest.getOrderNo();
        Boolean data = Boolean.valueOf(redisTemplate.opsForValue().get(QUERY_ORDER_STATUS + orderNo));
        if (Boolean.FALSE.equals(data)) {
            return ResultUtils.success(data);
        }
        ProductOrder productOrder = productOrderService.getProductOrderByOutTradeNo(orderNo);
        if (SUCCESS.getValue().equals(productOrder.getStatus())) {
            return ResultUtils.success(true);
        }
        redisTemplate.opsForValue().set(QUERY_ORDER_STATUS + orderNo, String.valueOf(false), 5, TimeUnit.MINUTES);
        return ResultUtils.success(false);
    }

    //
}

