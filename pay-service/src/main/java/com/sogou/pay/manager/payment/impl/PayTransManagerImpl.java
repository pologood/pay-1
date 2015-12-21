package com.sogou.pay.manager.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultBean;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.manager.model.Record;
import com.sogou.pay.manager.payment.PayTransManager;
import com.sogou.pay.service.entity.PayTransfer;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.payment.PayTransferBatchService;
import com.sogou.pay.service.payment.PayTransferService;
import com.sogou.pay.service.utils.AppXmlPacket;
import com.sogou.pay.service.utils.Constant;
import com.sogou.pay.service.utils.orderNoGenerator.SequencerGenerator;

@Service
public class PayTransManagerImpl implements PayTransManager {

    private static final Logger logger = LoggerFactory.getLogger(PayTransManagerImpl.class);

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TransactionDefinition txDefinition;

    @Autowired
    private PayTransferService payTransferService;

    @Autowired
    private PayTransferBatchService payTransferBatchService;

    @Autowired
    private SequencerGenerator sequencerGenerator;

    @Override
    public Result selectPayTransInfoByOutRef(List<String> recordList,
                                             int appId) {
        Result<List<PayTransfer>> result = ResultMap.build();
        List<PayTransfer> transferList = new ArrayList<PayTransfer>();
        try {
            transferList = payTransferService.queryByOutRefAndAppId(recordList, appId);
            //查询有记录，重复代付单号
            if (transferList.size() > 0)
                result.withError(ResultStatus.DB_REPEAT_ORDER_ERROR);
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            result.withError(ResultStatus.SYSTEM_DB_ERROR);
            return result;
        }
        return result;
    }

    /**
     * @param params
     * @return result
     * @Author huangguoqing
     * @MethodName doProcess
     * @Date 2015年6月11日
     * @Description:插入代付单以及代付单批次表
     */
    @Profiled(el = true, logger = "dbTimingLogger", tag = "PayTransManager_doProcess",
            timeThreshold = 100, normalAndSlowSuffixesEnabled = true)
    @Override
    public Result doProcess(PMap<String, String> params,List<String> payIdList) {
        Result result = ResultBean.build();
        TransactionStatus txStatus = null;
        try {
            //业务校验，是否重复的批次单以及代付单
            Result checkResult = bizCheck(params,payIdList);
            if(!Result.isSuccess(checkResult))
                return checkResult;
            txStatus = transactionManager.getTransaction(txDefinition);
            //插入代付单批次表
            List<Record> transferList = insertPayTransferBatch(params);
            //插入代付单表
            insertPayTransfer(params, transferList);
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.withError(ResultStatus.SYSTEM_DB_ERROR);
            if (txStatus != null) transactionManager.rollback(txStatus);
        }
        return result;
    }

    /**
     * @Author	huangguoqing 
     * @MethodName	bizCheck 
     * @param params
     * @param payIdList
     * @return boolean
     * @Date	2015年6月29日
     * @Description:校验是否有重复的单号
     */
    private Result bizCheck(PMap<String, String> params,List<String> payIdList) throws ServiceException{
        Result result = ResultBean.build();
        List<PayTransfer> transferList = new ArrayList<PayTransfer>();
        //1.检验是否有重复的批次号
        transferList = payTransferService.queryByBatchNo(params.getString("appId"),params.getString("batchNo"));
        if(null != transferList && transferList.size() > 0){
            logger.error("【代付请求】重复批次号提交失败！batchNo="+params.getString("batchNo"));
            result.withError(ResultStatus.DB_REPEAT_BATCHNO_ERROR);
            return result;
        }
        //2.校验是否有重复的代付单号
        transferList = payTransferService.queryByOutRefAndAppId(payIdList, params.getInt("appId"));
        if (null != transferList && transferList.size() > 0){
            logger.error("【代付请求】重复代付单提交失败！orderIdList="+payIdList.toString());
            result.withError(ResultStatus.DB_REPEAT_ORDER_ERROR);
        }
        return result;
    }

