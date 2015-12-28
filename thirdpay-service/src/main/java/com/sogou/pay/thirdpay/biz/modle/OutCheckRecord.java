package com.sogou.pay.thirdpay.biz.modle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author qibaichao
 * @ClassName OutCheckRecord
 * @Date 2015年2月16日
 * @Description:第三方记录
 */
public class OutCheckRecord {

    /**
     * 我方订单号
     */
    private String payNo;
    /**
     * 第三方订单号
     */
    private String outPayNo;
    /**
     * 第三方交易完成时间
     */
    private Date outTransTime;
    /**
     * 交易金额，单位为元
     */
    private BigDecimal money;
    /**
     * 手续费
     */
    private BigDecimal commssionFee;
    /**
     * 我方在第三方的账户余额
     */
    private BigDecimal balance;

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public String getOutPayNo() {
        return outPayNo;
    }

    public void setOutPayNo(String outPayNo) {
        this.outPayNo = outPayNo;
    }

    public Date getOutTransTime() {
        return outTransTime;
    }

    public void setOutTransTime(Date outTransTime) {
        this.outTransTime = outTransTime;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getCommssionFee() {
        return commssionFee;
    }

    public void setCommssionFee(BigDecimal commssionFee) {
        this.commssionFee = commssionFee;
    }

    public BigDecimal getBalance() { return balance; }

    public void setBalance(BigDecimal balance) { this.balance=balance; }

    @Override
    public String toString() {
        return "OutClearRecord [payNo=" + payNo + ", outPayNo=" + outPayNo
                + ", outTransTime=" + outTransTime + ", money=" + money + "]";
    }

}
