package api.development.platform.controller;

import api.development.platform.common.BaseResponse;
import api.development.platform.common.ErrorCode;
import api.development.platform.common.ResultUtils;
import api.development.platform.exception.BusinessException;
import api.development.platform.model.dto.productinfo.ProductInfoQueryRequest;
import api.development.platform.model.dto.productorder.ProductOrderQueryRequest;
import api.development.platform.model.entity.ProductInfo;
import api.development.platform.model.entity.ProductOrder;
import api.development.platform.model.enums.ProductInfoStatusEnum;
import api.development.platform.service.ProductInfoService;
import api.development.platform.service.ProductOrderService;
import api.development.platform.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 分页获取列表
     * 充值页面商品展示
     * @param productInfoQueryRequest 接口信息查询请求
     * @param request                 请求
     * @return {@link BaseResponse}<{@link Page}<{@link ProductInfo}>>
     */
    @GetMapping("/product/list/page")
    public BaseResponse<Page<ProductInfo>> listProductInfoByPage(ProductInfoQueryRequest productInfoQueryRequest, HttpServletRequest request) {
        if (productInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ProductInfo productInfoQuery = new ProductInfo();
        BeanUtils.copyProperties(productInfoQueryRequest, productInfoQuery);
        long size = productInfoQueryRequest.getPageSize();
        String sortField = productInfoQueryRequest.getSortField();
        String sortOrder = productInfoQueryRequest.getSortOrder();

        String name = productInfoQueryRequest.getName();
        long current = productInfoQueryRequest.getCurrent();
        String description = productInfoQueryRequest.getDescription();
        String productType = productInfoQueryRequest.getProductType();
        Integer addPoints = productInfoQueryRequest.getAddPoints();
        Integer total = productInfoQueryRequest.getTotal();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name)
                .like(StringUtils.isNotBlank(description), "description", description)
                .eq(StringUtils.isNotBlank(productType), "productType", productType)
                .eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints)
                .eq(ObjectUtils.isNotEmpty(total), "total", total);
        // 根据金额升序排列
        queryWrapper.orderByAsc("total");
        Page<ProductInfo> productInfoPage = productInfoService.page(new Page<>(current, size), queryWrapper);
        // 不是管理员只能查看已经上线的
        if (!userService.isAdmin(request)) {
            List<ProductInfo> productInfoList = productInfoPage.getRecords().stream()
                    .filter(productInfo -> productInfo.getStatus().equals(ProductInfoStatusEnum.ONLINE.getValue())).collect(Collectors.toList());
            productInfoPage.setRecords(productInfoList);
        }
        return ResultUtils.success(productInfoPage);
    }

    // todo 立即购买生成订单
    // todo 去支付

}

