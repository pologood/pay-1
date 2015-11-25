package com.sogou.pay.thirdpay.biz.modle;


import com.sogou.pay.thirdpay.biz.enums.CheckType;

/**
 * @Author qibaichao
 * @ClassName WechatCheckRequest
 * @Date 2015年2月16日
 * @Description:
 */
public class WechatCheckRequest {

    private String transTime;

    private CheckType checkType;

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
