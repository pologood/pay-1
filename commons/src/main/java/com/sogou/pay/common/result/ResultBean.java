package com.sogou.pay.common.result;

/**
 * Created by wujingpan on 2015/3/6.
 */
@SuppressWarnings("rawtypes")
public class ResultBean<T> extends Result {


    protected ResultBean(ResultStatus status, String message) {
		super(status, message);
	}
    public static <T> ResultBean<T> build() {
        return new ResultBean<T>(ResultStatus.SUCCESS, null);
    }
	@Override
    public Object getData() {
        return null;
    }
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    /**
     * 成功
     */
    public void success(T value) {
        this.withSuccess();
        this.value = value;
    }
}
