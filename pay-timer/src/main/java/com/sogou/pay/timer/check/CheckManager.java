package com.sogou.pay.timer.check;

import com.sogou.pay.common.http.HttpService;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.service.enums.AccessPlatform;
import com.sogou.pay.timer.PayPortal;
import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.service.model.PayCheckUpdateModel;
import com.sogou.pay.service.model.PayNotifyModel;
import com.sogou.pay.service.entity.*;
import com.sogou.pay.service.enums.AgencyCode;
import com.sogou.pay.service.enums.CheckStatus;
import com.sogou.pay.service.enums.CheckLogStatus;
import com.sogou.pay.service.enums.OrderType;
import com.sogou.pay.service.service.*;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.model.OutCheckRecord;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Component
public class CheckManager {

  private static final Logger log = LoggerFactory.getLogger(CheckManager.class);
  /**
   * 每批次处理500条.
   */
  private static final int BATCH_SIZE = 500;
  /**
   * 对账类型
   */
  private List<Integer> checkList = new ArrayList<>();
  @Autowired
  private PayCheckDayLogService payCheckDayLogService;

  @Autowired
  private PayCheckService payCheckService;

  @Autowired
  private PayCheckWaitingService payCheckWaitingService;

  @Autowired
  private PayCheckResultService payCheckResultService;

  @Autowired
  private PayCheckFeeResultService payCheckFeeResultService;

  @Autowired
  private PayAgencyMerchantService payAgencyMerchantService;

  @Autowired
  private PayCheckDiffService payCheckDiffService;

  @Autowired
  private PayCheckFeeDiffService payCheckFeeDiffService;

  @Autowired
  private PayPortal checkPortal;

  @Autowired
  private AgencyInfoService agencyInfoService;

  {
    checkList.add(CheckType.PAID.getValue());
    checkList.add(CheckType.REFUND.getValue());
    checkList.add(CheckType.WITHDRAW.getValue());
  }

  @Value(value = "${repair.pay.url}")
  private String REPAIR_PAY_URL;

  @Value(value = "${repair.refund.url}")
  private String REPAIR_REFUND_URL;

  @Value(value = "${repair.withdraw.url}")
  private String REPAIR_WITHDRAW_URL;

  /**
   * 1. 根据checkDate、agencyCode、merchantNo查询对账日志
   * 2. 不存在对账日志，创建新日志记录
   * 3. 根据各渠道分别下载支付、退款对账数据，批量入库
   * 4. 更新对账日志状态为DOWNLOADSUCCESS
   */
  public void downloadOrder(Date checkDate, String agencyCode) {

    log.info("[downloadOrder] begin, checkDate={}, agencyCode={}", checkDate, agencyCode);

    PayCheckDayLog payCheckDayLog = null;
    try {

      //查询商户
      List<PayAgencyMerchant> payAgencyMerchants = payAgencyMerchantService.selectPayAgencyMerchants(agencyCode);
      if (payAgencyMerchants == null || payAgencyMerchants.size() == 0) {
        log.warn("[downloadOrder] PayAgencyMerchant not exists, agencyCode={}", agencyCode);
        return;
      }
      //查询支付机构
      AgencyInfo agencyInfo = agencyInfoService.getAgencyInfoByCode(agencyCode, AccessPlatform.ACCESSPLATFORM_PC);

      String checkDateStr = DateUtil.formatCompactDate(checkDate);
      payCheckDayLog = payCheckDayLogService.getByCheckDateAndAgency(checkDateStr, agencyCode);
      // 如果没有日志，插入一条新日志
      if (payCheckDayLog == null) {
        payCheckDayLog = new PayCheckDayLog();
        payCheckDayLog.setAgencyCode(agencyCode);
        payCheckDayLog.setStatus(CheckLogStatus.INIT.value());
        payCheckDayLog.setCheckDate(checkDateStr);
        payCheckDayLogService.insert(payCheckDayLog);
        payCheckDayLog = payCheckDayLogService.getByCheckDateAndAgency(checkDateStr, agencyCode);
      }

      for (PayAgencyMerchant payAgencyMerchant : payAgencyMerchants) {
        //删除已下载数据
        payCheckService.deleteInfo(checkDateStr, agencyCode, payAgencyMerchant.getMerchantNo());
        //开始下载
        downloadOrder(checkDate, payAgencyMerchant, agencyInfo);
      }

      log.info("[downloadOrder] finish, checkDate={}, agencyCode={}", checkDate, agencyCode);

      //修改日志状态为下载成功
      payCheckDayLogService.updateStatus(payCheckDayLog.getId(), CheckLogStatus.DOWNLOADSUCCESS.value(),
              payCheckDayLog.getVersion(), CheckLogStatus.DOWNLOADSUCCESS.name());

    } catch (Exception e) {
      log.info("[downloadOrder] failed, {}", e);
      try {
        payCheckDayLogService.updateStatus(payCheckDayLog.getId(), CheckLogStatus.FAIL.value(),
                payCheckDayLog.getVersion(), CheckLogStatus.FAIL.name());
      } catch (ServiceException se) {
        log.info("[downloadOrder] failed, {}", se);
      }
    }
  }

