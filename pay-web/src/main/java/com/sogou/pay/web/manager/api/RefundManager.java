package com.sogou.pay.web.manager.api;

import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.service.model.QueryRefundModel;
import com.sogou.pay.service.model.RefundModel;
import com.sogou.pay.service.entity.*;
import com.sogou.pay.service.enums.*;
import com.sogou.pay.service.service.*;
import com.sogou.pay.service.utils.SequenceFactory;
import com.sogou.pay.web.portal.PayPortal;
import com.sogou.pay.service.enums.AgencyCode;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.web.manager.notify.RefundNotifyManager;
import org.apache.commons.collections.CollectionUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
public class RefundManager {
  private static final Logger logger = LoggerFactory.getLogger(RefundManager.class);

  private static final BigDecimal ZERO = BigDecimal.ZERO;

  @Autowired
  private PayPortal payPortal;
  @Autowired
  private PayOrderService payOrderService;
  @Autowired
  private PayOrderRelationService payOrderRelationService;
  @Autowired
  private PayResDetailService payResDetailService;
  @Autowired
  private PayAgencyMerchantService payAgencyMerchantService;
  @Autowired
  private RefundService refundService;
  @Autowired
  private AgencyInfoService agencyInfoService;
  @Autowired
  private SequenceFactory sequencerGenerator;
  @Autowired
  private PayCheckWaitingService payCheckWaitingService;

  @Autowired
  private RefundNotifyManager refundNotifyManager;

  @Profiled(el = true, logger = "dbTimingLogger", tag = "RefundManager_refund",
          timeThreshold = 100, normalAndSlowSuffixesEnabled = true)
  public ResultMap refundOrder(RefundModel model) {
    try {
      //检查退款请求是否合法
      ResultMap checkResult = isOrderRefundable(model);
      if (!Result.isSuccess(checkResult)) {
        return checkResult;
      }
      PayOrderInfo payOrderInfo = (PayOrderInfo) checkResult.getReturnValue();

      //查订单与支付流水关联表
      PayOrderRelation payOrderRelation = new PayOrderRelation();
      payOrderRelation.setPayId(payOrderInfo.getPayId());
      //payOrderRelation.setInfoStatus(RelationStatus.SUCCESS.getValue());
      List<PayOrderRelation> relations = payOrderRelationService.selectPayOrderRelation(payOrderRelation);
      if (CollectionUtils.isEmpty(relations)) {
        logger.error("[refundOrder] PayOrderRelation not exists, params={}", JSONUtil.Bean2JSON(payOrderRelation));
        return ResultMap.build(ResultStatus.ORDER_RELATION_NOT_EXIST);
      }
      //查支付回调流水表
      payOrderRelation = relations.get(0);

      //执行支付单退款逻辑
      model.setRefundStatus(RefundStatus.INIT.getValue());
      return refundPay(model, payOrderInfo, payOrderRelation.getPayDetailId());
    } catch (Exception e) {
      logger.error("[refundOrder] failed, params={}, {}", JSONUtil.Bean2JSON(model), e);
      return ResultMap.build(ResultStatus.SYSTEM_ERROR);
    }
  }

  //验证订单的金额、支付状态、退款标识等信息
  private ResultMap isOrderRefundable(RefundModel model) {
    ResultMap result = ResultMap.build();
    try {
      //验证退款金额
      BigDecimal refundAmount = model.getRefundAmount(); //退款金额
      String orderId = model.getOrderId();               //业务线订单号
      int appId = model.getApp().getAppId();                      //业务线ID
      //根据业务线订单号、业务线ID查询唯一订单信息
      PayOrderInfo payOrderInfo = payOrderService.selectPayOrderInfoByOrderId(orderId, appId);
      if (payOrderInfo == null) {
        logger.error("[isOrderRefundable] PayOrderInfo not exists, params={}", JSONUtil.Bean2JSON(model));
        return ResultMap.build(ResultStatus.ORDER_NOT_EXIST);
      }
      //检查是否成功支付
      if (OrderStatus.SUCCESS.getValue() != payOrderInfo.getPayOrderStatus()) {
        logger.error("[isOrderRefundable] order not paid, params={}, result={}", JSONUtil.Bean2JSON(model),
                JSONUtil.Bean2JSON(payOrderInfo));
        return ResultMap.build(ResultStatus.ORDER_NOT_PAY);
      }
      //检查是否已退款
      if (RefundFlag.SUCCESS.getValue() == payOrderInfo.getRefundFlag()) {
        logger.info("[isOrderRefundable] order already refunded, params={}, result={}", JSONUtil.Bean2JSON(model),
                JSONUtil.Bean2JSON(payOrderInfo));
        return ResultMap.build(ResultStatus.REFUND_ALREADY_DONE);
      }
      //检查退款金额与支付金额是否相同
      BigDecimal payMoney = payOrderInfo.getOrderMoney();            //订单支付金额
      if (refundAmount == null) {
        model.setRefundAmount(payMoney);
      } else if (refundAmount.compareTo(payMoney) != 0) {
        // 退款金额不等于余额
        logger.error("[isOrderRefundable] partial refund not supported, params={}, result={}", JSONUtil.Bean2JSON(model),
                JSONUtil.Bean2JSON(payOrderInfo));
        return ResultMap.build(ResultStatus.PARTIAL_REFUND_NOT_ALLOWED);
      }
      result.withReturn(payOrderInfo);
      return result;
    } catch (Exception e) {
      logger.info("[isOrderRefundable] failed, params={}, {}", JSONUtil.Bean2JSON(model), e);
      return ResultMap.build(ResultStatus.SYSTEM_ERROR);
    }
  }

