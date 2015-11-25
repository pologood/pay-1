package com.sogou.pay.manager.payment;

import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.manager.model.PayOrderQueryModel;

/**
 * Created by hgq on 15-4-15.
 */
public interface OrderQueryManager {

    public ResultMap queryPayOrder(PayOrderQueryModel model);

}
