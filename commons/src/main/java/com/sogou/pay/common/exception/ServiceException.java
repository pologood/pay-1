package com.sogou.pay.common.exception;


import com.sogou.pay.common.types.ResultStatus;

public class ServiceException extends Exception {
    private ResultStatus status;

    public ServiceException(ResultStatus status) {
        super("[" + status.getCode() + ", " + status + "]");
        this.status = status;
    }

    public ServiceException(String message, ResultStatus status) {
        super(message);
        this.status = status;
    }

    public ServiceException(String message, Throwable cause, ResultStatus status) {
        super(message, cause);
        this.status = status;
    }

    public ServiceException(Throwable cause, ResultStatus status) {
        super(cause);
        this.status = status;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public int getErrorCode() {
        return status.getCode();
    }

    public String getErrorName() {
        return status.name();
    }
}