  //针对支付流水单进行退款
  public ResultMap refundPay(RefundModel model, PayOrderInfo payOrderInfo, String payDetailId) throws Exception {
    PayResDetail payResDetail = payResDetailService.selectPayResById(payDetailId);
    if (payResDetail == null) {
      logger.error("[refundPay] PayResDetail not exists, payDetailId={}", payDetailId);
      return ResultMap.build(ResultStatus.RES_DETAIL_NOT_EXIST);
    }
    //查支付商户信息
    String agencyCode = payResDetail.getAgencyCode(); //支付机构编码
    String merchantNo = payResDetail.getMerchantNo(); //支付机构商户号
    PayAgencyMerchant agencyMerchant = payAgencyMerchantService
            .getMerchantByAgencyCodeAndMerchantNo(agencyCode, merchantNo);
    if (agencyMerchant == null) {
      logger.error("[refundPay] PayAgencyMerchant not exists, agencyCode={}, merchantNo={}", agencyCode, merchantNo);
      return ResultMap.build(ResultStatus.THIRD_MERCHANT_NOT_EXIST);
    }
    //查支付机构信息
    int accessPlatform = payResDetail.getAccessPlatform();
    AgencyInfo agencyInfo = agencyInfoService
            .getAgencyInfoByCode(agencyCode, accessPlatform);
    if (agencyInfo == null) {
      logger.error("[refundPay] AgencyInfo not exists, agencyCode={}, accessPlatform={}", agencyCode, accessPlatform);
      return ResultMap.build(ResultStatus.THIRD_AGENCY_NOT_EXIST);
    }
    //插入退款记录
    String refundId = sequencerGenerator.getRefundDetailId(); //退款单号
    boolean insertResult = insertRefundInfo(refundId, model, payOrderInfo, payResDetail);
    if (!insertResult) {
      logger.error("[refundPay] insertRefundInfo failed, params={}", JSONUtil.Bean2JSON(model));
      return ResultMap.build(ResultStatus.SYSTEM_DB_ERROR);
    }
    //调用退款网关
    ResultMap refundResult = doRefund(refundId, model, payOrderInfo, payResDetail, agencyMerchant, agencyInfo);
    if (!Result.isSuccess(refundResult)) {
      return refundResult;
    }
    //微信支付无回调,直接处理成功信息
    if (AgencyCode.WECHAT == AgencyCode.getValue(agencyCode)
            || AgencyCode.TEST_WECHAT == AgencyCode.getValue(agencyCode)) {
      boolean isFairRefund = model.getRefundStatus() == RefundStatus.FAIR.getValue();
      refundResult = completeRefund(model, payResDetail, payOrderInfo,
              (String) refundResult.getItem("agencyRefundId"), refundId, !isFairRefund);
      if (!Result.isSuccess(refundResult)) {
        return refundResult;
      }
      if (!isFairRefund) {
        //异步通知业务线
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setAppId(model.getApp().getAppId());
        refundInfo.setOrderId(model.getOrderId());
        refundInfo.setPayId(payOrderInfo.getPayId());
        refundInfo.setOrderMoney(payOrderInfo.getOrderMoney());
        refundInfo.setRefundMoney(model.getRefundAmount());
        refundInfo.setAppBgUrl(model.getBgurl());
        logger.info("[refundPay] 异步通知业务线开始: {}", JSONUtil.Bean2JSON(refundInfo));
        refundNotifyManager.notifyApp(refundInfo);
        logger.info("[refundPay] 异步通知业务线结束");
      }
    }
    return ResultMap.build();
  }

