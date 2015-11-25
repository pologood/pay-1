package com.sogou.pay.manager.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wujingpan on 2015/3/6.
 */
public class ChannelAdaptModel implements Serializable{

    private List<CommonAdaptModel> commonPay4DebitList;//网银支付银行列表(储蓄卡)
    private List<CommonAdaptModel> commonPay4CreditList;//网银支付银行列表(信用卡)
    private List<CommonAdaptModel> payOrgList;//第三方支付列表
    private List<CommonAdaptModel> scanCodeList;//扫码支付列表
    private List<CommonAdaptModel> b2bList;//B2B支付列表

    public List<CommonAdaptModel> getCommonPay4DebitList() {
        return commonPay4DebitList;
    }

    public void setCommonPay4DebitList(List<CommonAdaptModel> commonPay4DebitList) {
        this.commonPay4DebitList = commonPay4DebitList;
    }

    public List<CommonAdaptModel> getCommonPay4CreditList() {
        return commonPay4CreditList;
    }

    public void setCommonPay4CreditList(List<CommonAdaptModel> commonPay4CreditList) {
        this.commonPay4CreditList = commonPay4CreditList;
    }

    public List<CommonAdaptModel> getPayOrgList() {
        return payOrgList;
    }

    public void setPayOrgList(List<CommonAdaptModel> payOrgList) {
        this.payOrgList = payOrgList;
    }

    public List<CommonAdaptModel> getScanCodeList() {
        return scanCodeList;
    }

    public void setScanCodeList(List<CommonAdaptModel> scanCodeList) {
        this.scanCodeList = scanCodeList;
    }

    
    public List<CommonAdaptModel> getB2bList() {
        return b2bList;
    }

    public void setB2bList(List<CommonAdaptModel> b2bList) {
        this.b2bList = b2bList;
    }

    @Override
    public String toString() {
        return "ChannelAdaptModel{" +
                "commonPay4DebitList=" + commonPay4DebitList +
                ", commonPay4CreditList=" + commonPay4CreditList +
                ", payOrgList=" + payOrgList +
                ", scanCodeList=" + scanCodeList +
                ", b2bList=" + b2bList +
                '}';
    }
}
