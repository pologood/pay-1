package com.sogou.pay.service.utils;

import com.sogou.pay.common.result.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by qibaichao on 2015/6/23.
 */
public class AppXmlPacket {

    private static final Logger logger = LoggerFactory.getLogger(AppXmlPacket.class);
    //返回码
    private int retcode;
    //错误信息描述
    private String retmsg;
    private Map result; //<String,String>
    private Map resultDetail; //<String,Vector>

    public AppXmlPacket() {
        this.retcode = ResultStatus.SUCCESS.getCode();
        this.retmsg = ResultStatus.SUCCESS.getMessage();
        result = new Properties();
        resultDetail = new Properties();
    }


    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public Map getResult() {
        return result;
    }

    public void setResult(Map result) {
        this.result = result;
    }

    public Map getResultDetail() {
        return resultDetail;
    }

    public void setResultDetail(Map resultDetail) {
        this.resultDetail = resultDetail;
    }

    /**
     * 插入数据记录
     *
     * @param sSectionName
     * @param mpData       <String, String>
     */
    public void putResultDetail(String sSectionName, Map mpData) {
        if (resultDetail.containsKey(sSectionName)) {
            Vector vt = (Vector) resultDetail.get(sSectionName);
            vt.add(mpData);
        } else {
            Vector vt = new Vector();
            vt.add(mpData);
            resultDetail.put(sSectionName, vt);
        }
    }

    public void withError(ResultStatus status) {
        this.retcode = status.getCode();
        this.retmsg = status.getMessage();
    }

    /**
     * 把报文转换成XML字符串
     *
     * @return
     */
    public String toQueryXmlString() {
        StringBuffer sfData = new StringBuffer("<?xml version='1.0' encoding = 'GBK'?>");
        sfData.append("<root>");
        sfData.append("<retcode>" + retcode + "</retcode><retmsg>" + retmsg + "</retmsg>");
        if (result.size() != 0) {
            sfData.append("<result>");
            Iterator resultItr = result.keySet().iterator();
            while (resultItr.hasNext()) {
                String resultKey = (String) resultItr.next();
                String resultValue = String.valueOf(result.get(resultKey));
                sfData.append("<" + resultKey + ">");
                sfData.append(resultValue);
                sfData.append("</" + resultKey + ">");
            }
            if (resultDetail.size() != 0) {
                sfData.append("<result_set>");
                Iterator itr = resultDetail.keySet().iterator();
                while (itr.hasNext()) {
                    String secName = (String) itr.next();
                    Vector vt = (Vector) resultDetail.get(secName);
                    for (int i = 0; i < vt.size(); i++) {
                        Map record = (Map) vt.get(i);
                        Iterator itr2 = record.keySet().iterator();
                        sfData.append("<" + secName + ">");
                        while (itr2.hasNext()) {
                            String datakey = (String) itr2.next();
                            String dataValue = (String) record.get(datakey);
                            sfData.append("<" + datakey + ">");
                            sfData.append(dataValue);
                            sfData.append("</" + datakey + ">");
                        }
                        sfData.append("</" + secName + ">");
                    }
                }
                sfData.append("</result_set>");
            }
            sfData.append("</result>");
        }
        sfData.append("</root>");
        return sfData.toString();
    }

    /**
     * 把报文转换成XML字符串
     *
     * @return
     */
    public String toRefundXmlString() {
        StringBuffer sfData = new StringBuffer("<?xml version='1.0' encoding = 'GBK'?>");
        sfData.append("<root>");
        sfData.append("<retcode>" + retcode + "</retcode><retmsg>" + retmsg + "</retmsg>");
        if (result.size() != 0) {
            sfData.append("<result>");
            Iterator resultItr = result.keySet().iterator();
            while (resultItr.hasNext()) {
                String resultKey = (String) resultItr.next();
                String resultValue = String.valueOf(result.get(resultKey));
                sfData.append("<" + resultKey + ">");
                sfData.append(resultValue);
                sfData.append("</" + resultKey + ">");
            }
            if (resultDetail.size() != 0) {
                sfData.append("<cancel_set>");
                Iterator itr = resultDetail.keySet().iterator();
                while (itr.hasNext()) {
                    String secName = (String) itr.next();
                    Vector vt = (Vector) resultDetail.get(secName);
                    for (int i = 0; i < vt.size(); i++) {
                        Map record = (Map) vt.get(i);
                        Iterator itr2 = record.keySet().iterator();
                        sfData.append("<" + secName + ">");
                        while (itr2.hasNext()) {
                            String datakey = (String) itr2.next();
                            String dataValue = (String) record.get(datakey);
                            sfData.append("<" + datakey + ">");
                            sfData.append(dataValue);
                            sfData.append("</" + datakey + ">");
                        }
                        sfData.append("</" + secName + ">");
                    }
                }
                sfData.append("</cancel_set>");
            }
            sfData.append("</result>");
        }
        sfData.append("</root>");
        return sfData.toString();
    }


    public static void main(String args[]) {
//        testQuery();
        testRefund();
    }

    public static void testQuery() {
        AppXmlPacket appXmlPacket = new AppXmlPacket();
        appXmlPacket.setRetmsg("error");
        Map resultMap = new LinkedHashMap();
        resultMap.put("trade_state", "S");
        resultMap.put("total_count", "100");
        resultMap.put("total_amt", "100");
        resultMap.put("succ_count", "100");
        resultMap.put("succ_amt", "100");
        appXmlPacket.setResult(resultMap);

        for (int i = 0; i < 2; i++) {
            Map resultDetail = new LinkedHashMap();
            resultDetail.put("serial", i + "");
            resultDetail.put("rec_bankacc", "qibaichao");
            resultDetail.put("rec_name", "qibaichao");
            resultDetail.put("pay_amt", "10");
            resultDetail.put("pay_status", "S");
            resultDetail.put("result_msg", "success");
            appXmlPacket.putResultDetail("result_detail", resultDetail);
        }
        System.out.println(appXmlPacket.toQueryXmlString());
    }

    public static void testRefund() {
        AppXmlPacket appXmlPacket = new AppXmlPacket();
        appXmlPacket.setRetmsg("error");
        Map resultMap = new LinkedHashMap();
        resultMap.put("cancel_count", "100");
        appXmlPacket.setResult(resultMap);

        for (int i = 0; i < 2; i++) {
            Map resultDetail = new LinkedHashMap();
            resultDetail.put("pay_id", i + "");
            resultDetail.put("batch_no", "qibaichao");
            resultDetail.put("pay_amt", "10");
            resultDetail.put("cancel_time", "S");
            resultDetail.put("cancel_res", "success");
            appXmlPacket.putResultDetail("cancel_rec", resultDetail);
        }
        System.out.println(appXmlPacket.toRefundXmlString());
    }
}
