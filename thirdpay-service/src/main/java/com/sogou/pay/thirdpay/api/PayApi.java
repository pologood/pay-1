package com.sogou.pay.thirdpay.api;

import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.PMap;

/**
 * 支付请求组装参数
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:04
 */
public interface PayApi {

    public ResultMap<String> preparePay(PMap params);
}
