package com.sogou.pay.common.Model;

import java.io.Serializable;

/**
 * 订单支付状态查询同步返回对象
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/09/01 15:42
 */
public class QueryOrderResult implements Serializable {
    private String status;           // 程序调用状态
    private String message;         // 附加信息
    private Object payStatus;    //退款结果

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Object payStatus) {
        this.payStatus = payStatus;
    }
}
