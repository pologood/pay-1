package com.sogou.pay.thirdpay.api;

import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.PMap;

/**
 * 请求第三方支付机构退款
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:04
 */
public interface RefundApi {

    public ResultMap<String> refundOrder(PMap params);
}
