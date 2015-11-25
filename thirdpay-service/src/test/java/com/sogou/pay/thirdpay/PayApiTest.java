package com.sogou.pay.thirdpay;

import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.*;
import com.sogou.pay.thirdpay.api.PayApi;

import com.sogou.pay.thirdpay.biz.utils.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/28 15:41
 */
public class PayApiTest extends BaseTest {

    @Autowired
    private PayApi payApi;

    /**
     * 财付通账户支付
     */
    @Test
    public void testTenAccountPay() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "PC_TENPAY_ACCOUNT");
        pmap.put("merchantNo", "1900000109");
        pmap.put("sellerEmail", "");
        pmap.put("prepayUrl", "");
        pmap.put("payUrl", "https://gw.tenpay.com/gateway/pay.htm");
        pmap.put("serverNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("pageNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("subject", "搜狗糖猫");
        pmap.put("buyerIp", "127.0.0.1");
        pmap.put("md5securityKey", "8934e7d15453e97507ef794cf7b0519d");
        pmap.put("publicCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("privateCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("bankCode", "ABC");
        pmap.put("payTime", new Date());
        pmap.put("orderAmount", 0.01);
        pmap.put("serialNumber", "ZFD201503101812");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }

    /**
     * 财付通网关支付
     */
    @Test
    public void testTenGatewayPay() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "PC_TENPAY_GATEWAY");
        pmap.put("merchantNo", "1900000109");
        pmap.put("sellerEmail", "");
        pmap.put("prepayUrl", "");
        pmap.put("payUrl", "https://gw.tenpay.com/gateway/pay.htm");
        pmap.put("serverNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("pageNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("subject", "搜狗糖猫");
        pmap.put("buyerIp", "127.0.0.1");
        pmap.put("md5securityKey", "8934e7d15453e97507ef794cf7b0519d");
        pmap.put("publicCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("privateCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("bankCode", "ABC");
        pmap.put("payTime", new Date());
        pmap.put("orderAmount", "1");
        pmap.put("serialNumber", "200-20150104-70441927-036306");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }

    /**
     * 支付宝账户支付
     */
    @Test
    public void testAliAccountPay() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "PC_ALIPAY_ACCOUNT");
        pmap.put("merchantNo", "2088811923135335");
        pmap.put("sellerEmail", "");
        pmap.put("prepayUrl", "");
        pmap.put("payUrl", "https://mapi.alipay.com/gateway.do");
        pmap.put("serverNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("pageNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("subject", "搜狗糖猫");
        pmap.put("buyerIp", "127.0.0.1");
        pmap.put("md5securityKey", "20w5obaxam7keamcuzk7cfiu46j4htg0");
        pmap.put("publicCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("privateCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("payTime", new Date());
        pmap.put("orderAmount", "12");
        pmap.put("serialNumber", "20234234234345");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }

    /**
     * 支付宝网关支付
     */
    @Test
    public void testAliGatewayPay() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "PC_ALIPAY_GATEWAY");
        pmap.put("merchantNo", "2088811923135335");
        pmap.put("sellerEmail", "");
        pmap.put("prepayUrl", "");
        pmap.put("payUrl", "https://mapi.alipay.com/gateway.do");
        pmap.put("serverNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("pageNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("subject", "搜狗糖猫");
        pmap.put("buyerIp", "127.0.0.1");
        pmap.put("md5securityKey", "20w5obaxam7keamcuzk7cfiu46j4htg0");
        pmap.put("publicCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("privateCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("bankCode", "ABC");
        pmap.put("payTime", new Date());
        pmap.put("orderAmount", "12");
        pmap.put("serialNumber", "200-20150104-7044927-036306sss");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }

    /**
     * 支付宝扫码支付
     */
    @Test
    public void testSweepYardsPreparePayInfo() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "PC_ALIPAY_SWEEPYARD");
        pmap.put("merchantNo", "2088811923135335");
        pmap.put("sellerEmail", "sogouwangluo@sogou-inc.com");
        pmap.put("prepayUrl", "");
        pmap.put("payUrl", "https://mapi.alipay.com/gateway.do");
        pmap.put("serverNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("pageNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("subject", "搜狗糖猫");
        pmap.put("buyerIp", "127.0.0.1");
        pmap.put("md5securityKey", "20w5obaxam7keamcuzk7cfiu46j4htg0");
        pmap.put("publicCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("privateCertFilePath", "/pay_key/tenpay/cacert.pem");
        pmap.put("bankCode", "ABC");
        pmap.put("payTime", new Date());
        pmap.put("orderAmount", "12");
        pmap.put("serialNumber", "20234234223232334345");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }

    /**
     * 微信扫码支付
     */
    @Test
    public void testWechatSweepYardsPreparePayInfo() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "PC_WECHAT_SWEEPYARD");
        pmap.put("merchantNo", "1246690601");
        pmap.put("sellerEmail", "wx9a3d0ebef06f62b8");
        pmap.put("prepayUrl", "https://api.mch.weixin.qq.com/pay/unifiedorder");
        pmap.put("payUrl", "https://api.mch.weixin.qq.com/pay/unifiedorder");
        pmap.put("serverNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("pageNotifyUrl", "http: //center.pay.sogou.com/notify/wechat/pay/webSync");
        pmap.put("subject", "测试商品");
        pmap.put("buyerIp", "127.0.0.1");
        pmap.put("md5securityKey", "2idEk034kdui3WESr8Ef8wW9edfQw3s2");
        pmap.put("publicCertFilePath", "/pay_key/wechat_keji/rootca.pem");
        pmap.put("privateCertFilePath", "/pay_key/wechat_keji/apiclient_cert.p12");
        pmap.put("payTime", new Date());
        pmap.put("orderAmount", "0.01");
        pmap.put("serialNumber", "ZF20150715110914715001");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }

    /**
     * 支付宝wap支付
     */
    @Test
    public void testAliWapPay() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "MOBILE_ALIPAY_WAP");
        pmap.put("merchantNo", "2088811923135335");
        pmap.put("sellerEmail", "sogouwangluo@sogou-inc.com");
        pmap.put("prepayUrl", "http://wappaygw.alipay.com/service/rest.htm?_input_charset=utf-8");
        pmap.put("payUrl", "http://wappaygw.alipay.com/service/rest.htm");
        pmap.put("serverNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("pageNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("subject", "搜狗糖猫");
        pmap.put("buyerIp", "127.0.0.1");
        pmap.put("md5securityKey", "20w5obaxam7keamcuzk7cfiu46j4htg0");
        pmap.put("publicCertFilePath", "/pay_key/alipay/alipay_public_key.pem");
        pmap.put("privateCertFilePath", "/pay_key/alipay/rsa_private_key.pem");
        pmap.put("bankCode", "ABC");
        pmap.put("payTime", new Date());
        pmap.put("orderAmount", "2");
        pmap.put("serialNumber", "200-20150104-7044921111323");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }

    /**
     * 快钱账户支付
     */
    @Test
    public void testBillAccountPay() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "PC_99BILL");
        pmap.put("merchantNo", "10012138842");
        pmap.put("prepayUrl", "https://www.99bill.com/gateway/recvMerchantInfoAction.htm");
        pmap.put("payUrl", "https://sandbox.99bill.com/gateway/recvMerchantInfoAction.htm");
        pmap.put("serverNotifyUrl", "http://sg.pay.sogou.com/pay-web/notify/ali/pay/webAsync");
        pmap.put("pageNotifyUrl", "http://sg.pay.sogou.com/pay-web/notify/ali/pay/webSync");
        pmap.put("subject", "测试商品");
        pmap.put("buyerIp", "220.181.124.128");
        pmap.put("md5securityKey", "8934e7d15453e97507ef794cf7b0519d");
        pmap.put("publicCertFilePath", "/pay_key/99bill/tester-rsa.pfx");
        pmap.put("privateCertFilePath", "/pay_key/99bill/tester-rsa.pfx");
        pmap.put("payTime", "20150302165723");
        pmap.put("orderAmount", 0.01);
        pmap.put("serialNumber", "ZF20150709183241121001");
