package com.sogou.pay.manager.payment;

import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.manager.model.RefundModel;
import com.sogou.pay.manager.model.thirdpay.FairAccRefundModel;

/**
 * Created by hjf on 15-3-2.
 */
public interface RefundManager {

    public ResultMap refund(RefundModel model);

    public Result fairAccountRefund(FairAccRefundModel model);

}
