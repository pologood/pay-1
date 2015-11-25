package com.sogou.pay.thirdpay.biz.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.*;
import com.sogou.pay.thirdpay.biz.WechatCheckService;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.modle.OutCheckRecord;
import com.sogou.pay.thirdpay.biz.utils.*;
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
 * Created by qibaichao on 2015/3/4.
 * 微信对账数据获取
 */
@Service
public class WechatCheckServiceImpl implements WechatCheckService {

    private static final Logger logger = LoggerFactory.getLogger(WechatCheckServiceImpl.class);

    @Override
    public ResultMap doQuery(String appId, String merchantNo, CheckType checkType, String checkDate, String key) throws ServiceException {

        ResultMap result = ResultMap.build();
        PMap requestPMap = new PMap();
        requestPMap.put("appid", appId);//公众账号ID
        requestPMap.put("mch_id", merchantNo);//商户号
        requestPMap.put("nonce_str", RandomUtils.getUUID()); //随机32位字符串
        requestPMap.put("bill_date", checkDate);//对账单日起
        if (checkType == CheckType.PAYCASH) {
            // 成功支付的订单
            requestPMap.put("bill_type", "SUCCESS");
        } else if (checkType == CheckType.REFUND) {
            // 退款订单
            requestPMap.put("bill_type", "REFUND");
        }
        //获取md5签名
        ResultMap sign = SecretKeyUtil.tenMd5sign(requestPMap, key, WechatPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            logger.error("wechat md5签名异常，参数:" + requestPMap);
            return sign;
        }
        requestPMap.put("sign", sign.getData().get("signValue"));
        //将请求参数转换成 xml 数据
        String paramsStr = com.sogou.pay.common.utils.XMLUtil.mapToXmlString("xml", requestPMap);
        // 通信对象
        WechatHttpClient httpClient = new WechatHttpClient();
        String message = "";
        if (httpClient.callHttpPost(WechatPayUtil.DOWNLOAD_GATEWAY, paramsStr)) {
            message = httpClient.getResContent();
            System.out.println(message);
            logger.info(String.format("do wechat check query for checkDate: %s|checkType: %s|merchantNo: %s|message：%s", checkDate, checkType.name(), merchantNo, message));
            result = validateAndParseMessage(message, checkType);
        } else {
            logger.info(String.format("do wechat check query error for checkDate: %s|checkType: %s|merchantNo: %s", checkDate, checkType.name(), merchantNo));
            result.withError(ResultStatus.THIRD_QUERY_WECHAT_PAY_INFO_ERROR);
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
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (!reader.hasNextLine()) {
                logger.info("wechat check data  error for noData ");
                result.withError(ResultStatus.THIRD_QUERY_WECHAT_PAY_INFO_ERROR);
                return result;
            }
            line = reader.nextLine();// 第一行是表头，忽略
            if (line.startsWith("<xml>")) {
                PMap pMap = XMLParseUtil.doXMLParse(message);
                String errorText = String.valueOf(pMap.get("return_msg"));
                logger.info("wechat check data result  text: " + errorText);
                //没有对账单数据
                if ("No Bill Exist".equals(errorText)) {
                    return result;
                }
                result.withError(ResultStatus.THIRD_QUERY_WECHAT_PAY_INFO_ERROR);
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
                //汇总标题 结束
                if (!line.startsWith("`")) {
                    break;
                }
                String[] parts = line.split(",");
                OutCheckRecord record = new OutCheckRecord();
                if (type == CheckType.PAYCASH) {
                    //第三方交易时间
                    record.setOutTransTime(df.parse(parts[0].trim().replaceFirst("`", "")));
                    // 第三方订单号
                    record.setOutPayNo(parts[5].trim().replaceFirst("`", ""));
                    //我方单号
                    record.setPayNo(parts[6].trim().replaceFirst("`", ""));
                    //交易金额
                    BigDecimal money = BigDecimal.valueOf(Double.parseDouble(parts[12].trim().replaceFirst("`", "")));
                    record.setMoney(money);
                    //手续费
                    BigDecimal commssionFee = BigDecimal.valueOf(Double.parseDouble(parts[16].trim().replaceFirst("`", "")));
                    record.setCommssionFee(commssionFee);
                } else if (type == CheckType.REFUND) {
                    //第三方退款完成时间
                    record.setOutTransTime(df.parse(parts[14].trim().replaceFirst("`", "")));
                    // 第三方订单号
                    record.setOutPayNo(parts[16].trim().replaceFirst("`", ""));
                    //我方单号
                    record.setPayNo(parts[17].trim().replaceFirst("`", ""));
                    //交易金额
                    BigDecimal money = BigDecimal.valueOf(Double.parseDouble(parts[18].trim().replaceFirst("`", "")));
                    record.setMoney(money);
                    //手续费
                    BigDecimal commssionFee = BigDecimal.valueOf(Double.parseDouble(parts[24].trim().replaceFirst("`", "")));
                    record.setCommssionFee(commssionFee);
                } else {
                    logger.warn("unexpected check type: " + type.name());
                    result.withError(ResultStatus.THIRD_QUERY_WECHAT_PAY_INFO_ERROR);
                    return result;
                }
                records.add(record);
            }
            result.getData().put("records", records);
        } catch (Exception e) {
            result.withError(ResultStatus.THIRD_QUERY_WECHAT_PAY_INFO_ERROR);
            logger.error("wechat result parse error . [line " + size + "]:" + line, e);
        }
        return result;
    }

    private String buildProcessedKey(String merchantNo, String clearType, String startTime) {
        return merchantNo + ";" + clearType + ";" + startTime;
    }
}
