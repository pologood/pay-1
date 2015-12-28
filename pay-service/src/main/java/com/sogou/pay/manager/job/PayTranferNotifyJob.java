package com.sogou.pay.manager.job;

import com.sogou.pay.common.utils.httpclient.MerchantHttpClient;
import com.sogou.pay.common.utils.httpclient.MerchantResponse;
import com.sogou.pay.service.config.PayConfig;
import com.sogou.pay.service.entity.PayTransfer;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.payment.PayTransferBatchService;
import com.sogou.pay.service.payment.PayTransferService;
import com.sogou.pay.service.utils.AppXmlPacket;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qibaichao on 2015/6/19.
 * 代发通知 任务
 */
@Service
public class PayTranferNotifyJob {

    private static final Logger logger = LoggerFactory.getLogger(PayTranferNotifyJob.class);

    @Autowired
    private PayTransferService payTransferService;

    @Autowired
    private PayTransferBatchService payTransferBatchService;

    private int NOT_NOTIFY = 0;

    private int NOTIFY_OK = 1;

    /**
     * <?xml version="1.0" encoding="gb2312"?>
     * <result>
     * <batch_no>批次号（YYYYMMDDXXX）</batch_no>
     * <trade_state>批次状态（S:成功  F:失败）</trade_state>
     * <total_count>总笔数</total_count>
     * <total_amt>总金额</total_amt>
     * <succ_count>成功笔数</succ_count>
     * <succ_amt>成功金额</succ_amt>
     * <result_detail>
     * <serial>单笔序列号</serial>
     * <rec_bankacc>收款方银行帐号</rec_bankacc>
     * <rec_name>收款方真实姓名</rec_name>
     * <pay_amt>付款金额(以元为单位)</pay_amt>
     * <pay_status>S:成功  F:失败</pay_status>
     * <err_msg>付款失败中文描述</err_msg>
     * </result_detail>
     * </result>
     */
    public void doProcess(String notifyDate) {

        List<PayTransferBatch> notifyList = payTransferBatchService.queryByNotifyFlag(NOT_NOTIFY, notifyDate);
        if (CollectionUtils.isEmpty(notifyList)) {
            return;
        }
        List<PayTransfer> payTranferList = null;
        for (PayTransferBatch payTransferBatch : notifyList) {
            payTranferList = payTransferService.queryByBatchNo(String.valueOf(payTransferBatch.getAppId()),payTransferBatch.getBatchNo());
            //组装通知数据
            if (payTranferList != null) {
                notifyApp(payTransferBatch, payTranferList);
            }
        }
    }

    @Transactional
    private void notifyApp(PayTransferBatch payTransferBatch, List<PayTransfer> payTranferList) {

        AppXmlPacket appXmlPacket = new AppXmlPacket();
        Map resultMap = new LinkedHashMap();
        resultMap.put("batch_no", payTransferBatch.getBatchNo());
        resultMap.put("trade_state", payTransferBatch.getTradeState());
        resultMap.put("total_count", payTransferBatch.getPlanTotal());
        resultMap.put("total_amt", String.valueOf(payTransferBatch.getPlanAmt()));
        resultMap.put("succ_count", payTransferBatch.getSucTotal());
        resultMap.put("succ_amt", String.valueOf(payTransferBatch.getSucAmt()));
        appXmlPacket.setResult(resultMap);
        //组装通知数据
        Map resultDetail = null;
        for (PayTransfer payTransfer : payTranferList) {
            resultDetail = new LinkedHashMap();
            resultDetail.put("serial", payTransfer.getOutRef());
            resultDetail.put("rec_bankacc", payTransfer.getRecBankAcc());
            resultDetail.put("rec_name", payTransfer.getRecName());
            resultDetail.put("pay_amt", String.valueOf(payTransfer.getPayAmt()));
            resultDetail.put("pay_status", String.valueOf(payTransfer.getPayStatus()));
            resultDetail.put("result_msg", payTransfer.getResultDesc());
            appXmlPacket.putResultDetail("result_detail", resultDetail);
        }
        Map notifyData = new HashMap();
        logger.info("代付通知 ,data=" + appXmlPacket.toQueryXmlString());
        notifyData.put("data", appXmlPacket.toQueryXmlString());
        MerchantResponse merchantResponse = MerchantHttpClient.getInstance().doPost(PayConfig.payTranferNotifyUrl, notifyData);
        if (merchantResponse.isSuccess() == true) {
            //通知
            payTransferBatchService.updateNotifyFlagByBatchNo(String.valueOf(payTransferBatch.getAppId()), payTransferBatch.getBatchNo(), NOTIFY_OK);
        }
    }
}
