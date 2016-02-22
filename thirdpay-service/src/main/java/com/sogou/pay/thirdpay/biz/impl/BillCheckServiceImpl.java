package com.sogou.pay.thirdpay.biz.impl;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.thirdpay.biz.BillCheckService;
import com.sogou.pay.thirdpay.biz.model.OutCheckRecord;
import com.sogou.pay.thirdpay.biz.utils.billpay.*;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by qibaichao on 2015/6/29.
 */
@Service
public class BillCheckServiceImpl implements BillCheckService {

    private static final Logger logger = LoggerFactory.getLogger(BillCheckServiceImpl.class);

    @Override
    public ResultMap doPayQuery(String merchantNo,
                                String startTime,
                                String endTime,
                                String pageNo,
                                String key) {

        ResultMap result = ResultMap.build();
        GatewayOrderQueryRequest queryRequest = null;

        try {

            //1.根据文档说明，组装md5加密参数
            merchantNo = merchantNo + "01";
            String signMsgVal = "";
            signMsgVal = appendParam(signMsgVal, "inputCharset", BillPayUtil.inputCharset);
            signMsgVal = appendParam(signMsgVal, "version", BillPayUtil.version);
            signMsgVal = appendParam(signMsgVal, "signType", BillPayUtil.querySignType);
            signMsgVal = appendParam(signMsgVal, "merchantAcctId", merchantNo);
            signMsgVal = appendParam(signMsgVal, "queryType", "1");
            signMsgVal = appendParam(signMsgVal, "queryMode", BillPayUtil.queryMode);
            signMsgVal = appendParam(signMsgVal, "startTime", startTime);
            signMsgVal = appendParam(signMsgVal, "endTime", endTime);
            signMsgVal = appendParam(signMsgVal, "requestPage", pageNo);
            signMsgVal = appendParam(signMsgVal, "key", key);
            String signMsg = BillMD5Util.md5Hex(signMsgVal.getBytes()).toUpperCase();

            queryRequest = new GatewayOrderQueryRequest();
            queryRequest.setInputCharset(BillPayUtil.inputCharset);
            queryRequest.setVersion(BillPayUtil.version);
            queryRequest.setSignType(Integer.parseInt(BillPayUtil.querySignType));
            queryRequest.setMerchantAcctId(merchantNo);
            queryRequest.setQueryType(Integer.parseInt("1"));
            queryRequest.setQueryMode(Integer.parseInt(BillPayUtil.queryMode));
            queryRequest.setStartTime(startTime);
            queryRequest.setEndTime(endTime);
            queryRequest.setRequestPage(pageNo);
            queryRequest.setSignMsg(signMsg);

            GatewayOrderQueryServiceLocator locator = new GatewayOrderQueryServiceLocator();
            GatewayOrderQueryResponse queryResponse = locator.getgatewayOrderQuery().gatewayOrderQuery(queryRequest);

            String errCode = queryResponse.getErrCode();
            if (!errCode.equals("")) {

                logger.error("快钱订单分页查询请求返回参数错误，errCode!=null queryRequest参数:" + JSON.toJSONString(queryRequest));
                result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
                return result;
            }

            GatewayOrderDetail[] orderDetail = queryResponse.getOrders();
            if (orderDetail == null || orderDetail.length == 0) {

                logger.info("快钱订单分页查询没有对应的查询结果， queryRequest参数:" + JSON.toJSONString(queryRequest));
                return result;
            }

            List<OutCheckRecord> records = null;
            //转换数据
            records = parsePayMessage(orderDetail);
            result.getData().put("records", records);
        } catch (RemoteException e) {
            logger.error("快钱订单分页查询请求异常，queryRequest参数:" + JSON.toJSONString(queryRequest) + "异常e：" + e);
            result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        } catch (javax.xml.rpc.ServiceException e) {
            logger.error("快钱订单分页查询请求异常，queryRequest参数:" + JSON.toJSONString(queryRequest) + "异常e：" + e);
            result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        } catch (Exception e) {
            logger.error("快钱解析 orderDetail 异常 ", e);
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public ResultMap doRefundQuery(String merchantNo,
                                   String startTime,
                                   String endTime,
                                   String pageNo,
                                   String key) {

        ResultMap result = ResultMap.build();
        GatewayRefundQueryRequest refundQueryRequest = null;

        try {

            String signMsgVal = "";
            merchantNo = merchantNo + "01";
            signMsgVal = appendParam(signMsgVal, "version", BillPayUtil.version);
            signMsgVal = appendParam(signMsgVal, "signType", BillPayUtil.querySignType);
            signMsgVal = appendParam(signMsgVal, "merchantAcctId", merchantNo);
            signMsgVal = appendParam(signMsgVal, "lastUpdateStartDate", startTime);
            signMsgVal = appendParam(signMsgVal, "lastUpdateEndDate", endTime);
            signMsgVal = appendParam(signMsgVal, "requestPage", pageNo);
            signMsgVal = appendParam(signMsgVal, "key", key);
            String signMsg = BillMD5Util.md5Hex(signMsgVal.getBytes()).toUpperCase();

            refundQueryRequest = new GatewayRefundQueryRequest();
            refundQueryRequest.setVersion(BillPayUtil.version);
            refundQueryRequest.setSignType(BillPayUtil.querySignType);
            refundQueryRequest.setMerchantAcctId(merchantNo);
            refundQueryRequest.setLastupdateStartDate(startTime);
            refundQueryRequest.setLastupdateEndDate(endTime);
            refundQueryRequest.setRequestPage(pageNo);
            refundQueryRequest.setSignMsg(signMsg);

            GatewayRefundQueryServiceLocator locator = new GatewayRefundQueryServiceLocator();
            GatewayRefundQueryResponse refundQueryResponse  = locator.getgatewayRefundQuery().query(refundQueryRequest);

            String errCode = refundQueryResponse.getErrCode();
            if (!errCode.equals("")) {
                logger.error("快钱订单分退款页查询请求返回参数错误，errCode!=null queryRequest参数:" + JSON.toJSONString(refundQueryRequest));
                result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
                return result;
            }

            GatewayRefundQueryResultDto[] refundDetails = refundQueryResponse.getResults();
            if (refundDetails == null || refundDetails.length == 0) {
                logger.info("快钱订单退款分页查询没有对应的查询结果， queryRequest参数:" + JSON.toJSONString(refundQueryRequest));
                return result;
            }

            List<OutCheckRecord> records = null;
            //转换数据
            records = parseRefundMessage(refundDetails);
            result.getData().put("records", records);

        } catch (RemoteException e) {
            logger.error("快钱订单退款分页查询请求异常，queryRequest参数:" + JSON.toJSONString(refundQueryRequest) + "异常e：" + e);
            result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        } catch (javax.xml.rpc.ServiceException e) {
            logger.error("快钱订单退款分页查询请求异常，queryRequest参数:" + JSON.toJSONString(refundQueryRequest) + "异常e：" + e);
            result.withError(ResultStatus.THIRD_QUERY_RESPONSE_PARAM_ERROR);
        } catch (Exception e) {
            logger.error("快钱退款分页查询解析 refundDetails 异常 ", e);
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }


    private List<OutCheckRecord> parsePayMessage(GatewayOrderDetail[] orderDetails) throws Exception {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        List<OutCheckRecord> records = new LinkedList<OutCheckRecord>();
        OutCheckRecord outCheckRecord = null;
        for (GatewayOrderDetail orderDetail : orderDetails) {
            outCheckRecord = new OutCheckRecord();
            //第三方交易时间
            outCheckRecord.setOutTransTime(df.parse(orderDetail.getDealTime()));
            // 第三方订单号
            outCheckRecord.setOutPayNo(orderDetail.getDealId());
            //我方单号
            outCheckRecord.setPayNo(orderDetail.getOrderId());
            //交易金额
            outCheckRecord.setMoney(TenpayUtils.parseFromFen(String.valueOf(orderDetail.getOrderAmount())));
            //手续费
            outCheckRecord.setCommssionFee(TenpayUtils.parseFromFen(String.valueOf(orderDetail.getFee())));
            records.add(outCheckRecord);
        }
        return records;
    }

    private List<OutCheckRecord> parseRefundMessage(GatewayRefundQueryResultDto[] refundDetails) throws Exception {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        List<OutCheckRecord> records = new LinkedList<OutCheckRecord>();
        OutCheckRecord outCheckRecord = null;
        for (GatewayRefundQueryResultDto refundDetail : refundDetails) {
            outCheckRecord = new OutCheckRecord();
            //第三方交易时间
            outCheckRecord.setOutTransTime(df.parse(refundDetail.getLastUpdateTime()));
            // 第三方订单号
            outCheckRecord.setOutPayNo(refundDetail.getSequenceId());
            //我方单号
            outCheckRecord.setPayNo(refundDetail.getOrderId());
            //交易金额
            outCheckRecord.setMoney(TenpayUtils.parseFromFen(String.valueOf(refundDetail.getOrderAmout())));
            //手续费
            outCheckRecord.setCommssionFee(TenpayUtils.parseFromFen(String.valueOf(refundDetail.getOwnerFee())));
            records.add(outCheckRecord);
        }
        return records;
    }

    public String appendParam(String returns, String paramId, String paramValue) {
        if (returns != "") {
            if (paramValue != "") {
                returns += "&" + paramId + "=" + paramValue;
            }
        } else {
            if (paramValue != "") {
                returns = paramId + "=" + paramValue;
            }
        }
        return returns;
    }
}
