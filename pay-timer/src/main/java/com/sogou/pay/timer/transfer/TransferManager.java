package com.sogou.pay.timer.transfer;

import com.sogou.pay.common.types.*;
import com.sogou.pay.common.enums.PayTransferStatus;
import com.sogou.pay.common.enums.PayTransferBatchStatus;
import com.sogou.pay.timer.PayPortal;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.service.entity.PayTransfer;
import com.sogou.pay.service.entity.PayTransferBatch;
import com.sogou.pay.service.payment.PayTransferBatchService;
import com.sogou.pay.service.payment.PayTransferService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Component
public class TransferManager {

  private static final Logger logger = LoggerFactory.getLogger(TransferManager.class);

  @Autowired
  private PayTransferService payTransferService;

  @Autowired
  private PayTransferBatchService payTransferBatchService;

  @Autowired
  private PayPortal payPortal;

  public Result queryTransfer(String appId, String batchNo) {

    Result result = ResultBean.build();
    try {
      PayTransferBatch payTransferBatch = payTransferBatchService.queryByBatchNo(appId, batchNo);
      if (payTransferBatch == null) {
        logger.error("[queryTransfer] PayTransferBatch not exists, batchNo={}", batchNo);
        result.withError(ResultStatus.PAY_TRANSFER_BATCH_NOT_EXIST);
        return result;
      }
      result = queryTransfer(payTransferBatch);
    } catch (Exception ex) {
      logger.error("[queryTransfer] failed, {}", ex);
      result.withError(ResultStatus.SYSTEM_ERROR);
    }
    return result;
  }

  public Result queryTransfer(PayTransferBatch payTransferBatch) {
    ResultBean result = ResultBean.build();
    try {
      PMap params = BeanUtil.Bean2PMap(payTransferBatch);
      ResultMap resultMap = payPortal.queryTransfer(params);
      if (!Result.isSuccess(resultMap)) {
        logger.error("");
        return result.withError(ResultStatus.SYSTEM_ERROR);
      }
      PMap updatePayTransferBatch = (PMap) resultMap.getItem("result");

      String reqNbr = updatePayTransferBatch.getString("reqNbr");
      params.put("isDetail", true);
      params.put("reqNbr", reqNbr);
      resultMap = payPortal.queryTransfer(params);
      if (!Result.isSuccess(resultMap)) {
        logger.error("");
        return result.withError(ResultStatus.SYSTEM_ERROR);
      }
      List<PMap> updateList = (List<PMap>) resultMap.getItem("result");

      if (CollectionUtils.isNotEmpty(updateList)) {
        //修改
        updateBatchAndDetail(updateList, updatePayTransferBatch);
      }

    } catch (Exception ex) {
      logger.error("[queryTransfer] failed, {}", ex);
      result.withError(ResultStatus.SYSTEM_ERROR);
    }
    return result;
  }

  /**
   * 修改代付单批次及代付单 信息
   * 事务处理
   */
  @Transactional
  public void updateBatchAndDetail(List<PMap> updateList, PMap updatePayTransferBatch) {

    try {
      PayTransfer payTransfer = null;
      payTransferBatchService.updateTransferBatch(BeanUtil.Map2Bean(updatePayTransferBatch, PayTransferBatch.class));
      for (PMap updatePayTransfer : updateList) {
        //首选查询一遍，如果代付单状态为成功，要修改为失败，说明是退票
        payTransfer = payTransferService.queryBySerialNo(updatePayTransfer.getString("SerialNo"));
        if (payTransfer == null) {
          logger.warn("[updateBatchAndDetail] PayTransfer not exists, serialNo={}", updatePayTransfer.getString("SerialNo"));
          continue;
        }
        if (payTransfer.getPayStatus() != updatePayTransfer.getInt("PayStatus")) {
          if (payTransfer.getPayStatus() == PayTransferStatus.SUCCESS.getValue()
                  && updatePayTransfer.getInt("PayStatus") == PayTransferStatus.FAIL.getValue()) {
            payTransferService.updateStatusBySerialNo(updatePayTransfer.getString("SerialNo"),
                    PayTransferStatus.REFUND.getValue(), updatePayTransfer.getString("ResultDesc"));
          } else {
            payTransferService.updateStatusBySerialNo(updatePayTransfer.getString("SerialNo"),
                    updatePayTransfer.getInt("PayStatus"), updatePayTransfer.getString("ResultDesc"));
          }
        }
      }
    } catch (Exception ex) {
      logger.error("[updateBatchAndDetail] failed, {}", ex);
    }
  }

