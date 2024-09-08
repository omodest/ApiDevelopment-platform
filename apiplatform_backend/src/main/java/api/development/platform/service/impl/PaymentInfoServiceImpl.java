package api.development.platform.service.impl;

import api.development.platform.model.vo.PaymentInfoVo;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import api.development.platform.model.entity.PaymentInfo;
import api.development.platform.service.PaymentInfoService;
import api.development.platform.mapper.PaymentInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author poise
* @description 针对表【payment_info(付款信息)】的数据库操作Service实现
* @createDate 2024-09-07 10:19:59
*/
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
    implements PaymentInfoService{

    @Override
    public boolean createPaymentInfo(PaymentInfoVo paymentInfoVo) {
        String transactionId = paymentInfoVo.getTransactionId();
        String tradeType = paymentInfoVo.getTradeType();
        String tradeState = paymentInfoVo.getTradeState();
        String tradeStateDesc = paymentInfoVo.getTradeStateDesc();
        String successTime = paymentInfoVo.getSuccessTime();
//        WxPayOrderQueryV3Result.Payer payer = paymentInfoVo.getPayer();
//        WxPayOrderQueryV3Result.Amount amount = paymentInfoVo.getAmount();

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderNo(paymentInfoVo.getOutTradeNo());
        paymentInfo.setTransactionId(transactionId);
        paymentInfo.setTradeType(tradeType);
        paymentInfo.setTradeState(tradeState);
        if (StringUtils.isNotBlank(successTime)) {
            paymentInfo.setSuccessTime(successTime);
        }
//        paymentInfo.setOpenid(payer.getOpenid());
//        paymentInfo.setPayerTotal(amount.getPayerTotal());
//        paymentInfo.setCurrency(amount.getCurrency());
//        paymentInfo.setPayerCurrency(amount.getPayerCurrency());
//        paymentInfo.setTotal(amount.getTotal());
        paymentInfo.setTradeStateDesc(tradeStateDesc);
        paymentInfo.setContent(JSONUtil.toJsonStr(paymentInfoVo));
        return this.save(paymentInfo);
    }
}




