package com.sogou.pay.thirdpay.biz;

import com.sogou.pay.common.types.ResultMap;

/**
 * Created by qibaichao on 2015/6/29.
 * 块钱对账查询
 */
public interface BillCheckService {

    public ResultMap doPayQuery(String merchantNo,
                             String startTime,
                             String endTime,
                             String pageNo,
                             String key) ;

    public ResultMap doRefundQuery(String merchantNo,
                                String startTime,
                                String endTime,
                                String pageNo,
                                String key) ;
}
