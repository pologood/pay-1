package com.sogou.pay.manager.payment.impl;

import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultBean;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.manager.payment.PayTranferRequestManager;
import com.sogou.pay.service.config.PayConfig;
import com.sogou.pay.service.entity.PayTransfer;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.enums.PayTransferBatchStatus;
import com.sogou.pay.service.enums.PayTransferStatus;
import com.sogou.pay.service.payment.PayTransferBatchService;
import com.sogou.pay.service.payment.PayTransferService;
import com.sogou.pay.service.utils.XmlPacket;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by qibaichao on 2015/6/12.
 * 代发manager
 */
@Component
public class PayTranferRequestManagerImpl implements PayTranferRequestManager {

    private static final Logger logger = LoggerFactory.getLogger(PayTranferRequestManagerImpl.class);

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TransactionDefinition txDefinition;

    @Autowired
    private PayTransferService payTransferService;

    @Autowired
    private PayTransferBatchService payTransferBatchService;

    private Lock lock = new ReentrantLock();    //注意这个地方

    @Override
    public Result doProcess(String appId, String batchNo) {
        logger.info("【代发批次处理开始!】,batchNo = " + batchNo);
        // 取得锁
        lock.lock();
        ResultBean result = ResultBean.build();
        try {
            PayTransferBatch payTransferBatch = payTransferBatchService.queryByAppIdAndBatchNo(appId, batchNo);
            //验证代发单批次是否存在
            if (payTransferBatch == null) {
                logger.error("【代发批次信息不存在!】,batchNo = " + batchNo);
                result.withError(ResultStatus.PAY_TRANFER_BATCH_NOT_EXIST);
                return result;
            }
            if (payTransferBatch.getTradeState() != PayTransferBatchStatus.AUDIT_PASS.getValue()) {
                logger.error("【代发批次单审核状态不是审核通过!】,batchNo = " + batchNo);
                result.withError(ResultStatus.PAY_TRANFER_BATCH_STATUS_NOT_AUDIT_PASS);
                return result;
            }
            //验证代发单是否存在
            List<PayTransfer> payTransferList = payTransferService.queryByBatchNo(appId, batchNo);
            if (CollectionUtils.isEmpty(payTransferList)) {
                logger.error("【代发单信息不存在!】,batchNo = " + batchNo);
                result.withError(ResultStatus.PAY_TRANFER_NOT_EXIST);
                return result;
            }

            if (payTransferBatch.getTradeState() == PayTransferBatchStatus.IN_PROCESSING.getValue()) {
                logger.error("【代发批次已经提交到银行，请勿重复提交!】，batchNo = " + batchNo);
                result.withError(ResultStatus.PAY_TRANFER_BATCH_REPEAT_SUBMITTED);
                return result;
            }
            //组装提交数据
            String requestData = getRequestStr(payTransferBatch, payTransferList);
            logger.info("【代发请求xml!】:" + requestData);
            //数据提交
            String responseStr = HttpUtil.sendPost(PayConfig.payTranferHost, requestData);
            logger.info("【代发请求返回数据】xml：" + responseStr);
            //处理返回结果
            processResult(responseStr, appId, batchNo);
        } catch (Exception e) {
            logger.error("代发请求异常,batchNo :" + batchNo + "error：" + e);
            result.withError(ResultStatus.SYSTEM_ERROR);
        } finally {
            // 释放锁
            lock.unlock();
        }
        logger.info("【代发批次处理结束!】,batchNo = " + batchNo);
        return result;
    }

