package com.sogou.pay.service.utils;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * @Author huangguoqing
 * @Date 2015/3/4 18:27
 * @Description: 常量类
 */
public class Constant {
    //支付宝编码
    public static final String ALIPAY = "ALIPAY";
    //财付通编码
    public static final String TENPAY = "TENPAY";
    //微信扫码支付
    public static final String WECHAT = "WECHAT";
    //支付宝扫码支付
    public static final String ALIPAY_SY = "ALIPAY_SY";
    /**
     * 支付机构支付方式*
     */
    //网关支付
    public static final String PAY_FEE_TYPE_1 = "1";
    //第三方支付
    public static final String PAY_FEE_TYPE_2 = "2";
    //扫码支付
    public static final String PAY_FEE_TYPE_3 = "3";
    //企业网银支付
    public static final String PAY_FEE_TYPE_4 = "4";
    /**
     * 支付平台
     */
    public static final String ACCESS_PLATFORM_PC = "1";
    
    public static final String ACCESS_PLATFORM_WAP = "2";
    
    public static final String ACCESS_PLATFORM_SDK = "3";
    
    /**
     * 查询接口URL*
     */
    //支付查询接口URL
    public static final HashMap<String, String> QUERY_URL_MAP = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            put("ALIPAY", "https://mapi.alipay.com/gateway.do");
            put("TENPAY", "https://gw.tenpay.com/gateway/normalorderquery.xml");
            put("WECHAT", "https://api.mch.weixin.qq.com/pay/orderquery");
        }
    };

    //公司名称
    public static final HashMap<Integer, String> COMPANYMAP = new HashMap<Integer, String>() {
        {
            put(1, "北京搜狗网络技术有限公司");
            put(2, "北京搜狗科技发展有限公司");
        }
    };
    
    //支付成功
    public static final int PAY_SUCCESS = 3;
    public static final int REFUND_SUCCESS = 3;
    public static final int PAYS_TATUS = 1;

    //支付单支付流水关联表
    public static final int PAY_SUCCESS_1 = 1;
    public static final int PAY_REFUND_3 = 3;
    
    //支付宝快捷支付信用卡渠道类型
    public static final String CREDIT_CARTOON = "CREDIT_CARTOON";
    public static final String MOTO_CREDIT_CARD = "MOTO_CREDIT_CARD";
    public static final String OPTIMIZED_MOTO = "OPTIMIZED_MOTO";
    public static final String BIGAMOUNT_CREDIT_CARTOON = "BIGAMOUNT_CREDIT_CARTOON";
    public static final String CREDIT_EXPRESS_INSTALLMENT = "CREDIT_EXPRESS_INSTALLMENT";
    public static final BigDecimal FEE_RATE= BigDecimal.valueOf(0.01);
    
    
    /**
     * 业务模式编号
     */
    public static final String BUS_MOD_1 = "00001";
    
    /**
     * 业务类别
     */
    //代发工资
    public static final String BUS_COD_SALARY = "N03010";
    //代发
    public static final String BUS_COD_OTHER = "N03020";
    //代扣
    public static final String BUS_COD_WITHHOLDING = "N03030";
    
    /**
     * 交易代码名称
     */
    //代发工资
    public static final String PAY_SALARY = "BYSA";
    //代发其他
    public static final String PAY_OTHER = "BYBK";
}
