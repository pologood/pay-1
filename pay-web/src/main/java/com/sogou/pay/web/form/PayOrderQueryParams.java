package com.sogou.pay.web.form;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import com.sogou.pay.common.constraint.Amount;
import com.sogou.pay.common.constraint.PositiveNumber;

/**
 * Created by hgq on 15-4-15.
 */
public class PayOrderQueryParams {
    @NotBlank
    @PositiveNumber
    private String appId;           // 应用ID
    @NotBlank
    private String orderId;         // 商户订单ID

    @NotBlank
    private String sign;            // 签名

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
