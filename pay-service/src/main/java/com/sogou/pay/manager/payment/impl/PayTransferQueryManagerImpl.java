package com.sogou.pay.manager.payment.impl;

import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultBean;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.manager.payment.PayTransferQueryManager;
import com.sogou.pay.service.config.PayConfig;
import com.sogou.pay.service.entity.PayTransfer;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.enums.PayTransferBatchStatus;
import com.sogou.pay.service.enums.PayTransferStatus;
import com.sogou.pay.service.payment.PayTransferBatchService;
import com.sogou.pay.service.payment.PayTransferService;
import com.sogou.pay.service.utils.XmlPacket;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by qibaichao on 2015/7/3.
 */
@Component
public class PayTransferQueryManagerImpl implements PayTransferQueryManager {

    private static final Logger logger = LoggerFactory.getLogger(PayTransferQueryManagerImpl.class);

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TransactionDefinition txDefinition;

    @Autowired
    private PayTransferService payTransferService;

    @Autowired
    private PayTransferBatchService payTransferBatchService;

    @Override
    public Result doProcess(String appId, String batchNo) {

        Result result = ResultBean.build();
        try {
            PayTransferBatch payTransferBatch = payTransferBatchService.queryByBatchNo(appId, batchNo);
            if (payTransferBatch == null) {
                logger.error("【代发批次信息不存在!】,batchNo = " + batchNo);
                result.withError(ResultStatus.PAY_TRANFER_BATCH_NOT_EXIST);
                return result;
            }
            result = doProcess(payTransferBatch);
        } catch (Exception ex) {
            logger.error("【代付查询异常】," + ex.getMessage());
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Result doProcess(PayTransferBatch payTransferBatch) {
        ResultBean result = ResultBean.build();
        try {
            String agentRequestStr = getAgentRequestStr(payTransferBatch);
            logger.info("【代发查询交易概要信息请求xml】:" + agentRequestStr);
            //查询交易概要信息 数据提交
            String agentResultStr = HttpUtil.sendPost(PayConfig.payTranferHost, agentRequestStr);
            logger.info("【查询交易概要信息响应xml】:" + agentResultStr);
            //处理返回结果 流程实例号
            PayTransferBatch updatePayTransferBatch = processAgentResult(agentResultStr, String.valueOf(payTransferBatch.getAppId()), payTransferBatch.getBatchNo());
            if (updatePayTransferBatch != null) {
                String agentDetailRequestStr = getAgentDetailRequestStr(updatePayTransferBatch.getReqNbr());
                logger.info("【查询交易明细信息请求xml】:" + agentDetailRequestStr);
                //查询交易明细信息 数据提交
                String resultDetailStr = HttpUtil.sendPost(PayConfig.payTranferHost, agentDetailRequestStr);
                logger.info("【查询交易明细信息响应xml】:" + resultDetailStr);
                //处理明细返回结果
                List<PayTransfer> updateList = processDetailResult(resultDetailStr);
                if (CollectionUtils.isNotEmpty(updateList)) {
                    //修改
                    updateBatchAndDetail(updateList, updatePayTransferBatch);
                }
            }
        } catch (Exception ex) {
            logger.error("【代付查询异常】," + ex.getMessage());
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }

    /**
     * 修改代付单批次及代付单 信息
     * 事务处理
     *
     * @param updateList
     * @param updatePayTransferBatch
     */
    public void updateBatchAndDetail(List<PayTransfer> updateList, PayTransferBatch updatePayTransferBatch) {

        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
        try {
            PayTransfer payTransfer = null;
            payTransferBatchService.updateTransferBatch(updatePayTransferBatch);
            for (PayTransfer updatePayTransfer : updateList) {
                //首选查询一遍，如果代付单状态为成功，要修改为失败，说明是退票
                payTransfer = payTransferService.queryBySerialNo(updatePayTransfer.getSerialNo());
                if (payTransfer == null) {
                    logger.warn("【代付单信息不存在!】 serialNo:" + updatePayTransfer.getSerialNo());
                    continue;
                }
                if (payTransfer.getPayStatus() != updatePayTransfer.getPayStatus()) {
                    logger.info("【付款单状态发生变更!】,变更前" + payTransfer.getPayStatus() + ",变更后:" + updatePayTransfer.getPayStatus());
                    if (payTransfer.getPayStatus() == PayTransferStatus.SUCCESS.getValue()
                            && updatePayTransfer.getPayStatus() == PayTransferStatus.FAIL.getValue()) {
                        payTransferService.updateStatusBySerialNo(updatePayTransfer.getSerialNo(), PayTransferStatus.REFUND.getValue(), updatePayTransfer.getResultDesc());
                    } else {
                        payTransferService.updateStatusBySerialNo(updatePayTransfer.getSerialNo(), updatePayTransfer.getPayStatus(), updatePayTransfer.getResultDesc());
                    }
                }
            }
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            logger.error("数据库错误error：" + e);
            if (txStatus != null) {
                transactionManager.rollback(txStatus);
            }
        }
    }

    /**
     * 生成概要请求报文
     *
     * @return
     */
    protected String getAgentRequestStr(PayTransferBatch payTransferBatch) {

        // 构造直接代发代扣的请求报文
        XmlPacket xmlPkt = new XmlPacket("GetAgentInfo", PayConfig.payTranferLgnName);
        Map queryMap = new Properties();
        //业务类别
        queryMap.put("BUSCOD", payTransferBatch.getBusCod());
        //起始日期
        queryMap.put("BGNDAT", DateUtil.formatCompactDate(payTransferBatch.getCreateTime()));
        //结束日期
        queryMap.put("ENDDAT", DateUtil.formatCompactDate(new Date()));
//        queryMap.put("BGNDAT", "20150308");
//        //结束日期
//        queryMap.put("ENDDAT", "20150308");
        //业务参考号
        queryMap.put("YURREF", payTransferBatch.getYurref());
        xmlPkt.putProperty("SDKATSQYX", queryMap);
        return xmlPkt.toQueryXmlString();
    }

    /**
     * 生成概要详情请求报文
     *
     * @param reqNbr
     * @return
     */
    protected String getAgentDetailRequestStr(String reqNbr) {

        XmlPacket xmlPkt = new XmlPacket("GetAgentDetail", PayConfig.payTranferLgnName);
        Map queryMap = new Properties();
        //流程实例号
        queryMap.put("REQNBR", reqNbr);
        xmlPkt.putProperty("SDKATDQYX", queryMap);
        return xmlPkt.toQueryXmlString();
    }

    /**
     * 处理概要返回的结果
     *
     * @param resultData
     * @param batchNo
     * @return reqNbr：流程实例号
     */
    protected PayTransferBatch processAgentResult(String resultData, String appId, String batchNo) {
        PayTransferBatch payTransferBatch = null;
        XmlPacket pktRsp = XmlPacket.valueOf(resultData);
        if (pktRsp == null) {
            throw new RuntimeException("响应报文解析失败");
        }
        if (pktRsp.isError()) {
            logger.error("【代发请求错误!】 :" + pktRsp.getERRMSG());
            return null;
        }
        Map propAgent = pktRsp.getProperty("NTQATSQYZ", 0);
        if (propAgent == null) {
            logger.info("【查询交易概要信息,没有要处理的信息】 ");
            return null;
        }
        //业务请求状态
        String reqSta = String.valueOf(propAgent.get("REQSTA"));
        //判断业务请求状态 是否完成
        if (!"FIN".equals(reqSta)) {
            logger.info("【查询交易概要信息,业务请求状态不是完成状态】 reqSta:" + reqSta);
            return null;
        }
        //更新 代付批次状态 为成功
        payTransferBatch = new PayTransferBatch();
        payTransferBatch.setAppId(Integer.valueOf(appId));
        payTransferBatch.setBatchNo(batchNo);
        //业务处理结果
        String rtnFlg = String.valueOf(propAgent.get("RTNFLG"));
        // 流程实例号
        String reqNbr = String.valueOf(propAgent.get("REQNBR"));
        payTransferBatch.setReqNbr(reqNbr);
        //业务处理结果是否成功
        if ("S".equals(rtnFlg)) {
            //修改代付单批次信息
            String sucNum = String.valueOf(propAgent.get("SUCNUM"));
            String sucAmt = String.valueOf(propAgent.get("SUCAMT"));
            payTransferBatch.setTradeState(PayTransferBatchStatus.COMPLETION.getValue());
            payTransferBatch.setResultDesc("");
            payTransferBatch.setSucTotal(Integer.valueOf(sucNum));
            payTransferBatch.setSucAmt(BigDecimal.valueOf(Double.valueOf(sucAmt)));
        } else {
            //业务处理结果是否失败
            String errDep = String.valueOf(propAgent.get("ERRDSP"));
            payTransferBatch.setTradeState(PayTransferBatchStatus.FAIL.getValue());
            payTransferBatch.setResultDesc(errDep);
        }
        return payTransferBatch;
    }

    /**
     * 处理明细返回结果
     *
     * @param resultDetailData
     * @return
     * @throws Exception
     */
    private List<PayTransfer> processDetailResult(String resultDetailData) {

        List<PayTransfer> updateList = null;
        XmlPacket pktRsp = XmlPacket.valueOf(resultDetailData);
        if (pktRsp == null) {
            throw new RuntimeException("响应报文解析失败");
        }
        if (pktRsp.isError()) {
            logger.info("【查询交易明细信息，错误信息】，errorMsg:" + pktRsp.getERRMSG());
            return null;
        }
        int size = pktRsp.getSectionSize("NTQATDQYZ");
        logger.info("【查询交易明细信息】，size :" + size);
        if (size != 0) {
            updateList = new ArrayList<>();
            //代付序列号
            String serialNo = "";
            //付款状态S：成功；F：失败；C：撤消；I：数据录入
            String payStatus = "";
            //付款结果说明
            String resultDesc = "";
            PayTransfer updatePayTransfer = null;
            for (int i = 0; i < size; i++) {
                updatePayTransfer = new PayTransfer();
                Map propDtl = pktRsp.getProperty("NTQATDQYZ", i);
                serialNo = String.valueOf(propDtl.get("TRSDSP"));
                payStatus = String.valueOf(propDtl.get("STSCOD"));
                resultDesc = String.valueOf(propDtl.get("ERRDSP"));
                updatePayTransfer.setSerialNo(serialNo);
                //付款成功
                if ("S".equals(payStatus)) {
                    //成功
                    updatePayTransfer.setPayStatus(PayTransferStatus.SUCCESS.getValue());
                    updatePayTransfer.setResultDesc("SUCCESS");
                } else {
                    // 失败
                    updatePayTransfer.setPayStatus(PayTransferStatus.FAIL.getValue());
                    updatePayTransfer.setResultDesc(resultDesc);
                }
                updateList.add(updatePayTransfer);
            }
        }
        return updateList;
    }

}