  /**
   * 1.对账并更新相关的对账状态
   * 2.更新对账日志状态为SUCCESS
   */
  public void checkOrder(Date checkDate, String agencyCode) {

    log.info("[checkOrder] begin, checkDate={}, agencyCode={}", checkDate, agencyCode);

    PayCheckDayLog payCheckDayLog = null;
    try {
      String checkDateStr = DateUtil.format(checkDate, DateUtil.DATE_FORMAT_DAY_SHORT);

      payCheckDayLog = payCheckDayLogService.getByCheckDateAndAgency(checkDateStr, agencyCode);

      //验证对账文件下载
      if (payCheckDayLog == null || payCheckDayLog.getStatus() != CheckLogStatus.DOWNLOADSUCCESS.value()) {
        log.warn("[checkOrder] PayCheckDayLog not exists, checkDate={}, agencyCode={}", checkDate, agencyCode);
        return;
      }
      /**
       * 根据业务编码勾兑
       * 1. 支付
       * 2. 退款
       */
      int total = 0;
      for (int checkType : checkList) {

        int page = 0;
        int size = 0;
        do {
          // 查询指定渠道、日期范围内，未对账成功的记录，每次查500条
          List<Map<String, Object>> bills = payCheckService.queryByMerAndDateAndCheckType(checkDateStr, agencyCode, checkType,
                  page * BATCH_SIZE, BATCH_SIZE);
          size = bills.size();
          if (size > 0) {
            doBatchUpdate(bills);
            total += size;
          }
          page++;
        } while (size >= BATCH_SIZE);// 查询结果数量小于每批次的数量，说明已经是最后一页了
      }

      // 刷新统计数据
      if (total > 0) {
        //自动勾稽处理
        updatePayCheckDiff();
        //刷新对账结果数据
        updatePayCheckResult(checkDate, agencyCode);
        //财付通不对手续费
        if (!AgencyCode.TENPAY.name().equals(agencyCode)) {
          //刷新手续费对账结果数据
          updatePayCheckFeeResult(checkDate, agencyCode);
        }
      }

      //修改对账日志为对账成功
      payCheckDayLogService.updateStatus(payCheckDayLog.getId(), CheckLogStatus.SUCCESS.value(),
              payCheckDayLog.getVersion(), CheckLogStatus.SUCCESS.name());

      log.info("[checkOrder] finish, checkDate={}, agencyCode={}, total={}", checkDate, agencyCode, total);
    } catch (Exception ex) {
      log.info("[checkOrder] failed, {}", ex);
      try {
        payCheckDayLogService.updateStatus(payCheckDayLog.getId(), CheckLogStatus.FAIL.value(), payCheckDayLog.getVersion(), CheckLogStatus.FAIL.name());
      } catch (ServiceException se) {
        log.info("[checkOrder] failed, {}", se);
      }
    }
  }

