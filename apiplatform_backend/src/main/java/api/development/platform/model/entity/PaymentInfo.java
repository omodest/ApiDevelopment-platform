package api.development.platform.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 付款信息
 * @TableName payment_info
 */
@TableName(value ="payment_info")
@Data
public class PaymentInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)
     */
    private String tradeState;

    /**
     * 交易状态描述
     */
    private String tradeStateDesc;

    /**
     * 支付完成时间
     */
    private String successTime;

    /**
     * 用户标识
     */
    private String openid;

    /**
     * 用户支付金额
     */
    private Long payerTotal;

    /**
     * 货币类型
     */
    private String currency;

    /**
     * 用户支付币种
     */
    private String payerCurrency;

    /**
     * 接口返回内容
     */
    private String content;

    /**
     * 总金额(分)
     */
    private Long total;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}