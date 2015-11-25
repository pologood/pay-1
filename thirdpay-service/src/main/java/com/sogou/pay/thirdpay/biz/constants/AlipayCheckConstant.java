package com.sogou.pay.thirdpay.biz.constants;

/**
 * @Author qibaichao
 * @ClassName AlipayConstant
 * @Date 2015年2月16日
 * @Description:ALIPAY 公共常量
 */
public class AlipayCheckConstant {


    /**
     * 这个token作为备注信息传给支付宝，清算时候通过这个token来识别是不是退款交易
     */
    public final static String REFUND_ORDER_TOKEN = "refund-no";


    /**
     * 清算网关
     */
    public final static String CLEAR_GATEWAY = "https://mapi.alipay.com/gateway.do";




}
