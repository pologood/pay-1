package com.sogou.pay.thirdpay.biz.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.common.utils.XMLParseUtil;
import com.sogou.pay.thirdpay.biz.AliPayService;
import com.sogou.pay.thirdpay.biz.enums.OrderState;
import com.sogou.pay.thirdpay.biz.utils.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:12
 */
@Service
public class AliPayServiceImpl implements AliPayService {

    private static final Logger log = LoggerFactory.getLogger(AliPayServiceImpl.class);

    /**
     * 1.支付宝账户支付
     */
    @Override
    public ResultMap AccountPreparePayInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.根据文档说明，组装md5加密参数
        PMap requestPMap = new PMap();
        requestPMap.put("service", AliPayUtil.ALI_ACCOUNT_SERVICE);             //接口名称
        requestPMap.put("partner", params.getString("merchantNo"));             //合作者身份ID
        requestPMap.put("_input_charset", AliPayUtil.INPUT_CHARSET);            //参数编码
        requestPMap.put("sign_type", AliPayUtil.SIGN_TYPE);                     //签名方式
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));     //服务器异步通知页面路径
        requestPMap.put("return_url", params.getString("pageNotifyUrl"));       //页面跳转同步通知页面路径（可空）
        requestPMap.put("out_trade_no", params.getString("serialNumber"));      //商户网站唯一订单号
        requestPMap.put("subject", params.getString("subject"));                //商品名称
        requestPMap.put("payment_type", AliPayUtil.PAYMENT_TYPE);               //支付类型
        requestPMap.put("seller_id", params.getString("merchantNo"));             //支付类型
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        requestPMap.put("total_fee", orderAmount);                                    //支付金额
        String md5securityKey = params.getString("md5securityKey");
        //2.获取md5签名
        ResultMap
                sign =
                SecretKeyUtil.aliMd5sign(requestPMap, md5securityKey, AliPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_ALI_SIGN_ERROR);
        }
        requestPMap.put("sign", sign.getData().get("signValue"));
        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;

    }

    /**
     * 2.支付宝网关支付
     */
    @Override
    public ResultMap GatwayPreparePayInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.组装md5加密参数
        PMap requestPMap = new PMap();
        requestPMap.put("service", AliPayUtil.ALI_ACCOUNT_SERVICE);                   //接口名称
        requestPMap.put("partner", params.getString("merchantNo"));                   //合作者身份ID
        requestPMap.put("_input_charset", AliPayUtil.INPUT_CHARSET);                  //参数编码
        requestPMap.put("sign_type", AliPayUtil.SIGN_TYPE);                           //签名方式
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));           //服务器异步通知页面路径
        requestPMap
                .put("return_url", params.getString("pageNotifyUrl"));             //页面跳转同步通知页面路径（可空）
        requestPMap.put("out_trade_no", params.getString("serialNumber"));            //商户网站唯一订单号
        /**账户支付和网关支付区别参数开始**/
        requestPMap.put("paymethod", "bankPay");
        requestPMap.put("defaultbank", params.get("bankCode"));
        /**账户支付和网关支付区别参数结束**/
        requestPMap.put("subject", params.getString("subject"));                      //商品名称
        requestPMap.put("payment_type", AliPayUtil.PAYMENT_TYPE);                     //支付类型
        requestPMap.put("seller_id", params.getString("merchantNo"));             //支付类型
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        requestPMap.put("total_fee", orderAmount);
        String md5securityKey = params.getString("md5securityKey");
        //2.获取md5签名
        ResultMap
                sign = SecretKeyUtil.aliMd5sign(requestPMap, md5securityKey, AliPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_ALI_SIGN_ERROR);
        }
        requestPMap.put("sign", sign.getData().get("signValue"));
        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;
    }


    /**
     * 3.支付宝扫码支付
     */
    @Override
    public ResultMap SweepYardsPreparePayInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.根据文档说明，组装md5加密参数
        PMap requestPMap = new PMap();
        requestPMap.put("service", AliPayUtil.ALI_ACCOUNT_SERVICE);             //接口名称
        requestPMap.put("partner", params.getString("merchantNo"));             //合作者身份ID
        requestPMap.put("_input_charset", AliPayUtil.INPUT_CHARSET);            //参数编码
        requestPMap.put("sign_type", AliPayUtil.SIGN_TYPE);                     //签名方式
        requestPMap.put("notify_url", params.getString("serverNotifyUrl"));     //服务器异步通知页面路径
        requestPMap.put("return_url", params.getString("pageNotifyUrl"));       //页面跳转同步通知页面路径（可空）
        requestPMap.put("out_trade_no", params.getString("serialNumber"));      //商户网站唯一订单号
        requestPMap.put("subject", params.getString("subject"));                //商品名称
        requestPMap.put("payment_type", AliPayUtil.PAYMENT_TYPE);               //支付类型
        requestPMap.put("seller_id", params.getString("merchantNo"));             //支付类型
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        requestPMap.put("total_fee", orderAmount);                                    //支付金额
        String md5securityKey = params.getString("md5securityKey");
        /**账户支付和扫码支付区别参数开始**/
        //扫码支付 :
        // 1） 简约前置模式：qr_pay_mode=0;
        // 2） 前置模式：qr_pay_mode=1;
        // 3） 页面跳转模式：这个参数的值 qr_pay_mode=2 ，直接进入到支付宝收银台
        requestPMap.put("qr_pay_mode", AliPayUtil.QR_PAY_MODE);
        /**账户支付和扫码关支付区别参数结束**/
        //2.获取md5签名
        ResultMap
                sign =
                SecretKeyUtil.aliMd5sign(requestPMap, md5securityKey, AliPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + requestPMap);
            return ResultMap.build(ResultStatus.THIRD_PAY_ALI_SIGN_ERROR);
        }
        requestPMap.put("sign", sign.getData().get("signValue"));
        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        return result;

    }

    /**
     * 4.支付宝客户端支付
     */
    @Override
    public ResultMap ClientPreparePayInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        // 1.组装签名用到的参数
        String orderInfo = getClientSignParams(params);
        // 2.获取商户私钥路径
        String privateCertFilePath = params.getString("privateCertFilePath");
        // 3.获取商户私钥
        String privateCertKey = "";
        try {
            FileReader read = new FileReader(privateCertFilePath);
            BufferedReader br = new BufferedReader(read);
            String row;
            while ((row = br.readLine()) != null) {
                privateCertKey = privateCertKey + row;
            }
        } catch (Exception e) {
            log.error("获取支付平台第三方账户密钥异常，参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_PAY_SECRET_KEY_ERROR);
        }
        // 4.签名
        String
                strsign =
                SecretKeyUtil.aliClientRsaSign(orderInfo, privateCertKey, AliPayUtil.INPUT_CHARSET);
        // 5.对签名进行编码
        try {
            strsign = URLEncoder.encode(strsign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("对签名进行编码出错，参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_PAY_ALI_CLIENT_ENCODE_ERROR);
        }
        // 6.组装商户需要的订单信息参数
        String payInfo = orderInfo + "&sign=\"" + strsign + "\"&"
                + AliPayUtil.getSignType();
        // 7.获取客户端需要的支付宝公钥
        String publicCertFilePath = params.getString("publicCertFilePath");
        String publicCertKey = "";
        try {
            FileReader read = new FileReader(publicCertFilePath);
            BufferedReader br = new BufferedReader(read);
            String row;
            while ((row = br.readLine()) != null) {
                publicCertKey = publicCertKey + row;
            }
        } catch (Exception e) {
            log.error("获取支付宝公钥异常，参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_PAY_GET_ALIS_KEY_ERROR);
        }
        result.addItem("strOrderInfo", payInfo);
        result.addItem("aliPublicKey", publicCertKey);
        return result;
    }

    public static String getClientSignParams(PMap params) {
        // 1.合作者身份ID
        String orderInfo = "partner=" + "\""
                + params.getString("merchantNo") + "\"";
        // 2.卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + params.getString("merchantNo")
                + "\"";
        // 3.商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\""
                + params.getString("serialNumber") + "\"";
        // 4.商品名称
        orderInfo += "&subject=" + "\"" + params.getString("subject") + "\"";
        // 5.商品详情
        orderInfo += "&body=" + "\"" + params.getString("subject") + "\"";
        // 6.商品金额
        orderInfo += "&total_fee=" + "\"" + params.getString("orderAmount")
                + "\"";
        // 7.服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\""
                + params.getString("serverNotifyUrl") + "\"";
        // 8.接口名称， 固定值
        orderInfo += "&service=" + "\""
                + AliPayUtil.SERVICE + "\"";
        // 9.支付类型， 固定值
        orderInfo += "&payment_type=" + "\""
                + AliPayUtil.PAYMENT_TYPE + "\"";
        // 10.参数编码， 固定值
        orderInfo += "&_input_charset=" + "\""
                + AliPayUtil.INPUT_CHARSET + "\"";
        // 11.设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=" + "\""
                + AliPayUtil.IT_B_PAY + "\"";
        return orderInfo;
    }

    /**
     * 5.支付宝查询订单信息
     */
    @Override
    public ResultMap queryOrderInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        // 1.组装签名用到的参数
        PMap requestData = new PMap();
        requestData.put("service", AliPayUtil.ALI_QUERY_SERVICE);                  //查询订单接口名
        requestData.put("partner", params.getString("merchantNo"));                //商户号
        requestData.put("_input_charset", AliPayUtil.INPUT_CHARSET);               //编码
        requestData.put("sign_type", AliPayUtil.SIGN_TYPE);                        //签名类型
        requestData.put("out_trade_no", params.getString("serialNumber"));             //订单号
        // 2.获取商户私钥
        String md5securityKey = params.getString("md5securityKey");                //加密秘钥
        // 3.签名
        ResultMap
                sign =
                SecretKeyUtil.aliMd5sign(requestData, md5securityKey, AliPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + requestData);
            return ResultMap.build(ResultStatus.THIRD_QUERY_ALI_SIGN_ERROR);
        }
        requestData.put("sign", sign.getData().get("signValue"));
        // 4.获取支付机构请求报文处理配置
        ResultMap httpResponse = HttpClient.buildRequest(
                params.getString("queryUrl"), requestData, "POST", "utf-8");
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("http请求失败，参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_QUERY_ALI_HTTP_ERROR);
        }
        String resContent = httpResponse.getData().get("responseData").toString();
        // 5.获取返回参数
        PMap alipayMap;
        PMap responseMap;
        try {
            alipayMap = XMLParseUtil.doXMLParse(resContent);
            // 6.验证返回参数合法性
            if (alipayMap == null) {
                log.error("支付宝订单查询返回参数异常，没有键值alipay，参数:" + params + "返回串" + resContent);
                return ResultMap.build(ResultStatus.THIRD_QUERY_ALI_PAY_INFO_ERROR);
            }
            String alipayIsSuccess = alipayMap.getString("is_success");
            if (alipayIsSuccess.isEmpty()) {
                log.error("支付宝订单查询返回参数异常，没有键值is_success，参数:" + params + "返回串" + resContent);
                return ResultMap.build(ResultStatus.THIRD_QUERY_ALI_PAY_INFO_ERROR);
            }
            if (!"T".equals(alipayIsSuccess.toUpperCase())) {
                log.error("支付宝订单查询返回参数异常，状态is_success!=T，参数:" + params + "返回串" + resContent);
                return ResultMap.build(ResultStatus.THIRD_QUERY_ALI_PAY_INFO_ERROR);
            }
            responseMap = XMLParseUtil.doXMLParse(alipayMap.getString("response"));
        } catch (Exception e) {
            log.error("支付宝订单查询http请求失败，参数:" + requestData);
            return ResultMap.build(ResultStatus.THIRD_QUERY_ALI_HTTP_ERROR);
        }
        // 7.获取对应退款单共同参数，并返回
        String trade_status = getTradeState(responseMap.getString("trade_status"));
        result.addItem("order_state", trade_status);
        return result;
    }


    /**
     * 6.支付宝订单退款
     */
    @Override
    public ResultMap refundOrderInfo(PMap params) throws ServiceException {
        // 将日期转换为退款请求的当前时间。 格式为：yyyy-MM-dd hh:mm:ss
        Date refundDate = params.getDate("refundReqTime");
        String refundStr = Utils.dateToString(refundDate, "yyyy-MM-dd hh:mm:ss");
        ResultMap result = ResultMap.build();
        PMap requestData = new PMap();
        requestData.put("service", AliPayUtil.ALI_REFUND_SERVICE);                     //接口名
        requestData.put("partner", params.getString("merchantNo"));                    //商户号
        requestData.put("_input_charset", AliPayUtil.INPUT_CHARSET);                   //编码
        requestData.put("sign_type", AliPayUtil.SIGN_TYPE);                            //加密类型
        requestData.put("batch_no", params.getString("refundSerialNumber")); //退款批次号
        requestData.put("refund_date", refundStr);                                     //退款请求时间
        requestData.put("batch_num", AliPayUtil.BATCH_NUM);                            //退款笔数
        BigDecimal oAmount = new BigDecimal(params.getString("refundAmount"));         //退款金额
        String refundAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        requestData.put(
                "detail_data",
                params.getString("agencySerialNumber") + "^"
                        + refundAmount + "^"
                        + params.getString("refundSerialNumber") );
        requestData.put("notify_url", params.getString("refundNotifyUrl"));

        String md5securityKey = params.getString("md5securityKey");
        ResultMap
                sign =
                SecretKeyUtil.aliMd5sign(requestData, md5securityKey, AliPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("支付宝订单退款签名失败，参数:"
                    + requestData);
            return ResultMap.build(ResultStatus.THIRD_REFUND_ALI_SIGN_ERROR);
        }
        requestData.put("sign", sign.getData().get("signValue").toString());
        // 获取支付机构请求报文处理配置
        ResultMap httpResponse = HttpClient.buildRequest(
                params.getString("refundUrl"), requestData, "POST", AliPayUtil.INPUT_CHARSET);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("支付宝订单退款http请求失败，参数:"
                    + requestData);
            return ResultMap.build(ResultStatus.THIRD_REFUND_ALI_HTTP_ERROR);
        }
        String responseString = httpResponse.getData().get("responseData").toString();
        PMap alipayMap;
        try {
            alipayMap = XMLParseUtil.doXMLParse(responseString);
        } catch (Exception e) {
            log.error("支付宝订单退款返回参数XML解析异常，参数:" + requestData + "返回串" + responseString);
            throw new ServiceException(e, ResultStatus.THIRD_REFUND_ALI_BACK_PARAM_ERROR);
        }
        if (alipayMap == null) {
            log.error("支付宝订单退款返回参数xml节点alipay不存在，参数:" + requestData + "返回串" + responseString);
            return ResultMap.build(ResultStatus.THIRD_REFUND_ALI_BACK_PARAM_ERROR);
        }
        String is_success = alipayMap.getString("is_success");
        if (Utils.isEmpty(is_success) || "F".equals(is_success)) {
            result.addItem("error_code", is_success);
            result.addItem("error_msg", alipayMap.getString("error"));
            result.withError(ResultStatus.THIRD_REFUND_ALI_BACK_PARAM_ERROR);
        }

        return result;
    }

    /**
     * 7.支付宝查询订单退款信息
     */
    @Override
    public ResultMap queryRefundInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        // 1.组装签名用到的参数
        PMap requestData = new PMap();
        requestData.put("service", AliPayUtil.ALI_QUERY_REFUND_SERVICE);                  //查询订单接口名
        requestData.put("partner", params.getString("merchantNo"));                //商户号
        requestData.put("_input_charset", AliPayUtil.INPUT_CHARSET);               //编码
        requestData.put("sign_type", AliPayUtil.SIGN_TYPE);                        //签名类型
        requestData.put("batch_no", params.getString("out_refund_no"));             //退款号
        // 2.获取商户私钥
        String md5securityKey = params.getString("md5securityKey");                //加密秘钥
        // 3.签名
        ResultMap
                sign =
                SecretKeyUtil.aliMd5sign(requestData, md5securityKey, AliPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("支付宝订单退款查询d5签名异常，参数:" + requestData);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_ALI_SIGN_ERROR);
        }
        requestData.put("sign", sign.getData().get("signValue"));
        // 4.获取支付机构请求报文处理配置
        ResultMap httpResponse = HttpClient.buildRequest(
                params.getString("queryRefundUrl"), requestData, "POST", "utf-8");
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("支付宝订单退款查询http请求失败，参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_ALI_HTTP_ERROR);
        }
        String resContent = httpResponse.getData().get("responseData").toString();
        //5.判断业务参数合法性
        if (resContent == null) {
            log.error("支付宝订单退款查询返回参数异常，返回HTML为空，参数:" + params + "返回串" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_ALI_PAY_INFO_ERROR);
        }
        String[] refundResult = resContent.split("&");
        String isSuccess = refundResult[0];
        if (isSuccess.isEmpty()) {
            log.error("支付宝订单退款查询返回参数异常，没有键值is_success，参数:" + params + "返回串" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_ALI_PAY_INFO_ERROR);
        }
        if (!"IS_SUCCESS=T".equals(isSuccess.toUpperCase())) {
            log.error("支付宝订单退款查询返回参数异常，状态is_success!=T，参数:" + params + "返回串" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_ALI_PAY_INFO_ERROR);
        }
        String result_details_str = refundResult[1];
        if (Utils.isEmpty(result_details_str)) {
            log.error(
                    "支付宝订单退款查询返回参数异常，is_success为T，但result_details为空，参数:" + params + "返回串" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_ALI_PAY_INFO_ERROR);
        }
        String[] details = result_details_str.split("=");
        String result_details_result = details[1];
        if (Utils.isEmpty(result_details_result)) {
            log.error(
                    "支付宝订单退款查询返回参数异常，is_success为T，但result_detail格式异常，参数:" + params + "返回串" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_ALI_PAY_INFO_ERROR);
        }
        String[] refundStrArr = result_details_result.split("\\^");
        String retunnRefundId = refundStrArr[0];
        String retunnRefundMon = refundStrArr[2];
        String refundIsSuccess = refundStrArr[3];
        if (Utils.isEmpty(retunnRefundId, retunnRefundMon, refundIsSuccess)) {
            log.error(
                    "支付宝订单退款查询返回参数异常，is_success为T，但result_detail格式异常，参数:" + params + "返回串" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_ALI_PAY_INFO_ERROR);
        }
        if (!retunnRefundId.equals(params.getString("out_refund_no"))) {
            log.error(
                    "支付宝订单退款查询返回参数异常，is_success为T，但result_detail参数非法，参数:" + params + "返回串" + resContent);
            return ResultMap.build(ResultStatus.THIRD_Q_RF_ALI_PAY_INFO_ERROR);
        }
        result.addItem("refund_status", refundIsSuccess);
        return result;
    }

    /**
     * 5.1支付宝查询订单状态转换为通用
     */
    private String getTradeState(String trade_status) {
        String tradeStatus;
        if (Utils.isEmpty(trade_status)) {
            return OrderState.FAILURE.name();
        }
        switch (trade_status) {
            case "TRADE_PENDING":                    //等待卖家收款
            case "TRADE_FINISHED":                   //交易成功结束
            case "TRADE_SUCCESS":                    //支付成功
            case "BUYER_PRE_AUTH":                   //买家已付款（语音支付）
            case "WAIT_SELLER_SEND_GOODS":           //买家已付款，等待卖家发货
            case "WAIT_BUYER_CONFIRM_GOODS":         //卖家已发货，等待买家确认
            case "WAIT_SYS_PAY_SELLER":              //买家确认收货，等待支付宝打款给卖家
            case "COD_WAIT_SYS_PAY_SELLER":         //签收成功等待系统打款给卖家（货到付款）
                tradeStatus = OrderState.SUCCESS.name();
                break;
            case "WAIT_BUYER_PAY":                   //等待买家付款
            case "COD_WAIT_SELLER_SEND_GOODS":      //等待卖家发货（货到付款）
            case "COD_WAIT_BUYER_PAY":              //等待买家签收付款（货到付款）

                tradeStatus = OrderState.NOTPAY.name();
                break;
            case "TRADE_CLOSED":                     //交易中途关闭（已结束，未成功完成）
            case "TRADE_CANCEL":                    //立即支付交易取消
                tradeStatus = OrderState.CLOSED.name();
                break;
            case "WAIT_SYS_CONFIRM_PAY":             //支付宝确认买家银行汇款中，暂勿发货
                tradeStatus = OrderState.USERPAYING.name();
                break;
            case "TRADE_REFUSE":                     //立即支付交易拒绝
            case "TRADE_REFUSE_DEALING":             //立即支付交易拒绝中
                tradeStatus = OrderState.FAILURE.name();
                break;
            default:
                tradeStatus = OrderState.FAILURE.name();

        }
        return tradeStatus;
    }

    /**
     * 4.支付宝WAP支付
     */
    @Override
    public ResultMap WapPreparePayInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        // 1.组装签名用到的参数
        PMap requestData = getWapSignParams(params);
        //2.获取md5签名
        String md5securityKey = params.getString("md5securityKey");                //加密秘钥
        ResultMap sign = SecretKeyUtil.aliMd5sign(requestData, md5securityKey, AliPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + requestData);
            return ResultMap.build(ResultStatus.THIRD_PAY_ALI_SIGN_ERROR);
        }
        requestData.put("sign", sign.getData().get("signValue"));
        // 获取支付机构请求报文处理配置
        ResultMap httpResponse = HttpClient.buildRequest(
                params.getString("prepayUrl"), requestData, "POST", "UTF-8");
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("支付宝Wap预支付请求失败，参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_PAY_ALI_HTTP_ERROR);
        }
        String responseString = httpResponse.getData().get("responseData").toString();
        String request_token;
        try {
            responseString = URLDecoder.decode(responseString, AliPayUtil.INPUT_CHARSET);
            request_token = getRequestToken(responseString);
        } catch (Exception e) {
            log.error("支付宝Wap预支付请求decode异常，参数" + requestData);
            return ResultMap.build(ResultStatus.THIRD_PAY_ALI_HTTP_ERROR);
        }
        PMap requestPayData = new PMap();
        requestPayData.put("service", AliPayUtil.WAP_PAY_SERVICE);
        requestPayData.put("partner", params.getString("merchantNo"));
        requestPayData.put("_input_charset", AliPayUtil.INPUT_CHARSET);
        requestPayData.put("sec_id", AliPayUtil.SIGN_TYPE);
        requestPayData.put("format", AliPayUtil.WAP_FORMAT);
        requestPayData.put("v", AliPayUtil.WAP_V);
        requestPayData.put("req_data", "<auth_and_execute_req><request_token>" + request_token + "</request_token></auth_and_execute_req>");
        // 4.签名
        ResultMap paySign = SecretKeyUtil.aliMd5sign(requestPayData, md5securityKey, AliPayUtil.INPUT_CHARSET);
        if (paySign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + requestData);
            return ResultMap.build(ResultStatus.THIRD_PAY_ALI_SIGN_ERROR);
        }
        requestPayData.put("sign", paySign.getData().get("signValue"));
        requestPayData.put("payUrl", params.getString("payUrl"));
        result.addItem("returnData", requestPayData);
        return result;
    }

    /**
     * 组装支付宝WAP支付加密参数
     */

    public static PMap getWapSignParams(PMap params) {
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        String orderAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        PMap requestData = new PMap();
        requestData.put("service", AliPayUtil.WAP_SERVICE);
        requestData.put("format", AliPayUtil.WAP_FORMAT);
        requestData.put("v", AliPayUtil.WAP_V);
        requestData.put("partner", params.getString("merchantNo"));
        requestData.put("req_id", params.getString("serialNumber"));
        requestData.put("_input_charset", AliPayUtil.INPUT_CHARSET);
        requestData.put("sec_id", AliPayUtil.SIGN_TYPE);
        String reqData = "<direct_trade_create_req><subject>" + params.getString("subject") + "</subject><out_trade_no>"
                + params.getString("serialNumber") + "</out_trade_no><total_fee>" + orderAmount + "</total_fee>"
                + "<seller_account_name>" + params.getString("sellerEmail") + "</seller_account_name>"
                + "<call_back_url>" + params.getString("pageNotifyUrl") + "</call_back_url>"
                + "<notify_url>" + params.getString("serverNotifyUrl") + "</notify_url></direct_trade_create_req>";

        requestData.put("req_data", reqData);
        return requestData;
    }

    /**
     * 解析远程模拟提交后返回的信息，获得token
     *
     * @param text 要解析的字符串
     * @return 解析结果
     * @throws Exception
     */
    public static String getRequestToken(String text) throws Exception {
        String request_token = "";
        //以“&”字符切割字符串
        String[] strSplitText = text.split("&");
        //把切割后的字符串数组变成变量与数值组合的字典数组
        Map<String, String> paraText = new HashMap<>();
        for (int i = 0; i < strSplitText.length; i++) {

            //获得第一个=字符的位置
            int nPos = strSplitText[i].indexOf("=");
            //获得字符串长度
            int nLen = strSplitText[i].length();
            //获得变量名
            String strKey = strSplitText[i].substring(0, nPos);
            //获得数值
            String strValue = strSplitText[i].substring(nPos + 1, nLen);
            //放入MAP类中
            paraText.put(strKey, strValue);
        }
        if (paraText.get("res_data") != null) {
            String res_data = paraText.get("res_data");
            //token从res_data中解析出来（也就是说res_data中已经包含token的内容）
            Document document = DocumentHelper.parseText(res_data);
            request_token = document.selectSingleNode("//direct_trade_create_res/request_token").getText();
        }
        return request_token;
    }

}
