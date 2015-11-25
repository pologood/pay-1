package com.sogou.pay.thirdpay.biz.utils.alipay;


import com.sogou.pay.thirdpay.biz.sign.MD5;
import com.sogou.pay.thirdpay.biz.utils.HttpProtocolHandler;
import com.sogou.pay.thirdpay.biz.utils.HttpRequest;
import com.sogou.pay.thirdpay.biz.utils.HttpResponse;
import com.sogou.pay.thirdpay.biz.utils.HttpResultType;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * @Author qibaichao
 * @ClassName AlipaySubmit
 * @Date 2015年2月16日
 * @Description:组装请求参数
 */
public class AlipaySubmit {

    private static final Logger logger = LoggerFactory.getLogger(AlipaySubmit.class);

    private static final boolean DEFAULT_NEED_SIGN_TYPE = true;


    /**
     * 清算请求
     *
     * @param requestParams
     * @param url
     * @param key
     * @param signType
     * @param inputCharset
     * @return
     */
    public static String buildClearRequest(Map<String, String> requestParams,
                                           String url, String key, String signType, String inputCharset) {
        return doRequest(requestParams, url, key, signType, inputCharset,
                DEFAULT_NEED_SIGN_TYPE);
    }


    private static String doRequest(Map<String, String> requestParams,
                                    String url, String key, String signType, String inputCharset,
                                    boolean needSignType) {

        Map<String, String> params = buildRequestPara(requestParams, key,
                signType, inputCharset, needSignType);

        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
                .getInstance();

        HttpRequest request = new HttpRequest(HttpResultType.BYTES);

        // 设置编码集
        request.setCharset(inputCharset);

        request.setParameters(generatNameValuePair(params));

        request.setUrl(url + "?" + "_input_charset=" + inputCharset);

        try {
            HttpResponse response = httpProtocolHandler
                    .execute(request, "", "");
            if (response == null) {
                return null;
            }

            String strResult = response.getStringResult();

            return strResult;
        } catch (Exception e) {
            logger.error(e.toString());
            return "";
        }
    }

    /**
     * MAP类型数组转换成NameValuePair类型
     *
     * @param properties MAP类型数组
     * @return NameValuePair类型数组
     */
    private static NameValuePair[] generatNameValuePair(
            Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(),
                    entry.getValue());
        }

        return nameValuePair;
    }

    /**
     * 生成要请求给支付宝的参数数组
     *
     * @param requestParams the request params
     * @param key           the key
     * @param signType      the sign type
     * @param inputCharset  the input charset
     * @param needSignType  the need sign type
     * @return the map
     */
    private static Map<String, String> buildRequestPara(
            Map<String, String> requestParams, String key, String signType,
            String inputCharset, boolean needSignType) {
        /**
         * 除去数组中的空值和签名参数
         */
        Map<String, String> sPara = com.sogou.pay.thirdpay.biz.utils.alipay.AlipayCore.paraFilter(requestParams);

        /**
         * 生成签名结果
         */
        String sign = buildRequestSign(sPara, key, signType, inputCharset);

        /**
         * 签名结果与签名方式加入请求提交参数组中
         */
        sPara.put("sign", sign);
        if (needSignType) {
            sPara.put("sign_type", signType);
        }

        return sPara;
    }

    /**
     * 生成签名结果
     *
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
    private static String buildRequestSign(Map<String, String> sPara,
                                           String key, String signType, String inputCharset) {
        /**
         * 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
         */
        String prestr = AlipayCore.createLinkString(sPara);

        String sign = "";
        if (signType.equals("MD5")) {
            sign = MD5.sign(prestr, key, inputCharset);
        }
        return sign;
    }

}
