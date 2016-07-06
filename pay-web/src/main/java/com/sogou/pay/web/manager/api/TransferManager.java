package com.sogou.pay.web.manager.api;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.*;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.service.entity.PayTransfer;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.service.PayTransferBatchService;
import com.sogou.pay.service.service.PayTransferService;
import com.sogou.pay.service.utils.SequenceFactory;
import com.sogou.pay.web.form.TransferRecord;
import org.apache.commons.collections.CollectionUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TransferManager {

  private static final Logger logger = LoggerFactory.getLogger(TransferManager.class);

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Autowired
  private TransactionDefinition txDefinition;

  @Autowired
  private PayTransferService payTransferService;

  @Autowired
  private PayTransferBatchService payTransferBatchService;

  @Autowired
  private SequenceFactory sequencerGenerator;

  public ResultMap selectPayTransInfoByOutRef(List<String> recordList,
                                           int appId) {
    ResultMap<List<PayTransfer>> result = ResultMap.build();
    List<PayTransfer> transferList;
    try {
      transferList = payTransferService.queryByOutRefAndAppId(recordList, appId);
      //查询有记录，重复代付单号
      if (transferList.size() > 0)
        result.withError(ResultStatus.PAY_TRANSFER_ALREADY_EXIST);
    } catch (ServiceException e) {
      logger.error("[selectPayTransInfoByOutRef] failed, {}", e);
      result.withError(ResultStatus.SYSTEM_DB_ERROR);
      return result;
    }
    return result;
  }

  /**
   * 插入代付单以及代付单批次表
   */
  @Profiled(el = true, logger = "dbTimingLogger", tag = "PayTransManager_doProcess",
          timeThreshold = 100, normalAndSlowSuffixesEnabled = true)
  public ResultMap doProcess(PMap<String, String> params, List<String> payIdList) {
    ResultMap result = ResultMap.build();
    TransactionStatus txStatus = null;
    try {
      //业务校验，是否重复的批次单以及代付单
      ResultMap checkResult = bizCheck(params, payIdList);
      if (!Result.isSuccess(checkResult))
        return checkResult;
      txStatus = transactionManager.getTransaction(txDefinition);
      //插入代付单批次表
      List<TransferRecord> transferList = insertPayTransferBatch(params);
      //插入代付单表
      insertPayTransfer(params, transferList);
      transactionManager.commit(txStatus);
    } catch (Exception e) {
      logger.error("[doProcess] failed, {}", e);
      result.withError(ResultStatus.SYSTEM_DB_ERROR);
      if (txStatus != null) transactionManager.rollback(txStatus);
    }
    return result;
  }

  /**
   * 校验是否有重复的单号
   */
  private ResultMap bizCheck(PMap<String, String> params, List<String> payIdList) throws ServiceException {
    ResultMap result = ResultMap.build();
    List<PayTransfer> transferList;
    //1.检验是否有重复的批次号
    transferList = payTransferService.queryByBatchNo(params.getString("appId"), params.getString("batchNo"));
    if (null != transferList && transferList.size() > 0) {
      logger.error("【代付请求】重复批次号提交失败！batchNo=" + params.getString("batchNo"));
      result.withError(ResultStatus.PAY_TRANSFER_BATCH_ALREADY_EXIST);
      return result;
    }
    //2.校验是否有重复的代付单号
    transferList = payTransferService.queryByOutRefAndAppId(payIdList, params.getInt("appId"));
    if (null != transferList && transferList.size() > 0) {
      logger.error("【代付请求】重复代付单提交失败！orderIdList=" + payIdList.toString());
      result.withError(ResultStatus.PAY_TRANSFER_ALREADY_EXIST);
    }
    return result;
  }

  /**
   * 业务线->代付查询
   */
  public ResultMap queryByBatchNo(String appId, String batchNo) {
    ResultMap result = ResultMap.build();
    try {
      PayTransferBatch payTransferBatch = payTransferBatchService.queryByBatchNo(appId, batchNo);
      //验证代付批次是否存在
      if (payTransferBatch == null) {
        logger.error("payTransferBatch is not found,batchNo = " + batchNo);
        result.withError(ResultStatus.PAY_TRANSFER_BATCH_NOT_EXIST);
        return result;
      }
      //验证代发单是否存在
      List<PayTransfer> payTransferList = payTransferService.queryByBatchNo(appId, batchNo);
      if (CollectionUtils.isEmpty(payTransferList)) {
        logger.error("payTransfer is not found , batchNo = " + batchNo);
        result.withError(ResultStatus.PAY_TRANSFER_NOT_EXIST);
        return result;
      }
      //组装结果数据
      LinkedList resultDetails = new LinkedList();
      //组装明细数据
      for (PayTransfer payTransfer : payTransferList) {
        Map resultDetail = new LinkedHashMap();
        resultDetail.put("pay_id", payTransfer.getOutRef());
        resultDetail.put("rec_bankacc", payTransfer.getRecBankAcc());
        resultDetail.put("rec_name", payTransfer.getRecName());
        resultDetail.put("pay_amt", String.valueOf(payTransfer.getPayAmt()));
        resultDetail.put("pay_status", String.valueOf(payTransfer.getPayStatus()));
        resultDetail.put("err_msg", payTransfer.getResultDesc());
        resultDetails.add(resultDetail);
      }
      result.addItem("batch_no", payTransferBatch.getBatchNo());
      result.addItem("trade_status", String.valueOf(payTransferBatch.getTradeState()));
      result.addItem("total_count", payTransferBatch.getPlanTotal());
      result.addItem("total_amt", String.valueOf(payTransferBatch.getPlanAmt()));
      result.addItem("succ_count", payTransferBatch.getSucTotal());
      result.addItem("succ_amt", String.valueOf(payTransferBatch.getSucAmt()));
      result.addItem("result_desc", payTransferBatch.getResultDesc());
      result.addItem("result_detail", resultDetails);
    } catch (Exception e) {
      logger.error("[queryByBatchNo] failed, {}", e);
      result.withError(ResultStatus.SYSTEM_DB_ERROR);
    }
    return result;
  }

  /**
   * 业务线->退票查询
   */
  public ResultMap queryRefund(String startTime, String endTime, String recBankacc, String recName) {
    ResultMap result = ResultMap.build();
    try {
      List<PayTransfer> payTransferList = payTransferService.queryRefund(startTime, endTime, recBankacc, recName);
      if (CollectionUtils.isEmpty(payTransferList)) {
        //组装结果数据
        result.addItem("cancel_count", 0);
        return result;
      }
      //组装结果数据
      LinkedList resultDetails = new LinkedList();
      //组装明细数据
      for (PayTransfer payTransfer : payTransferList) {
        Map resultDetail = new LinkedHashMap();
        resultDetail.put("pay_id", payTransfer.getOutRef());
        resultDetail.put("batch_no", payTransfer.getBatchNo());
        resultDetail.put("pay_amt", String.valueOf(payTransfer.getPayAmt()));
        resultDetail.put("cancel_time", DateUtil.formatTime(payTransfer.getModifyTime()));
        resultDetail.put("cancel_res", payTransfer.getResultDesc());
        resultDetails.add(resultDetail);
      }
      result.addItem("cancel_count", payTransferList.size());
      result.addItem("cancel_set", resultDetails);
    } catch (Exception e) {
      logger.error("[queryRefund] failed, {}", e);
      result.withError(ResultStatus.SYSTEM_DB_ERROR);
    }
    return result;
  }


  /**
   * 插入代付单表
   */

  private void insertPayTransfer(PMap<String, String> param, List<TransferRecord> recordList) throws ServiceException {
    Date now = new Date();
    String batchNo = param.getString("batchNo");
    List<PayTransfer> transferList = new ArrayList<PayTransfer>();
    PayTransfer payTransfer = null;
    for (TransferRecord record : recordList) {
      String serialNo = sequencerGenerator.getTransferNo();
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
   * 插入代付单批次表
   */
  private List<TransferRecord> insertPayTransferBatch(PMap<String, String> params) throws ServiceException {
    Date now = new Date();
    PayTransferBatch batch = new PayTransferBatch();
    batch.setAppId(Integer.parseInt(params.getString("appId")));
    batch.setBatchNo(params.getString("batchNo"));
    batch.setBbkNbr(params.getString("bbkNbr"));
    batch.setDbtAcc(params.getString("dbtAcc"));
    batch.setCompanyName(params.getString("companyName"));
//    batch.setBusCod(Constant.BUS_COD_OTHER);//代发
//    batch.setBusMod(Constant.BUS_MOD_1);//业务模式编码
//    batch.setTrsTyp(Constant.PAY_OTHER);//代发其他
    batch.setMemo(params.getString("memo"));
    batch.setCreateTime(now);
    batch.setModifyTime(now);
    batch.setYurref(sequencerGenerator.getTransferBatchNo());
    //代发单明细
    BigDecimal plan_amt = new BigDecimal(0);
    List<Map> jsonList = JSONUtil.JSON2Bean(params.getString("recordList"), List.class);
    List<TransferRecord> recordList = new ArrayList<>();
    TransferRecord record = null;
    for (Map map : jsonList) {
      record = BeanUtil.Map2Bean(map, TransferRecord.class);
      plan_amt = plan_amt.add(new BigDecimal(record.getPayAmt()));
      recordList.add(record);
    }
    batch.setPlanTotal(recordList.size());//计划代发单数
    batch.setPlanAmt(plan_amt);             //计划总金额
    payTransferBatchService.insert(batch);
    return recordList;
  }
}
