package com.sogou.pay.common.result;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hjf on 15-2-28.
 */
public class ResultMap<T> extends Result<T> {

    private Map<String, Object> data = new LinkedHashMap<>();

    protected ResultMap(ResultStatus status, String message) {
        super(status, message);
    }

    public static <T> ResultMap<T> build() {
        return new ResultMap<>(ResultStatus.SUCCESS, null);
    }

    public static <T> ResultMap<T> build(ResultStatus resultStatus) {
        return new ResultMap<>(resultStatus, null);
    }

    public ResultMap<T> addItems(Map data) {
        this.data.putAll(data);
        return this;
    }

    public ResultMap<T> addItem(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }
}
