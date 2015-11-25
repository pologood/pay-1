package com.sogou.pay.thirdpay.biz.impl;

import com.sogou.pay.common.cache.CacheConstant;
import com.sogou.pay.common.cache.RedisUtils;
import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.common.utils.XMLParseUtil;
import com.sogou.pay.common.utils.XMLUtil;
import com.sogou.pay.thirdpay.biz.WechatPayService;
import com.sogou.pay.thirdpay.biz.enums.OrderRefundState;
import com.sogou.pay.thirdpay.biz.utils.ClientCustomSSL;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.biz.utils.Sha1Util;
import com.sogou.pay.thirdpay.biz.utils.Utils;
import com.sogou.pay.thirdpay.biz.utils.WechatHttpClient;
import com.sogou.pay.thirdpay.biz.utils.WechatPayUtil;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:18
 */
@Service
public class WechatPayServiceImpl implements WechatPayService {

    private RedisUtils redisUtils;
    private static final Logger log = LoggerFactory.getLogger(
            WechatPayServiceImpl.class);

    /**
     * 1.微信扫码支付
     */
    public ResultMap SweepYardsPreparePayInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装签名参数
        PMap payMap = getParamsInfo("PAY_SY", params);
        //2.MD5签名
        String md5securityKey = params.getString("md5securityKey");
        ResultMap sign =
                SecretKeyUtil
                        .tenMd5sign(payMap, md5securityKey, WechatPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + payMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_WECHAT_SIGN_ERROR);
        }

        payMap.put("sign", sign.getData().get("signValue"));
        String paramsStr = XMLUtil.mapToXmlString("xml", payMap);
        //3.模拟请求获取支付回调参数
        PMap payPMap = null;
        WechatHttpClient httpClient = new WechatHttpClient();
        httpClient.setReqContent(params.getString("payUrl"));
        httpClient.setTimeOut(8000);//设置超时时间为8s
        String resContent = null;
        try {
            if (httpClient
                    .callHttpPost(params.getString("payUrl"), paramsStr)) {
                resContent = httpClient.getResContent();
                payPMap = XMLParseUtil.doXMLParse(resContent);
            }
            //4.检查返回参数
            if (Utils.isEmpty(payPMap.getString("return_code"), payPMap.getString("result_code"),
                    payPMap.getString("sign"))) {
                log.error("返回参数异常 ,返回串："
                        + resContent + "请求参数："
                        + payMap);
                return ResultMap.build(ResultStatus.THIRD_PAY_WEHCHAT_BACK_PARAM_ERROR);

            }
        } catch (Exception e) {
            log.error("返回参数异常 ,返回串："
                    + resContent + "请求参数："
                    + payMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_WEHCHAT_BACK_PARAM_ERROR);
        }

        //5.签名校验
        boolean
                signMd5 =
                SecretKeyUtil.tenCheckMd5sign(payPMap, md5securityKey, payPMap.getString("sign"),
                        WechatPayUtil.INPUT_CHARSET);
        if (!signMd5) {
            log.error("返回参数异常 ,返回串："
                    + resContent + "请求参数："
                    + payMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_WEHCHAT_BACK_PARAM_ERROR);
        }
        String trade_type = payPMap.getString("trade_type");
        if (!trade_type.equals(WechatPayUtil.TRADE_TYPE)) {
            log.error(
                    "返回数据异常，trade_type不相符，返回串"
                            + resContent + "请求参数："
                            + payMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_WEHCHAT_BACK_PARAM_ERROR);
        }
        //6.返回二维码链接
        result.addItem("returnUrl", payPMap.getString("code_url"));//二维码链接
        return result;

    }

    /**
     * 2.微信客户端支付
     */
    public ResultMap ClientPreparePayInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装支付必需参数
        PMap payMap = new PMap();
        payMap.put("appid", params.getString("sellerEmail"));          // 公众账号ID
        payMap.put("mch_id", params.getString("merchantNo"));          // 商户号
        payMap.put("nonce_str", Utils.getNonceStr());                  // 随机字符串，不长于32位
        payMap.put("body", params.getString("subject"));               // 商品描述
        payMap.put("out_trade_no", params.getString("serialNumber"));  //订单号
        payMap.put("fee_type", WechatPayUtil.FEE_TYPE);                //支付币种
        String orderAmount = Utils.fenParseFromYuan(params.getString("orderAmount"));
        payMap.put("total_fee", orderAmount);                          //总金额
        payMap.put("spbill_create_ip", params.getString("buyerIp"));   //买家IP
        payMap.put("notify_url", params.getString("serverNotifyUrl")); //异步回调地址
        payMap.put("trade_type", WechatPayUtil.SDK_TRADE_TYPE);            //交易类型
        //2.MD5签名
        String md5securityKey = params.getString("md5securityKey");
        ResultMap sign =
                SecretKeyUtil
                        .tenMd5sign(payMap, md5securityKey, WechatPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + payMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_WECHAT_SIGN_ERROR);
        }

        payMap.put("sign", sign.getData().get("signValue"));
        String paramsStr = XMLUtil.mapToXmlString("xml", payMap);
        //3.模拟请求获取预支付参数
        PMap payPMap = null;
        WechatHttpClient httpClient = new WechatHttpClient();
        httpClient.setReqContent(params.getString("prepayUrl"));
        httpClient.setTimeOut(8000);//设置超时时间为8s
        String resContent = null;
        try {
            if (httpClient
                    .callHttpPost(params.getString("prepayUrl"), paramsStr)) {
                resContent = httpClient.getResContent();
                payPMap = XMLParseUtil.doXMLParse(resContent);
            }
            //4.检查返回参数
            if (Utils.isEmpty(payPMap.getString("return_code"), payPMap.getString("result_code"),
                    payPMap.getString("sign"))) {
                log.error("返回参数异常 ,返回串："
                        + resContent + "请求参数："
                        + payMap);
                return ResultMap.build(ResultStatus.THIRD_PAY_WEHCHAT_BACK_PARAM_ERROR);
            }
        } catch (Exception e) {
            log.error("返回参数异常 ,返回串："
                    + resContent + "请求参数："
                    + payMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_WEHCHAT_BACK_PARAM_ERROR);
        }
        //4.签名校验
        boolean
                signMd5 =
                SecretKeyUtil.tenCheckMd5sign(payPMap, md5securityKey, payPMap.getString("sign"),
                        WechatPayUtil.INPUT_CHARSET);
        if (!signMd5) {
            log.error("返回参数异常 ,返回串："
                    + resContent + "请求参数："
                    + payMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_WEHCHAT_BACK_PARAM_ERROR);
        }
        String trade_type = payPMap.getString("trade_type");
        if (!trade_type.equals(WechatPayUtil.SDK_TRADE_TYPE)) {
            log.error(
                    "返回数据异常，trade_type不相符，返回串"
                            + resContent + "请求参数："
                            + payMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_WEHCHAT_BACK_PARAM_ERROR);
        }
        //5.组装发往商户参数
        PMap signMap = new PMap();
        signMap.put("appid", params.getString("sellerEmail"));
        signMap.put("partnerid", params.getString("merchantNo"));
        signMap.put("prepayid", payPMap.get("prepay_id"));
        signMap.put("package", "Sign=WXPay");
        signMap.put("noncestr", Utils.getNonceStr());
        signMap.put("timestamp", Utils.getTimeStamp());
        ResultMap signMapSign =
                SecretKeyUtil
                        .tenMd5sign(signMap, md5securityKey, WechatPayUtil.INPUT_CHARSET);
        if (signMapSign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + payMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_WECHAT_SIGN_ERROR);
        }
        signMap.put("sign", signMapSign.getData().get("signValue"));
        result.addItem("returnData", signMap);
        return result;
    }

    /**
     * 组装微信第三方支付所需参数
     */
    private ResultMap getTokenInfo(PMap wechatPayMap) {
        ResultMap result = ResultMap.build();
        //1.从缓存获取和验证token
        String
                access_token =
                redisUtils.get(CacheConstant.CACHE_PREFIX_SDK_WECHAT_ACCESSTOKEN);
        //2.缓存token为空，请求微信获取token
        if ("".equals(access_token) || access_token == null) {
            result = getTokenReal(wechatPayMap);
            if (result.getStatus() == ResultStatus.SUCCESS) {
                Object
                        isUpdateToken =
                        result.getData().get("isUpdateToken");
                if (isUpdateToken.equals(1)) {
                    access_token =
                            result.getData().get("access_token").toString();
                    //保存修改后的token值在缓存或者数据库
                    redisUtils
                            .set(CacheConstant.CACHE_PREFIX_SDK_WECHAT_ACCESSTOKEN,
                                    access_token);
                    redisUtils.expire(
                            CacheConstant.CACHE_PREFIX_SDK_WECHAT_ACCESSTOKEN,
                            CacheConstant.SDK_WECHAT_ACCESSTOKEN_EXPIRE);
                }
            } else {
                result.withError(ResultStatus.THIRD_GETACCESSTOKEN_ERROR);
                log.error("获取Token失败"
                        + wechatPayMap);
                return result;
            }
        }
        result.addItem("access_token", access_token);
        return result;
    }

    /**
     * 获取access_token
     */
    private static ResultMap getTokenReal(PMap parameter) {
        ResultMap result = ResultMap.build();
        //1.拼装requestUrl
        String
                requestUrl =
                parameter.get("prepayURL") + "?grant_type="
                        + WechatPayUtil.GRANT_TYPE + "&appid="
                        + parameter.get("appID") + "&secret=" + parameter.get("appSecret");
        String access_token = null;
        //2.模拟请求
        WechatHttpClient httpClient = new WechatHttpClient();
        httpClient.setMethod("GET");
        httpClient.setReqContent(requestUrl);
        try {
            if (httpClient.call()) {
                String resContent = httpClient.getResContent();
                if (resContent.indexOf(WechatPayUtil.ACCESS_TOKEN) > 0) {
                    PMap pMap = new PMap();
                    pMap = JsonUtil.jsonToPMap(resContent, pMap);
                    access_token = pMap.getString(WechatPayUtil.ACCESS_TOKEN);
                    result.addItem("access_token", access_token);
                    result.addItem("isUpdateToken", 1);
                } else {
                    result.addItem("isUpdateToken", 0);
                    log.error("微信WechatPayApp Token请求http请求失败，出参"
                            + resContent);
                }
            }
        } catch (Exception e) {
            result.addItem("isUpdateToken", 0);
            log.error("微信WechatPayApp Token预支付请求http请求异常"
                    + result);
        }
        return result;
    }

    /**
     * 3.微信查询订单信息
     */
    @Override
    public ResultMap queryOrderInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap queryParams = new PMap();
        //1拼装请求参数
        queryParams = getParamsInfo("QUERY", params);
        //2.获得密钥，MD5签名
        String md5securityKey = params.getString("md5securityKey");
        ResultMap sign =
                SecretKeyUtil
                        .tenMd5sign(queryParams, md5securityKey, WechatPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("微信查询订单信息md5签名异常，参数：" + queryParams);
            return ResultMap.build(ResultStatus.THIRD_QUERY_WECHAT_SIGN_ERROR);
        }
        queryParams.put("sign", sign.getData().get("signValue"));
        String paramsStr = XMLUtil.mapToXmlString("xml", queryParams);
        //3.模拟请求，获取查询参数
        PMap orderPMap = null;
        WechatHttpClient httpClient = new WechatHttpClient();
        httpClient.setReqContent(params.getString("queryUrl"));
        httpClient.setTimeOut(8000);//设置超时时间为8s
        String resContent = null;
        try {
            if (httpClient
                    .callHttpPost(params.getString("queryUrl"), paramsStr)) {
                resContent = httpClient.getResContent();
                orderPMap = XMLParseUtil.doXMLParse(resContent);
            }
        } catch (JDOMException e) {
            log.error("微信查询订单信息请求异常，XML解析失败，参数：" + queryParams + "返回串：" + resContent);
            e.printStackTrace();
            return
                    ResultMap.build(ResultStatus.THIRD_QUERY_WECHAT_XMLTOMAP_ERROR);
        } catch (IOException e) {
            log.error("微信查询订单信息请求异常，XML解析失败，参数：" + queryParams + "返回串：" + resContent);
            e.printStackTrace();
            return ResultMap.build(ResultStatus.THIRD_QUERY_WECHAT_XMLTOMAP_ERROR);
        }
        //4.检查返回参数
        if (Utils.isEmpty(orderPMap.getString("return_code"), orderPMap.getString("result_code"),
                orderPMap.getString("sign"), orderPMap.getString("trade_state"))) {
            log.error("微信查询订单信息返回参数异常，参数：" + queryParams + "返回串：" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_WECHAT_PAY_INFO_ERROR);

        }
        //5.签名校验
        boolean
                signMd5 =
                SecretKeyUtil.tenCheckMd5sign(orderPMap, md5securityKey, orderPMap.getString("sign"),
                        WechatPayUtil.INPUT_CHARSET);
        if (!signMd5) {
            log.error("微信查询订单信息返回参数异常，参数：" + queryParams + "返回串：" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_WECHAT_BACK_SIGN_ERROR);
        }
        //6.返回交易状态
        result.addItem("order_state", orderPMap.getString("trade_state").toUpperCase());
        return result;
    }

    /**
     * 4.微信订单退款
     */
    @Override
    public ResultMap refundOrderInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装退款签名参数
        PMap requestData = getParamsInfo("REFUND", params);
        //2.获得加密密钥，进行参数加密
        String md5securityKey = params.getString("md5securityKey");        // 加密秘钥
        ResultMap sign =
                SecretKeyUtil.tenMd5sign(requestData, md5securityKey, WechatPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            return sign;
        }
        requestData.put("sign", sign.getData().get("signValue")); // 密钥
        String paramsStr = XMLUtil.mapToXmlString("xml", requestData);
        //3.模拟请求获取退款回调参数
        String responseString = null;
        try {
            responseString = ClientCustomSSL.doRefund(params.getString("refundUrl"),
                    params.getString("privateCertFilePath"),
                    params.getString("merchantNo"), paramsStr);
        } catch (Exception e) {
            log.error("微信订单退款请求HTTP异常，参数：" + requestData);
            e.printStackTrace();
            return ResultMap.build(ResultStatus.THIRD_REFUND_WECHAT_XMLTOMAP_ERROR);
        }
        //4.调用财付通提供的解析xml方法转换参数
        PMap responseMap = new PMap();
        try {
            responseMap = XMLParseUtil.doXMLParse(responseString);
        } catch (JDOMException e) {
            log.error("微信订单退款请求异常，XML解析失败，参数：" + requestData + "返回串：" + responseString);
            e.printStackTrace();
            return ResultMap.build(ResultStatus.THIRD_REFUND_WECHAT_XMLTOMAP_ERROR);
        } catch (IOException e) {
            log.error("微信订单退款请求异常，XML解析失败，参数：" + requestData + "返回串：" + responseString);
            return ResultMap.build(ResultStatus.THIRD_REFUND_WECHAT_XMLTOMAP_ERROR);
        }
        //5.检查返回参数
        if (responseMap == null) {
            log.error("微信订单退款请求返回参数异常，参数:" + params + "返回串：" + responseString);
            return ResultMap.build(ResultStatus.THIRD_REFUND_WECHAT_BACK_PARAM_ERROR);
        }
        String return_code = responseMap.getString("return_code");
        if (Utils.isEmpty(return_code) || !"SUCCESS".equals(return_code)) {
            log.error("微信订单退款请求返回参数异常，return_code!=SUCCESS，参数:" + params + "返回串" + responseString);
            result.addItem("error_code", return_code);
            result.addItem("error_msg", responseMap.getString("return_msg"));
            result.withError(ResultStatus.THIRD_REFUND_WECHAT_BACK_PARAM_ERROR);
            return result;
        }
        //6.签名校验
        boolean
                signMd5 =
                SecretKeyUtil
                        .tenCheckMd5sign(responseMap, md5securityKey, responseMap.getString("sign"),
                                WechatPayUtil.INPUT_CHARSET);
        if (!signMd5) {
            log.error("微信订单退款请求返回参数异常，返回数据验证签名错误，参数：" + requestData + "返回串：" + responseString);
            return ResultMap.build(ResultStatus.THIRD_REFUND_WECHAT_BACK_SIGN_ERROR);
        }
        String result_code = responseMap.getString("result_code");
        if (Utils.isEmpty(result_code) || !"SUCCESS".equals(result_code)) {
            log.error("微信订单退款请求返回参数异常，return_code!=SUCCESS，参数:" + params + "返回串" + responseString);
            result.addItem("error_code", responseMap.getString("err_code"));
            result.addItem("error_msg", responseMap.getString("err_code_des"));
            result.withError(ResultStatus.THIRD_QUERY_WECHAT_PAY_INFO_ERROR);
            return result;
        }
        return result.addItem("third_refund_id", responseMap.getString("refund_id"));
    }

    /**
     * 3.微信查询订单退款信息
     */
    @Override
    public ResultMap queryRefundInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap queryParams = new PMap();
        //1拼装请求参数
        queryParams = getParamsInfo("QUERY_REFUND", params);
        //2.获得密钥，MD5签名
        String md5securityKey = params.getString("md5securityKey");
        ResultMap sign =
                SecretKeyUtil
                        .tenMd5sign(queryParams, md5securityKey, WechatPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("微信查询订单退款md5签名异常，参数：" + queryParams);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_WECHAT_SIGN_ERROR);
        }
        queryParams.put("sign", sign.getData().get("signValue"));
        String paramsStr = XMLUtil.mapToXmlString("xml", queryParams);
        //3.模拟请求，获取查询参数
        PMap orderPMap = null;
        WechatHttpClient httpClient = new WechatHttpClient();
        httpClient.setReqContent(params.getString("queryRefundUrl"));
        httpClient.setTimeOut(8000);//设置超时时间为8s
        String resContent = null;
        try {
            if (httpClient
                    .callHttpPost(params.getString("queryRefundUrl"), paramsStr)) {
                resContent = httpClient.getResContent();
                orderPMap = XMLParseUtil.doXMLParse(resContent);
            }
        } catch (JDOMException e) {
            log.error("微信查询订单退款请求异常，XML解析失败，参数：" + queryParams + "返回串：" + resContent);
            e.printStackTrace();
            return ResultMap.build(ResultStatus.THIRD_Q_RF_WECHAT_XMLTOMAP_ERROR);
        } catch (IOException e) {
            log.error("微信查询订单退款请求异常，XML解析失败，参数：" + queryParams + "返回串：" + resContent);
            e.printStackTrace();
            return ResultMap.build(ResultStatus.THIRD_Q_RF_WECHAT_XMLTOMAP_ERROR);
        }
        //4.检查返回参数
        if (Utils.isEmpty(orderPMap.getString("result_code"), orderPMap.getString("sign"))) {
            log.error("微信查询订单退款返回参数异常，参数：" + queryParams + "返回串：" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_WECHAT_PAY_INFO_ERROR);

        }
        //5.签名校验
        boolean
                signMd5 =
                SecretKeyUtil.tenCheckMd5sign(orderPMap, md5securityKey, orderPMap.getString("sign"),
                        WechatPayUtil.INPUT_CHARSET);
        if (!signMd5) {
            log.error("微信查询订单退款返回参数异常，参数：" + queryParams + "返回串：" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_WECHAT_BACK_SIGN_ERROR);
        }

        //6.返回交易状态
        String result_code = orderPMap.getString("result_code");
        if ("SUCCESS".equals(result_code)) {
            result.addItem("refund_status", OrderRefundState.SUCCESS);
        } else if ("FAIL".equals(result_code)) {
            result.addItem("refund_status", OrderRefundState.FAIL);
        } else {
            result.addItem("refund_status", OrderRefundState.UNKNOWN);
        }
        return result;
    }

    /**
     * 2.1.组装微信第三方支付所需参数
     */
    private PMap getParamsInfo(String business, PMap params) {
        PMap pMap = new PMap();
        //1.组装共同参数
        pMap.put("appid", params.getString("sellerEmail"));          // 公众账号ID
        pMap.put("mch_id", params.getString("merchantNo"));          // 商户号
        pMap.put("nonce_str", Utils.getNonceStr());                  // 随机字符串，不长于32位
        //2.根据业务组装签名参数
        switch (business) {
            //1.1微信扫码支付
            case "PAY_SY":
                pMap.put("body", params.getString("subject"));               // 商品描述
                pMap.put("out_trade_no", params.getString("serialNumber"));  //订单号
                pMap.put("fee_type", WechatPayUtil.FEE_TYPE);                //支付币种
                String orderAmount = Utils.fenParseFromYuan(params.getString("orderAmount"));
                pMap.put("total_fee", orderAmount);                          //总金额
                pMap.put("spbill_create_ip","127.0.0.1");   //买家IP
//                pMap.put("spbill_create_ip", params.getString("buyerIp"));   //买家IP
                pMap.put("notify_url", params.getString("serverNotifyUrl")); //异步回调地址
                pMap.put("trade_type", WechatPayUtil.TRADE_TYPE);            //交易类型
                break;
            //3.微信查询订单
            case "QUERY":
                pMap.put("out_trade_no", params.getString("serialNumber"));  //商户订单号
                break;
            //4.微信退款
            case "REFUND":
                pMap.put("transaction_id", params.getString("agencySerialNumber")); //微信订单号
                pMap.put("out_trade_no", params.getString("serialNumber"));  //订单号
                pMap.put("out_refund_no", params.getString("refundSerialNumber"));  //商户退款号
                String total_fee = Utils.fenParseFromYuan(params.getString("totalAmount"));
                pMap.put("total_fee", total_fee);                          //总金额
                String refundAmount = Utils.fenParseFromYuan(params.getString("refundAmount"));
                pMap.put("refund_fee", refundAmount);                          //退款金额
                pMap.put("refund_fee_type", WechatPayUtil.FEE_TYPE);   //货币种类
                pMap.put("op_user_id", params.getString("merchantNo"));   //操作员
                break;
            case "QUERY_REFUND":
                pMap.put("out_refund_no", params.getString("out_refund_no"));  //商户退款号
                break;
        }
        return pMap;
    }


    /**
     * 2.4.创建签名SHA1
     * <p/>
     * signParams
     */
    private static String createSHA1Sign(Map contextMap) {
        StringBuffer sb = new StringBuffer();
        Set es = contextMap.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(k + "=" + v + "&");
        }
        String params = sb.substring(0, sb.lastIndexOf("&"));
        String appsign = Sha1Util.getSha1(params);
        return appsign;
    }

    /**
     * 构建请求参数
     */
    private static String buildRequestParam(Map<String, String> contextMap) {
        List<String> keys = new ArrayList<String>(contextMap.keySet());
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer("{");
        Set es = contextMap.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"appkey".equals(k)) {
                sb.append("\"" + k + "\":\"" + v + "\",");
            }
        }
        String params = sb.substring(0, sb.lastIndexOf(","));
        params += "}";
        return params;
    }

    /**
     * 获取prepayId
     */
    private static ResultMap sendPrepay(String access_token, PMap parameter) {
        ResultMap result = ResultMap.build();
        Map prepayHandler = new TreeMap();
        prepayHandler.put("appid", parameter.get("appID"));
        prepayHandler.put("appkey", parameter.get("appKey"));
        prepayHandler.put("noncestr", Utils.getNonceStr());
        prepayHandler.put("package", parameter.get("package"));
        prepayHandler.put("timestamp", Utils.getTimeStamp());
        prepayHandler.put("traceid", "");
        // 生成支付签名
        String sign = createSHA1Sign(prepayHandler);
        prepayHandler.put("app_signature", sign);
        prepayHandler.put("sign_method", WechatPayUtil.SIGN_METHOD);
        String params = buildRequestParam(prepayHandler);
        WechatHttpClient httpClient = new WechatHttpClient();
        httpClient.setReqContent(parameter.get("payUrl") + access_token);
        String resContent = null;
        String prepayid = null;
        try {
            if (httpClient
                    .callHttpPost(parameter.get("payURL") + access_token, params)) {
                log.debug(
                        "sendPrepay:微信Token预支付请求http请求，入参"
                                + params);
                resContent = httpClient.getResContent();
                if (resContent.indexOf("prepayid") > 0) {
                    //获取对应的errcode的值
                    PMap pMap = new PMap();
                    pMap = JsonUtil.jsonToPMap(resContent, pMap);
                    prepayid = pMap.getString("prepayid");
                    result.addItem("prepayID", prepayid);
                } else {
                    result.addItem("prepayID", prepayid);
                    log.error(
                            "微信Token预支付请求http请求失败，出参" + resContent);
                }
            }

        } catch (Exception e) {
            result.addItem("prepayID", prepayid);
            log.error("微信Token预支付请求http请求异常" + result);
        }
        return result;
    }

}