  /**
   * 对账业务处理
   */
  @Transactional(value = "transactionManager")
  public void doBatchUpdate(List<Map<String, Object>> bills) throws Exception {

    List<PayCheckUpdateModel> payCheckUpdates = new ArrayList<>();

    List<PayCheckUpdateModel> payCheckWaitingUpdates = new ArrayList<>();

    for (Map<String, Object> bill : bills) {

      PayCheckUpdateModel payCheckUpdateModel = new PayCheckUpdateModel();

      String instructId = (String) bill.get("instruct_id");
      payCheckUpdateModel.setInstructId(instructId);

      // 对账金额
      BigDecimal pcAmt = new BigDecimal(bill.get("pc_amt").toString());

      // 待对账算金额
      BigDecimal pcwAmt = bill.get("pcw_amt") == null ? null : new BigDecimal(bill.get("pcw_amt").toString());

      // 对账ID
      long payCheckId = Long.valueOf(bill.get("pay_check_id").toString());
      payCheckUpdateModel.setPayCheckId(payCheckId);

      int checkType = Integer.valueOf(bill.get("check_type").toString());

      //在t_pay_check_waiting表中缺失记录，检查是否丢单
      if (pcwAmt == null) {
        PayCheck payCheck = payCheckService.getByInstructIdAndCheckType(instructId, checkType);
        //补单
        Result result = repairBill(payCheck);
        if (Result.isSuccess(result)) {
          PayCheckWaiting payCheckWaiting = payCheckWaitingService.getByInstructId(instructId);
          if (payCheckWaiting != null) {
            pcwAmt = payCheckWaiting.getBizAmt();
          }
        }
      }

      if (pcwAmt == null) {
        //补单失败
        log.warn("[doBatchUpdate] repair process error! instructId=" + instructId);
        payCheckUpdateModel.setPayCheckStatus(CheckStatus.LOST.getValue());
        payCheckUpdates.add(payCheckUpdateModel);
      } else {
        //对账与待对账记录匹配，比较金额
        if (pcAmt.compareTo(pcwAmt) == 0) {
          // 金额匹配
          payCheckUpdateModel.setPayCheckStatus(CheckStatus.SUCCESS.getValue());
          payCheckUpdateModel.setPayCheckWaitingStatus(CheckStatus.SUCCESS.getValue());
        } else {
          // 金额不匹配
          payCheckUpdateModel.setPayCheckStatus(CheckStatus.UNBALANCE.getValue());
          payCheckUpdateModel.setPayCheckWaitingStatus(CheckStatus.UNBALANCE.getValue());
        }
        payCheckUpdates.add(payCheckUpdateModel);
        payCheckWaitingUpdates.add(payCheckUpdateModel);
      }
    }

    if (payCheckUpdates.size() != 0) {
      payCheckService.batchUpdateStatus(payCheckUpdates);
    }
    if (payCheckWaitingUpdates.size() != 0) {
      payCheckWaitingService.batchUpdateStatus(payCheckWaitingUpdates);
    }
  }

  /**
   * 补单
   **/
  private Result repairBill(PayCheck payCheck) {
    PMap params=null;
    String url=null;
    if (payCheck.getCheckType() == OrderType.PAY.getValue()) {
      //支付补单
      PayNotifyModel payNotifyModel = new PayNotifyModel();
      payNotifyModel.setPayDetailId(payCheck.getInstructId());
      payNotifyModel.setAgencyOrderId(payCheck.getOutOrderId());
      payNotifyModel.setAgencyPayTime(payCheck.getOutTransTime());
      payNotifyModel.setTrueMoney(payCheck.getBizAmt());
      params = BeanUtil.Bean2PMap(payNotifyModel);
      url = REPAIR_PAY_URL;
    } else if (payCheck.getCheckType() == OrderType.REFUND.getValue()) {
      //退款补单
      params = new PMap<String, Object>();
      params.put("reqId", payCheck.getInstructId());
      params.put("agencyRefundId", payCheck.getOutOrderId());
      params.put("refundMoney", payCheck.getBizAmt().toString());
      params.put("refundStatus", "SUCCESS");
      url = REPAIR_REFUND_URL;
    } else if (payCheck.getCheckType() == OrderType.WITHDRAW.getValue()) {
      //提现补单
      params = new PMap<String, Object>();
      params.put("instructId", payCheck.getInstructId());
      params.put("outOrderId", payCheck.getOutOrderId());
      params.put("outTransTime", payCheck.getOutTransTime());
      params.put("bizAmt", payCheck.getBizAmt());
      params.put("accessPlatform", 1);
      params.put("appId", 0);
      params.put("agencyCode", payCheck.getAgencyCode());
      params.put("merchantNo", payCheck.getMerchantNo());
      params.put("payType", 99);
      params.put("bankCode", payCheck.getAgencyCode());
      url = REPAIR_WITHDRAW_URL;
    }
    Result result = HttpService.getInstance().doPost(url, params, null, null);
    if(!Result.isSuccess(result)){
      log.error("[repairBill] http request failed, url={}, params={}", url, JSONUtil.Bean2JSON(params));
    }
    return result;
  }

