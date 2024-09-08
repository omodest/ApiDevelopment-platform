package api.development.platform.service;

import api.development.platform.model.entity.PaymentInfo;
import api.development.platform.model.vo.PaymentInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author poise
* @description 支付信息服务
* @createDate 2024-09-07 10:19:59
*/
public interface PaymentInfoService extends IService<PaymentInfo> {

    /**
     * 创建付款信息
     * @param paymentInfo 付款信息vo
     * @return
     */
    boolean createPaymentInfo(PaymentInfoVo paymentInfo);
}