//        pmap.put("bankCode", "ABC_B2B");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }

    /**
     * 支付宝客户端支付
     */
    @Test
    public void testAliClinPay() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "MOBILE_ALIPAY_CLIENT");
        pmap.put("merchantNo", "2088811923135335");
        pmap.put("sellerEmail", "sogouwangluo@sogou-inc.com");
        pmap.put("prepayUrl", "http://wappaygw.alipay.com/service/rest.htm?_input_charset=utf-8");
        pmap.put("payUrl", "http://wappaygw.alipay.com/service/rest.htm");
        pmap.put("serverNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("pageNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("subject", "搜狗糖猫");
        pmap.put("buyerIp", "127.0.0.1");
        pmap.put("md5securityKey", "20w5obaxam7keamcuzk7cfiu46j4htg0");
        pmap.put("publicCertFilePath", "/pay_key/alipay/alipay_public_key.pem");
        pmap.put("privateCertFilePath", "/pay_key/alipay/rsa_private_key.pem");
        pmap.put("bankCode", "ABC");
        pmap.put("payTime", new Date());
        pmap.put("orderAmount", "2");
        pmap.put("serialNumber", "200-20150104-7044921111323");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }

    /**
     * 快钱账户支付
     */
    @Test
    public void testBillB2BPay() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "PC_99BILL");
        pmap.put("merchantNo", "10012138842");
        pmap.put("prepayUrl", "https://www.99bill.com/gateway/recvMerchantInfoAction.htm");
        pmap.put("payUrl", "https://sandbox.99bill.com/gateway/recvMerchantInfoAction.htm");
        pmap.put("serverNotifyUrl", "http://sg.pay.sogou.com/pay-web/notify/ali/pay/webAsync");
        pmap.put("pageNotifyUrl", "http://center.pay.sogou.com/notify/bil99/pay/webSync");
        pmap.put("subject", "测试商品");
        pmap.put("buyerIp", "220.181.124.128");
        pmap.put("md5securityKey", "8934e7d15453e97507ef794cf7b0519d");
        pmap.put("publicCertFilePath", "/pay_key/99bill/tester-rsa.pfx");
        pmap.put("privateCertFilePath", "/pay_key/99bill/tester-rsa.pfx");
        pmap.put("payTime", "20150302165723");
        pmap.put("orderAmount", 10);
        pmap.put("serialNumber", "ZF201507091183241121001");
        pmap.put("bankCode", "ABC_B2B");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }


    /**
     * 微信sdk支付--妙手中医
     */
    @Test
    public void testWechatSdkPreparePayInfo() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("payChannle", "MOBILE_WECHAT_CLIENT");
        pmap.put("merchantNo", "1274744201");
        pmap.put("sellerEmail", "wx0947a8d5acd6d28e");
        pmap.put("prepayUrl", "https://api.mch.weixin.qq.com/pay/unifiedorder");
        pmap.put("payUrl", "https://api.mch.weixin.qq.com/pay/unifiedorder");
        pmap.put("serverNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("pageNotifyUrl", "http: //center.pay.sogou.com/notify/wechat/pay/webSync");
        pmap.put("subject", "11111");
        pmap.put("buyerIp", "127.0.0.1");
        pmap.put("md5securityKey", "34dfgdf45gfh56fbhd34gdr456768fg1");
        pmap.put("publicCertFilePath", "/pay_key/wechat_zhongyi/rootca.pem");
        pmap.put("privateCertFilePath", "/pay_key/wechat_zhongyi/apiclient_cert.p12");
        pmap.put("payTime", new Date());
        pmap.put("orderAmount", "0.01");
        pmap.put("serialNumber", "11233423434dd2342343");
        result = payApi.preparePay(pmap);
        System.out.print("result" + result);
    }

    /**
     * 微信sdk支付--妙手中医
     */
    @Test
    public void testWechatSdkPreparePayInfo1() {
        boolean isConnect = false;
        URL url = null;
        try {
            url = new URL("https://api.mch.weixin.qq.com/secapi/pay/refund");
            String A = url.getAuthority();
            String b = url.getFile();
            String v = url.getHost();
            String d = url.getPath();
            String ff = url.getProtocol();
            String f = url.getQuery();

            try {
                InputStream in = url.openStream();
                in.close();
                isConnect = true;
            } catch (IOException e) {
                isConnect = false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.print("isConnect" + isConnect);

    }

    /**
     * 第一个.微信公众号支付-获取授权code
     * 注意：
     * 1、在微信公众号请求用户网页授权之前，开发者需要先到公众平台官网中的开发者中心页配置授权回调域名。
     *    请注意，这里填写的是域名（是一个字符串），而不是URL，因此请勿加http://等协议头；
     * 2、授权回调域名配置规范为全域名，比如需要网页授权的域名为：www.qq.com，配置以后此域名下面的页面
     *    http://www.qq.com/music.html 、 http://www.qq.com/login.html 都可以进行OAuth2.0鉴权。
     *    但http://pay.qq.com 、 http://music.qq.com 、 http://qq.com无法进行OAuth2.0鉴权
     * 3、以snsapi_base为scope发起的网页授权，是用来获取进入页面的用户的openid的，并且是静默授权并自
     *    动跳转到回调页的。用户感知的就是直接进入了回调页（往往是业务页面）
     * 4、以snsapi_userinfo为scope发起的网页授权，是用来获取用户的基本信息的。但这种授权需要用户手动同意，
     *    并且由于用户同意过，所以无须关注，就可在授权后获取该用户的基本信息。
     * 5、用户管理类接口中的“获取用户基本信息接口”，是在用户和公众号产生消息交互或关注后事件推送后，
     *    才能根据用户OpenID来获取用户基本信息。这个接口，包括其他微信接口，都是需要该用户（即openid）
     *    关注了公众号后，才能调用成功的。
     * 6、上面已经提到，对于以snsapi_base为scope的网页授权，就静默授权的，用户无感知；
     * 7、对于已关注公众号的用户，如果用户从公众号的会话或者自定义菜单进入本公众号的网页授权页，即使是scope
     *    为snsapi_userinfo，也是静默授权，用户无感知。
     * 8、code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     */
    @Test
    public void testWechatGetCode() {
        ResultMap result = ResultMap.build();
        //1.根据文档说明，组装md5加密参数
        PMap requestPMap = new PMap();
        //公众号的唯一标识
        requestPMap.put("appid", "wx9a3d0ebef06f62b8");
        //授权后重定向的回调链接地址，请使用urlencode对链接进行处理
        requestPMap.put("redirect_uri", "http://center.pay.sogou.com/notify/ali/pay/webAsync");
        //返回类型，请填写code
        requestPMap.put("response_type", "code");
        //应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息）
        requestPMap.put("scope", "snsapi_base");
        String returnUrl = HttpUtil.packHttpsGetUrl("https://open.weixin.qq.com/connect/oauth2/authorize?", requestPMap) + "#wechat_redirect";
        System.out.print("isConnect" + returnUrl);

    }

    /**
     * 2.微信公众号支付-获取用户openid
     */
    @Test
    public void testWechatGetOpenId() {
        ResultMap result = ResultMap.build();
        //1.根据文档说明，组装md5加密参数
        PMap requestPMap = new PMap();
        //公众号的唯一标识
        requestPMap.put("appid", "wx9a3d0ebef06f62b8");
        //公众号的appsecret
        requestPMap.put("secret", "30d9eec40ffdaa591fe9f0dfe259481f");
        //填写第一步获取的code参数
        requestPMap.put("code", "031c4cd2840ec1ee0445ac77b446034m");
        //填写为authorization_code
        requestPMap.put("grant_type", "authorization_code");
        String returnUrl = HttpUtil.packHttpsGetUrl("https://api.weixin.qq.com/sns/oauth2/access_token?", requestPMap);
        //1.拼装requestUrl
        String access_token = null;
        //2.模拟请求
        WechatHttpClient httpClient = new WechatHttpClient();
        httpClient.setMethod("GET");
        httpClient.setReqContent(returnUrl);
        try {
            if (httpClient.call()) {
                String resContent = httpClient.getResContent();
                PMap pMap = new PMap();
                pMap = JsonUtil.jsonToPMap(resContent, pMap);
                access_token = pMap.getString(WechatPayUtil.ACCESS_TOKEN);
                result.addItem("access_token", access_token);
            }
        } catch (Exception e) {
            result.addItem("isUpdateToken", 0);
        }

    }


    /**
     * 3.微信公众号支付-获取预支付prepayid
     */
    @Test
    public void testWechatSdkPreparePayInfos11() {
        ResultMap result = ResultMap.build();
        //1.组装支付必需参数
        PMap payMap = new PMap();
        // 公众账号ID
        payMap.put("appid", "wx9a3d0ebef06f62b8");
        // 商户号
        payMap.put("mch_id", "1246690601");
        // 随机字符串，不长于32位
        payMap.put("nonce_str", Utils.getNonceStr());
        // 商品描述
        payMap.put("body", "sadsd");
        //订单号
        payMap.put("out_trade_no", "aasdsdfdfgergdfgswfs");
        //支付币种
        payMap.put("fee_type", WechatPayUtil.FEE_TYPE);
        //总金额
        payMap.put("total_fee", "100");
        //买家IP
        payMap.put("spbill_create_ip", "10.10.10.10");
        //异步回调地址
        payMap.put("notify_url", "https://open.weixin.qq.com/connect/oauth2/authorize");
        //交易类型
        payMap.put("trade_type", "JSAPI");
        //用户唯一标识
        payMap.put("openid", "oHw8Nj5dDNEj-rD_hIESKfB3Xnbo");
        //2.MD5签名
        String md5securityKey = "2idEk034kdui3WESr8Ef8wW9edfQw3s2"; //秘钥
        ResultMap sign =
                SecretKeyUtil
                        .tenMd5sign(payMap, md5securityKey, WechatPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            System.out.print("isConnect" + sign);
        }

        payMap.put("sign", sign.getData().get("signValue"));
        String paramsStr = com.sogou.pay.common.utils.XMLUtil.mapToXmlString("xml", payMap);
        //3.模拟请求获取预支付参数
        PMap payPMap = null;
        WechatHttpClient httpClient = new WechatHttpClient();
        httpClient.setReqContent("https://api.mch.weixin.qq.com/pay/unifiedorder");
        httpClient.setTimeOut(8000);//设置超时时间为8s
        String resContent = null;
        try {
            if (httpClient
                    .callHttpPost("https://api.mch.weixin.qq.com/pay/unifiedorder", paramsStr)) {
                resContent = httpClient.getResContent();
                payPMap = XMLParseUtil.doXMLParse(resContent);
            }
            //4.检查返回参数
            if (Utils.isEmpty(payPMap.getString("return_code"), payPMap.getString("result_code"),
                    payPMap.getString("sign"))) {
            }
        } catch (Exception e) {
        }
        //4.签名校验
        boolean
                signMd5 =
                SecretKeyUtil.tenCheckMd5sign(payPMap, md5securityKey, payPMap.getString("sign"),
                        WechatPayUtil.INPUT_CHARSET);
        if (!signMd5) {
        }
        String trade_type = payPMap.getString("trade_type");
        if (!trade_type.equals("JSAPI")) {
        }

    }

}