  /**
   * 1.查询未处理的差异信息
   * 2.自动处理
   */
  @Transactional(value = "transactionManager")
  public void updatePayCheckDiff() throws Exception {

    int count = payCheckDiffService.selectUnResolvedCount();
    if (count > 0) {
      List<PayCheckDiff> diffList = payCheckDiffService.selectUnResolvedList();
      for (PayCheckDiff payCheckDiff : diffList) {
        PayCheck payCheck = payCheckService.getByInstructIdAndCheckType(payCheckDiff.getInstructId(), payCheckDiff.getCheckType());
        if (payCheck != null && payCheck.getStatus() == CheckStatus.SUCCESS.getValue()) {
          payCheckDiffService.updateStatus(payCheckDiff.getId(), 1, "auto handle success");
        }
      }
    }
  }

  /**
   * 生成对账结果数据
   */
  @Transactional(value = "transactionManager")
  public void updatePayCheckResult(Date checkDate, String agencyCode) throws Exception {
    String checkDateStr = DateUtil.format(checkDate, DateUtil.DATE_FORMAT_DAY_SHORT);

    //先删除历史统计
    payCheckResultService.delete(checkDateStr, agencyCode);
    //插入最新统计
    payCheckResultService.insert(checkDateStr, agencyCode);

    List<PayCheckResult> payCheckResultList = payCheckResultService.queryByDateAndAgency(checkDateStr, agencyCode);

    for (PayCheckResult payCheckResult : payCheckResultList) {

      BigDecimal outTotalAmt = payCheckResult.getOutTotalAmt();
      int outTotalNum = payCheckResult.getOutTotalNum();
      BigDecimal totalAmt = payCheckResult.getTotalAmt();
      int totalNum = payCheckResult.getTotalNum();

      int status = 0;
      if (outTotalNum == totalNum && outTotalAmt.compareTo(totalAmt) == 0) {
        //对账成功
        status = CheckStatus.SUCCESS.getValue();
      } else {
        //对账失败
        status = CheckStatus.UNBALANCE.getValue();
      }
      payCheckResultService.updateStatus(payCheckResult.getId(), status);
    }
    //生成对账差异信息
    payCheckDiffService.delete(checkDateStr, agencyCode);
    payCheckDiffService.insertAmtDiff(checkDateStr, agencyCode);
    payCheckDiffService.insertOutMoreDiff(checkDateStr, agencyCode);
    payCheckDiffService.insertOutLessDiff(checkDateStr, agencyCode);
  }

  /**
   * 修改手续费对账结果
   */
  @Transactional(value = "transactionManager")
  private void updatePayCheckFeeResult(Date checkDate, String agencyCode) throws Exception {
    String checkDateStr = DateUtil.format(checkDate, DateUtil.DATE_FORMAT_DAY_SHORT);

    //先删除历史统计
    payCheckFeeResultService.delete(checkDateStr, agencyCode);
    //插入最新统计
    payCheckFeeResultService.insert(checkDateStr, agencyCode);

    List<PayCheckFeeResult> payCheckFeeResultList = payCheckFeeResultService.queryByDateAndAgency(checkDateStr, agencyCode);

    for (PayCheckFeeResult payCheckFeeResult : payCheckFeeResultList) {

      BigDecimal outTotalFee = payCheckFeeResult.getOutTotalFee().setScale(2, BigDecimal.ROUND_HALF_UP);
      int outTotalNum = payCheckFeeResult.getOutTotalNum();
      BigDecimal totalFee = payCheckFeeResult.getTotalFee().setScale(2, BigDecimal.ROUND_HALF_UP);
      int totalNum = payCheckFeeResult.getTotalNum();

      int status = 0;
      if (outTotalNum == totalNum && outTotalFee.compareTo(totalFee) == 0) {
        //对账成功
        status = CheckStatus.SUCCESS.getValue();
      } else {
        //对账失败
        status = CheckStatus.UNBALANCE.getValue();
      }
      payCheckFeeResultService.updateFeeStatus(payCheckFeeResult.getId(), status);
    }

    //生成手续费差异信息
    payCheckFeeDiffService.delete(checkDateStr, agencyCode);
    payCheckFeeDiffService.insertFeeDiff(checkDateStr, agencyCode);
  }


