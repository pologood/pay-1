package com.sogou.pay.thirdpay.biz.impl;


import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.MD5Util;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.common.utils.XMLParseUtil;
import com.sogou.pay.common.utils.XMLUtil;
import com.sogou.pay.thirdpay.biz.TenPayService;
import com.sogou.pay.thirdpay.biz.enums.OrderRefundState;
import com.sogou.pay.thirdpay.biz.enums.OrderState;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.biz.utils.TenPayHttpClient;
import com.sogou.pay.thirdpay.biz.utils.TenPayUtil;
import com.sogou.pay.thirdpay.biz.utils.Utils;
import com.sogou.pay.thirdpay.biz.utils.WechatPayUtil;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:18
 */
@Service
public class TenPayServiceImpl implements TenPayService {

    private static final Logger log = LoggerFactory.getLogger(
        TenPayServiceImpl.class);

    /**
     * 1.财付通账户支付
     */
    @Override
    public ResultMap AccountPreparePayInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap requestPMap = new PMap();
        //1.获取通用固定参数
        requestPMap.put("fee_type", TenPayUtil.FEE_TYPE);                    // 币种：1-人民币
        requestPMap.put("input_charset", TenPayUtil.INPUT_CHARSET);          //编码格式
        requestPMap.put("sign_type", TenPayUtil.WEB_SIGN_TYPE);              //加密方法
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));  //异步回调地址
        requestPMap.put("return_url", params.getString("pageNotifyUrl"));    //页面回调地址
        requestPMap.put("partner", params.getString("merchantNo"));          //商户号
        //2.获取财付通账户支付银行类型值
        requestPMap
            .put("bank_type", TenPayUtil.ACCOUNT_BANK_TYPE); //经暂时调研，财付通账户支付银行类型字段为固定值：DEFAULT
        //3.组装其他可变参数
        requestPMap.put("spbill_create_ip", params.getString("buyerIp"));    // 买家浏览器IP
        String orderAmount = Utils.fenParseFromYuan(params.getString("orderAmount"));
        requestPMap.put("total_fee", orderAmount);                           // 订单总金额，以分为单位，整数
        requestPMap.put("out_trade_no", params.getString("serialNumber"));   //订单id
        requestPMap.put("body", params.getString("subject"));                //商品描述
        //5.获取md5签名
        String md5securityKey = params.getString("md5securityKey");
        ResultMap
            sign = SecretKeyUtil.tenMd5sign(requestPMap, md5securityKey, TenPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_TEN_SIGN_ERROR);
        }
        requestPMap.put(TenPayUtil.SIGN_NAME, sign.getData().get("signValue"));
        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;

    }


    /**
     * 2.财付通网关支付
     */
    @Override
    public ResultMap GatwayYPreparePayInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap requestPMap = new PMap();
        //1.获取通用固定参数
        requestPMap.put("fee_type", TenPayUtil.FEE_TYPE);                    // 币种：1-人民币
        requestPMap.put("input_charset", TenPayUtil.INPUT_CHARSET);          //编码格式
        requestPMap.put("sign_type", TenPayUtil.WEB_SIGN_TYPE);              //加密方法
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));  //异步回调地址
        requestPMap.put("return_url", params.getString("pageNotifyUrl"));    //页面回调地址
        requestPMap.put("partner", params.getString("merchantNo"));          //商户号
        //2.获取财付通网关支付银行类型值
        requestPMap.put("bank_type", params.get("bankCode"));                //财付通网关支付银行类型值
        //3.组装其他可变参数
        requestPMap.put("spbill_create_ip", params.getString("buyerIp"));    // 买家浏览器IP
        String orderAmount = Utils.fenParseFromYuan(params.getString("orderAmount"));
        requestPMap.put("total_fee", orderAmount);                           // 订单总金额，以分为单位，整数
        requestPMap.put("out_trade_no", params.getString("serialNumber"));   //订单id
        requestPMap.put("body", params.getString("subject"));                //商品描述
        //5.获取md5签名
        String md5securityKey = params.getString("md5securityKey");
        ResultMap
            sign = SecretKeyUtil.tenMd5sign(requestPMap, md5securityKey, TenPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_TEN_SIGN_ERROR);
        }
        requestPMap.put(TenPayUtil.SIGN_NAME, sign.getData().get("signValue"));
        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;

    }


    /**
     * 3.财付通客户端支付
     */
    @Override
    public ResultMap ClientPreparePayInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装签名参数
        PMap newParams = new PMap();
        newParams.put("ver", TenPayUtil.VER);                           //接口版本
        newParams.put("sale_plat", TenPayUtil.SALE_PLAT);               //请求来源
        newParams.put("charset", TenPayUtil.WAP_CHARSET);               //编码
        newParams.put("bank_type", TenPayUtil.BANK_TYPE);               //银行类型
        newParams.put("desc", params.getString("subject"));             //商品描述
        newParams.put("bargainor_id", params.getString("merchantNo"));
        newParams.put("sp_billno", params.getString("serialNumber"));
        String orderAmount = Utils.fenParseFromYuan(params.getString("orderAmount"));
        newParams.put("total_fee", orderAmount);                        // 订单总金额，以分为单位，整数
        newParams.put("fee_type", TenPayUtil.FEE_TYPE);                 //币种
        newParams.put("notify_url", params.getString("serverNotifyUrl"));
        newParams.put("attach", params.getString("subject"));
        //2.获取md5签名
        ResultMap sign = SecretKeyUtil
            .tenMd5sign(newParams, params.getString("md5securityKey"), TenPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + newParams);
            return ResultMap.build(ResultStatus.THIRD_PAY_TEN_SIGN_ERROR);
        }
        newParams.put("sign", sign.getData().get("ChkValue"));
        //3.组装访问url，并且获取预支付ID
        String requestUrl = HttpUtil.packHttpGetUrl(params.getString("prepayUrl"), newParams);
        TenPayHttpClient httpClient = new TenPayHttpClient();
        httpClient.setMethod("GET");
        httpClient.setReqContent(requestUrl);
        httpClient.setTimeOut(8000);//设置超时时间为8s
        Object token_id = null;
        try {
            if (httpClient.call()) {
                String resContent = httpClient.getResContent();
                if (resContent.indexOf("token_id") > 0) {
                    Map map = XMLUtil.xmlToBean(resContent, Map.class);
                    token_id = map.get("token_id");
                    result.addItem("token_id", token_id);
                } else {
                    log.error("获取预支付id请求http请求失败，出参"
                              + resContent);
                    return ResultMap.build(ResultStatus.THIRD_PAY_GET_TEN_TOKENID_ERROR);
                }
            }
        } catch (Exception e) {
            log.error("获取预支付id请求http请求异常，参数"
                      + newParams);
            return ResultMap.build(ResultStatus.THIRD_PAY_GET_TEN_TOKENID_ERROR);
        }
        return result;


    }

    /**
     * 4.财付通查询订单信息
     *
     * 只能查询半年内的订单，超过半年的订单调用此查询接口会报“88221009交易单不存在”
     */
    @Override
    public ResultMap queryOrderInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装查询参数
        PMap queryOrder = new PMap();
        queryOrder.put("input_charset", TenPayUtil.INPUT_CHARSET);
        queryOrder.put("partner", params.getString("merchantNo"));
        queryOrder.put("out_trade_no", params.getString("serialNumber"));
        //2.获取md5签名
        ResultMap sign = SecretKeyUtil
            .tenMd5sign(queryOrder, params.getString("md5securityKey"), TenPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数Params" + queryOrder);
            return ResultMap.build(ResultStatus.THIRD_QUERY_TEN_SIGN_ERROR);
        }
        queryOrder.put("sign", sign.getData().get("signValue"));
        //3.组装访问url
        String requestUrl = HttpUtil.packHttpGetUrl(params.getString("queryUrl"), queryOrder);
        TenPayHttpClient httpClient = new TenPayHttpClient();
        httpClient.setMethod("GET");
        httpClient.setReqContent(requestUrl);
        httpClient.setTimeOut(8000);//设置超时时间为8s
        PMap queryPmap = new PMap();
        String resContent = null;
        try {
            if (httpClient.call()) {
                resContent = httpClient.getResContent();
                if (resContent.indexOf("retcode") > 0) {
                    //调用财付通提供的解析xml方法
                    queryPmap = XMLParseUtil.doXMLParse(resContent);
                } else {
                    log.error("财付通查询订单信息返回参数异常retcode<=0，请求参数：" + queryOrder + "返回串：" + resContent);
                    return ResultMap.build(ResultStatus.THIRD_QUERY_TEN_PAY_INFO_ERROR);
                }
            }
        } catch (Exception e) {
            log.error("财付通查询订单信息XML解析异常，请求参数：" + queryOrder + "返回串：" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_TEN_XMLTOMAP_ERROR);
        }
        //5.签名校验
        boolean
            signMd5 =
            SecretKeyUtil
                .tenCheckMd5sign(queryPmap, params.getString("md5securityKey"),
                                 queryPmap.getString("sign"), WechatPayUtil.INPUT_CHARSET);
        if (!signMd5) {
            log.error("财付通订单查询返回数据验证签名错误，参数：" + params + "返回串：" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_ALI_BACK_SIGN_ERROR);
        }
        String retcode = queryPmap.getString("retcode");
        if (Utils.isEmpty(retcode) || !retcode.equals("0")) {
            log.error("财付通订单查询请求返回参数异常，retcode!=0，参数:" + params + "返回串" + resContent);
            return ResultMap.build(ResultStatus.THIRD_QUERY_TEN_PAY_INFO_ERROR);
        }
        String trade_state = queryPmap.getString("trade_state");
        if (Utils.isEmpty(trade_state) || !trade_state.equals("0")) {
            log.error("财付通订单查询请求返回参数异常，retcode!=0，参数:" + params + "返回串" + resContent);
            result.addItem("order_state", OrderState.USERPAYING.name());
        } else if (trade_state.equals("0")) {
            result.addItem("order_state", OrderState.SUCCESS.name());
        }
        return result;
    }

    /**
     * 5.财付通订单退款
     *
     * 只能退半年内的订单，超过半年的订单调用此退款接口会报“88221009交易单不存在”
     */
    @Override
    public ResultMap refundOrderInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        PMap requestData = new PMap();
        //1.组装参数开始
        requestData.put("input_charset", TenPayUtil.INPUT_CHARSET);         //编码
        requestData.put("partner", params.getString("merchantNo"));         // 商户号
        requestData.put("service_version", "1.1");
        requestData.put("out_trade_no", params.getString("serialNumber"));  // 商户订单号
        requestData.put("out_refund_no",
                        params.getString("refundSerialNumber"));            // 商户退款单号
        String totalAmount = Utils.fenParseFromYuan(params.getString("totalAmount"));
        requestData.put("total_fee", totalAmount);                          // 总金额,调用端添加
        String refundAmount = Utils.fenParseFromYuan(params.getString("refundAmount"));
        requestData.put("refund_fee", refundAmount);                        // 退款金额
        requestData.put("op_user_id", params.getString("merchantNo"));               // 商户账号
        String md5securityKey = params.getString("md5securityKey");         // 加密秘钥
        boolean iswl = false;
        String opUserPasswd;//操作员登录密码
        String certPasswd;//证书导入密码
        if(params.getString("merchantNo").equals(TenPayUtil.WL_OP_USER_ID)){
            opUserPasswd = TenPayUtil.WL_OP_USER_PASSWD;
            certPasswd = TenPayUtil.WL_CERT_PASSWD;
        }else {
            opUserPasswd = TenPayUtil.KJ_OP_USER_PASSWD;
            certPasswd = TenPayUtil.KJ_CERT_PASSWD;
        }
        //1.1操作员密码md5加密
        String signString = null;
        try {
            signString =
                MD5Util.MD5Encode(opUserPasswd, TenPayUtil.INPUT_CHARSET)
                    .toUpperCase();
        } catch (Exception e) {
            log.error("md5签名异常，参数：" + requestData);
            return ResultMap.build(ResultStatus.THIRD_REFUND_TEN_SIGN_ERROR);
        }
        requestData.put("op_user_passwd", signString); // 商户账号密码密钥md5加密之后
        requestData.put("notify_url", params.getString("refundNotifyUrl"));         //异步回调地址

        //2.获得加密密钥，进行参数加密
        ResultMap sign =
            SecretKeyUtil.tenMd5sign(requestData, md5securityKey, TenPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数：" + requestData);
            return ResultMap.build(ResultStatus.THIRD_REFUND_TEN_SIGN_ERROR);
        }
        requestData.put("sign", sign.getData().get("signValue")); // 密钥
        // 3.获取支付机构请求报文处理配置
        ResultMap httpResponse = TenPayHttpClient.buildRequest(
            params.getString("refundUrl"), requestData, "POST", TenPayUtil.INPUT_CHARSET,
            TenPayUtil.TIME_OUT, params.getString("publicCertFilePath"),
            params.getString("privateCertFilePath"),
            params.getString("merchantNo"),certPasswd);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("财付通订单退款HTTP请求异常，参数：" + requestData);
            return ResultMap.build(ResultStatus.THIRD_REFUND_TEN_HTTP_ERROR);
        }
        String responseString = httpResponse.getData().get("responseData").toString();
        PMap responseMap = new PMap();
        //4.调用财付通提供的解析xml方法
        try {
            //调用财付通提供的解析xml方法
            responseMap = XMLParseUtil.doXMLParse(responseString);
        } catch (JDOMException e) {
            e.printStackTrace();
            log.error("财付通订单退款返回参数异常，请求参数：" + requestData + "返回串：" + responseString);
            return ResultMap.build(ResultStatus.THIRD_REFUND_TEN_BACK_PARAM_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("财付通订单退款返回参数异常，请求参数：" + requestData + "返回串：" + responseString);
            return ResultMap.build(ResultStatus.THIRD_REFUND_TEN_BACK_PARAM_ERROR);
        }
        //5.检查返回参数
        if (responseMap == null) {
            log.error("财付通订单退款请求返回参数异常，参数:" + params + "返回串：" + responseString);
            return ResultMap.build(ResultStatus.THIRD_REFUND_TEN_BACK_PARAM_ERROR);
        }
        //6.签名校验
        boolean
            signMd5 =
            SecretKeyUtil
                .tenCheckMd5sign(responseMap, md5securityKey, responseMap.getString("sign"),
                                 WechatPayUtil.INPUT_CHARSET);
        if (!signMd5) {
            log.error("财付通退款返回数据验证签名错误，参数：" + requestData + "返回串："
                      + responseString);
            return ResultMap.build(ResultStatus.THIRD_REFUND_TEN_BACK_SIGN_ERROR);
        }
        String retcode = responseMap.getString("retcode");
        if (Utils.isEmpty(retcode) || !"0".equals(retcode)) {
            log.error(
                "财付通退款返回数据错误retcode！=0，请求参数：" + requestData + "返回串：" + responseString);
            return ResultMap.build(ResultStatus.THIRD_REFUND_TEN_BACK_PARAM_ERROR);
        }
        String refund_status = responseMap.getString("refund_status");
        //退款状态：
        //4，10：退款成功。
        //3，5，6：退款失败。
        //8，9，11：退款处理中。
        //1，2：未确定，需要商户原退款单号重新发起。
        //7：转入代发，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，资金回流到商户的现金帐号，需要商户人工干预，通过线下或者财付通转账的方式进行退款。
        //只有退款失败才返回错误码，其他状态返回退款成功
        if (Utils.isEmpty(refund_status) || "3".equals(refund_status) || "5"
            .equals(refund_status) || "6".equals(refund_status)) {
            result.addItem("error_code", OrderRefundState.FAIL);
            result.addItem("error_msg", refund_status);
            result.withError(ResultStatus.THIRD_REFUND_TEN_BACK_PARAM_ERROR);
        }
        return result;
    }

    /**
     * 6.财付通查询订单退款信息
     *
     * 只能查询半年内的订单，超过半年的订单调用此查询接口会报“88221009交易单不存在”
     */
    @Override
    public ResultMap queryRefundInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装查询参数
        PMap queryOrder = new PMap();
        queryOrder.put("input_charset", TenPayUtil.INPUT_CHARSET);
        queryOrder.put("partner", params.getString("merchantNo"));
        queryOrder.put("out_refund_no", params.getString("out_refund_no"));
        queryOrder.put("service_version", "1.1");
        //2.获取md5签名
        ResultMap sign = SecretKeyUtil
            .tenMd5sign(queryOrder, params.getString("md5securityKey"), TenPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("财付通查询订单退款md5签名异常，参数Params" + queryOrder);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_TEN_SIGN_ERROR);
        }
        queryOrder.put("sign", sign.getData().get("signValue"));
        //3.组装访问url
        String requestUrl = HttpUtil.packHttpGetUrl(params.getString("queryRefundUrl"), queryOrder);
        TenPayHttpClient httpClient = new TenPayHttpClient();
        httpClient.setMethod("GET");
        httpClient.setReqContent(requestUrl);
        httpClient.setTimeOut(8000);//设置超时时间为8s
        PMap queryPmap = new PMap();
        String resContent = null;
        try {
            if (httpClient.call()) {
                resContent = httpClient.getResContent();
                if (resContent.indexOf("retcode") > 0) {
                    //调用财付通提供的解析xml方法
                    queryPmap = XMLParseUtil.doXMLParse(resContent);
                } else {
                    log.error("财付通查询订单退款返回参数异常retcode<=0，请求参数：" + queryOrder + "返回串：" + resContent);
                    return ResultMap.build(ResultStatus.THIRD_Q_RF_TEN_PAY_INFO_ERROR);
                }
            }
        } catch (Exception e) {
            log.error("财付通查询订单退款XML解析异常，请求参数：" + queryOrder + "返回串：" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_TEN_XMLTOMAP_ERROR);
        }
        //5.签名校验
        boolean
            signMd5 =
            SecretKeyUtil
                .tenCheckMd5sign(queryPmap, params.getString("md5securityKey"),
                                 queryPmap.getString("sign"), WechatPayUtil.INPUT_CHARSET);
        if (!signMd5) {
            log.error("财付通查询订单退款返回数据验证签名错误，参数：" + params + "返回串：" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_ALI_BACK_SIGN_ERROR);
        }
        String retcode = queryPmap.getString("retcode");
        if (Utils.isEmpty(retcode) || !retcode.equals("0")) {
            log.error("财付通查询订单退款请求返回参数异常，retcode!=0，参数:" + params + "返回串" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_TEN_PAY_INFO_ERROR);
        }
        String refund_state = getRefundState(queryPmap.getString("refund_state_0"));
        result.addItem("refund_status", refund_state);
        return result;
    }

    /**
     * 6.1财付通查询订单退款状态转换为通用
     */
    private String getRefundState(String refund_status) {
        String tradeStatus;
        if (Utils.isEmpty(refund_status)) {
            return OrderRefundState.UNKNOWN.name();
        }
        switch (refund_status) {
            //refund_status为4、10，代表退款成功（最终态），资金已返回买家银行卡或者财付通账号。
            case "4":
            case "10":
                tradeStatus = OrderRefundState.SUCCESS.name();
                break;
            //refund_status为8、9、11，代表退款处理中（中间态），9代表财付通已经提交退款请求给银行
            // （资金已从商户号中扣减，退款记录会出现在对账单中）；
            case "8":
            case "9":
            case "11":
                //refund_status为1、2，代表状态未确定（中间态），需要商户使用原退款单号重新发起退款
            case "1":
            case "2":
                tradeStatus = OrderRefundState.PROCESSING.name();
                break;
            //refund_status为3、5、6，代表失败（最终态），需要商户更换退款单号重新发起退款
            case "3":
            case "5":
            case "6":
                tradeStatus = OrderRefundState.FAIL.name();
                break;
            //refund_status为7，代表退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，
            // 资金回流到商户的现金帐号，需要商户人工干预，通过线下或者财付通转账的方式进行退款。
            case "7":
                tradeStatus = OrderRefundState.OFFLINE.name();
                break;
            default:
                tradeStatus = OrderRefundState.UNKNOWN.name();

        }
        return tradeStatus;
    }

}