  //插入退款记录
  private boolean insertRefundInfo(String refundId, RefundModel model, PayOrderInfo payOrderInfo, PayResDetail payResDetail) {
    try {
      RefundInfo refundInfo = new RefundInfo();
      refundInfo.setAppId(model.getApp().getAppId());              //appId
      refundInfo.setRefundId(refundId);                            //退款号
      refundInfo.setPayId(payOrderInfo.getPayId());                //支付ID
      refundInfo.setPayDetailId(payResDetail.getPayDetailId());    //支付回调ID
      refundInfo.setAppBgUrl(model.getBgurl());                    //业务线异步回调url
      refundInfo.setOrderId(model.getOrderId());                   //订单号
      refundInfo.setOrderMoney(payOrderInfo.getOrderMoney());      //订单支付金额
      refundInfo.setAgencyCode(payResDetail.getAgencyCode());      //支付机构编码
      refundInfo.setMerchantNo(payResDetail.getMerchantNo());      //商户号
      refundInfo.setUseBalance(ZERO);
      refundInfo.setRefundMoney(model.getRefundAmount());          //退款金额
      refundInfo.setBankRefund(model.getRefundAmount());     //退款金额
      refundInfo.setBalanceRefund(ZERO);
      refundInfo.setRefundStatus(model.getRefundStatus());       //退款初始状态
      refundInfo.setTaskStatus(1);                                 //任务初始状态
      refundInfo.setRefundReqTime(new Date());                     //请求时间
      return refundService.insertRefundInfo(refundInfo) == 1;
    } catch (Exception e) {
      logger.error("[insertRefundInfo] failed, params={}, {}", JSONUtil.Bean2JSON(model), e);
      return false;
    }
  }

  //组装参数并调用退款网关
  private ResultMap doRefund(String refundId, RefundModel model, PayOrderInfo payOrderInfo,
                             PayResDetail payResDetail, PayAgencyMerchant agencyMerchant, AgencyInfo agencyInfo) {
    try {
      PMap<String, Object> pMap = new PMap<>();
      pMap.put("agencyCode", payResDetail.getAgencyCode());                                     //支付机构编码
      pMap.put("merchantNo", payResDetail.getMerchantNo());                                     //商户号
      pMap.put("refundUrl", agencyInfo.getRefundUrl());                                         //退款请求url
      String notifyUrl = agencyInfo.getRefundNotifyBackUrl() + "/" + agencyMerchant.getMerchantId();
      pMap.put("refundNotifyUrl", notifyUrl);                                                   //异步回调url
      pMap.put("md5securityKey", agencyMerchant.getEncryptKey());                               //MD5加密秘钥
      pMap.put("publicCertFilePath", agencyMerchant.getPubKeypath());                           //公钥证书地址
      pMap.put("privateCertFilePath", agencyMerchant.getPrivateKeypath());                      //私钥证书地址
      pMap.put("refundSerialNumber", refundId);                                                 //退款单号
      pMap.put("refundReqTime", DateUtil.formatShortTime(new Date()));// 请求时间
      pMap.put("serialNumber", payResDetail.getPayDetailId());                                  //订单号
      pMap.put("agencySerialNumber", payResDetail.getAgencyOrderId());                          //支付机构订单号
      pMap.put("refundAmount", String.valueOf(model.getRefundAmount().doubleValue()));                     //退款金额
      pMap.put("totalAmount", String.valueOf(payOrderInfo.getOrderMoney().doubleValue()));                        //支付金额
      String sellerEmail = agencyMerchant.getSellerEmail();
      pMap.put("sellerEmail", sellerEmail);                                                     //商户邮箱或者公众号ID
      ResultMap refundResult = payPortal.refundOrder(pMap);
      if (!Result.isSuccess(refundResult)) {
        String errorCode = (String) refundResult.getData().get("errorCode");                 //退款错误码
        String errorMsg = (String) refundResult.getData().get("errorMsg");                 //退款错误信息
        refundService.updateRefundFail(refundId, null, errorCode, errorMsg);
      }
      return refundResult;
    } catch (Exception e) {
      logger.error("[doRefund] failed, params={}, {}", JSONUtil.Bean2JSON(model), e);
      return ResultMap.build(ResultStatus.SYSTEM_ERROR);
    }
  }

