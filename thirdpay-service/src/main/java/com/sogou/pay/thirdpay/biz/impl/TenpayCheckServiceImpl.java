package com.sogou.pay.thirdpay.biz.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.thirdpay.biz.TenpayCheckService;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.modle.OutCheckRecord;
import com.sogou.pay.thirdpay.biz.modle.TenpayCheckResponse;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.biz.utils.TenPayHttpClient;
import com.sogou.pay.thirdpay.biz.utils.TenPayUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


/**
 * @Author qibaichao
 * @ClassName AbstractTenpayClearService
 * @Date 2015年2月16日
 * @Description:TENPAY 财付通对账数据获取
 */
@Service
public class TenpayCheckServiceImpl implements TenpayCheckService {


    private static final Logger logger = LoggerFactory.getLogger(TenpayCheckServiceImpl.class);

    @Override
    public ResultMap doQuery(String merchantNo, CheckType checkType, String checkDate, String key) throws ServiceException {


        ResultMap result = ResultMap.build();
        StringBuilder sb = new StringBuilder();
        // 参数需严格按照以下顺序添加，会影响签名
        sb.append("spid=" + merchantNo).append("&");
        sb.append("trans_time=" + checkDate).append("&");
        sb.append("stamp=" + String.valueOf(System.currentTimeMillis())).append("&");
        /**
         * 0:返回当日成功的订单
         * 1：返回当日成功支付的订单
         * 2：返回当日退款的订单
         */
        if (checkType == CheckType.ALL) {
            sb.append("mchtype=0");
        } else if (checkType == CheckType.PAYCASH) {
            sb.append("mchtype=1");
        } else if (checkType == CheckType.REFUND) {
            sb.append("mchtype=2");
        }
        //2.获取md5签名
        String sign = SecretKeyUtil.tenMd5sign(sb.toString(), key, TenPayUtil.INPUT_CHARSET);
        //3.组装访问url
        String requestUrl = TenPayUtil.DOWNLOAD_GATEWAY + "?" + sb.toString() + "&sign=" + sign;
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
            logger.info(String.format("do tenpay check query success  for checkDate:%s, checkType:%s, merchantNo:%s, message:%s",
                    checkDate, checkType, merchantNo, message));
            if (checkType == CheckType.ALL) {
                result = validateAndParseMessage(message);
            } else {
                result = validateAndParseMessage(message, checkType);
            }
        } else {
            logger.info(String.format("do tenpay check query error  for checkDate:%s, checkType:%s, merchantNo:%s",
                    checkDate, checkType, merchantNo));
            result.withError(ResultStatus.THIRD_QUERY_TEN_PAY_INFO_ERROR);
        }
        return result;
    }


    private ResultMap validateAndParseMessage(String message) {

        ResultMap result = ResultMap.build();
        String line = null;
        Scanner reader = null;
        int size = 0;
        List<OutCheckRecord> payRecords = new LinkedList<OutCheckRecord>();
        List<OutCheckRecord> refRecords = new LinkedList<OutCheckRecord>();
        try {
            reader = new Scanner(message);
            SimpleDateFormat df = new SimpleDateFormat("`yyyy-MM-dd HH:mm:ss");
            if (!reader.hasNextLine()) {
                logger.error("tenpay check data for error ");
                result.withError(ResultStatus.THIRD_QUERY_TEN_PAY_INFO_ERROR);
                return result;
            }
            line = reader.nextLine();// 第一行是表头，忽略
            if (line.startsWith("<html>")) {
                reader.nextLine();
                String text = reader.nextLine();// 错误描述在第3行
                logger.info("tenpay check data result  text: " + text);
                //03020120:昨日对账单未生成或没有符合条件交易记录，请稍候再试或进入交易管理查询
                if (text.startsWith("03020123") || text.startsWith("03020120")) {
                    return result;
                }
                result.withError(ResultStatus.THIRD_QUERY_TEN_PAY_INFO_ERROR);
                return result;
            }
            boolean isStatPart = false;
            while (reader.hasNextLine()) {
                line = reader.nextLine();
                if (StringUtils.isBlank(line)) {
                    isStatPart = true;
                    continue;
                }
                if (isStatPart) {
                    continue;
                }
                size++;
                String[] parts = line.split(",");
                OutCheckRecord record = new OutCheckRecord();
                String flag = parts[5].trim();
                //判断支付 or 退款
                if ("用户已支付".equals(flag)) {
                    //交易完成时间
                    record.setOutTransTime(df.parse(parts[0].trim()));
                    //第三方流水号
                    record.setOutPayNo(parts[1].trim().replaceFirst("`", ""));
                    //我方流水号
                    record.setPayNo(parts[2].trim().replaceFirst("`", ""));
                    //交易金额
                    BigDecimal money = BigDecimal.valueOf(Double.parseDouble(parts[6].trim()));
                    record.setMoney(money);
                    //手续费
                    // BigDecimal commssionFee = BigDecimal.valueOf(Double.parseDouble(parts[10].trim()));
                    //record.setCommssionFee(commssionFee);
                    record.setCommssionFee(BigDecimal.ZERO);
                    payRecords.add(record);
                } else if ("转入退款".equals(flag)) {
                    //交易完成时间
                    record.setOutTransTime(df.parse(parts[0].trim()));
                    //第三方流水号
                    record.setOutPayNo(parts[1].trim().replaceFirst("`", ""));
                    //我放流水号
                    record.setPayNo(parts[7].trim().replaceFirst("`", ""));
                    //交易金额
                    BigDecimal money = BigDecimal.valueOf(Double.parseDouble(parts[8].trim()));
                    record.setMoney(money);
                    //手续费
                    //BigDecimal commssionFee = BigDecimal.valueOf(Double.parseDouble(parts[10].trim()));
                    //record.setCommssionFee(commssionFee);
                    record.setCommssionFee(BigDecimal.ZERO);
                    refRecords.add(record);
                }
            }
            result.getData().put("payRecords", payRecords);
            result.getData().put("refRecords", refRecords);
        } catch (Exception e) {
            result.withError(ResultStatus.THIRD_QUERY_TEN_PAY_INFO_ERROR);
            logger.error("tenpay result parse error . [line " + size + "]:" + line, e);
        }
        return result;
    }

    private ResultMap validateAndParseMessage(String message, CheckType type) {

        ResultMap result = ResultMap.build();
        String line = null;
        Scanner reader = null;
        int size = 0;
        List<OutCheckRecord> records = new LinkedList<OutCheckRecord>();
        try {
            reader = new Scanner(message);
            SimpleDateFormat df = new SimpleDateFormat("`yyyy-MM-dd HH:mm:ss");
            if (!reader.hasNextLine()) {
                logger.error("tenpay check data for error ");
                result.withError(ResultStatus.THIRD_QUERY_TEN_PAY_INFO_ERROR);
                return result;
            }
            line = reader.nextLine();// 第一行是表头，忽略
            if (line.startsWith("<html>")) {
                reader.nextLine();
                String errorText = reader.nextLine();// 错误描述在第3行
                logger.info("tenpay check data result error text: " + errorText);
                result.withError(ResultStatus.THIRD_QUERY_TEN_PAY_INFO_ERROR);
                return result;
            }
            boolean isStatPart = false;
            while (reader.hasNextLine()) {
                line = reader.nextLine();
                if (StringUtils.isBlank(line)) {
                    isStatPart = true;
                    continue;
                }
                if (isStatPart) {
                    continue;
                }
                size++;
                String[] parts = line.split(",");
                OutCheckRecord record = new OutCheckRecord();
                if (type == CheckType.PAYCASH) {
                    //交易时间
                    record.setOutTransTime(df.parse(parts[0].trim()));
                    //第三方流水号
                    record.setOutPayNo(parts[1].trim().replaceFirst("`", ""));
                    //我方流水号
                    record.setPayNo(parts[2].trim().replaceFirst("`", ""));
                    //交易金额
                    BigDecimal money = BigDecimal.valueOf(Double.parseDouble(parts[5].trim()));
                    record.setMoney(money);
                    //手续费 0
                    record.setCommssionFee(BigDecimal.valueOf(0));
                } else if (type == CheckType.REFUND) {
                    //交易完成时间
                    record.setOutTransTime(df.parse(parts[1].trim()));
                    //第三方流水号
                    record.setOutPayNo(parts[7].trim().replaceFirst("`", ""));
                    //我放流水号
                    record.setPayNo(parts[3].trim().replaceFirst("`", ""));
                    //交易金额
                    BigDecimal money = BigDecimal.valueOf(Double.parseDouble(parts[10].trim()));
                    record.setMoney(money);
                    //手续费 0
                    record.setCommssionFee(BigDecimal.valueOf(0));
                } else {
                    logger.warn("unexpected check type: " + type.name());
                    result.withError(ResultStatus.THIRD_QUERY_TEN_PAY_INFO_ERROR);
                    return result;
                }
                records.add(record);
            }
            result.getData().put("records", records);
        } catch (Exception e) {
            result.withError(ResultStatus.THIRD_QUERY_TEN_PAY_INFO_ERROR);
            logger.error("tenpay result parse error . [line " + size + "]:" + line, e);
        }
        return result;
    }


}
