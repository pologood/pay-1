package com.sogou.pay.manager.payment;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.service.entity.PayTransferBatch;

/**
 * Created by qibaichao on 2015/7/3.
 */
public interface PayTransferQueryManager {

    public Result doProcess(String appId,String batchNo);

    public Result doProcess(PayTransferBatch payTransferBatch);
}
