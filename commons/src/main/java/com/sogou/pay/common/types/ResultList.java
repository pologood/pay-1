package com.sogou.pay.common.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjf on 15-2-28.
 */
public class ResultList<T> extends Result<T> {
    private List data = new ArrayList<>();

    protected ResultList(ResultStatus status, String message) {
        super(status, message);
    }

    public static <T> ResultList<T> build() {
        return new ResultList<>(ResultStatus.SUCCESS, null);
    }

    public Result<T> addItems(List data) {
        this.data.addAll(data);
        return this;
    }

    public Result<T> addItem(Object value) {
        this.data.add(value);
        return this;
    }

    @Override
    public List getData() {
        return data;
    }
}
