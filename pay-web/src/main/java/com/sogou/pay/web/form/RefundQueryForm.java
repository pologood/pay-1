package com.sogou.pay.web.form;

import com.sogou.pay.common.constraint.PositiveNumber;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

/**
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/16 11:11
 */
public class RefundQueryForm {
    @NotBlank(message = "业务ID不能为空！")
    @Digits(integer = 6, fraction = 0, message = "业务平台ID必须为整数")
    @PositiveNumber
    private String appId;           // 应用ID

    @NotBlank(message = "商户订单号不能为空！")
    @Length(max = 30)
    private String orderId;         // 商户订单ID

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
