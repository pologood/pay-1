package com.sogou.pay.thirdpay.api;

import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.PMap;

/**
 * 请求第三方支付机构查询订单退款结果
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/9 11:11
 */
public interface QueryRefundApi {

    public ResultMap<String> queryRefund(PMap params);

}
