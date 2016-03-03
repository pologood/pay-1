package com.sogou.pay.thirdpay.biz.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.common.utils.XMLUtil;
import com.sogou.pay.thirdpay.biz.BillPayService;
import com.sogou.pay.common.enums.OrderRefundStatus;
import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.thirdpay.biz.utils.*;
import com.sogou.pay.thirdpay.biz.utils.billpay.*;
import com.sogou.pay.thirdpay.biz.utils.billpay.BillPayUtil;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/06/16 17:00
 */
@Service
public class BillPayServiceImpl implements BillPayService {

    private static final Logger log = LoggerFactory.getLogger(BillPayServiceImpl.class);

    /**
     * 1.快钱账户支付
     */
    @Override
    public ResultMap preparePayInfo(PMap params) throws ServiceException {
        log.info("preparePayInfo start....");
        ResultMap result = ResultMap.build();
        //1.组装支付所需要的参数
        PMap requestPMap = new PMap();
        requestPMap.put("inputCharset", BillPayUtil.inputCharset);
        requestPMap.put("pageUrl", params.getString("pageNotifyUrl"));//接收支付结果的页面地址
        requestPMap.put("bgUrl", params.getString("serverNotifyUrl"));//服务器接收支付结果的后台地址
        requestPMap.put("version", BillPayUtil.version);//网关版本
        requestPMap.put("language", BillPayUtil.language);//语言种类
        requestPMap.put("signType", BillPayUtil.signType);//签名类型
        requestPMap.put("merchantAcctId", params.getString("merchantNo") + "01");//人民币网关账号，该账号为11位人民币网关商户编号+01
        requestPMap.put("orderId", params.getString("serialNumber"));//商户网站唯一订单号
        String orderAmount = TenpayUtils.fenParseFromYuan(params.getString("orderAmount"));
        requestPMap.put("orderAmount", orderAmount);
        requestPMap.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(params.getDate("payTime")));
        //2.根据文档说明，组装RSA加密参数
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "inputCharset", BillPayUtil.inputCharset);
        signMsgVal = appendParam(signMsgVal, "pageUrl", params.getString("pageNotifyUrl"));
        signMsgVal = appendParam(signMsgVal, "bgUrl", params.getString("serverNotifyUrl"));
        signMsgVal = appendParam(signMsgVal, "version", BillPayUtil.version);
        signMsgVal = appendParam(signMsgVal, "language", BillPayUtil.language);
        signMsgVal = appendParam(signMsgVal, "signType", BillPayUtil.signType);
        signMsgVal = appendParam(signMsgVal, "merchantAcctId", params.getString("merchantNo") + "01");
        signMsgVal = appendParam(signMsgVal, "orderId", params.getString("serialNumber"));
        signMsgVal = appendParam(signMsgVal, "orderAmount", orderAmount);
        signMsgVal = appendParam(signMsgVal, "orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(params.getDate("payTime")));
        //如果银行ID为空则为B2C快钱账户支付
        if (StringUtil.checkExistNullOrEmpty(params.getString("bankCode"))) {
            requestPMap.put("payType", BillPayUtil.payType_acc);
            signMsgVal = appendParam(signMsgVal, "payType", BillPayUtil.payType_acc);
        } else {
            requestPMap.put("payType", BillPayUtil.payType_b2b);    //如果银行ID不为空则为B2B企业网银支付
            signMsgVal = appendParam(signMsgVal, "payType", BillPayUtil.payType_b2b);
            signMsgVal = appendParam(signMsgVal, "bankId", params.getString("bankCode"));
            requestPMap.put("bankId", params.getString("bankCode"));
        }
        log.info("签名开始....");
        // 2.1 获取商户证书路径
        String privateCertFilePath = params.getString("privateCertFilePath");
        // 2.2获取签名
        Pkipair pki = new Pkipair();
        String signMsg = pki.signMsg(signMsgVal, privateCertFilePath);
        log.info("签名结束，signMsg：" + signMsg);
        requestPMap.put("signMsg", signMsg);
        //3.获取支付URL
        String returnUrl = HttpUtil.packHttpsGetUrl(params.getString("payUrl"), requestPMap);
        result.addItem("returnUrl", returnUrl);
        log.info("preparePayInfo end....");
        return result;

    }

    /**
     * 2.快钱查询订单信息
     */
    @Override
    public ResultMap queryOrderInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.根据文档说明，组装md5加密参数
        PMap requestPMap = new PMap();
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "inputCharset", BillPayUtil.inputCharset);
        signMsgVal = appendParam(signMsgVal, "version", BillPayUtil.version);
        signMsgVal = appendParam(signMsgVal, "signType", BillPayUtil.querySignType);
        signMsgVal = appendParam(signMsgVal, "merchantAcctId", params.getString("merchantNo") + "01");
        signMsgVal = appendParam(signMsgVal, "queryType", BillPayUtil.queryType);
        signMsgVal = appendParam(signMsgVal, "queryMode", BillPayUtil.queryMode);
        signMsgVal = appendParam(signMsgVal, "orderId", params.getString("serialNumber"));
        signMsgVal = appendParam(signMsgVal, "key", params.getString("md5securityKey"));
        String signMsg = BillMD5Util.md5Hex(signMsgVal.getBytes()).toUpperCase();
        GatewayOrderQueryRequest queryRequest = new GatewayOrderQueryRequest();
        queryRequest.setInputCharset(BillPayUtil.inputCharset);
        queryRequest.setVersion(BillPayUtil.version);
        queryRequest.setSignType(Integer.parseInt(BillPayUtil.querySignType));
        queryRequest.setMerchantAcctId(params.getString("merchantNo") + "01");
        queryRequest.setQueryType(Integer.parseInt(BillPayUtil.queryType));
        queryRequest.setQueryMode(Integer.parseInt(BillPayUtil.queryMode));
        queryRequest.setOrderId(params.getString("serialNumber"));
        queryRequest.setSignMsg(signMsg);
        GatewayOrderQueryResponse queryResponse = new GatewayOrderQueryResponse();
        try {
            GatewayOrderQueryServiceLocator locator = new GatewayOrderQueryServiceLocator();
            queryResponse = locator.getgatewayOrderQuery().gatewayOrderQuery(queryRequest);
        } catch (RemoteException e) {
            log.error("快钱订单查询请求异常，参数:" + params + "异常e：" + e);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        } catch (javax.xml.rpc.ServiceException e) {
            log.error("快钱订单查询请求异常，参数:" + params + "异常e：" + e);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        }
        String errCode = queryResponse.getErrCode();
        if (!errCode.equals("")) {
            log.error("快钱订单查询请求返回参数错误，errCode!=null 参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);

        }
        GatewayOrderDetail[] orderDetail = queryResponse.getOrders();
        if (orderDetail.length != 1) {
            log.error("快钱订单查询请求返回参数错误，orderDetail.length != 1 参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);

        }
        GatewayOrderDetail onlyoneOrder = orderDetail[0];
        String payResult = onlyoneOrder.getPayResult();
        if (StringUtil.isBlankOrNull(payResult)) {
            log.error("快钱订单查询请求返回参数异常，payResult!=0，参数:" + params + "返回串" + payResult);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        }
        if (payResult.equals("10")) {
            result.addItem("order_state", OrderStatus.SUCCESS.name());
        } else {
            result.addItem("order_state", OrderStatus.FAILURE.name());
        }
        return result;
    }


    /**
     * 3.快钱订单退款
     */
    @Override
    public ResultMap refundOrderInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.根据文档说明，组装md5加密参数
        //生成加密签名串
        String macVal = "";
        macVal = refundAppendParam(macVal, "merchant_id", params.getString("merchantNo"));   //商户编号
        macVal = refundAppendParam(macVal, "version", BillPayUtil.refund_version);          //退款接口版本号
        macVal = refundAppendParam(macVal, "command_type", BillPayUtil.command_type);    //操作类型
        macVal = refundAppendParam(macVal, "orderid", params.getString("serialNumber"));//商户网站唯一订单号
        BigDecimal oAmount = new BigDecimal(params.getString("refundAmount"));         //退款金额
        String refundAmount = oAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        macVal = refundAppendParam(macVal, "amount", params.getString("refundAmount"));
        macVal = refundAppendParam(macVal, "postdate", params.getString("refundReqTime"));//退款请求时间
        macVal = refundAppendParam(macVal, "txOrder", params.getString("refundSerialNumber"));
        macVal = refundAppendParam(macVal, "merchant_key", params.getString("md5securityKey"));
        String mac = "";
        try {
            mac = BillMD5Util.md5Hex(macVal.getBytes("utf-8")).toUpperCase();
        } catch (UnsupportedEncodingException e) {

        }
        PMap requestPMap = new PMap();
        requestPMap.put("merchant_id", params.getString("merchantNo"));   //商户编号
        requestPMap.put("version", BillPayUtil.refund_version);          //退款接口版本号
        requestPMap.put("command_type", BillPayUtil.command_type);    //操作类型
        requestPMap.put("orderid", params.getString("serialNumber"));//商户网站唯一订单号
        requestPMap.put("amount", params.getString("refundAmount"));
        requestPMap.put("postdate", params.getString("refundReqTime"));//退款请求时间
        requestPMap.put("txOrder", params.getString("refundSerialNumber"));
        requestPMap.put("mac", mac);
        ResultMap httpResponse = HttpClient.buildRequest(
                params.getString("refundUrl"), requestPMap, "POST", "utf-8");
        String responseString = httpResponse.getData().get("responseData").toString();
        PMap billRefundMap;
        try {
            billRefundMap = XMLUtil.XML2PMap(responseString);
        } catch (Exception e) {
            log.error("快钱订单退款返回参数XML解析异常，参数:" + requestPMap + "返回串" + responseString);
            throw new ServiceException(e, ResultStatus.THIRD_REFUND_RESPONSE_PARAM_ERROR);
        }
        if (billRefundMap == null) {
            log.error("快钱订单退款返回参数xml节点alipay不存在，参数:" + requestPMap + "返回串" + responseString);
            return ResultMap.build(ResultStatus.THIRD_REFUND_RESPONSE_PARAM_ERROR);
        }
        String resultStr = billRefundMap.getString("RESULT");
        if (StringUtil.isEmpty(resultStr) || "N".equals(resultStr)) {
            result.addItem("error_code", OrderRefundStatus.FAIL);
            result.addItem("error_msg", billRefundMap.getString("CODE"));
            result.withError(ResultStatus.THIRD_REFUND_RESPONSE_PARAM_ERROR);
        }

        return result;
    }

    /**
     * 4.快钱查询订单退款信息
     */
    @Override
    public ResultMap queryRefundInfo(PMap params) throws ServiceException {
        ResultMap result = ResultMap.build();
        //1.根据文档说明，组装md5加密参数
        PMap requestPMap = new PMap();
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "version", BillPayUtil.version);
        signMsgVal = appendParam(signMsgVal, "signType", BillPayUtil.querySignType);
        signMsgVal = appendParam(signMsgVal, "merchantAcctId", params.getString("merchantNo") + "01");
        signMsgVal = appendParam(signMsgVal, "startDate", new java.text.SimpleDateFormat("yyyyMMdd").format(params.getDate("refund_time")));
        signMsgVal = appendParam(signMsgVal, "endDate", new java.text.SimpleDateFormat("yyyyMMdd").format(params.getDate("refund_time")));
        signMsgVal = appendParam(signMsgVal, "orderId", params.getString("out_refund_no"));
        signMsgVal = appendParam(signMsgVal, "requestPage", "1");
        signMsgVal = appendParam(signMsgVal, "key", params.getString("md5securityKey"));
        String signMsg = "";
        try {
            signMsg = BillMD5Util.md5Hex(signMsgVal.getBytes("utf-8")).toUpperCase();
        } catch (UnsupportedEncodingException e) {
            log.error("快钱订单查询请求MD5加密异常，参数:" + params + "异常e：" + e);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        }
        GatewayRefundQueryRequest queryRequestBean = new GatewayRefundQueryRequest();
        queryRequestBean.setVersion(BillPayUtil.version);
        queryRequestBean.setSignType(BillPayUtil.querySignType);
        queryRequestBean.setMerchantAcctId(params.getString("merchantNo") + "01");
        queryRequestBean.setStartDate(new java.text.SimpleDateFormat("yyyyMMdd").format(params.getDate("refund_time")));
        queryRequestBean.setEndDate(new java.text.SimpleDateFormat("yyyyMMdd").format(params.getDate("refund_time")));
        queryRequestBean.setOrderId(params.getString("out_refund_no"));
        queryRequestBean.setRequestPage("1");
        queryRequestBean.setSignMsg(signMsg);
        GatewayRefundQueryResponse queryResponse = new GatewayRefundQueryResponse();
        try {
            GatewayRefundQueryServiceLocator locator = new GatewayRefundQueryServiceLocator();
            queryResponse = locator.getgatewayRefundQuery().query(queryRequestBean);
        } catch (RemoteException e) {
            log.error("快钱订单查询请求异常，参数:" + params + "异常e：" + e);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        } catch (javax.xml.rpc.ServiceException e) {
            log.error("快钱订单查询请求异常，参数:" + params + "异常e：" + e);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        }
        String errCode = queryResponse.getErrCode();
        if (!errCode.equals("")) {
            log.error("快钱订单查询请求返回参数错误，errCode!=null 参数:" + params + "返回参数errCode：" + errCode);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);

        }
        GatewayRefundQueryResultDto[] orderDetail = queryResponse.getResults();
        if (orderDetail.length != 1) {
            log.error("快钱订单查询请求返回参数错误，orderDetail.length != 1 参数:" + params);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);

        }
        GatewayRefundQueryResultDto onlyoneOrder = orderDetail[0];
        String status = onlyoneOrder.getStatus();
        if (StringUtil.isBlankOrNull(status)) {
            log.error("快钱订单查询请求返回参数异常，payResult!=0，参数:" + params + "返回串" + status);
            return ResultMap.build(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        }
        //数字串:0 代表进行中、1 代表成功、2 代表失败
        if (status.equals("1")) {
            result.addItem("refund_status", OrderRefundStatus.SUCCESS.name());
        } else if (status.equals("0")) {
            result.addItem("refund_status", OrderRefundStatus.PROCESSING.name());
        } else if (status.equals("2")) {
            result.addItem("refund_status", OrderRefundStatus.FAIL.name());
        } else {
            result.addItem("refund_status", OrderRefundStatus.UNKNOWN.name());
        }
        return result;
    }

    public String appendParam(String returns, String paramId, String paramValue) {
        if (returns != "") {
            if (paramValue != null && !paramValue.equals("")) {
                returns += "&" + paramId + "=" + paramValue;
            }
        } else {
            if (paramValue != null && !paramValue.equals("")) {
                returns = paramId + "=" + paramValue;
            }
        }

        return returns;
    }


    public String refundAppendParam(String returns, String paramId, String paramValue) {
        if (returns != "") {
            if (paramValue != "") {

                returns += paramId + "=" + paramValue;
            }

        } else {

            if (paramValue != "") {
                returns = paramId + "=" + paramValue;
            }
        }

        return returns;
    }

}