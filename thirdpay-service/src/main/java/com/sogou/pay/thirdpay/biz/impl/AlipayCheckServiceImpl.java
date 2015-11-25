package com.sogou.pay.thirdpay.biz.impl;


import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.thirdpay.biz.AlipayCheckService;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.modle.OutCheckRecord;
import com.sogou.pay.thirdpay.biz.utils.AliPayUtil;
import com.sogou.pay.thirdpay.biz.utils.HttpClient;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.enums.AlipayTradeCode;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author qibaichao
 * @ClassName AlipayCheckServiceImpl
 * @Date 2015年2月16日
 * @Description:支付宝对账数据获取
 */
@Service
public class AlipayCheckServiceImpl implements AlipayCheckService {

    private static final Logger logger = LoggerFactory.getLogger(AlipayCheckServiceImpl.class);

    public ResultMap doQuery(String merchantNo, CheckType checkType, String startTime, String endTime, String pageNo, String pageSize, String key) throws ServiceException {

        ResultMap result = ResultMap.build();
        PMap requestPMap = new PMap();
        requestPMap.put("service", AliPayUtil.ACCOUNT_PAGE_QUERY_SERVCICE);//接口名称
        requestPMap.put("partner", merchantNo);//商户号
        requestPMap.put("_input_charset", "gbk");//编码字符集
        requestPMap.put("sign_type", AliPayUtil.SIGN_TYPE); //签名类型
        requestPMap.put("page_no", pageNo);//查询页号
        requestPMap.put("gmt_start_time", startTime);//账务查询开始时间
        requestPMap.put("gmt_end_time", endTime);//账务查询结束时间
        requestPMap.put("pageSize", pageSize);
//        requestPMap.put("merchant_out_order_no", "ZF20150410160304291001");
        if (checkType == CheckType.PAYCASH) {
            // 6001代表在线支付数据
            requestPMap.put("trans_code", AlipayTradeCode.TRADE_CODE_PAY.getValue());
        } else if (checkType == CheckType.REFUND) {
            // 3011代表转账，其中包含退款
            requestPMap.put("trans_code", AlipayTradeCode.TRADE_CODE_TRANSFER.getValue());
        } else if (checkType == CheckType.CHARGE) {
            // 3012代表收费
            requestPMap.put("trans_code", AlipayTradeCode.TRADE_CODE_CHARGE.getValue());
        }
        //获取md5签名
        ResultMap sign = SecretKeyUtil.aliMd5sign(requestPMap, key, AliPayUtil.INPUT_CHARSET);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            logger.error("支付宝对账数据分页查询，md5签名异常，参数:" + requestPMap);
            return sign;
        }
        requestPMap.put("sign", sign.getData().get("signValue"));
        // 获取支付机构请求报文处理配置
        ResultMap httpResponse = HttpClient.buildRequest(AliPayUtil.CHECK_URL, requestPMap, "POST", "gbk");
        logger.info("url：" + HttpUtil.packHttpsGetUrl(AliPayUtil.CHECK_URL, requestPMap));
        System.out.println("url：" + HttpUtil.packHttpsGetUrl(AliPayUtil.CHECK_URL, requestPMap));
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            logger.error("支付宝对账分页查询订单信息http请求失败，参数" + requestPMap);
            return httpResponse;
        }
        String resContent = httpResponse.getData().get("responseData").toString();
        logger.info(String.format("alipay  query success  for startTime:%s, endTime:%s, checkType:%s, merchantNo:%s, message:%s",
                startTime, endTime, checkType, merchantNo, resContent));
        try {
            result = parseMessage(resContent, checkType);
        } catch (Exception e) {
            logger.error("alipay result xml parse error ", e);
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }


    /**
     * @param checkMessage
     * @param type
     * @return解析xml
     */
    private ResultMap parseMessage(String checkMessage, CheckType type) throws DocumentException {

        ResultMap result = ResultMap.build();
        SAXReader reader = new SAXReader();
        reader.setEncoding(AliPayUtil.INPUT_CHARSET);
        Document doc = reader.read(new StringReader(checkMessage));
        String alipayIsSuccess = doc.selectSingleNode("/alipay/is_success").getText();
        logger.info("alipay check result  flag: " + alipayIsSuccess);
        //判断请求是否成功
        if (!"T".equals(alipayIsSuccess)) {
            String errorText = doc.selectSingleNode("/alipay/error").getText();
            logger.error("支付宝对账分页查询订单信息返回数据状态码错误，状态is_success!=T返回串 " + errorText);
            result.withError(ResultStatus.THIRD_QUERY_ALI_HTTP_ERROR);
            return result;
        }
        Node hasNextPage = doc.selectSingleNode("/alipay/response/account_page_query_result/has_next_page");
        //判断是否还有下一页
        if (hasNextPage != null) {
            result.addItem("hasNextPage", "T".equals(hasNextPage.getText()));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Node> accountLogVoList;
        accountLogVoList = doc.selectNodes("/alipay/response/account_page_query_result/account_log_list/AccountQueryAccountLogVO");
        Node item;
        logger.info("alipay check result size " + accountLogVoList.size());
        List<OutCheckRecord> records = new LinkedList<OutCheckRecord>();
        //支付数据解析
        if (type == CheckType.PAYCASH) {
            for (Node accountLogVo : accountLogVoList) {

                OutCheckRecord record = new OutCheckRecord();
                //手续费
                item = accountLogVo.selectSingleNode("service_fee");
                record.setCommssionFee(BigDecimal.valueOf(Double.parseDouble(item.getText())));
                //交易金额
                item = accountLogVo.selectSingleNode("income");
                record.setMoney(BigDecimal.valueOf(Double.parseDouble(item.getText())));
                //我方订单号
                item = accountLogVo.selectSingleNode("merchant_out_order_no");
                record.setPayNo(item.getText());
                // 支付宝交易号
                item = accountLogVo.selectSingleNode("trade_no");
                record.setOutPayNo(item.getText());
                //交易完成时间
                item = accountLogVo.selectSingleNode("trans_date");
                try {
                    Date date = simpleDateFormat.parse(item.getText());
                    record.setOutTransTime(date);
                } catch (ParseException e) {
                    logger.error("parse date string " + item.getText() + " failed.", e);
                }
                records.add(record);
            }
            //退款数据解析
        } else if (type == CheckType.REFUND) {
            for (Node accountLogVo : accountLogVoList) {
                OutCheckRecord record = new OutCheckRecord();
                item = accountLogVo.selectSingleNode("sub_trans_code_msg");
//                if (!AlipayTradeCode.TRADE_CODE_SUB_REFUND.getValue().equals(item.getText())) {
//                    // 非交易退款记录，跳过
//                    continue;
//                }
                //退款手续费 为0
                record.setCommssionFee(BigDecimal.valueOf(0));
                //退款金额
                item = accountLogVo.selectSingleNode("outcome");
                record.setMoney(BigDecimal.valueOf(Double.parseDouble(item.getText())));
                //我方订单号
                item = accountLogVo.selectSingleNode("memo");
                record.setPayNo(item.getText().replace("[", "").replace("]", ""));
                // 支付宝交易号
                item = accountLogVo.selectSingleNode("trade_no");
                record.setOutPayNo(item.getText());
                //交易完成时间
                item = accountLogVo.selectSingleNode("trans_date");
                try {
                    Date date = simpleDateFormat.parse(item.getText());
                    record.setOutTransTime(date);
                } catch (ParseException e) {
                    logger.error("parse date string " + item.getText() + " failed.", e);
                }
                records.add(record);
            }
        } else if (type == CheckType.CHARGE) {
            //收费数据解析
            for (Node accountLogVo : accountLogVoList) {
                OutCheckRecord record = new OutCheckRecord();
                //手续费
                record.setCommssionFee(BigDecimal.valueOf(0));
                //交易金额
                item = accountLogVo.selectSingleNode("outcome");
                record.setMoney(BigDecimal.valueOf(Double.parseDouble(item.getText())));
                //我方订单号
                item = accountLogVo.selectSingleNode("merchant_out_order_no");
                record.setPayNo(item.getText());
                // 支付宝交易号
                item = accountLogVo.selectSingleNode("trade_no");
                record.setOutPayNo(item.getText());
                //交易完成时间
                item = accountLogVo.selectSingleNode("trans_date");
                try {
                    Date date = simpleDateFormat.parse(item.getText());
                    record.setOutTransTime(date);
                } catch (ParseException e) {
                    logger.error("parse date string " + item.getText() + " failed.", e);
                }
                records.add(record);
            }
        } else {
            logger.warn("unexpected check type: " + type.name());
            result.withError(ResultStatus.THIRD_QUERY_ALI_HTTP_ERROR);
            return result;
        }
        result.getData().put("records", records);
        logger.info(String.format("alipay check records:%s ,size:%s ", JsonUtil.beanToJson(records), records.size()));
        return result;
    }

}