  //退款成功之后的业务逻辑
  @Transactional
  public ResultMap completeRefund(RefundModel model, PayResDetail payResDetail, PayOrderInfo payOrderInfo,
                                  String agencyRefundId, String refundId, boolean needUpdateOrder) throws Exception {
    if (needUpdateOrder) {
      //更新支付单退款状态
      BigDecimal allRefundMoney = payOrderInfo.getRefundMoney().add(model.getRefundAmount());
      int payRefundFlag;
      if (allRefundMoney.compareTo(payOrderInfo.getOrderMoney()) == 0) {
        payRefundFlag = RefundFlag.SUCCESS.getValue();
      } else {
        payRefundFlag = RefundFlag.PART_REFUND.getValue();
      }
      if (payOrderService.updateAddRefundMoney(payOrderInfo.getPayId(), model.getRefundAmount(), payRefundFlag) != 1) {
        logger.error("[completeRefund] updateAddRefundMoney failed, params={}", JSONUtil.Bean2JSON(model));
        throw new RuntimeException(ResultStatus.SYSTEM_DB_ERROR.getMessage());
      }
    }
    //更新支付关联单和退款单状态
    Date sucDate = new Date();
    payOrderRelationService.updatePayOrderRelation(RelationStatus.REFUND.getValue(), payResDetail.getPayDetailId());
    refundService.updateRefundSuccess(refundId, agencyRefundId, sucDate);
    //插入对账单
    if (!insertPayCheckWaiting(model, payResDetail, agencyRefundId, refundId, sucDate)) {
      logger.error("[completeRefund] 插入对账单失败: {}", JSONUtil.Bean2JSON(model));
      throw new RuntimeException(ResultStatus.SYSTEM_DB_ERROR.getMessage());
    }
    return ResultMap.build();
  }

  /**
   * 生成对账单
   */
  private boolean insertPayCheckWaiting(RefundModel model, PayResDetail payResDetail,
                                        String thirdRefundId, String refundId, Date sucDate) throws Exception {
    PayCheckWaiting payCheckWaiting = new PayCheckWaiting();
    payCheckWaiting.setCreateTime(sucDate);                                                //入库时间
    payCheckWaiting.setModifyTime(sucDate);                                                //修改时间
    payCheckWaiting.setVersion((short) 0);                                                 //版本号
    payCheckWaiting.setInstructId(refundId);                                               //我方流水号
    payCheckWaiting.setCheckType(CheckType.REFUND.getValue());                               //流水类型
    payCheckWaiting.setOutTransTime(sucDate);                                              //对方交易时间
    payCheckWaiting.setOutOrderId(thirdRefundId); //对方流水号
    payCheckWaiting.setBizAmt(model.getRefundAmount());                                    //对帐金额-退款金额
    payCheckWaiting.setStatus(CheckStatus.INIT.getValue());                                   //对账状态
    payCheckWaiting.setAccessPlatform(payResDetail.getAccessPlatform());                   //接入平台
    payCheckWaiting.setAppId(model.getApp().getAppId());                                   //业务线ID
    payCheckWaiting.setCheckDate(DateUtil.format(sucDate, DateUtil.DATE_FORMAT_DAY_SHORT));//对账日期
    payCheckWaiting.setAgencyCode(payResDetail.getAgencyCode());                                             //机构编码
    payCheckWaiting.setMerchantNo(payResDetail.getMerchantNo());                                             //商户号
//    if (payResDetail.getAgencyCode().equals(AgencyCode.ALIPAY.name())) {
//      //支付宝不退手续费?
//      payCheckWaiting.setFeeRate(BigDecimal.ZERO);//费率
//      payCheckWaiting.setCommissionFeeAmt(BigDecimal.ZERO);//手续费
//    } else {
    payCheckWaiting.setFeeRate(payResDetail.getFeeRate());//费率
    payCheckWaiting.setCommissionFeeAmt(payResDetail.getPayFee().negate());//手续费
//    }
    payCheckWaiting.setBankCode(payResDetail.getBankCode());                                   //银行编码
    payCheckWaiting.setPayType(payResDetail.getPayFeeType());                                  //付款方式
    return payCheckWaitingService.insert(payCheckWaiting) == 1;
  }

