package com.sogou.pay.manager.payment;

import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultMap;

/**
 * Created by qibaichao on 2015/6/12.
 * 代付manager
 */
public interface PayTranferRequestManager {

    public Result doProcess(String appId,String batchNo);
}
