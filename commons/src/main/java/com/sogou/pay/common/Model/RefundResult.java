package com.sogou.pay.common.Model;

import java.io.Serializable;

/**
 * 退款同步返回对象
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/09/01 10:08
 */
public class RefundResult implements Serializable {
    private String status;           // 程序调用状态
    private String message;         // 附加信息
    private String errorCode = "";           // 错误码
    private String errorMsg = "";            // 提示信息

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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
