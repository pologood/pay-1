package com.sogou.pay.thirdpay.api.impl;

import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.*;
import com.sogou.pay.common.utils.XMLUtil;
import com.sogou.pay.thirdpay.api.BankPayApi;
import com.sogou.pay.thirdpay.biz.modle.QueryReturnRecord;
import com.sogou.pay.thirdpay.biz.modle.Record;
import com.sogou.pay.thirdpay.biz.modle.RefundQueryRecord;
import com.sogou.pay.thirdpay.biz.utils.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;


/**
 * 批量银行代付实现接口
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/06/02 12:04
 */
@Component
public class BankPayApiImpl implements BankPayApi {
    private static final Logger log = LoggerFactory.getLogger(BankPayApiImpl.class);

    /**
     * 1.批量银行代付提交接口
     */
    public ResultMap<String> paySubmit(PMap params) {
        log.info("Bank Pay Submit Start!Parameters:" + JsonUtil.beanToJson(params));
        ResultMap result = ResultMap.build();
        //1.验证参数合法性
        boolean isEmptyParams = verifyParams(params);
        if (!isEmptyParams) {
            log.error("Bank Pay Submit:Lack Parameter Or Parameter Illegal,Parameters:" + JsonUtil.beanToJson(params));
            return result.build(ResultStatus.BANK_PAY_PARAM_ERROR);
        }
        //根据商户号获取商户证书导入密码
        String certPasswd;
        String opPasswd;
        if (params.getString("merchantNo").equals(TenPayUtil.WL_OP_USER_ID)) {
            certPasswd = TenPayUtil.WL_CERT_PASSWD;
            opPasswd = TenPayUtil.WL_OP_USER_PASSWD;
        } else {
            certPasswd = TenPayUtil.KJ_CERT_PASSWD;
            opPasswd = TenPayUtil.KJ_OP_USER_PASSWD;
        }
        //2.按照文档要求组装请求参数
        params.put("opPasswd",opPasswd);
        ResultMap reqParamsMap = assemblyReqParams(params);
        if (reqParamsMap.getStatus() != ResultStatus.SUCCESS) {
            log.error("Bank Pay Submit:Assembly Request Parameter Illegal,Params：" + JsonUtil.beanToJson(reqParamsMap));
            return ResultMap.build(ResultStatus.BANK_PAY_GET_PARAM_ERROR);
        }
        PMap reqParams = (PMap) reqParamsMap.getReturnValue();
        //3.请求财付通代付接口
        ResultMap httpResponse = TenPayHttpClient.buildRequest(
                params.getString("bankPaySubUrl"), reqParams, "POST", "GBK",
                TenPayUtil.TIME_OUT, params.getString("publicCertFilePath"),
                params.getString("privateCertFilePath"),
                params.getString("merchantNo"), certPasswd);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("Bank Pay Submit:Http Request Illegal,Params：" + JsonUtil.beanToJson(reqParamsMap));
            return ResultMap.build(ResultStatus.BANK_PAY_HTTP_ERROR);
        }
        //4.检查返回参数
        ResultMap returnResult = checkResParamsInfo(httpResponse);
        if (returnResult.getStatus() != ResultStatus.SUCCESS) {
            log.error("Bank Pay Submit:Http Requset Return Params Illegal,Request Params：" + JsonUtil.beanToJson(params) + "Return Params：" + JsonUtil.beanToJson(httpResponse));
            return ResultMap.build(ResultStatus.BANK_PAY_BACK_PARAM_ERROR);
        }

