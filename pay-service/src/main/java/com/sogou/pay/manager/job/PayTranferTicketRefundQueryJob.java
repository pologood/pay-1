package com.sogou.pay.manager.job;

import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.utils.httpclient.MerchantHttpClient;
import com.sogou.pay.common.utils.httpclient.MerchantResponse;
import com.sogou.pay.manager.payment.PayTransferQueryManager;
import com.sogou.pay.service.config.PayConfig;
import com.sogou.pay.service.dao.PayTransferBatchDao;
import com.sogou.pay.service.entity.PayTransfer;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.payment.PayTransferBatchService;
import com.sogou.pay.service.payment.PayTransferService;
import com.sogou.pay.service.utils.AppXmlPacket;
import com.sogou.pay.service.utils.XmlPacket;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by qibaichao on 2015/6/18.
 * 判断是否有退票情况查询
 * 1.查询处理成功的批次单
 * 2.查询交易概要信息，判断成功笔数，成功金额是否有变化，有变化，说明有退票情况
 * 3.查询交易明细信息,找出付款失败的记录，判断该记录之前的状态是否成功，成功则为退票，修改记录为失败，修改代付单批次成功金额，成功笔数
 * 4.将退票记录通知给业务系统
 */
@Component
public class PayTranferTicketRefundQueryJob {

    private static final Logger logger = LoggerFactory.getLogger(PayTranferTicketRefundQueryJob.class);

    @Autowired
    private PayTransferBatchService payTransferBatchService;

    @Autowired
    private PayTransferService payTransferService;

    @Autowired
    private PayTransferQueryManager payTransferQueryManager;

    @Autowired
    private PayTransferBatchDao payTransferBatchDao;

    private int NOT_NOTIFY = 0;

    private int NOTIFY_OK = 1;


    public void doQuery(String beginDate, String endDate) {

        try {
            String requestStr = getTransInfoRequestStr(beginDate, endDate);
            logger.info("【查询账户交易信息请求xml】:" + requestStr);
            //数据提交
            String agentResultStr = HttpUtil.sendPost(PayConfig.payTransferHost, requestStr);
            logger.info("【查询账户交易信息响应xml】:" + agentResultStr);
            if (StringUtils.isEmpty(agentResultStr)) {
                logger.error("【请求前置机异常!】");
                return;
            }
            //处理返回结果
            Set<String> yurrefSet = processTransInfoResult(agentResultStr);
            logger.info("【包含退票的批次号有】:size：" + yurrefSet.size() + ",batchNos:" + yurrefSet);
            PayTransferBatch payTransferBatch = null;
            if (CollectionUtils.isNotEmpty(yurrefSet)) {
                for (String yurref : yurrefSet) {
                    payTransferBatch = payTransferBatchDao.queryByYurref(yurref);
                    if (payTransferBatch == null) {
                        logger.warn("【代付信息不存在】，业务参考号：yurref=" + yurref);
                        continue;
                    }
                    payTransferQueryManager.doProcess(payTransferBatch);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    private void notifyApp(PayTransferBatch payTransferBatch, List<PayTransfer> payTranferList) {

        AppXmlPacket appXmlPacket = new AppXmlPacket();
        Map resultMap = new LinkedHashMap();
        resultMap.put("batch_no", payTransferBatch.getBatchNo());
        resultMap.put("fail_count", payTransferBatch.getPlanTotal() - payTransferBatch.getSucTotal());
        resultMap.put("fail_amt", payTransferBatch.getPlanAmt().subtract(payTransferBatch.getSucAmt()));
        appXmlPacket.setResult(resultMap);
        //组装通知数据
        Map resultDetail = null;
        for (PayTransfer payTransfer : payTranferList) {
            resultDetail = new LinkedHashMap();
            resultDetail.put("serial", payTransfer.getOutRef());
            resultDetail.put("rec_bankacc", payTransfer.getRecBankAcc());
            resultDetail.put("rec_name", payTransfer.getRecName());
            resultDetail.put("pay_amt", String.valueOf(payTransfer.getPayAmt()));
            resultDetail.put("pay_status", "F");
            resultDetail.put("result_msg", payTransfer.getResultDesc());
            appXmlPacket.putResultDetail("result_detail", resultDetail);
        }
        Map notifyData = new HashMap();
        logger.info("代付通知 ,data=" + appXmlPacket.toQueryXmlString());
        notifyData.put("data", appXmlPacket.toQueryXmlString());
        MerchantResponse merchantResponse = MerchantHttpClient.getInstance().doPost(PayConfig.payTransferNotifyUrl, notifyData);
        if (merchantResponse.isSuccess() == true) {
            //通知
            payTransferBatchService.updateNotifyFlagByBatchNo(String.valueOf(payTransferBatch.getAppId()),payTransferBatch.getBatchNo(), NOTIFY_OK);
        }
    }

    /**
     * 生成查询账户交易信息 请求报文
     *
     * @return
     */
    private String getTransInfoRequestStr(String beginDate, String endDate) {
        //构造查询账户交易信息xml
        XmlPacket xmlPkt = new XmlPacket("GetTransInfo", PayConfig.payTransferLgnName);
        Map mpAccInfo = new Properties();
        //分行号
        mpAccInfo.put("BBKNBR", "59");
        //账号
        mpAccInfo.put("ACCNBR", "591902896010504");
        //起始日期
        mpAccInfo.put("BGNDAT", beginDate);
        //结束日期
        mpAccInfo.put("ENDDAT", endDate);
        //借贷码 C：收入 D：支出
//        mpAccInfo.put("AMTCDR", "D");
        xmlPkt.putProperty("SDKTSINFX", mpAccInfo);
        return xmlPkt.toQueryXmlString();
    }

    /**
     * 处理返回的结果
     *
     * @param resultData
     */
    private Set<String> processTransInfoResult(String resultData) {

        XmlPacket pktRsp = XmlPacket.valueOf(resultData);
        if (pktRsp == null) {
            throw new RuntimeException("响应报文解析失败");
        }
        if (pktRsp.isError()) {
            logger.error("【查询账户交易信息错误】,errorMsg:" + pktRsp.getERRMSG());
            return null;
        }
        int size = pktRsp.getSectionSize("NTQTSINFZ");
        logger.info("【查询账户交易信息】 size :" + size);
        if (size == 0) {
            return null;
        }
        //判断交易明细中是否有代发 退票记录
        Set<String> yurrefSet = new HashSet<>();
        for (int i = 0; i < size; i++) {
            Map propDtl = pktRsp.getProperty("NTQTSINFZ", i);
            String trsCode = String.valueOf(propDtl.get("TRSCOD"));
            String narYur = String.valueOf(propDtl.get("NARYUR"));
            // NARYUR=代发时，合作方余款退还
            if ("AGRD".equals(trsCode) && "代发时，合作方余款退还".equals(narYur)) {
                //业务参考号
                yurrefSet.add(String.valueOf(propDtl.get("YURREF")));
            }
        }
        return yurrefSet;
    }
}
