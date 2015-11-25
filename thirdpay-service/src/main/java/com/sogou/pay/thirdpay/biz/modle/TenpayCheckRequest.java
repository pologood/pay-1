package com.sogou.pay.thirdpay.biz.modle;


import com.sogou.pay.thirdpay.biz.enums.CheckType;

/**
 * @Author qibaichao
 * @ClassName TenpayClearRequest
 * @Date 2015年2月16日
 * @Description:
 */
public class TenpayCheckRequest {

    private String transTime;

    private CheckType checkType;

    private String merchantNo;

    private String key ;


    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public void setCheckType(CheckType checkType) {
        this.checkType = checkType;
    }


}
