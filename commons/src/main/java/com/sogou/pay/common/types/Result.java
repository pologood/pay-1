package com.sogou.pay.common.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sogou.pay.common.utils.JSONUtil;

/**
 * Created by hjf on 15-2-28.
 */
public abstract class Result<T> {
    private ResultStatus status;
    private String message;

    // 使用JsonMessageConverter返回值时不序列化该值，保证只在内部传递
    @JsonIgnore
    private T returnValue;

    protected Result(ResultStatus status, String message) {
        this.status = status;
        if (message == null)
            this.message = status.getMessage();
        else
            this.message = message;
    }

    /**
     * 提供静态方法检测是否成功，避免null值无法检测自身的情况
     *
     * @param result
     * @return 是否成功状态
     */
    public static boolean isSuccess(Result result) {
        return result != null && result.status == ResultStatus.SUCCESS;
    }

    /**
     * 同withStatus()方法，代替原方法
     *
     * @param status ResultStatus状态值
     * @return 当前实例
     */
    public Result<T> withError(ResultStatus status) {
        this.status = status;
        this.message = status.getMessage();
        return this;
    }

    /**
     * 设置状态为ResultStatus.SUCCESS
     *
     * @return 当前实例
     */
    public Result<T> withSuccess() {
        this.status = ResultStatus.SUCCESS;
        this.message = ResultStatus.SUCCESS.getMessage();
        return this;
    }

    /**
     * 增加信息覆盖ResultStatus的默认Message
     *
     * @param message
     * @return 当前实例
     */
    public Result<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 设置函数调用返回值，只在服务内部或服务间传输使用，不向外部输出
     *
     * @param object
     * @return
     */
    public Result<T> withReturn(T object) {
        this.returnValue = object;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        /*Map newMap = Maps.newHashMap(data);
        newMap.put("error_code", status.getOutputName());
        return JSONUtil.Bean2JSON(newMap);*/

        return JSONUtil.Bean2JSON(this);
    }

    public abstract Object getData();

    @JsonIgnore
    public T getReturnValue() {
        return returnValue;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message == null ? status.getMessage() : message;
    }

    private void setReturnValue(T returnValue) {
        this.returnValue = returnValue;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    private void setStatus(ResultStatus status) {
        this.status = status;
    }
}