  public ResultMap queryRefund(QueryRefundModel queryRefundModel) {
    try {
      String orderId = queryRefundModel.getOrderId();
      //检查支付单
      PayOrderInfo payOrderInfo = payOrderService.selectPayOrderInfoByOrderId(orderId, queryRefundModel.getApp().getAppId());
      if (payOrderInfo == null) {
        logger.error("[queryRefund] PayOrderInfo not exists, params={}", JSONUtil.Bean2JSON(queryRefundModel));
        return ResultMap.build(ResultStatus.ORDER_NOT_EXIST);
      }
      //检查支付单的退款状态是否退款成功
      if (payOrderInfo.getRefundFlag() == RefundFlag.SUCCESS.getValue()) {
        //result.withReturn(OrderRefundStatus.SUCCESS);
        //return result;
      }
      //检查是否有退款单
      List<RefundInfo> refundInfoList = refundService.selectRefundByOrderId(orderId);
      if (CollectionUtils.isEmpty(refundInfoList)) {
        logger.error("[queryRefund] RefundInfo not exists, params={}", JSONUtil.Bean2JSON(queryRefundModel));
        return ResultMap.build(ResultStatus.REFUND_NOT_EXIST);
      }
      //检查退款单状态是否有退款成功的
      for (RefundInfo refundInfo : refundInfoList)
        if (refundInfo.getRefundStatus() == RefundStatus.SUCCESS.getValue()) {
          //result.withReturn(OrderRefundStatus.SUCCESS);
          //return result;
        }
      //准备向第三方发起查询
      RefundInfo refundInfo = refundInfoList.get(0);
      //获得商户信息
      String agencyCode = refundInfo.getAgencyCode();
      PayAgencyMerchant queryMerchant = new PayAgencyMerchant();
      queryMerchant.setAgencyCode(agencyCode);
      queryMerchant.setCompanyId(queryRefundModel.getApp().getCompanyId());
      queryMerchant.setAppId(queryRefundModel.getApp().getAppId());
      PayAgencyMerchant agencyMerchant = payAgencyMerchantService.getMerchant(queryMerchant);
      if (agencyMerchant == null) {
        logger.error("[queryRefund] PayAgencyMerchant not exists, params={}", JSONUtil.Bean2JSON(queryRefundModel));
        return ResultMap.build(ResultStatus.THIRD_MERCHANT_NOT_EXIST);
      }
      //获得支付机构信息
      AgencyInfo agencyInfo = agencyInfoService
              .getAgencyInfoByCode(agencyCode, payOrderInfo.getAccessPlatForm());
      if (agencyInfo == null) {
        //支付机构不存在
        logger.error("[queryRefund] AgencyInfo not exists, params={}", JSONUtil.Bean2JSON(queryRefundModel));
        return ResultMap.build(ResultStatus.THIRD_AGENCY_NOT_EXIST);
      }
      //调用退款网关
      PMap<String, Object> queryRefundPMap = new PMap<>();
      queryRefundPMap.put("agencyCode", agencyCode);
      queryRefundPMap.put("merchantNo", agencyMerchant.getMerchantNo());
      queryRefundPMap.put("sellerEmail", agencyMerchant.getSellerEmail());
      queryRefundPMap.put("md5securityKey", agencyMerchant.getEncryptKey());
      queryRefundPMap.put("publicCertFilePath", agencyMerchant.getPubKeypath());
      queryRefundPMap.put("privateCertFilePath", agencyMerchant.getPrivateKeypath());
      queryRefundPMap.put("queryRefundUrl", agencyInfo.getQueryRefundUrl());
      queryRefundPMap.put("refundSerialNumber", refundInfo.getRefundId());
      queryRefundPMap.put("agencySerialNumber", refundInfo.getAgencyRefundId());
      ResultMap queryRefundResult = payPortal.queryRefund(queryRefundPMap);
      if (!Result.isSuccess(queryRefundResult)) {
        logger.error("[queryRefund] queryRefund failed, params={}, result={}", JSONUtil.Bean2JSON(queryRefundPMap),
                JSONUtil.Bean2JSON(queryRefundResult));
      }
      return queryRefundResult;
    } catch (Exception e) {
      logger.error("[queryRefund] failed, params={}, {}", JSONUtil.Bean2JSON(queryRefundModel), e);
      return ResultMap.build(ResultStatus.SYSTEM_ERROR);
    }
  }
}
