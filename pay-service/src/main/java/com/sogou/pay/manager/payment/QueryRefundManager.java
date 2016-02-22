package com.sogou.pay.manager.payment;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.manager.model.QueryRefundModel;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/16 11:11
 */
public interface QueryRefundManager {

    public ResultMap queryRefund(QueryRefundModel model);

}
