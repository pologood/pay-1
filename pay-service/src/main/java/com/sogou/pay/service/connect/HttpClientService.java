package com.sogou.pay.service.connect;

import com.sogou.pay.common.http.model.RequestModel;

/**
 * Created by hujunfei Date: 14-12-31 Time: 下午4:14
 */
public interface HttpClientService {
    public String executeStr(RequestModel requestModel);
}