    /**
     * 生成请求报文
     *
     * @return
     */
    private String getRequestStr(PayTransferBatch payTransferBatch, List<PayTransfer> payTransferList) {
        // 构造直接代发代扣的请求报文
        XmlPacket xmlPkt = new XmlPacket("AgentRequest", PayConfig.payTranferLgnName);
        Map payTransferOverview = new Properties();
        //业务类别
        payTransferOverview.put("BUSCOD", payTransferBatch.getBusCod());
        //业务模式编号
        payTransferOverview.put("BUSMOD", payTransferBatch.getBusMod());
        //交易代码名称
        //payTransferOverview.put("C_TRSTYP", "");
        //交易代码
        payTransferOverview.put("TRSTYP", payTransferBatch.getTrsTyp());
        //转出账号/转入账号
        payTransferOverview.put("DBTACC", payTransferBatch.getDbtAcc());
        //分行代码
        payTransferOverview.put("BBKNBR", payTransferBatch.getBbkNbr());
        //总笔数
        payTransferOverview.put("TOTAL", String.valueOf(payTransferBatch.getPlanTotal()));
        //总金额
        payTransferOverview.put("SUM", String.valueOf(payTransferBatch.getPlanAmt()));
        //业务参考号
        payTransferOverview.put("YURREF", payTransferBatch.getYurref());
        //用途
        payTransferOverview.put("MEMO", payTransferBatch.getMemo());
        xmlPkt.putProperty("SDKATSRQX", payTransferOverview);
        Map payTransferMap = null;
        for (PayTransfer payTransfer : payTransferList) {
            payTransferMap = new Properties();
            payTransferMap.put("ACCNBR", payTransfer.getRecBankAcc());
            payTransferMap.put("CLTNAM", payTransfer.getRecName());
            payTransferMap.put("TRSAMT", String.valueOf(payTransfer.getPayAmt()));
            //跨行
            if (StringUtils.equals("N", payTransfer.getBankFlag())) {
                payTransferMap.put("BNKFLG", payTransfer.getBankFlag());
                payTransferMap.put("EACBNK", payTransfer.getOtherBank());
                payTransferMap.put("EACCTY", payTransfer.getOtherCity());
            }
            //单笔序列号 付款说明中就是单笔序列号
            payTransferMap.put("TRSDSP", payTransfer.getSerialNo());
            xmlPkt.putProperty("SDKATDRQX", payTransferMap);
        }
        return xmlPkt.toXmlString();
    }

    /**
     * 处理返回的结果
     *
     * @param resultData
     */
    private void processResult(String resultData, String appId, String batchNo) throws Exception {

        XmlPacket pktRsp = XmlPacket.valueOf(resultData);
        if (pktRsp == null) {
            throw new RuntimeException("响应报文解析失败");
        }
        if (pktRsp.isError()) {
            logger.error("【代发请求错误!】 batchNo：" + batchNo + "error:" + pktRsp.getERRMSG());
            payTransferBatchService.updateTradeStatusByBatchNo(batchNo, PayTransferBatchStatus.FAIL.getValue(), pktRsp.getERRMSG());
            return;
        }
        Map propAcc = pktRsp.getProperty("NTREQNBRY", 0);
        //流程实例号
        String reqNbr = String.valueOf(propAcc.get("REQNBR"));
        logger.info("【代发请求成功!】,reqNbr:" + reqNbr);

        //修改代发单批次为处理中
        PayTransferBatch payTransferBatch = new PayTransferBatch();
        payTransferBatch.setAppId(Integer.valueOf(appId));
        payTransferBatch.setBatchNo(batchNo);
        payTransferBatch.setTradeState(PayTransferBatchStatus.IN_PROCESSING.getValue());
        payTransferBatch.setResultDesc(PayTransferBatchStatus.IN_PROCESSING.name());
        payTransferBatch.setReqNbr(reqNbr);

        //开启事务
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
        try {
            payTransferBatchService.updateByBatchNo(payTransferBatch);
            //修改代发单状态
            payTransferService.updateStatusByBatchNo(appId, batchNo, PayTransferStatus.IN_PROCESSING.getValue());
            //提交事务
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            logger.error("数据库错误error：" + e);
            if (txStatus != null) {
                //回滚事务
                transactionManager.rollback(txStatus);
            }
        }
    }
}
