package com.sogou.pay.web.form;

import com.sogou.pay.common.constraint.PositiveNumber;
import org.hibernate.validator.constraints.NotBlank;


public class TransferQueryForm {

    private String appId;           // 应用ID
    private String batchNo;         // 批次号
    private String sign;            // 签名
    private String signType;        // 签名方式

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
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
