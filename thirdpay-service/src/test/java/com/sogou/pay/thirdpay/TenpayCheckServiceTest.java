package com.sogou.pay.thirdpay;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.thirdpay.biz.TenpayCheckService;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.modle.TenpayCheckResponse;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.biz.utils.TenPayHttpClient;
import com.sogou.pay.thirdpay.biz.utils.TenPayUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by qibaichao on 2015/3/6.
 */
public class TenpayCheckServiceTest extends BaseTest {


    @Autowired
    private TenpayCheckService tenpayCheckService;

    @Test
    public void doQuery() {

        try {
            String transTime = "2015-05-12";
            CheckType checkType = CheckType.PAYCASH;
            String merchantNo = "1900000109";
            String key = "8934e7d15453e97507ef794cf7b0519d";
            ResultMap resultMap = tenpayCheckService.doQuery(merchantNo, checkType, transTime, key);
            System.out.println(JSON.toJSON(resultMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * http://mch.tenpay.com/cgi-bin/mchdown_real_new.cgi?spid=1900000109&trans_time=2015-03-03&stamp=1425630498622&mchtype=1&sign=030e66fc658c302c1cb1aca5c358fff7
     */
    @Test
    public void createUrl() throws Exception {

        String transTime = "2015-11-04";
        CheckType checkType = CheckType.ALL;
        String merchantNo = "1234639901";
//        String key = "8934e7d15453e97507ef794cf7b0519d";
        String key = "sdf23er4edfrgh5634sdf09qw23sdsd3";


        StringBuilder sb = new StringBuilder();

        // 参数需严格按照以下顺序添加，会影响签名
        sb.append("spid=" + merchantNo).append("&");
        sb.append("trans_time=" + transTime).append("&");
        sb.append("stamp=" + String.valueOf(System.currentTimeMillis())).append("&");
        /**
         * 1：返回当日成功支付的订单
         * 2：返回当日退款的订单
         */
//        if (checkType == CheckType.PAYCASH) {
//            sb.append("mchtype=1");
//        } else if (checkType == CheckType.REFUND) {
//            sb.append("mchtype=2");
//        }

        sb.append("mchtype=0");

        //2.获取md5签名
        String sign = SecretKeyUtil.tenMd5sign(sb.toString(), key, TenPayUtil.INPUT_CHARSET);

        //3.组装访问url
        String requestUrl = TenPayUtil.DOWNLOAD_GATEWAY + "?" + sb.toString() + "&sign=" + sign;
        System.out.println(requestUrl);

        // 通信对象
        TenPayHttpClient httpClient = new TenPayHttpClient();

        httpClient.setReqContent(requestUrl);

        // 设置发送类型 GET
        httpClient.setMethod(TenPayUtil.DOWNLOAD_METHOD);

        httpClient.setCharset("GBK");

        String message = "";

        TenpayCheckResponse tenpayClearResponse = new TenpayCheckResponse();

        if (httpClient.call()) {

            message = httpClient.getResContent();

            System.out.println(message);
//            String iso = new String(message.getBytes("UTF-8"),"ISO-8859-1");
//            System.out.println(new String(iso.getBytes("ISO-8859-1"),"UTF-8"));


        }

    }

}
