package com.sogou.pay.thirdpay.biz.modle;


import com.sogou.pay.thirdpay.biz.enums.CheckType;

/**
 * Created by qibaichao on 2015/3/4.
 */
public class AlipayCheckResquest {

    private int pageNo;
    private String startTime;
    private String endTime;
    private CheckType checkType;
    private String merchantNo;
    private String key;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public void setCheckType(CheckType checkType) {
        this.checkType = checkType;
    }

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
}