  public Result transfer(String appId, String batchNo) {
    logger.info("[transfer] begin, appId={}, batchNo={}, {}", appId, batchNo);
    ResultBean result = ResultBean.build();
    try {
      PayTransferBatch payTransferBatch = payTransferBatchService.queryByBatchNo(appId, batchNo);
      //验证代发单批次是否存在
      if (payTransferBatch == null) {
        logger.error("[transfer] PayTransferBatch not exists, appId={}, batchNo={}", appId, batchNo);
        result.withError(ResultStatus.PAY_TRANSFER_BATCH_NOT_EXIST);
        return result;
      }
      if (payTransferBatch.getTradeState() != PayTransferBatchStatus.FINAL_APPROVED.getValue()) {
        logger.error("[transfer] PayTransferBatch not approved, appId={}, batchNo={}", appId, batchNo);
        result.withError(ResultStatus.PAY_TRANSFER_BATCH_STATUS_NOT_AUDIT_PASS);
        return result;
      }
      //验证代发单是否存在
      List<PayTransfer> payTransferList = payTransferService.queryByBatchNo(appId, batchNo);
      if (CollectionUtils.isEmpty(payTransferList)) {
        logger.error("[transfer] PayTransfer not exists, appId={}, batchNo={}", appId, batchNo);
        result.withError(ResultStatus.PAY_TRANSFER_NOT_EXIST);
        return result;
      }

      if (payTransferBatch.getTradeState() == PayTransferBatchStatus.IN_PROCESSING.getValue()) {
        logger.error("[transfer] PayTransferBatch already in processing, appId={}, batchNo={}", appId, batchNo);
        result.withError(ResultStatus.PAY_TRANSFER_BATCH_ALREADY_SUBMITTED);
        return result;
      }
      PMap payTransferBatchPMap = BeanUtil.Bean2PMap(payTransferBatch);
      List<PMap> payTransferPMapList = new ArrayList<>();
      for (PayTransfer payTransfer : payTransferList)
        payTransferPMapList.add(BeanUtil.Bean2PMap(payTransfer));


      PMap params = new PMap();
      params.put("payTransferBatch", payTransferBatchPMap);
      params.put("payTransferList", payTransferPMapList);
      ResultMap resultMap = payPortal.queryTransfer(params);
      if (!Result.isSuccess(resultMap)) {
        logger.error("");
      }
      return resultMap;
    } catch (Exception e) {
      logger.error("[transfer] failed, appId={}, batchNo={}, {}", appId, batchNo, e);
      result.withError(ResultStatus.SYSTEM_ERROR);
    }
    logger.info("[transfer] finish, appId={}, batchNo={}, {}", appId, batchNo);
    return result;
  }

  /**
   * 判断是否有退票情况查询
   * 1.查询处理成功的批次单
   * 2.查询交易概要信息，判断成功笔数，成功金额是否有变化，有变化，说明有退票情况
   * 3.查询交易明细信息,找出付款失败的记录，判断该记录之前的状态是否成功，成功则为退票，修改记录为失败，修改代付单批次成功金额，成功笔数
   */
  public Result queryTransferRefund(String beginDate, String endDate) {
    try {
      PMap params = new PMap();
      params.put("beginDate", beginDate);
      params.put("endDate", endDate);
      ResultMap resultMap = payPortal.queryTransferRefund(params);
      if (!Result.isSuccess(resultMap)) {
        logger.error("");
        return resultMap;
      }

      List<String> yurrefList = (List<String>) resultMap.getItem("result");
      logger.info("[queryTransferRefund] found refund, yurrefList={}", yurrefList);
      PayTransferBatch payTransferBatch = null;
      if (CollectionUtils.isNotEmpty(yurrefList)) {
        for (String yurref : yurrefList) {
          payTransferBatch = payTransferBatchService.queryByYurref(yurref);
          if (payTransferBatch == null) {
            logger.info("[queryTransferRefund] PayTransferBatch not exists, yurref={}", yurref);
            continue;
          }
          queryTransfer(payTransferBatch);
        }
      }
    } catch (Exception ex) {
      logger.info("[queryTransferRefund] failed, {}", ex);
    }
    return ResultMap.build();
  }

}
