package com.sogou.pay.web.manager.api;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.enums.BankCardType;
import com.sogou.pay.enums.ChannelType;
import com.sogou.pay.manager.model.PayOrderQueryModel;
import com.sogou.pay.manager.model.PayNotifyModel;
import com.sogou.pay.service.entity.*;
import com.sogou.pay.service.enums.RelationStatus;
import com.sogou.pay.service.payment.*;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.web.portal.PayPortal;
import org.apache.commons.lang.StringUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;

@Component
public class PayManager {

  private static final Logger logger = LoggerFactory.getLogger(PayManager.class);

  @Autowired
  private AppService appService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private PayOrderService payOrderService;

  @Autowired
  private SequenceFactory sequencerGenerator;

  @Autowired
  private PayReqDetailService payReqDetailService;

  @Autowired
  private PayBankRouterService payBankRouterService;

  @Autowired
  private PayAgencyMerchantService payAgencyMerchantService;

  @Autowired
  private PayOrderRelationService payOrderRelationService;

  @Autowired
  private AgencyInfoService agencyInfoService;

  @Autowired
  private PayBankAliasService payBankAliasService;

  @Autowired
  private PayResIdService payResIdService;

  @Autowired
  private PayCheckWaitingService payCheckWaitingService;


  @Autowired
  private PayResDetailService payResDetailService;

  @Autowired
  private PayFeeService payFeeService;

  @Autowired
  private PayPortal payPortal;

  private static final PMap<String, Integer> thirdPayMap = new PMap<>();

  static {
    //PC网银支付
    thirdPayMap.put("1_1", PayPortal.PC_GATEWAY);
    //PC账户支付
    thirdPayMap.put("1_2", PayPortal.PC_ACCOUNT);
    //PC企业网银支付
    thirdPayMap.put("1_3", PayPortal.PC_GATEWAY);
    //扫码支付
    thirdPayMap.put("4_2", PayPortal.QRCODE);
    //SDK账户支付
    thirdPayMap.put("3_2", PayPortal.MOBILE_SDK);
    //WAP账户支付
    thirdPayMap.put("2_2", PayPortal.MOBILE_WAP);
  }

  private static int getThirdPayChannel(String platForm, String payFeeType) {
    String key = platForm + "_" + payFeeType;
    return thirdPayMap.get(key);
  }

