package com.sogou.pay.manager.secure;

import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.utils.PMap;

import java.util.Map;

/**
 * Created by hujunfei Date: 15-1-5 Time: 上午11:42
 */
public interface SecureManager {
    /**
     * 验证应用请求签名，必需参数sign/signType/appId
     * @param params 请求参数
     * @return 验证结果
     */
    public Result verifyAppSign(Object params);

    public Result appSign(Object paramMap);

    /**
     * 验证第三方回调签名
     * @param params 回调参数
     * @param merchantId 商户表ID
     * @return 验证结果
     */
    public Result verifyThirdSign(Object params, int merchantId);

    /**
     * 支付回调签名校验
     */
    public Result verifyNotifySign(PMap<String,String> pMap, String agency, String partner, String... platform);

}
