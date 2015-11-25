package com.sogou.pay.common.cache;

/**
 * User: Liwei
 * Date: 2015/1/19
 * Time: 18:07
 */
public class CacheConstant {
    /*================SDK 接入相关缓存常量=====================*/
    public static final String CACHE_PREFIX_SDK_WECHAT_ACCESSTOKEN = "PAY.SDK.WECHAT.ACCESS_TOKEN";   //微信支付access_token
    public static final long SDK_WECHAT_ACCESSTOKEN_EXPIRE = 1000 * 60 * 100; // 缓存失效时间，100分钟，单位ms
}
