package com.sogou.pay.web.form;

import com.sogou.pay.common.constraint.Amount;
import com.sogou.pay.common.constraint.PositiveNumber;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

/**
 * Created by hjf on 15-3-2.
 */
public class RefundParams {
    @NotBlank(message = "业务ID不能为空！")
    @Digits(integer=6,fraction=0,message = "业务平台ID必须为整数")
    private String appId;           // 应用ID

    @NotBlank(message = "商户订单号不能为空！")
    @Length(max = 30)
    private String orderId;         // 商户订单ID

    //@NotBlank(message = "订单金额不能空,！")
    @DecimalMin(value="0.01",message="金额最小为0.01元")
    @Amount(message = "订单金额格式为必须是大于0的浮点数，整数部分最多10位，两位小数")
    private String refundAmount;    // 退款金额，以元为单位小数

    @NotBlank(message = "退款通知地址不能为空！")
    @URL(message = "退款通知地址必须为URL格式！")
    @Length(max = 256)
    private String bgUrl;           // 回调地址

    @NotBlank(message = "签名不能为空！")
    private String sign;            // 签名

    @NotBlank(message = "签名类型不能为空！")
    private String signType;        // 签名方式

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }
}
