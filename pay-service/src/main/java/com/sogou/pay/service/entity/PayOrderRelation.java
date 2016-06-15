package com.sogou.pay.service.entity;

import java.util.Date;

/**
 * @Author huangguoqing
 * @Date 2015/3/4 15:14
 * @Description: 支付单与支付流水关联表
 */
public class PayOrderRelation {
    public static final int INFOSTATUS_INIT = 0;
    public static final int INFOSTATUS_SUCCESS = 1;
    public static final int INFOSTATUS_REFUND = 3;

    //支付流水号
    private String payDetailId;

    //支付单号
    private String payId;

    //信息状态 1:有效 2:无效
    private Integer infoStatus;

    //创建时间
    private Date createTime;

    public String getPayDetailId() {

        return payDetailId;
    }

    public void setPayDetailId(String payDetailId) {
        this.payDetailId = payDetailId;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public Integer getInfoStatus() {
        return infoStatus;
    }

    public void setInfoStatus(Integer infoStatus) {
        this.infoStatus = infoStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