    /**
     *  业务线->代付查询
     * @param appId
     * @param batchNo
     * @return
     */
    @Override
    public AppXmlPacket queryByBatchNo(String appId,String batchNo) {

        AppXmlPacket appXmlPacket = new AppXmlPacket();
        try {
            PayTransferBatch payTransferBatch = payTransferBatchService.queryByBatchNo(appId,batchNo);
            //验证代付批次是否存在
            if (payTransferBatch == null) {
                logger.error("payTransferBatch is not found,batchNo = " + batchNo);
                appXmlPacket.withError(ResultStatus.PAY_TRANFER_BATCH_NOT_EXIST);
                return appXmlPacket;
            }
            //验证代发单是否存在
            List<PayTransfer> payTransferList = payTransferService.queryByBatchNo(appId,batchNo);
            if (CollectionUtils.isEmpty(payTransferList)) {
                logger.error("payTransfer is not found , batchNo = " + batchNo);
                appXmlPacket.withError(ResultStatus.PAY_TRANFER_NOT_EXIST);
                return appXmlPacket;
            }
            Map resultMap = new LinkedHashMap();
            //组装结果数据
            resultMap.put("batch_no", payTransferBatch.getBatchNo());
            resultMap.put("trade_status", String.valueOf(payTransferBatch.getTradeState()));
            resultMap.put("total_count", payTransferBatch.getPlanTotal());
            resultMap.put("total_amt", String.valueOf(payTransferBatch.getPlanAmt()));
            resultMap.put("succ_count", payTransferBatch.getSucTotal());
            resultMap.put("succ_amt", String.valueOf(payTransferBatch.getSucAmt()));
            resultMap.put("result_desc", payTransferBatch.getResultDesc());
            appXmlPacket.setResult(resultMap);
            Map resultDetail = null;
            //组装明细数据
            for (PayTransfer payTransfer : payTransferList) {
                resultDetail = new LinkedHashMap();
                resultDetail.put("pay_id", payTransfer.getOutRef());
                resultDetail.put("rec_bankacc", payTransfer.getRecBankAcc());
                resultDetail.put("rec_name", payTransfer.getRecName());
                resultDetail.put("pay_amt", String.valueOf(payTransfer.getPayAmt()));
                resultDetail.put("pay_status", String.valueOf(payTransfer.getPayStatus()));
                resultDetail.put("err_msg", payTransfer.getResultDesc());
                appXmlPacket.putResultDetail("result_detail", resultDetail);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            appXmlPacket.withError(ResultStatus.SYSTEM_DB_ERROR);
        }
        return appXmlPacket;
    }

    /**
     * 业务线->退票查询
     * @param startTime
     * @param endTime
     * @param recBankacc
     * @param recName
     * @return
     */
    @Override
    public AppXmlPacket queryRefund(String startTime, String endTime, String recBankacc, String recName) {

        AppXmlPacket appXmlPacket = new AppXmlPacket();
        try {
            Map resultMap = new LinkedHashMap();
            List<PayTransfer> payTransferList = payTransferService.queryRefund(startTime,endTime,recBankacc,recName);
            if (CollectionUtils.isEmpty(payTransferList)) {
                //组装结果数据
                resultMap.put("cancel_count", 0);
                appXmlPacket.setResult(resultMap);
                return appXmlPacket;
            }
            //组装结果数据
            resultMap.put("cancel_count", payTransferList.size());
            appXmlPacket.setResult(resultMap);
            Map resultDetail = null;
            //组装明细数据
            for (PayTransfer payTransfer : payTransferList) {
                resultDetail = new LinkedHashMap();
                resultDetail.put("pay_id", payTransfer.getOutRef());
                resultDetail.put("batch_no",payTransfer.getBatchNo());
                resultDetail.put("pay_amt", String.valueOf(payTransfer.getPayAmt()));
                resultDetail.put("cancel_time", DateUtil.formatTime(payTransfer.getModifyTime()));
                resultDetail.put("err_msg", payTransfer.getResultDesc());
                appXmlPacket.putResultDetail("cancel_rec", resultDetail);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            appXmlPacket.withError(ResultStatus.SYSTEM_DB_ERROR);
        }
        return appXmlPacket;
    }


    /**
     * @param param
     * @param recordList
     * @Author huangguoqing
     * @MethodName insertPayTransfer
     * @Date 2015年6月11日
     * @Description:插入代付单表
     */

    private void insertPayTransfer(PMap<String, String> param, List<Record> recordList) throws ServiceException {
        Date now = new Date();
        String batchNo = param.getString("batchNo");
        List<PayTransfer> transferList = new ArrayList<PayTransfer>();
        PayTransfer payTransfer = null;
        for (Record record : recordList) {
            String serialNo = sequencerGenerator.getPayTransferNo();
            payTransfer = new PayTransfer();
            payTransfer.setAppId(Integer.parseInt(param.getString("appId")));
            payTransfer.setBatchNo(batchNo);
            payTransfer.setOutRef(record.getPayId());
            payTransfer.setRecBankAcc(record.getRecBankacc());
            payTransfer.setRecName(record.getRecName());
            payTransfer.setPayAmt(new BigDecimal(record.getPayAmt()));
            payTransfer.setPayDesc(record.getDesc());
            payTransfer.setBankFlag(record.getBankFlg());
            payTransfer.setFee(BigDecimal.ZERO);
            payTransfer.setOtherBank(record.getEacBank());
            payTransfer.setOtherCity(record.getEacCity());
            payTransfer.setSerialNo(serialNo);
            payTransfer.setCreateTime(now);
            payTransfer.setModifyTime(now);
            transferList.add(payTransfer);
        }
        payTransferService.batchInsert(transferList);
    }

    /**
     * @param params
     * @Author huangguoqing
     * @MethodName insertPayTransferBatch
     * @Date 2015年6月11日
     * @Description:插入代付单批次表
     */
    private List<Record> insertPayTransferBatch(PMap<String, String> params) throws ServiceException {
        Date now = new Date();
        PayTransferBatch batch = new PayTransferBatch();
        batch.setAppId(Integer.parseInt(params.getString("appId")));
        batch.setBatchNo(params.getString("batchNo"));
        batch.setBbkNbr(params.getString("bbkNbr"));
        batch.setDbtAcc(params.getString("dbtAcc"));
        batch.setCompanyName(params.getString("companyName"));
        batch.setBusCod(Constant.BUS_COD_OTHER);//代发
        batch.setBusMod(Constant.BUS_MOD_1);//业务模式编码
        batch.setTrsTyp(Constant.PAY_OTHER);//代发其他
        batch.setMemo(params.getString("memo"));
        batch.setCreateTime(now);
        batch.setModifyTime(now);
        batch.setYurref(sequencerGenerator.getPayTransferYurref());
        //代发单明细
        BigDecimal plan_amt = new BigDecimal(0);
        List<JSONObject> jsonList = JsonUtil.jsonToBean(params.getString("recordList"), ArrayList.class);
        List<Record> recordList = new ArrayList<Record>();
        Record record = null;
        for (JSONObject o : jsonList) {
            record = JsonUtil.jsonToBean(o.toString(), Record.class);
            plan_amt = plan_amt.add(new BigDecimal(record.getPayAmt()));
            recordList.add(record);
        }
        batch.setPlanTotal(recordList.size());//计划代发单数
        batch.setPlanAmt(plan_amt);             //计划总金额
        payTransferBatchService.insert(batch);
        return recordList;
    }
}
