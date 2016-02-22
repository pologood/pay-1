package com.sogou.pay.manager.payment;

import com.sogou.pay.common.types.Result;

/**
 * Created by qibaichao on 2015/6/12.
 * 代付manager
 */
public interface PayTranferRequestManager {

    public Result doProcess(String appId,String batchNo);
}
