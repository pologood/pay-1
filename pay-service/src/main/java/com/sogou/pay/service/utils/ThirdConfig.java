package com.sogou.pay.service.utils;

import com.sogou.pay.common.utils.PMap;

public class ThirdConfig {
    private static final PMap<String, String> thirdMap = new PMap<String, String>();
    static {
        //PC支付宝网关
        thirdMap.put("1_1_ALIPAY", "PC_ALIPAY_GATEWAY");
        //PC支付宝账户
        thirdMap.put("1_2_ALIPAY", "PC_ALIPAY_ACCOUNT");
        //PC支付宝扫码
        thirdMap.put("1_3_ALIPAY", "PC_ALIPAY_SWEEPYARD");
        //PC财付通网关
        thirdMap.put("1_1_TENPAY", "PC_TENPAY_GATEWAY");
        //PC财付通账户
        thirdMap.put("1_2_TENPAY", "PC_TENPAY_ACCOUNT");
        //PC快钱账户
        thirdMap.put("1_2_BILL99", "PC_99BILL");
        //PC快钱B2B支付
        thirdMap.put("1_4_BILL99", "PC_99BILL");
        //PC微信支付
        thirdMap.put("1_3_WECHAT", "PC_WECHAT_SWEEPYARD");
        //WAP支付宝账户
        thirdMap.put("2_2_ALIPAY", "MOBILE_ALIPAY_WAP");
        //SDK支付宝账户
        thirdMap.put("3_2_ALIPAY", "MOBILE_ALIPAY_CLIENT");
        //SDK微信账户
        thirdMap.put("3_2_WECHAT", "MOBILE_WECHAT_CLIENT");
    }

    public static String getInstanceName(String platForm, String payFeeType, String agencyCode) {
        
        String Key = platForm + "_" + payFeeType + "_" + agencyCode;
        return thirdMap.get(Key);
    }
}