  /**
   * 下载并保存对账数据
   */
  private void downloadOrder(Date checkDate, PayAgencyMerchant payAgencyMerchant, AgencyInfo agencyInfo)
          throws Exception {

    HashMap<String, CheckType> checkTypes = new HashMap<>();
    checkTypes.put("payRecords", CheckType.PAID);//获取支付记录
    checkTypes.put("refRecords", CheckType.REFUND);//获取退款记录
    checkTypes.put("feeRecords", CheckType.CHARGED);//获取支付宝手续费
    checkTypes.put("cashRecords", CheckType.WITHDRAW);//获取提现记录

    int pageNo = 1;
    boolean hasNext = true;

    String merchantNo = payAgencyMerchant.getMerchantNo();
    String agencyCode = payAgencyMerchant.getAgencyCode();
    String checkDateStr = DateUtil.format(checkDate, DateUtil.DATE_FORMAT_DAY_SHORT);

    PMap params = new PMap();
    params.put("agencyCode", agencyCode);
    params.put("checkDate", checkDate);
    params.put("checkType", CheckType.ALL);
    params.put("merchantNo", merchantNo);
    params.put("downloadUrl", agencyInfo.getDownloadUrl());
    params.put("md5securityKey", payAgencyMerchant.getEncryptKey());
    params.put("publicCertFilePath", payAgencyMerchant.getPubKeypath());
    params.put("privateCertFilePath", payAgencyMerchant.getPrivateKeypath());
    params.put("sellerEmail", payAgencyMerchant.getSellerEmail());
    params.put("pageSize", BATCH_SIZE);

    while (hasNext) {
      params.put("pageNo", pageNo);

      ResultMap result = checkPortal.downloadOrder(params);
      if (!Result.isSuccess(result)) {
        log.error("[downloadOrder] 下载对账单失败, 参数: checkDate={}, agencyCode={}, merchantNo={}",
                checkDate, agencyCode, merchantNo);
        throw new Exception(result.getStatus().getMessage());
      }

      for (Map.Entry<String, CheckType> entry : checkTypes.entrySet()) {
        String name = entry.getKey();
        List<OutCheckRecord> records = (List<OutCheckRecord>) result.getItem(name);
        //批量插入
        if (CollectionUtils.isNotEmpty(records)) {
          CheckType checkType = entry.getValue();
          if (checkType == CheckType.CHARGED) {
            payCheckService.batchUpdateFee(records);
          } else {
            doBatchInsert(checkDateStr, agencyCode, merchantNo, checkType.getValue(), records);
          }
        }
      }
      //是否有下一页
      hasNext = (boolean) result.getItem("hasNextPage");
      pageNo++;
    }
  }


  /**
   * 批量插入数据库
   */
  private void doBatchInsert(String checkDate, String agencyCode, String merchantNo, int checkType, List<OutCheckRecord> outRecords)
          throws Exception {
    int i = 0;
    List<PayCheck> payCheckList = new ArrayList<>();
    // 解析成功，批量插入数据库
    PayCheck payCheck = null;
    for (OutCheckRecord outRecord : outRecords) {
      payCheck = new PayCheck();
      payCheck.setInstructId(outRecord.getPayNo());
      payCheck.setOutOrderId((outRecord.getOutPayNo()));
      payCheck.setCheckType(checkType);
      payCheck.setOutTransTime(outRecord.getOutTransTime());
      payCheck.setBizAmt(outRecord.getMoney());
      payCheck.setCommissionFeeAmt(outRecord.getCommssionFee());
      payCheck.setCheckDate(checkDate);
      payCheck.setAgencyCode(agencyCode);
      payCheck.setMerchantNo(merchantNo);
      payCheck.setBalance(outRecord.getBalance());
      payCheckList.add(payCheck);
      i++;
      if (i % BATCH_SIZE == 0) {
        payCheckService.batchInsert(payCheckList);
        payCheckList.clear();
      }
    }
    if (payCheckList.size() != 0) {
      payCheckService.batchInsert(payCheckList);
    }
  }
}
