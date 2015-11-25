package com.sogou.pay.thirdpay.api;

import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.PMap;

/**
 * 请求第三方支付机构查询订单支付结果
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:04
 */
public interface QueryApi {

    public ResultMap<String> queryOrder(PMap params);

}