  //创建支付订单
  public Result createOrder(PMap params) {
    ResultMap result = ResultMap.build();
    try {
      String orderId = params.getString("orderId");
      Integer appId = params.getInt("appId");
      PayOrderInfo info = payOrderService.selectPayOrderInfoByOrderId(orderId, appId);
      if (info == null) {
        return insertPayOrder(params);
      } else if (info.getPayOrderStatus() == OrderStatus.SUCCESS.getValue()) {
        //该支付单已经支付成功，直接返回业务线
        //result.withError(ResultStatus.PAY_ORDER_ALREADY_DONE);
      }
      //继续支付
      result.withReturn(info.getPayId());
    } catch (ServiceException e) {
      logger.error("[createOrder] failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      result.withError(ResultStatus.PAY_SYSTEM_ERROR);
    }
    return result;
  }

  //创建第三方支付机构的支付流水单
  @Profiled(el = true, logger = "dbTimingLogger", tag = "PayManager_confirmPay",
          timeThreshold = 100, normalAndSlowSuffixesEnabled = true)
  @Transactional
  private ResultMap createAgencyOrder(PMap params) {
    logger.info("[createAgencyOrder] params={}", params);
    ResultMap result = ResultMap.build();

    //判断渠道是否是第三方支付
    String agencyCode = null;//最终调用的支付机构编码，通过channelCode路由得到
    int accessPlatform = params.getInt("accessPlatform");//接入平台
    String channelCode = params.getString("channelCode");//支付渠道编码
    int channelType;//支付渠道类型
    if (params.getString("channelType") == null) {
      Channel channel = channelService.selectChannelByCode(channelCode, accessPlatform);
      if (channel == null) {
        logger.error("[createAgencyOrder] channel not exists, params={}", JSONUtil.Bean2JSON(params));
        return (ResultMap) result.withError(ResultStatus.PAY_CHANNEL_NOT_EXIST);
      }
      channelType = channel.getChannelType();
    } else {
      channelType = params.getInt("channelType");
    }

    if (channelType == ChannelType.CHANNELTYPE_AGENCY) {
      //渠道为第三方机构
      agencyCode = channelCode;
    } else if (channelType == ChannelType.CHANNELTYPE_BANK || channelType == ChannelType.CHANNELTYPE_B2B) {
      //渠道为网银支付,路由出第三方支付机构
      agencyCode = routeBank(params);
      if (agencyCode == null) {
        logger.error("[createAgencyOrder] bank router not exists, params={}", JSONUtil.Bean2JSON(params));
        return (ResultMap) result.withError(ResultStatus.PAY_BANK_ROUTER_NOT_EXIST);
      }
    }

    //获取业务线信息
    App app = appService.selectApp(params.getInt("appId"));
    if (app == null) {
      //业务线不存在
      logger.error("[createAgencyOrder] appid not exists, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.PAY_APP_NOT_EXIST);
    }

    //获取在第三方支付开通的商户信息
    PayAgencyMerchant agencyMerchant = new PayAgencyMerchant();
    agencyMerchant.setAgencyCode(agencyCode);
    agencyMerchant.setCompanyCode(app.getBelongCompany());
    agencyMerchant.setAppId(app.getAppId());
    PayAgencyMerchant merchant = payAgencyMerchantService.selectPayAgencyMerchant(agencyMerchant);
    if (merchant == null) {
      //第三方支付商户不存在
      logger.error("[createAgencyOrder] merchant not exists, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) result.withError(ResultStatus.PAY_MERCHANT_NOT_EXIST);
    }

    //生成支付流水单
    String payReqId = sequencerGenerator.getPayDetailId();
    Date payTime = new Date();
    int bankCardType = BankCardType.BANKCARDTYPE_ANY;
    if (!StringUtils.isEmpty(params.getString("bankCardType"))) {
      bankCardType = params.getInt("bankCardType");
    }
    PayReqDetail payReqDetail = new PayReqDetail();
    payReqDetail.setPayDetailId(payReqId);
    payReqDetail.setAccessPlatform(accessPlatform);
    payReqDetail.setPayFeeType(channelType);
    payReqDetail.setBalance(BigDecimal.ZERO);
    payReqDetail.setTrueMoney(new BigDecimal(params.getString("orderAmount")));
    payReqDetail.setAgencyCode(agencyCode);
    payReqDetail.setBankCode(channelCode);
    payReqDetail.setMerchantNo(merchant.getMerchantNo());
    payReqDetail.setBankCardType(bankCardType);
    payReqDetail.setCreateTime(payTime);
    try {
      payReqDetailService.insertPayReqDetail(payReqDetail);
    } catch (ServiceException e) {
      logger.error("[createAgencyOrder] insertPayReqDetail failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return (ResultMap) result.withError(e.getStatus());
    }
    //生成支付单与支付流水单关联
    PayOrderRelation payOrderRelation = new PayOrderRelation();
    payOrderRelation.setPayDetailId(payReqDetail.getPayDetailId());
    payOrderRelation.setPayId(params.getString("payId"));
    payOrderRelation.setInfoStatus(PayOrderRelation.INFOSTATUS_INIT);//初始状态未支付
    payOrderRelation.setCreateTime(new Date());
    try {
      payOrderRelationService.insertPayOrderRelation(payOrderRelation);
    } catch (ServiceException e) {
      logger.error("[createAgencyOrder] insertPayOrderRelation failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return (ResultMap) result.withError(e.getStatus());
    }

    //返回第三方支付、银行、第三方支付商户、支付流水号、支付方式、支付时间
    result.addItem("agencyCode", agencyCode);
    result.addItem("bankCode", channelCode);
    result.addItem("agencyMerchant", merchant);
    result.addItem("payDetailId", payReqId);
    result.addItem("payFeeType", channelType);
    result.addItem("payTime", payTime);
    return result;
  }

  //支付订单
  public ResultMap payOrder(PMap params) {
    ResultMap result = ResultMap.build();
    //创建支付流水单
    ResultMap payResult = createAgencyOrder(params);
    if (!Result.isSuccess(payResult)) {
      return payResult;
    }
    //组装支付网关数据
    params.put("agencyCode", payResult.getItem("agencyCode"));
    params.put("bankCode", payResult.getItem("bankCode"));
    params.put("agencyMerchant", payResult.getItem("agencyMerchant"));
    params.put("payDetailId", payResult.getItem("payDetailId"));
    params.put("payFeeType", payResult.getItem("payFeeType"));
    params.put("payTime", payResult.getItem("payTime"));
    return getThirdPayServiceParams(params);
  }

  //根据路由信息选择网银对应的第三方支付
  private String routeBank(PMap params) {
    PayBankRouter payBankRouter = new PayBankRouter();
    payBankRouter.setBankCode(params.getString("channelCode"));
    if (!StringUtils.isEmpty(params.getString("bankCardType"))) {
      payBankRouter.setBankCardType(params.getInt("bankCardType"));
    }
    payBankRouter.setAppId(params.getInt("appId"));
    payBankRouter.setRouterStatus(1);//已启用
    //查询第三方支付路由信息
    List<PayBankRouter> routerList = payBankRouterService.selectPayBankRouterList(payBankRouter);
    if (routerList == null) {
      logger.info("[routeBank] bank router not exists, params={}", JSONUtil.Bean2JSON(params));
      return null;
    }
    //选择一个第三方支付
    return selectAgency(routerList);
  }

  //随机选择一个第三方支付，考虑权重
  private String selectAgency(List<PayBankRouter> routerList) {
    int random = (int) (Math.random() * 10000) + 1;
    logger.info("[selectAgency] selected random number is {}", random);
    int totalScale = 0;
    int size = routerList.size();
    for (int i = 0; i < size; i++) {
      PayBankRouter router = routerList.get(i);
      totalScale += router.getScale() * 10000;
      if (random <= totalScale) {
        logger.info("[selectAgency] selected agencyCode is {}", router.getAgencyCode());
        return router.getAgencyCode();
      }
    }
    return routerList.get(0).getAgencyCode();
  }

  //插入支付单信息
  public ResultMap insertPayOrder(PMap params) {
    ResultMap result = ResultMap.build();
    try {
      StringBuilder productInfo = new StringBuilder()
              .append("商品名称:").append(params.get("productName"))
              .append(",商品数量:").append(params.get("productNum"));
      if (!StringUtils.isEmpty(params.getString("productDesc"))) {
        productInfo.append(",商品描述:").append(params.get("productDesc"));
      }
      Date date = new Date();
      String payId = sequencerGenerator.getPayId();
      PayOrderInfo payOrderInfo = new PayOrderInfo();
      payOrderInfo.setPayId(payId);
      payOrderInfo.setOrderType(1);
      payOrderInfo.setOrderId(params.getString("orderId"));
      payOrderInfo.setProductInfo(productInfo.toString());
      payOrderInfo.setOrderMoney(new BigDecimal(params.getString("orderAmount")));
      payOrderInfo.setBuyHomeIp(params.getString("userIp"));
      payOrderInfo.setBuyHomeAccount(params.getString("accountId"));
      payOrderInfo.setAccessPlatForm(Integer.parseInt(params.getString("accessPlatform")));
      payOrderInfo.setChannelCode(params.getString("bankId"));
      payOrderInfo.setPayOrderStatus(1);//未支付
      payOrderInfo.setRefundMoney(BigDecimal.ZERO);
      payOrderInfo.setRefundFlag(1);//未退款
      payOrderInfo.setAppPageUrl(params.getString("pageUrl"));
      payOrderInfo.setAppBgUrl(params.getString("bgUrl"));
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      payOrderInfo.setOrderCreateTime(sdf.parse(params.getString("orderTime")));
      payOrderInfo.setCreateTime(date);
      payOrderInfo.setPaySuccessTime(date);
      payOrderInfo.setAppId(Integer.parseInt(params.getString("appId")));
      payOrderInfo.setNotifyStatus(0);//未通知
      int ret = payOrderService.insertPayOrder(payOrderInfo);
      if (ret != 1) {
        //插入支付单失败
        logger.error("[insertPayOrder] insert pay order failed, params={}", JSONUtil.Bean2JSON(payOrderInfo));
        result.withError(ResultStatus.PAY_INSERT_PAY_ORDER_ERROR);
      }
      result.withReturn(payId);
    } catch (Exception e) {
      logger.error("[insertPayOrder] insert pay order failed, {}", e);
      result.withError(ResultStatus.PAY_SYSTEM_ERROR);
    }
    return result;
  }

  //组装调用支付网关所需的参数
  public ResultMap getThirdPayServiceParams(PMap params) {
    ResultMap<PMap> result = ResultMap.build();
    PMap payGateMap = new PMap();
    try {
      //获得第三方支付信息
      String agencyCode = params.getString("agencyCode");
      String bankCode = params.getString("bankCode");
      int payFeeType = params.getInt("payFeeType");
      String accessPlatfrom = params.getString("accessPlatform");
      AgencyInfo agencyInfo = agencyInfoService.getAgencyInfoByCode(agencyCode, accessPlatfrom);
      if (agencyInfo == null) {
        logger.error("[getThirdPayServiceParams] agency not exists, params={}", JSONUtil.Bean2JSON(params));
        return (ResultMap) result.withError(ResultStatus.PAY_AGENCY_NOT_EXIST);
      }
      if (ChannelType.CHANNELTYPE_BANK == payFeeType) {
        //网银支付 判断该支付机构的银行是否有别名
        payGateMap.put("bankCode", bankCode);
        Integer bankCardType;
        Integer aliasFlag;
        if (StringUtils.isEmpty(params.getString("bankCardType"))) {
          //没有传递银行卡类型
          bankCardType = null;
          aliasFlag = AgencyInfo.ALIASFLAG_ALL;
        } else {
          //传递了银行卡类型
          bankCardType = params.getInt("bankCardType");
          aliasFlag = bankCardType;
        }
        if (AgencyInfo.ALIASFLAG_ALL == agencyInfo.getAliasFlag() || aliasFlag == agencyInfo.getAliasFlag()) {
          //银行有别名，检索银行别名
          PayBankAlias payBankAlias = payBankAliasService.selectPayBankAlias(agencyCode, bankCode, bankCardType);
          if (null != payBankAlias) bankCode = payBankAlias.getAliasName();
          payGateMap.put("bankCode", bankCode);
        }
      } else if (ChannelType.CHANNELTYPE_B2B == payFeeType) {
        //企业网银支付
        payGateMap.put("bankCode", bankCode);
      }
      payGateMap.put("agencyCode", agencyCode);
      payGateMap.put("payChannel", getThirdPayChannel(accessPlatfrom, String.valueOf(payFeeType)));
      PayAgencyMerchant payAgencyMerchant = (PayAgencyMerchant) params.get("agencyMerchant");
      //第三方支付机构商户号
      payGateMap.put("merchantNo", payAgencyMerchant.getMerchantNo());
      //收款账号对应邮箱
      payGateMap.put("sellerEmail", payAgencyMerchant.getSellerEmail());
      //微信支付时使用
      payGateMap.put("prepayUrl", agencyInfo.getPrepayUrl());
      //支付地址
      payGateMap.put("payUrl", agencyInfo.getPayUrl());
      //异步回调地址
      payGateMap.put("serverNotifyUrl", agencyInfo.getNotifyBackUrl());
      //同步页面回调地址
      payGateMap.put("pageNotifyUrl", agencyInfo.getPageBackUrl());
      //商品名称
      payGateMap.put("subject", params.getString("productName"));
      //买家IP
      payGateMap.put("buyerIp", params.getString("userIp"));
      //买家付款账号
      payGateMap.put("accountId", params.getString("accountId"));
      //MD5加密密钥
      payGateMap.put("md5securityKey", payAgencyMerchant.getEncryptKey());
      //支付机构公钥证书路径
      payGateMap.put("publicCertFilePath", payAgencyMerchant.getPubKeypath());
      //本地私钥证书路径
      payGateMap.put("privateCertFilePath", payAgencyMerchant.getPrivateKeypath());
      //支付请求时间
      payGateMap.put("payTime", new SimpleDateFormat("yyyyMMddHHmmss").format(params.getDate("payTime")));
      //订单金额
      payGateMap.put("orderAmount", params.getString("orderAmount"));
      //支付请求流水号
      payGateMap.put("serialNumber", params.getString("payDetailId"));
      result.withReturn(payGateMap);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("[getThirdPayServiceParams] error, {}", e);
      result.withError(ResultStatus.PAY_SYSTEM_ERROR);
    }
    return result;
  }

  @Profiled(el = true, logger = "dbTimingLogger", tag = "OrderQueryManager_queryPayOrder",
          timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
  public ResultMap queryPayOrder(PayOrderQueryModel model) {
    ResultMap result = ResultMap.build();
    App app = model.getApp();
    //根据订单查询支付回调信息
    String payStatus = null;
    String payReqId = null;
    List<PayReqDetail> payReqDetailList = null;
    try {
      //根据orderId和appId查询订单信息
      PayOrderInfo payOrderInfo = payOrderService.selectPayOrderInfoByOrderId(model.getOrderId(), app.getAppId());
      if (payOrderInfo == null) {
        logger.error("[queryPayOrder] PayOrderInfo not found, params={}", JSONUtil.Bean2JSON(model));
        return (ResultMap) result.withError(ResultStatus.PAY_ORDER_NOT_EXIST);
      }
      //检查支付单里的支付状态是否成功，成功则返回
      boolean orderSuccess = payOrderInfo.getPayOrderStatus() == OrderStatus.SUCCESS.getValue();
      if (orderSuccess && !model.isFromCashier()) {
        result.addItem("payStatus", OrderStatus.SUCCESS);
        return result;
      }
      //根据payId查询关联表
      PayOrderRelation paramRelation = new PayOrderRelation();
      paramRelation.setPayId(payOrderInfo.getPayId());
      List<PayOrderRelation> relationList = payOrderRelationService.selectPayOrderRelation(paramRelation);
      if (relationList == null || relationList.size() == 0) {
        logger.error("[queryPayOrder] PayOrderRelation not found, params={}", JSONUtil.Bean2JSON(paramRelation));
        return (ResultMap) result.withError(ResultStatus.PAY_ORDER_RELATION_NOT_EXIST);
      }
      //如果是收银台请求，则可以返回
      if (orderSuccess && model.isFromCashier()) {
        result.addItem("payStatus", OrderStatus.SUCCESS);
        result.addItem("payReqId", relationList.get(0).getPayDetailId());
        return result;
      }
      //查询支付单流水信息
      payReqDetailList = payReqDetailService.selectPayReqByReqIdList(relationList);
      if (payReqDetailList == null) {
        logger.error("[queryPayOrder] PayReqDetail not found, params={}", JSONUtil.Bean2JSON(relationList));
        return (ResultMap) result.withError(ResultStatus.PAY_ORDER_NOT_EXIST);
      }
      for (PayReqDetail payReqDetail : payReqDetailList) {
        payReqId = payReqDetail.getPayDetailId();
        PayAgencyMerchant merchant = new PayAgencyMerchant();
        merchant.setAgencyCode(payReqDetail.getAgencyCode());
        merchant.setAppId(app.getAppId());
        merchant.setCompanyCode(app.getBelongCompany());
        PayAgencyMerchant merchantQuery = payAgencyMerchantService.selectPayAgencyMerchant(merchant);
        if (merchantQuery == null) {
          logger.error("[queryPayOrder] PayAgencyMerchant not found, params={}", JSONUtil.Bean2JSON(merchant));
          return (ResultMap) result.withError(ResultStatus.PAY_MERCHANT_NOT_EXIST);
        }
        AgencyInfo agencyInfo = agencyInfoService.getAgencyInfoByCode(payReqDetail.getAgencyCode(),
                payReqDetail.getAccessPlatform().toString());
        if (agencyInfo == null) {
          logger.error("[queryPayOrder] AgencyInfo not found, params={}", JSONUtil.Bean2JSON(payReqDetail));
          return (ResultMap) result.withError(ResultStatus.PAY_AGENCY_NOT_EXIST);
        }
        //调用支付网关
        PMap queryPMap = new PMap();
        queryPMap.put("agencyCode", merchantQuery.getAgencyCode());
        queryPMap.put("merchantNo", merchantQuery.getMerchantNo());
        queryPMap.put("sellerEmail", merchantQuery.getSellerEmail());
        queryPMap.put("md5securityKey", merchantQuery.getEncryptKey());
        queryPMap.put("queryUrl", agencyInfo.getQueryUrl());
        queryPMap.put("serialNumber", payReqId);
        ResultMap queryResult = payPortal.queryOrder(queryPMap);
        if (!Result.isSuccess(queryResult)) {
          logger.error("[queryPayOrder] queryOrder failed, params={}, result={}", JSONUtil.Bean2JSON(queryPMap),
                  JSONUtil.Bean2JSON(queryResult));
          return (ResultMap) result.withError(queryResult.getStatus());
        }
        payStatus = queryResult.getData().get("order_state").toString();
        if (Objects.equals(payStatus, OrderStatus.SUCCESS.name()))
          break;
      }
      result.addItem("payStatus", payStatus);
      result.addItem("payReqId", payReqId);
    } catch (Exception e) {
      logger.error("[queryPayOrder] failed, params={}, {}", JSONUtil.Bean2JSON(model), e);
      result.withError(ResultStatus.SYSTEM_ERROR);
    }
    return result;
  }


  //支付成功之后的逻辑
  @Transactional
  public void completePay(PayNotifyModel payNotifyModel, PayOrderInfo payOrderInfo, PayReqDetail payReqDetail) throws Exception {

    //将回调ID插入排重表，如果失败会抛异常？
    payResIdService.insertPayResId(payReqDetail.getPayDetailId());

    //更新支付关联单和支付单状态
    String payId = payOrderRelationService.selectPayOrderId(payNotifyModel.getPayDetailId());
    payOrderRelationService.updatePayOrderRelation(RelationStatus.SUCCESS.getValue(), payNotifyModel.getPayDetailId());
    payOrderService.updatePayOrderByPayId(payId, payReqDetail.getBankCode(), OrderStatus.SUCCESS.getValue(), payNotifyModel.getAgencyPayTime());

    //插入响应流水
    if (!insertResDetail(payNotifyModel, payReqDetail, payOrderInfo)) {
      logger.error("[handlePayNotify] 插入响应流水失败: {}", JSONUtil.Bean2JSON(payNotifyModel));
      throw new RuntimeException(ResultStatus.INSERT_RES_DETAIL_ERROR.getMessage());
    }
    //插入对账单
    if (!insertPayCheckWaiting(payNotifyModel, payOrderInfo, payReqDetail)) {
      logger.error("[handlePayNotify] 插入对账单失败: {}", JSONUtil.Bean2JSON(payNotifyModel));
      throw new RuntimeException(ResultStatus.INSERT_PAY_CHECK_WAITING_ERROR.getMessage());
    }
  }

  private boolean insertResDetail(PayNotifyModel payNotifyModel, PayReqDetail payReqDetail,
                                  PayOrderInfo payOrderInfo) throws Exception {
    PayResDetail payResDetail = new PayResDetail();
    //计算手续费
    PMap<String, BigDecimal> fee = computerFee(payNotifyModel, payOrderInfo, payReqDetail);
    if (fee != null) {
      payResDetail.setPayFee(fee.get("fee"));
      payResDetail.setFeeRate(fee.get("feeRate"));
    }
    //组装回调流水对象
    payResDetail.setPayDetailId(payNotifyModel.getPayDetailId());
    payResDetail.setAgencyOrderId(payNotifyModel.getAgencyOrderId());
    payResDetail.setBankOrderId(payNotifyModel.getBankOrderId());
    payResDetail.setPayStatus(1);
    payResDetail.setAgencyPayTime(payNotifyModel.getAgencyPayTime());
    payResDetail.setTrueMoney(payNotifyModel.getTrueMoney());
    payResDetail.setBankCode(payReqDetail.getBankCode());
    payResDetail.setAccessPlatform(payReqDetail.getAccessPlatform());
    payResDetail.setPayFeeType(payReqDetail.getPayFeeType());
    payResDetail.setBalance(payReqDetail.getBalance());
    payResDetail.setAgencyCode(payReqDetail.getAgencyCode());
    payResDetail.setBankCardType(payReqDetail.getBankCardType());
    payResDetail.setMerchantNo(payReqDetail.getMerchantNo());
    return payResDetailService.insertPayResDetail(payResDetail) == 1;
  }

  //计算手续费
  private PMap<String, BigDecimal> computerFee(PayNotifyModel payNotifyModel, PayOrderInfo payOrderInfo, PayReqDetail payReqDetail) throws Exception {
    BigDecimal payAmount = payNotifyModel.getTrueMoney();
    String merchantNo = payReqDetail.getMerchantNo();
    int payFeeType = payReqDetail.getPayFeeType();
    int accessPlatform = payOrderInfo.getAccessPlatForm();
    return payFeeService.getPayFee(payAmount, merchantNo, payFeeType, accessPlatform);
  }

  /**
   * 生成对账单
   */
  private boolean insertPayCheckWaiting(PayNotifyModel payNotifyModel, PayOrderInfo payOrderInfo, PayReqDetail payReqDetail) throws Exception {

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
      return payCheckWaitingService.insert(payCheckWaiting) == 1;
    }
    return false;
  }


}