        return returnResult;
    }

    /**
     * 2.批量银行代付查询接口
     */
    public ResultMap<String> payQuery(PMap params) {
        log.info("Bank Pay Query Start!Parameters:" + JsonUtil.beanToJson(params));
        ResultMap result = ResultMap.build();
        //1.验证参数合法性
        boolean isEmptyParams = verifyParams(params);
        if (!isEmptyParams) {
            log.error("Bank Pay Query:Lack Parameter Or Parameter Illegal,Parameters:" + JsonUtil.beanToJson(params));
            return result.build(ResultStatus.BANK_PAY_QUERY_PARAM_ERROR);
        }
        //根据商户号获取商户证书导入密码
        String certPasswd;
        String opPasswd;
        if (params.getString("merchantNo").equals(TenPayUtil.WL_OP_USER_ID)) {
            certPasswd = TenPayUtil.WL_CERT_PASSWD;
            opPasswd = TenPayUtil.WL_OP_USER_PASSWD;
        } else {
            certPasswd = TenPayUtil.KJ_CERT_PASSWD;
            opPasswd = TenPayUtil.KJ_OP_USER_PASSWD;
        }
        params.put("opPasswd",opPasswd);
        //2.按照文档要求组装请求参数
        ResultMap reqParamsMap = assemblyQueryReqParams(params);
        if (reqParamsMap.getStatus() != ResultStatus.SUCCESS) {
            log.error("Bank Pay Query:Assembly Request Parameter Illegal,Params：" + JsonUtil.beanToJson(params));
            return ResultMap.build(ResultStatus.BANK_PAY_QUERY_GET_PARAM_ERROR);
        }
        String reqParamsXml = (String) reqParamsMap.getReturnValue();
        //3.请求财付通代付接口
        ResultMap httpResponse = TenPayHttpClient.buildRequestXml(
                params.getString("bankPaySubUrl"), reqParamsXml, "POST", "GBK",
                TenPayUtil.TIME_OUT, params.getString("publicCertFilePath"),
                params.getString("privateCertFilePath"),
                params.getString("merchantNo"), certPasswd);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("Bank Pay Query:Http Request Illegal,Params：" + JsonUtil.beanToJson(reqParamsMap));
            return ResultMap.build(ResultStatus.BANK_PAY_QUERY_HTTP_ERROR);
        }
        String responseString = httpResponse.getData().get("responseData").toString();
        //4.检查返回参数
        try {
            result = parseMessage(responseString);
        } catch (Exception e) {
            log.error("Bank Pay Query: Result Xml Parse error ", e);
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        if (result.getStatus() != ResultStatus.SUCCESS) {
            log.error("Bank Pay Query:Http Requset Return Params Illegal,Request Params：" + params + "Return Params：" + httpResponse);
            return ResultMap.build(ResultStatus.BANK_PAY_QUERY_BACK_PARAM_ERROR);
        }


        return result;
    }

    /**
     * 3.退票查询接口
     */
    public ResultMap<String> refundQuery(PMap params) {
        log.info("Bank Refund Query Start!Parameters:" + JsonUtil.beanToJson(params));
        ResultMap result = ResultMap.build();
        //1.验证参数合法性
        if (Utils.isEmpty(params.getString("merchantNo"), params.getString("startTime"), params.getString("endTime"))) {
            log.error("Bank Refund Query:Lack Parameter Or Parameter Illegal,Parameters:" + JsonUtil.beanToJson(params));
            return result.build(ResultStatus.BANK_REFUND_QUERY_GET_PARAM_ERROR);
        }
        //2.按照文档要求组装请求参数
        ResultMap reqParamsMap = assemblyRefundQueryReqParams(params);
        if (reqParamsMap.getStatus() != ResultStatus.SUCCESS) {
            log.error("Bank Refund Query:Assembly Request Parameter Illegal,Params：" + JsonUtil.beanToJson(params));
            return ResultMap.build(ResultStatus.BANK_REFUND_QUERY_GET_PARAM_ERROR);
        }
        String reqParamsXml = (String) reqParamsMap.getReturnValue();
        //3.请求财付通代付接口
        //根据商户号获取商户证书导入密码
        String certPasswd;
        if (params.getString("merchantNo").equals(TenPayUtil.WL_OP_USER_ID)) {
            certPasswd = TenPayUtil.WL_CERT_PASSWD;
        } else {
            certPasswd = TenPayUtil.KJ_CERT_PASSWD;
        }
        ResultMap httpResponse = TenPayHttpClient.buildRequestXml(
                params.getString("refundQueryUrl"), reqParamsXml, "POST", "GBK",
                TenPayUtil.TIME_OUT, params.getString("publicCertFilePath"),
                params.getString("privateCertFilePath"),
                params.getString("merchantNo"), certPasswd);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("Bank Refund Query:Http Request Illegal,Params：" + JsonUtil.beanToJson(reqParamsMap));
            return ResultMap.build(ResultStatus.BANK_REFUND_QUERY_HTTP_ERROR);
        }
        String responseString = httpResponse.getData().get("responseData").toString();
        //4.检查返回参数
        try {
            result = refundQueryParseMessage(responseString);
        } catch (Exception e) {
            log.error("Bank Refund Query: Result Xml Parse error ", e);
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        if (result.getStatus() != ResultStatus.SUCCESS) {
            log.error("Bank Refund Query:Http Requset Return Params Illegal,Request Params：" + params + "Return Params：" + httpResponse);
            return ResultMap.build(ResultStatus.BANK_REFUND_QUERY_BACK_PARAM_ERROR);
        }


        return result;
    }

    /**
     * 4.对账单下载接口
     */
    public ResultMap<String> downloadBill(PMap params) {
        return ResultMap.build();
    }

    /**
     * 1.1批量银行代付提交接口-验证参数合法性
     */
    public boolean verifyParams(PMap params) {
        //Todo
        return true;
    }

    /**
     * 1.2批量银行代付提交接口-组装请求参数
     */
    public ResultMap assemblyReqParams(PMap params) {
        ResultMap result = ResultMap.build();
        //1.取出单独字段值
        PMap reqParam = new PMap();
        reqParam.put("op_code", params.getString("op_code"));
        reqParam.put("op_name", params.getString("op_name"));
        reqParam.put("op_user", params.getString("merchantNo"));
        reqParam.put("op_time", params.getString("op_time"));
        reqParam.put("sp_id", params.getString("merchantNo"));
        reqParam.put("package_id", params.getString("package_id"));
        reqParam.put("total_num", params.getString("total_num"));
        reqParam.put("total_amt", params.getString("total_amt"));
        reqParam.put("client_ip", "127.0.0.1");
        //1.1操作员密码md5加密
        String signString;
        try {
            signString =
                    MD5Util.MD5Encode(params.getString("opPasswd"), "GBK")
                            .toUpperCase();
        } catch (Exception e) {
            log.error("md5签名异常,参数：" + params);
            return ResultMap.build(ResultStatus.BANK_PAY_SIGN_ERROR);
        }
        reqParam.put("op_passwd", signString);
        //2.取出record_set字段值单独xml处理
        List<Record> record_set = (List<Record>) params.get("record_set");
        String recordXml = "";
        for (Record record : record_set) {
            String ss = record.toXml();
            recordXml += ss;
        }
        //3.将1操作的的参数xml化,并合并2操作的参数
        String reqParamStr = XMLUtil.mapToXmlString("root", reqParam);
        reqParamStr = reqParamStr.substring(0, reqParamStr.length() - 7);
        reqParamStr += "<record_set>" + recordXml + "</record_set></root>";
        //4.按照文档要求组装参数B
        String content = new sun.misc.BASE64Encoder().encode(reqParamStr.getBytes());
        //5.对B进行MD5生成C（小写）,C后面加上商户的key再进行一次MD5,生成D（小写）
        String md5securityKey = params.getString("md5securityKey");
        String abstractStr;
        try {
            abstractStr = MD5Util.MD5Encode(MD5Util.MD5Encode(content, "GBK") + md5securityKey, "GBK");
        } catch (Exception e) {
            result.withError(ResultStatus.PAY_SING_ERROR);
            return result;
        }
        PMap returnPMap = new PMap();
        returnPMap.put("content", content);
        returnPMap.put("abstract", abstractStr);
        result.withReturn(returnPMap);
        return result;

    }

    /**
     * 1.3批量银行代付提交接口-检查返回参数
     */
    public ResultMap checkResParamsInfo(ResultMap httpResponse) {
        ResultMap result = ResultMap.build();
        String responseString = httpResponse.getData().get("responseData").toString();
        PMap responseMap;
        //1.调用财付通提供的解析xml方法
        try {
            responseMap = XMLParseUtil.doXMLParse(responseString);
        } catch (JDOMException e) {
            e.printStackTrace();
            return ResultMap.build(ResultStatus.BANK_PAY_BACK_PARAM_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultMap.build(ResultStatus.BANK_PAY_BACK_PARAM_ERROR);
        }
        //2.检查返回参数
        if (responseMap == null) {
            return ResultMap.build(ResultStatus.BANK_PAY_BACK_PARAM_ERROR);
        }
        String retcode = responseMap.getString("retcode");
        if (Utils.isEmpty(retcode) || !"0".equals(retcode) || !"00".equals(retcode)) {
            result.addItem("retcode", retcode);
            log.error("批量银行代付查询返回数据状态码错误，状态retmsg!=00/0:" + responseMap.getString("retmsg"));
            result.withError(ResultStatus.BANK_PAY_BACK_PARAM_ERROR);
        }

        return result;

    }

    /**
     * 2.1批量银行代付查询接口组装请求参数
     */
    public ResultMap assemblyQueryReqParams(PMap params) {
        ResultMap result = ResultMap.build();
        //1.取出单独字段值
        PMap reqParam = new PMap();
        reqParam.put("op_code", params.getString("op_code"));
        reqParam.put("op_name", "batch_draw_query");
        reqParam.put("service_version", "1.2");
        reqParam.put("op_user", params.getString("merchantNo"));
        //1.1操作员密码md5加密
        String signString;
        try {
            signString =
                    MD5Util.MD5Encode(params.getString("opPasswd"), "GBK")
                            .toUpperCase();
        } catch (Exception e) {
            log.error("md5签名异常,参数：" + params);
            return ResultMap.build(ResultStatus.BANK_PAY_SIGN_ERROR);
        }
        reqParam.put("op_passwd", signString);
        reqParam.put("op_time", params.getString("op_time"));
        reqParam.put("sp_id", params.getString("merchantNo"));
        reqParam.put("package_id", params.getString("package_id"));
        reqParam.put("client_ip", "127.0.0.1");
        String reqParamStr = XMLUtil.mapToXmlString("root", reqParam);
        result.withReturn(reqParamStr);
        return result;

    }

    /**
     * 2.2检查返回参数
     */
    private ResultMap parseMessage(String checkMessage) throws DocumentException {

        ResultMap result = ResultMap.build();
        SAXReader reader = new SAXReader();
        reader.setEncoding("GBK");
        Document doc = reader.read(new StringReader(checkMessage));
        String retcode = doc.selectSingleNode("/root/retcode").getText();
        result.addItem("retcode", retcode);
        //判断请求是否成功
        if (!"0".equals(retcode) && !"0".equals(retcode)) {
            String errorText = doc.selectSingleNode("/root/retmsg").getText();
            log.error("批量银行代付查询返回数据状态码错误，状态retmsg!=00/0返回串 " + errorText);
            result.withError(ResultStatus.BANK_PAY_QUERY_BACK_PARAM_ERROR);
            return result;
        }
        String trade_state = doc.selectSingleNode("/root/result/trade_state").getText();
        if (trade_state != null) {
            result.addItem("trade_state", trade_state);
        } else {

            result.addItem("trade_state", 0);
        }
        if (!"6".equals(trade_state)) {
            return result;
        }
        //初始状态
        List<Node> originRecList = doc.selectNodes("/root/result/origin_set/origin_rec");
        List<QueryReturnRecord> originRecListrecords = getQueryReturnRecordList(originRecList);
        //成功状态
        List<Node> sucRecList = doc.selectNodes("/root/result/success_set/suc_rec");
        List<QueryReturnRecord> sucRecListrecords = getQueryReturnRecordList(sucRecList);
        //已提交银行状态
        List<Node> tobankRecList = doc.selectNodes("/root/result/tobank_set/tobank_rec");
        List<QueryReturnRecord> tobankRecListrecords = getQueryReturnRecordList(tobankRecList);
        //失败状态
        List<Node> failRecList = doc.selectNodes("/root/result/fail_set/fail_rec");
        List<QueryReturnRecord> failRecListrecords = getQueryReturnRecordList(failRecList);
        //处理中状态
        List<Node> handlingRecList = doc.selectNodes("/root/result/handling_set/handling_rec");
        List<QueryReturnRecord> handlingRecListrecords = getQueryReturnRecordList(handlingRecList);
        //退票中状态
        List<Node> retTicketRecList = doc.selectNodes("/root/result/return_ticket_set/ret_ticket_rec");
        List<QueryReturnRecord> retTicketRecListrecords = getQueryReturnRecordList(retTicketRecList);
        result.addItem("originList", originRecListrecords);
        result.addItem("successList", sucRecListrecords);
        result.addItem("tobankList", tobankRecListrecords);
        result.addItem("failList", failRecListrecords);
        result.addItem("handingList", handlingRecListrecords);
        result.addItem("retTicketList", retTicketRecListrecords);
        return result;
    }


    /**
     * @param originRecList
     * @return解析xml
     */
    private List<QueryReturnRecord> getQueryReturnRecordList(List<Node> originRecList) throws DocumentException {
        List<QueryReturnRecord> records = new LinkedList<QueryReturnRecord>();
        Node item;
        for (Node originRec : originRecList) {
            QueryReturnRecord record = new QueryReturnRecord();
            //单笔序列号
            item = originRec.selectSingleNode("serial");
            if (null != item)
                record.setSerial(item.getText());
            //收款方银行帐号
            item = originRec.selectSingleNode("rec_bankacc");
            if (null != item)
                record.setRec_bankacc(item.getText());
            //银行类型
            item = originRec.selectSingleNode("bank_type");
            if (null != item)
                record.setBank_type(item.getText());
            //收款方真实姓名
            item = originRec.selectSingleNode("rec_name");
            if (null != item)
                record.setRec_name(item.getText());
            //付款金额(以分为单位)
            item = originRec.selectSingleNode("pay_amt");
            if (null != item)
                record.setPay_amt(item.getText());
            //账户类型
            item = originRec.selectSingleNode("acc_type");
            if (null != item)
                record.setAcc_type(item.getText());
            //开户地区
            item = originRec.selectSingleNode("area");
            if (null != item)
                record.setArea(item.getText());
            //开户城市
            item = originRec.selectSingleNode("city");
            if (null != item)
                record.setCity(item.getText());
            //支行名称
            item = originRec.selectSingleNode("subbank_name");
            if (null != item)
                record.setSubbank_name(item.getText());
            //说明
            item = originRec.selectSingleNode("desc");
            if (null != item)
                record.setDesc(item.getText());
            //最后修改时间，格式：yyyy-MM-dd HH:mm:ss
            item = originRec.selectSingleNode("modify_time");
            if (null != item)
                record.setModify_time(item.getText());
            //错误码
            item = originRec.selectSingleNode("err_code");
            if (null != item)
                record.setErr_code(item.getText());
            //错误信息
            item = originRec.selectSingleNode("err_msg");
            if (null != item)
                record.setErr_msg(item.getText());
            records.add(record);
        }
        return records;
    }

    /**
     * 3.1退票查询接口组装请求参数
     */
    public ResultMap assemblyRefundQueryReqParams(PMap params) {
        ResultMap result = ResultMap.build();
        //1.取出单独字段值
        PMap reqParam = new PMap();
        reqParam.put("partner", params.getString("merchantNo"));//商户号
        reqParam.put("start_time", params.getString("startTime"));//退票的开始时间，格式为yyyyMMddHHmmss
        reqParam.put("end_time", params.getString("endTime"));//退票的结束时间，格式为yyyyMMddHHmmss
        //2.获取md5签名
        String md5securityKey = params.getString("md5securityKey");
        ResultMap
                sign = SecretKeyUtil.tenMd5sign(reqParam, md5securityKey, "GBK");
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            log.error("md5签名异常，参数:" + reqParam);
            return ResultMap.build(ResultStatus.BANK_PAY_SIGN_ERROR);
        }
        reqParam.put(sign, sign.getData().get("signValue"));
        //3.xml格式化
        String reqParamStr = XMLUtil.mapToXmlString("root", reqParam);
        result.withReturn(reqParamStr);
        return result;

    }

    /**
     * 3.2解析返回结果
     */
    private ResultMap refundQueryParseMessage(String checkMessage) throws DocumentException {

        ResultMap result = ResultMap.build();
        SAXReader reader = new SAXReader();
        reader.setEncoding("GBK");
        Document doc = reader.read(new StringReader(checkMessage));
        String retcode = doc.selectSingleNode("/root/retcode").getText();
        //判断请求是否成功
        if (!"0".equals(retcode)) {
            result.addItem("retcode", retcode);
            String errorText = doc.selectSingleNode("/root/retmsg").getText();
            log.error("退票查询返回数据状态码错误，状态retcode!=0返回串 " + errorText);
            result.withError(ResultStatus.BANK_REFUND_QUERY_BACK_PARAM_ERROR);
            return result;
        }
        //初始状态
        List<Node> cancelRecList = doc.selectNodes("/root/cancel_set/cancel_rec");
        List<RefundQueryRecord> records = new LinkedList<RefundQueryRecord>();
        Node item;
        for (Node cancelRec : cancelRecList) {
            RefundQueryRecord record = new RefundQueryRecord();
            //提现单号
            item = cancelRec.selectSingleNode("draw_id");
            if (null != item)
                record.setDraw_id(item.getText());
            //批次号
            item = cancelRec.selectSingleNode("package_id");
            if (null != item)
                record.setPackage_id(item.getText());
            //单笔序列号
            item = cancelRec.selectSingleNode("serial");
            if (null != item)
                record.setSerial(item.getText());
            //付款金额
            item = cancelRec.selectSingleNode("pay_amt");
            if (null != item)
                record.setPay_amt(item.getText());
            //银行编码
            item = cancelRec.selectSingleNode("bank_type");
            if (null != item)
                record.setBank_type(item.getText());
            // 代付发起时间
            item = cancelRec.selectSingleNode("draw_time");
            if (null != item)
                record.setDraw_time(item.getText());
            //退票时间
            item = cancelRec.selectSingleNode("cancel_time");
            if (null != item)
                record.setCancel_time(item.getText());
            //退票原因
            item = cancelRec.selectSingleNode("cancel_res");
            if (null != item)
                record.setCancel_res(item.getText());
            records.add(record);

        }
        result.addItem("cancelRecList", records);
        return result;
    }

}
