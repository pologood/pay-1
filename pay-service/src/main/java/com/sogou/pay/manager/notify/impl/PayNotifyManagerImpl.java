package com.sogou.pay.manager.notify.impl;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.model.notify.PayNotifyModel;
import com.sogou.pay.manager.model.thirdpay.FairAccRefundModel;
import com.sogou.pay.manager.notify.PayNotifyManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.service.connect.QueueNotifyProducer;
import com.sogou.pay.service.entity.*;
import com.sogou.pay.service.enums.PayOrderStatus;
import com.sogou.pay.service.payment.*;
import com.sogou.pay.service.utils.Constant;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName PayNotifyManagerImpl
 * @Date 2015年04月09日
 * @Description:
 */
@Service
public class PayNotifyManagerImpl implements PayNotifyManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(PayNotifyManagerImpl.class);

  private static final String REFUND_CODE = "9";

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Autowired
  private TransactionDefinition txDefinition;

  @Autowired
  private PayOrderService payOrderService;

  @Autowired
  private PayReqDetailService payReqDetailService;

  @Autowired
  private PayResDetailService payResDetailService;

  @Autowired
  private PayFeeService payFeeService;

  @Autowired
  private AppService appService;

  @Autowired
  private PayAgencyMerchantService payAgencyMerchantService;

  @Autowired
  private QueueNotifyProducer queueNotifyProducer;

  @Autowired
  private PayCheckWaitingService payCheckWaitingService;

  @Autowired
  private PayOrderRelationService payOrderRelationService;

  @Autowired
  private PayResIdService payResIdService;

  @Autowired
  private SecureManager secureManager;

  @Autowired
  private AgencyInfoService agencyInfoService;

  /**
   * 业务上校验回调参数是否合法，包括
   * 1.验证
   * 2.业务勾兑
   * 3.通知
   *
   * @param payNotifyModel
   * @return
   */
  @Override
  public ResultMap doProcess(PayNotifyModel payNotifyModel) {
    ResultMap result = ResultMap.build();
    String payReqId = payNotifyModel.getPayDetailId();
    try {
      LOGGER.info("[doProcess] 处理第三方异步通知: {}", payNotifyModel.toString());
      //将回调ID插入排重表
      payResIdService.insertPayResId(payReqId);
      PayReqDetail payReqDetail = payReqDetailService.selectPayReqDetailById(payReqId);
      if (null == payReqDetail) {
        LOGGER.error("[doProcess] 查询PayReqDetail失败: {}", payNotifyModel.toString());
        result.withError(ResultStatus.PAY_ORDER_NOT_EXIST);
        return result;
      }
      //获取支付单号
      String payId = payOrderRelationService.selectPayOrderId(payReqId);
      PayOrderInfo payOrderInfo = payOrderService.selectPayOrderById(payId);
      if (null == payOrderInfo) {
        LOGGER.error("[doProcess] 查询PayOrderInfo失败: {}", payNotifyModel.toString());
        result.withError(ResultStatus.PAY_ORDER_NOT_EXIST);
        return result;
      }
      //校验
      Integer flag = validateOrder(payNotifyModel, payOrderInfo, payReqDetail);
      if (0 == flag) {
        //业务勾兑
        bizCheck(payNotifyModel, payOrderInfo, payReqDetail);
        LOGGER.info("[doProcess] bizCheck成功: {}", payNotifyModel.toString());
        //写入队列以通知业务线
        writeQueue(payOrderInfo, payNotifyModel);
        LOGGER.info("[doProcess] writeQueue成功: {}", payNotifyModel.toString());
      } else {
        //业务勾兑
        bizCheck(payNotifyModel, payOrderInfo, payReqDetail, REFUND_CODE);
        LOGGER.info("[doProcess] bizCheck成功: {}" + payNotifyModel.toString());
        FairAccRefundModel model = new FairAccRefundModel();
        model.setAppId(payOrderInfo.getAppId());
        model.setOrderId(payOrderInfo.getOrderId());
        model.setPayId(payOrderInfo.getPayId());
        model.setPayDetailId(payReqDetail.getPayDetailId());
        result.addItem("fairAccRefundModel", model);
      }
      result.withReturn(flag);
    } catch (DuplicateKeyException e) {
      //支付成功，重复通知，丢弃该通知
      LOGGER.error("[doProcess] 重复的第三方异步通知, 丢弃: {}", payNotifyModel.toString());
      result.withError(ResultStatus.RES_DETAIL_ALREADY_EXIST);
    } catch (Exception e) {
      LOGGER.error("[doProcess] 处理第三方异步通知失败: {}, {}", payNotifyModel.toString(), e.getMessage());
      result.withError(ResultStatus.REPAIR_ORDER_ERROR);
    }
    return result;
  }

  /**
   * 验证
   * 1.请求订单是否支付成功的前置状态
   * 2.金额一致性校验
   *
   * @return 0:验证通过 1：重复支付，需要平账退款
   */
  public int validateOrder(PayNotifyModel payNotifyModel, PayOrderInfo payOrderInfo, PayReqDetail payReqDetail) throws Exception {
    //支付单状态 验证
    int payStatus = payOrderInfo.getPayOrderStatus();
    //根据payReqDetail查询支付回调表
    if (payStatus == PayOrderStatus.SUCCESS.getValue()) {
      // 重复支付，该笔支付流水单需要平账退款
      return 1;
    }
    //金额校验
    BigDecimal trueMoney = payReqDetail.getTrueMoney();
    BigDecimal trueMoney2 = payNotifyModel.getTrueMoney();
    if (trueMoney.compareTo(trueMoney2) != 0) { //BigDecimal不能用equals
      LOGGER.error("[validateOrder] 金额不相等, payReqId={}, " +
                      "PayReqDetail.trueMoney={}, payNotifyModel.trueMoney={}"
              , payReqDetail.getPayDetailId(), trueMoney, trueMoney2);
      throw new Exception("biz check error: money not equal");
    }
    return 0;
  }

  /**
   * 业务勾兑
   * 在一个事务中
   *
   * @param payNotifyModel
   * @param payReqDetail
   * @param refundCode     是否需要平账退款
   * @throws Exception
   */

  public void bizCheck(PayNotifyModel payNotifyModel, PayOrderInfo payOrderInfo, PayReqDetail payReqDetail, String... refundCode) throws Exception {
    TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
    try {
      //更新支付单数据
      //根据pay_detail_id查询pay_id
      String payId = payOrderRelationService.selectPayOrderId(payNotifyModel.getPayDetailId());
      payOrderService.updatePayOrderByPayId(payId, payReqDetail.getBankCode(), PayOrderStatus.SUCCESS.getValue(), payNotifyModel.getAgencyPayTime());

      if (null != refundCode && refundCode.length > 0) {
        //平账退款更新支付单支付流水关联表、插入响应流水
        payOrderRelationService.updatePayOrderRelation(Constant.PAY_REFUND_3, payReqDetail.getPayDetailId());
        insertResDetail(payNotifyModel, payReqDetail, payOrderInfo, refundCode[0]);
      } else {
        //支付成功更新支付单支付流水关联表、插入响应流水
        payOrderRelationService.updatePayOrderRelation(Constant.PAY_SUCCESS_1, payReqDetail.getPayDetailId());
        insertResDetail(payNotifyModel, payReqDetail, payOrderInfo, null);
      }

      //生成对账单
      insertPayCheckWaiting(payNotifyModel, payOrderInfo, payReqDetail);

      transactionManager.commit(txStatus);
    } catch (Exception e) {
      if (txStatus != null)
        transactionManager.rollback(txStatus);
    }
  }

  public void insertResDetail(PayNotifyModel payNotifyModel, PayReqDetail payReqDetail,
                              PayOrderInfo payOrderInfo, String refundCode) throws Exception {

    PayResDetail payResDetail = new PayResDetail();
    //判断是否支付宝快捷支付信用卡
    boolean isAlipayCredit = isAlipayDirectCredit(payNotifyModel);
    if (isAlipayCredit) {
      //支付宝快捷支付信用卡专用计算手续费
      BigDecimal fee = payNotifyModel.getTrueMoney().multiply(Constant.FEE_RATE).setScale(2, BigDecimal.ROUND_HALF_UP);
      payResDetail.setPayFee(fee);
      payResDetail.setFeeRate(Constant.FEE_RATE);
    } else {
      //计算手续费
      PMap<String, BigDecimal> fee = computerFee(payNotifyModel, payOrderInfo, payReqDetail);
      if (!fee.isEmpty()) {
        payResDetail.setPayFee(fee.get("fee"));
        payResDetail.setFeeRate(fee.get("feeRate"));
      }
    }
    //组装回调流水对象
    payResDetail.setPayDetailId(payNotifyModel.getPayDetailId());
    payResDetail.setAgencyOrderId(payNotifyModel.getAgencyOrderId());
    payResDetail.setBankOrderId(payNotifyModel.getBankOrderId());
    payResDetail.setPayStatus(1);
    payResDetail.setAgencyPayTime(payNotifyModel.getAgencyPayTime());
    payResDetail.setTrueMoney(payNotifyModel.getTrueMoney());
    payResDetail.setRefundCode(refundCode);
    payResDetail.setBankCode(payReqDetail.getBankCode());
    payResDetail.setAccessPlatform(payReqDetail.getAccessPlatform());
    payResDetail.setPayFeeType(payReqDetail.getPayFeeType());
    payResDetail.setBalance(payReqDetail.getBalance());
    payResDetail.setAgencyCode(payReqDetail.getAgencyCode());
    payResDetail.setBankCardType(payReqDetail.getBankCardType());
    payResDetail.setMerchantNo(payReqDetail.getMerchantNo());
    payResDetailService.insertPayResDetail(payResDetail);
  }

  //判断是否支付宝快捷支付信用卡
  private boolean isAlipayDirectCredit(PayNotifyModel payNotifyModel) {
    if (!StringUtils.isEmpty(payNotifyModel.getChannelType())
            && (Constant.CREDIT_CARTOON.equals(payNotifyModel.getChannelType())
            || Constant.OPTIMIZED_MOTO.equals(payNotifyModel.getChannelType())
            || Constant.MOTO_CREDIT_CARD.equals(payNotifyModel.getChannelType())
            || Constant.BIGAMOUNT_CREDIT_CARTOON.equals(payNotifyModel.getChannelType())
            || Constant.CREDIT_EXPRESS_INSTALLMENT.equals(payNotifyModel.getChannelType())))
      return true;
    return false;
  }

  //计算手续费
  public PMap<String, BigDecimal> computerFee(PayNotifyModel payNotifyModel, PayOrderInfo payOrderInfo, PayReqDetail payReqDetail) throws Exception {


    PayAgencyMerchant payAgencyMerchant = new PayAgencyMerchant();

    payAgencyMerchant.setAgencyCode(payReqDetail.getAgencyCode());
    payAgencyMerchant.setAppId(payOrderInfo.getAppId());

    App app = appService.selectApp(payOrderInfo.getAppId());
    payAgencyMerchant.setCompanyCode(app.getBelongCompany());
    PayAgencyMerchant pam = payAgencyMerchantService.selectPayAgencyMerchant(payAgencyMerchant);
    String merchantNo = pam.getMerchantNo();

    int payFeeType = payReqDetail.getPayFeeType();
    BigDecimal payAmount = payNotifyModel.getTrueMoney();
    int accessPlatform = payOrderInfo.getAccessPlatForm();
    PMap<String, BigDecimal> feeMap = payFeeService.getPayFee(payAmount, merchantNo, payFeeType, accessPlatform);

    LOGGER.info("[computerFee] 计算手续费结果: fee={}, feeRate={}", feeMap.get("fee"), feeMap.get("feeRate"));
    return feeMap;
  }

  /**
   * 生成对账单
   *
   * @param payNotifyModel
   * @param payOrderInfo
   * @param payReqDetail
   * @throws Exception
   */
  public void insertPayCheckWaiting(PayNotifyModel payNotifyModel, PayOrderInfo payOrderInfo, PayReqDetail payReqDetail) throws Exception {

    PayCheckWaiting payCheckWaiting = payCheckWaitingService.getByInstructId(payReqDetail.getPayDetailId());
    if (payCheckWaiting == null) {
      payCheckWaiting = new PayCheckWaiting();
      PayResDetail payResDetail = payResDetailService.selectPayResById(payReqDetail.getPayDetailId());
      payCheckWaiting.setOutOrderId(payResDetail.getAgencyOrderId());//第三方流水号
      payCheckWaiting.setInstructId(payResDetail.getPayDetailId());//请求流水号
      payCheckWaiting.setBizCode(CheckType.PAID.getValue());//业务代码 1.支付、2.充值、3.退款
      payCheckWaiting.setOutTransTime(payResDetail.getAgencyPayTime());//交易时间
      payCheckWaiting.setBizAmt(payResDetail.getTrueMoney());//交易金额
      payCheckWaiting.setFeeRate(payResDetail.getFeeRate());//费率
      payCheckWaiting.setCommissionFeeAmt(payResDetail.getPayFee());//交易手续费
      payCheckWaiting.setAccessPlatform(payResDetail.getAccessPlatform());//接入平台
      payCheckWaiting.setAppId(payOrderInfo.getAppId());//应用id
      String date = DateUtil.formatCompactDate(payNotifyModel.getAgencyPayTime());
      payCheckWaiting.setCheckDate(date);//对账日期
      payCheckWaiting.setAgencyCode(payResDetail.getAgencyCode());//机构编码
      payCheckWaiting.setMerchantNo(payResDetail.getMerchantNo());//商户号
      payCheckWaiting.setBankCode(payResDetail.getBankCode());//银行编码
      payCheckWaiting.setPayType(payResDetail.getPayFeeType());//支付方式
      payCheckWaitingService.insert(payCheckWaiting);
    }
  }

  /**
   * 消息队列
   *
   * @param payOrderInfo
   */
  public void writeQueue(PayOrderInfo payOrderInfo, PayNotifyModel patyNotifyModel) {
    PMap<String, String> map = new PMap<String, String>();
    map.put("isSuccess", "T");
    map.put("appId", payOrderInfo.getAppId().toString());
    map.put("signType", "0");
    map.put("orderId", payOrderInfo.getOrderId());
    map.put("payId", payOrderInfo.getPayId());
    map.put("orderMoney", payOrderInfo.getOrderMoney().toString());
    map.put("tradeStatus", "SUCCESS");
    map.put("successTime", (new SimpleDateFormat("yyyyMMddHHssmm")).format(patyNotifyModel.getAgencyPayTime()));
    ResultMap result = (ResultMap) secureManager.appSign(map);
    Map resultMap = (Map) result.getReturnValue();
    resultMap.put("appBgUrl", payOrderInfo.getAppBgUrl());
    queueNotifyProducer.sendPayMessage(resultMap);
  }


  @Override
  public ResultMap<PMap<String, String>> getQueryOrderParam(Map map) {
    ResultMap<PMap<String, String>> result = ResultMap.build();
    App app = appService.selectApp(Integer.parseInt(map.get("appId").toString()));
    PayAgencyMerchant merchant = new PayAgencyMerchant();
    merchant.setAgencyCode(Constant.WECHAT);
    merchant.setAppId(app.getAppId());
    merchant.setCompanyCode(app.getBelongCompany());
    merchant = payAgencyMerchantService.selectPayAgencyMerchant(merchant);

    AgencyInfo agencyInfo = agencyInfoService.getAgencyInfoByCode(Constant.WECHAT,
            "99", "99");
    if(agencyInfo==null){
      LOGGER.error("[getQueryOrderParam] PAY_AGENCY_NOT_EXIST");
      result.withError(ResultStatus.PAY_AGENCY_NOT_EXIST);
      return result;
    }
    PMap<String, String> pmap = new PMap<String, String>();
    pmap.put("agencyCode", Constant.WECHAT);
    pmap.put("merchantNo", merchant.getMerchantNo());
    pmap.put("sellerEmail", merchant.getSellerEmail());
    pmap.put("md5securityKey", merchant.getEncryptKey());
    pmap.put("queryUrl", agencyInfo.getQueryUrl());
    pmap.put("serialNumber", map.get("payReqId").toString());
    result.withReturn(pmap);
    return result;
  }

  /**
   * @param payOrderInfo
   * @return 获得同步回调参数
   * @Author huangguoqing
   * @MethodName getNotifyMap
   * @Date 2015年5月4日
   * @Description:
   */
  @Override
  public ResultMap<Map> getNotifyMap(
          PayOrderInfo payOrderInfo) {
    Map<String, String> notifyMap = new HashMap<String, String>();
    notifyMap.put("isSuccess", "T");
    notifyMap.put("appId", payOrderInfo.getAppId().toString());
    notifyMap.put("signType", "0");
    notifyMap.put("orderId", payOrderInfo.getOrderId());
    notifyMap.put("payId", payOrderInfo.getPayId());
    notifyMap.put("payChannelCode", payOrderInfo.getChannelCode());
    notifyMap.put("successTime", new SimpleDateFormat("yyyyMMddHHmmss").format(payOrderInfo.getPaySuccessTime()));
    Result result = secureManager.appSign(notifyMap);
    if (!Result.isSuccess(result)) {
      LOGGER.error("[getNotifyMap] 获取同步通知参数失败, {}", result.getStatus().getMessage());
      return ResultMap.build(result.getStatus());
    }
    ResultMap resultMap = ResultMap.build();
    resultMap.withReturn((Map) result.getReturnValue());
    return resultMap;
  }
}
