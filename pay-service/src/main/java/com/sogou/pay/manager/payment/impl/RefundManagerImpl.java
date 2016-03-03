package com.sogou.pay.manager.payment.impl;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.manager.model.RefundModel;
import com.sogou.pay.manager.model.thirdpay.FairAccRefundModel;
import com.sogou.pay.manager.payment.RefundManager;
import com.sogou.pay.service.entity.*;
import com.sogou.pay.service.enums.*;
import com.sogou.pay.service.payment.*;
import com.sogou.pay.service.utils.Constant;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;
//import com.sogou.pay.thirdpay.api.RefundApi;
import com.sogou.pay.thirdpay.api.PayPortal;
import com.sogou.pay.thirdpay.biz.enums.AgencyType;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import org.apache.commons.collections.CollectionUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 退款处理实现类
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/2 10:08
 */
@Component
public class RefundManagerImpl implements RefundManager {
    private static final Logger logger = LoggerFactory.getLogger(RefundManagerImpl.class);

    private static final BigDecimal ZERO = BigDecimal.ZERO;

//    @Autowired
//    private RefundApi refundApi;
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

    @Profiled(el = true, logger = "dbTimingLogger", tag = "RefundManager_refund",
            timeThreshold = 100, normalAndSlowSuffixesEnabled = true)
    @Override
    public ResultMap refund(RefundModel model) {
        try {
            //1.验证订单相关信息
            ResultMap orderCheckMap = checkOrderInfo(model);
            if (!Result.isSuccess(orderCheckMap)) {
                return orderCheckMap;
            }
            PayOrderInfo payOrderInfo = (PayOrderInfo) orderCheckMap.getReturnValue();
            //2.根据支付单payId查询退款表里面所有初始化的退款记录
            List<RefundInfo> refundInfoList = refundService.selectByPayIdAndRefundStatus(payOrderInfo.getPayId(), RefundStatus.SUCCESS.getValue());
            if (CollectionUtils.isNotEmpty(refundInfoList)) {
                // 已经退款成功
                logger.error("Refund Request ,Already SUCCESS,params :" + JSONUtil.Bean2JSON(model));
               // return ResultMap.build(ResultStatus.REFUND_REFUND_ALREADY_DONE);
            }
            //3.查订单与流水关联表
            PayOrderRelation payOrderRelation = new PayOrderRelation();
            payOrderRelation.setPayId(payOrderInfo.getPayId());
            payOrderRelation.setInfoStatus(RelationStatus.SUCCESS.getValue());
            List<PayOrderRelation> relations = payOrderRelationService.selectPayOrderRelation(payOrderRelation);
            if (CollectionUtils.isEmpty(relations)) {
                // 无Refund Request 单
                logger.error("Refund Request ,No Payment Request,params :" + JSONUtil.Bean2JSON(model));
                return ResultMap.build(ResultStatus.REFUND_PARAM_ERROR);
            }
            //4.查支付回调流水表
            payOrderRelation = relations.get(0);
            PayResDetail payResDetail = payResDetailService.selectPayResById(payOrderRelation.getPayDetailId());
            if (null == payResDetail) {
                logger.error("Refund Request ,PayResDetail Not Exist,params :" + payOrderRelation.getPayDetailId());
                return ResultMap.build(ResultStatus.REFUND_PARAM_ERROR);
            }
            //5.查支付商户信息
            String agencyCode = payResDetail.getAgencyCode(); //支付机构编码
            String merchantNo = payResDetail.getMerchantNo(); //支付机构商户号
            PayAgencyMerchant agencyMerchant = payAgencyMerchantService
                    .selectByAgencyAndMerchant(agencyCode, merchantNo);
            if (null == agencyMerchant) {
                logger.error("Refund Request ,Query AgencyMerchant Error,params :" + agencyCode, merchantNo);
                return ResultMap.build(ResultStatus.REFUND_PARAM_ERROR);
            }
            //5.查支付机构信息
            AgencyInfo agencyInfo = agencyInfoService
                    .getAgencyInfoByCode(agencyCode, String.valueOf(payResDetail.getAccessPlatform()), String.valueOf(payResDetail.getPayFeeType()));
            if (null == agencyInfo) {
                logger.error("Refund Request ,Query Agency Error,params :" + agencyCode);
                return ResultMap.build(ResultStatus.REFUND_PARAM_ERROR);
            }
            // 6.插入退款单中退款记录
            String refundId = sequencerGenerator.getRefundDetailId(); //订单号
            boolean insertResult = insertRefundInfo(refundId, model, payOrderInfo.getPayId(), payOrderInfo.getOrderMoney(), payResDetail);
            if (!insertResult) {
                logger.error("FairAccountRefund Request,Add The Order Abnormal,params :" + JSONUtil.Bean2JSON(model));
                return ResultMap.build(ResultStatus.REFUND_DB_ERROR);
            }
            //7.调用退款网关
            ResultMap refundResult = refundOrder(payResDetail, model.getRefundAmount(), payOrderInfo.getOrderMoney(), agencyInfo, agencyMerchant, refundId);
            if (!Result.isSuccess(refundResult)) {
                return refundResult;
            }
            //8.微信支付无回调,直接处理成功信息
            ResultMap wechatResult = refundSuccessInfo(model, payResDetail, payOrderInfo, refundResult, refundId, agencyCode, merchantNo);
            return wechatResult;
        } catch (Exception e) {
            logger.error("Refund Request error,params :" + JSONUtil.Bean2JSON(model) + "error：" + e);
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }
    }

    /**
     * 验证订单的金额、支付状态、退款标识等信息
     *
     * @param model 退款请求参数
     * @author 用户平台事业部---高朋辉
     */
    private ResultMap checkOrderInfo(RefundModel model) {
        ResultMap result = ResultMap.build();
        try {
            //1.验证退款金额
            BigDecimal refundAmount = model.getRefundAmount(); //退款金额
            String orderId = model.getOrderId();               //业务线订单号
            int appId = model.getAppId();                      //业务线ID
            //2.根据业务线订单号、业务线ID查询唯一订单信息
            PayOrderInfo payOrderInfo = payOrderService.selectPayOrderInfoByOrderId(orderId, appId);
            if (null == payOrderInfo) {
                logger.error("Refund Request ,Check PayOrder Anomaly,params :" + JSONUtil.Bean2JSON(model));
                return ResultMap.build(ResultStatus.REFUND_ORDER_NOT_EXIST);
            }

            if (PayOrderStatus.SUCCESS.getValue() != payOrderInfo.getPayOrderStatus()) {
                logger.error("Refund Request ,PayOrder Not To Pay Success,params :" + JSONUtil.Bean2JSON(model));
                return ResultMap.build(ResultStatus.REFUND_ORDER_NOT_PAY);
            }
            //3.检查支付订单是否已经退款
            if (RefundFlag.SUCCESS.getValue() == payOrderInfo.getRefundFlag()) {
                logger.error("Refund Request ,PayOrder Already Refund,params :" + JSONUtil.Bean2JSON(model));
              //  return ResultMap.build(ResultStatus.REFUND_REFUND_ALREADY_DONE);
            }
            //4.检查退款金额与支付金额是否相同
            BigDecimal payMoney = payOrderInfo.getOrderMoney();            //订单支付金额
            // BigDecimal notRefund = payMoney.subtract(payRefundMoney);   //没有退款的金额
            // BigDecimal payRefundMoney = payOrderInfo.getRefundMoney();  //订单退款金额
            if(refundAmount==null){
                model.setRefundAmount(payMoney);
            }else if (refundAmount.compareTo(payMoney) != 0) {
                // 退款金额不等于余额
                logger.error("Refund Request ,The Refund Amount Ss Not Equal Pay Amount,params :" + JSONUtil.Bean2JSON(model));
                return ResultMap.build(ResultStatus.REFUND_PARTIAL_REFUND);
            }
            result.withReturn(payOrderInfo);
            return result;
        } catch (Exception e) {
            logger.error("Refund Request error,params :" + JSONUtil.Bean2JSON(model) + "error：" + e);
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }
    }

    /**
     * 组装参数插入退款信息表
     *
     * @param refundId     退款ID
     * @param model        退款请求参数
     * @param payId        支付ID
     * @param orderMoney   订单支付金额
     * @param payResDetail 支付回调流水
     * @author 用户平台事业部---高朋辉
     */
    private boolean insertRefundInfo(String refundId, RefundModel model, String payId, BigDecimal orderMoney, PayResDetail payResDetail) {
        try {
            RefundInfo refundInfo = new RefundInfo();
            refundInfo.setAppId(model.getAppId());                       //appId
            refundInfo.setRefundId(refundId);                            //退款号
            refundInfo.setPayId(payId);                //支付ID
            refundInfo.setPayDetailId(payResDetail.getPayDetailId());    //支付回调ID
            refundInfo.setAppBgUrl(model.getBgurl());                    //业务线异步回调url
            refundInfo.setOrderId(model.getOrderId());                   //订单号
            refundInfo.setOrderMoney(orderMoney);      //订单支付金额
            refundInfo.setAgencyCode(payResDetail.getAgencyCode());      //支付机构编码
            refundInfo.setMerchantNo(payResDetail.getMerchantNo());      //商户号
            refundInfo.setUseBalance(ZERO);
            refundInfo.setRefundMoney(model.getRefundAmount());          //退款金额
            refundInfo.setNetBalanceRefund(model.getRefundAmount());     //退款金额
            refundInfo.setBalanceRefund(ZERO);
            refundInfo.setRefundStatus(RefundService.REFUND_INIT);       //退款初始状态
            refundInfo.setTaskStatus(1);                                 //任务初始状态
            refundInfo.setRefundReqTime(new Date());                     //请求时间
            int insertResult = refundService.insertRefundInfo(refundInfo);
            if (insertResult != 1) {
                return false;
            }
        } catch (Exception e) {
            logger.error("Refund Request error,params :" + JSONUtil.Bean2JSON(model) + "error：" + e);
            return false;
        }
        return true;
    }

    /**
     * 组装参数、请求退款网关
     *
     * @param payResDetail   支付回调流水
     * @param refundAmount   退款金额
     * @param orderMoney     支付金额
     * @param agencyInfo     支付机构信息
     * @param agencyMerchant 支付商户信息
     * @param refundId       退款单号
     * @author 用户平台事业部---高朋辉
     */
    private ResultMap refundOrder(PayResDetail payResDetail, BigDecimal refundAmount, BigDecimal orderMoney, AgencyInfo agencyInfo,
                                  PayAgencyMerchant agencyMerchant, String refundId) {
        try {
            PMap<String, Object> pMap = new PMap<>();
            pMap.put("agencyCode", payResDetail.getAgencyCode());                                     //支付机构编码
            pMap.put("merchantNo", payResDetail.getMerchantNo());                                     //商户号
            pMap.put("refundUrl", agencyInfo.getRefundUrl());                                         //退款请求url
            String notifyUrl = ResourceBundle.getBundle("config").getString("refund.back.url") +
                    payResDetail.getAgencyCode().toLowerCase() + "/" + agencyMerchant.getId();
            pMap.put("refundNotifyUrl", notifyUrl);                                                   //异步回调url
            pMap.put("md5securityKey", agencyMerchant.getEncryptKey());                               //MD5加密秘钥
            pMap.put("publicCertFilePath", agencyMerchant.getPubKeypath());                           //公钥证书地址
            pMap.put("privateCertFilePath", agencyMerchant.getPrivateKeypath());                      //私钥证书地址
            pMap.put("refundSerialNumber", refundId);                                                 //订单号
            pMap.put("refundReqTime", DateUtil.format(new Date(), DateUtil.DATE_FORMAT_SECOND_SHORT));// 请求时间
            pMap.put("serialNumber", payResDetail.getPayDetailId());                                  //订单号
            pMap.put("agencySerialNumber", payResDetail.getAgencyOrderId());                          //支付机构订单号
            pMap.put("refundAmount", String.valueOf(refundAmount.doubleValue()));                     //退款金额
            pMap.put("totalAmount", String.valueOf(orderMoney.doubleValue()));                        //支付金额
            String sellerEmail = agencyMerchant.getSellerEmail();
            pMap.put("sellerEmail", sellerEmail);                                                     //商户邮箱或者公众号ID
            //ResultMap refundResult = refundApi.refundOrder(pMap);
            ResultMap refundResult = payPortal.refundOrder(pMap);
            if (!Result.isSuccess(refundResult)) {
                String errorCode = (String) refundResult.getData().get("error_code");                 //退款错误码
                String errorInfo = (String) refundResult.getData().get("error_info");                 //退款错误信息
                refundService.updateRefundFail(refundId, errorCode, errorInfo);
                return ResultMap.build(ResultStatus.THIRD_REFUND_ERROR);
            }
            return refundResult;
        } catch (Exception e) {
            logger.error("Refund Request RefundOrder error,params :" + JSONUtil.Bean2JSON(payResDetail) + "error：" + e);
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }
    }

    /**
     * 组装参数、处理微信、退款退款成功之后的业务信息
     *
     * @param model        退款请求参数
     * @param payResDetail 支付回调流水
     * @param payOrderInfo 支付单信息
     * @param refundResult 退款写入result
     * @param refundId     退款单号
     * @param agencyCode   支付机构编码
     * @param merchantNo   商户号
     * @author 用户平台事业部---高朋辉
     */
    private ResultMap refundSuccessInfo(RefundModel model, PayResDetail payResDetail, PayOrderInfo payOrderInfo,
                                        ResultMap refundResult, String refundId, String agencyCode, String merchantNo) {
        try {
            if (AgencyType.WECHAT == AgencyType.getType(payResDetail.getAgencyCode()) || AgencyType.BILL99 == AgencyType.getType(payResDetail.getAgencyCode())) {
                int payRefundFlag = 2;
                BigDecimal allRefundMoney = payOrderInfo.getRefundMoney().add(model.getRefundAmount());
                if (allRefundMoney.compareTo(payOrderInfo.getOrderMoney()) == 0) {
                    payRefundFlag = 3;
                }
                Date sucDate = new Date();
                int payUpdateResult = payOrderService.updateAddRefundMoney(payOrderInfo.getPayId(), model.getRefundAmount(), payRefundFlag);
                if (payUpdateResult != 1) {
                    // 支付单退款状态修改错误
                    logger.info("Refund Request ,update payOrder refund status error!,params :" + JSONUtil.Bean2JSON(model));
                    return ResultMap.build(ResultStatus.REFUND_DB_ERROR);
                }
                refundService.updateRefundSuccess(refundId, sucDate);
                PayCheckWaiting payCheckWaiting = new PayCheckWaiting();
                payCheckWaiting.setCreateTime(sucDate);                                                //入库时间
                payCheckWaiting.setModifyTime(sucDate);                                                //修改时间
                payCheckWaiting.setVersion((short) 0);                                                 //版本号
                payCheckWaiting.setInstructId(refundId);                                               //我方流水号
                payCheckWaiting.setBizCode(CheckType.REFUND.getValue());                               //流水类型
                payCheckWaiting.setOutTransTime(sucDate);                                              //对方交易时间
                payCheckWaiting.setOutOrderId((String) refundResult.getData().get("third_refund_id")); //对方流水号
                payCheckWaiting.setBizAmt(model.getRefundAmount());                                    //对帐金额-退款金额
                payCheckWaiting.setStatus(CheckStatus.INIT.value());                                   //对账状态
                payCheckWaiting.setAccessPlatform(payResDetail.getAccessPlatform());                   //接入平台
                payCheckWaiting.setAppId(model.getAppId());                                            //业务线ID
                payCheckWaiting.setCheckDate(DateUtil.format(sucDate, DateUtil.DATE_FORMAT_DAY_SHORT));//对账日期
                payCheckWaiting.setAgencyCode(agencyCode);                                             //机构编码
                payCheckWaiting.setMerchantNo(merchantNo);                                             //商户号
                payCheckWaiting.setFeeRate(payResDetail.getFeeRate());                                 //费率
                payCheckWaiting.setCommissionFeeAmt(payResDetail.getPayFee().multiply(new BigDecimal(-1)));//手续费
                payCheckWaiting.setBankCode(payResDetail.getBankCode());                                   //银行编码
                payCheckWaiting.setPayType(payResDetail.getPayFeeType());                                  //付款方式
                payCheckWaitingService.insert(payCheckWaiting);

                // 4.发送Refund Request 通知入队列
                logger.info("Send out  Refund Request to queue,params:" + JSONUtil.Bean2JSON(model));
                String appBgUrl = model.getBgurl();
                if (!StringUtil.isBlank(appBgUrl)) {
                    Map<String, String> data = new HashMap<>();
                    data.put("orderId", payOrderInfo.getOrderId());
                    data.put("payId", payOrderInfo.getPayId());
                    data.put("payAmount", String.valueOf(payOrderInfo.getOrderMoney().doubleValue()));
                    data.put("refundAmount", String.valueOf(model.getRefundAmount().doubleValue()));
                    data.put("refundSuccessTime", DateUtil.format(sucDate, DateUtil.DATE_FORMAT_SECOND_SHORT));
                    data.put("appId", String.valueOf(model.getAppId()));
                    data.put("refundStatus","SUCCESS");
                    data.put("signType", "0"); //签名类型
                    ResultMap<Map<String, String>> resultMap = ResultMap.build();
                    resultMap.addItem("appBgUrl", model.getBgurl());
                    resultMap.addItem("appId", model.getAppId());
                    resultMap.withReturn(data);
                    return resultMap;
                }
            }
            return ResultMap.build();
        } catch (Exception e) {
            logger.error("Refund Request error,params :" + JSONUtil.Bean2JSON(model) + "error：" + e);
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }
    }

    /**
     * 重复支付-平账退款
     *
     * @author 用户平台事业部---高朋辉
     */
    @Override
    public Result fairAccountRefund(FairAccRefundModel model) {
        logger.info("FairAccountRefund Request start!payDetailId" + JSONUtil.Bean2JSON(model));
        int appId = model.getAppId();
        String orderId = model.getOrderId();
        String payId = model.getPayId();
        String payDetailId = model.getPayDetailId();
        try {
            //1.根据支付回调ID查询记录
            PayResDetail payResDetail = payResDetailService.selectPayResById(payDetailId);
            if (payResDetail == null || Constant.PAYS_TATUS != payResDetail.getPayStatus()) {
                logger.error("FairAccountRefund Request,Check payOrder anomaly Or payOrder not to pay success,params :" + payDetailId);
                return ResultMap.build(ResultStatus.FAIL_ACC_REFUND_NOT_REQ_ERROR);
            }
            BigDecimal refundAmount = payResDetail.getTrueMoney();
            //2.查询支付机构商户信息
            String agencyCode = payResDetail.getAgencyCode();
            String merchantNo = payResDetail.getMerchantNo();
            PayAgencyMerchant agencyMerchant = payAgencyMerchantService
                    .selectByAgencyAndMerchant(agencyCode, merchantNo);
            if (null == agencyMerchant) {
                logger.error("FairAccountRefund Request,Query AgencyMerchant Error,params :" + agencyCode, merchantNo);
                return ResultMap.build(ResultStatus.FAIL_ACC_REFUND_NOT_MERCHANT_ERROR);
            }
            //3.查询支付机信息
            AgencyInfo agencyInfo = agencyInfoService
                    .getAgencyInfoByCode(agencyCode, String.valueOf(payResDetail.getAccessPlatform()), String.valueOf(payResDetail.getPayFeeType()));
            if (null == agencyInfo) {
                logger.error("FairAccountRefund Request,Query Agency Error,params :" + agencyCode);
                return ResultMap.build(ResultStatus.FAIL_ACC_REFUND_NOT_AGENCY_ERROR);
            }
            // 4.插入退款单中退款记录
            String refundId = sequencerGenerator.getRefundDetailId();
            RefundInfo refundInfo = new RefundInfo();
            refundInfo.setAppId(appId);
            refundInfo.setRefundId(refundId);
            refundInfo.setPayId(payId);
            refundInfo.setPayDetailId(payDetailId);
            refundInfo.setOrderId(orderId);
            refundInfo.setOrderMoney(payResDetail.getTrueMoney());
            refundInfo.setAgencyCode(payResDetail.getAgencyCode());
            refundInfo.setMerchantNo(payResDetail.getMerchantNo());
            refundInfo.setPayFeeType(9);//平账退款
            refundInfo.setUseBalance(ZERO);
            refundInfo.setRefundMoney(refundAmount);
            refundInfo.setNetBalanceRefund(refundAmount);
            refundInfo.setBalanceRefund(ZERO);
            refundInfo.setRefundStatus(RefundService.REFUND_INIT);
            refundInfo.setTaskStatus(1);
            refundInfo.setRefundReqTime(new Date());
            int insertResult = refundService.insertRefundInfo(refundInfo);
            if (insertResult != 1) {
                logger.error("FairAccountRefund Request,Add The Order Abnormal,params :" + payDetailId);
                return ResultMap.build(ResultStatus.REFUND_DB_ERROR);
            }
            //7.调用退款网关
            ResultMap refundResult = refundOrder(payResDetail, payResDetail.getTrueMoney(), payResDetail.getTrueMoney(),
                    agencyInfo, agencyMerchant, refundId);
            if (!Result.isSuccess(refundResult)) {
                return refundResult;
            }
            // 8.微信支付无回调,直接处理成功信息
            ResultMap wechatResult = fairWechatRefundSuccessInfo(model, payResDetail, refundResult, refundId, agencyCode, merchantNo);
            if (!Result.isSuccess(wechatResult)) {
                return wechatResult;
            }
            return ResultMap.build();
        } catch (Exception e) {
            logger.error("FairAccountRefund Request Error,params :" + payDetailId + "error：" + e);
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }

    }

    /**
     * 平账退款-处理微信退款成功之后的业务信息
     *
     * @param model        退款请求参数
     * @param payResDetail 支付回调流水
     * @param refundResult 退款写入result
     * @param refundId     退款单号
     * @param agencyCode   支付机构编码
     * @param merchantNo   商户号
     * @author 用户平台事业部---高朋辉
     */
    private ResultMap fairWechatRefundSuccessInfo(FairAccRefundModel model, PayResDetail payResDetail,
                                                  ResultMap refundResult, String refundId, String agencyCode, String merchantNo) {
        try {
            if (AgencyType.WECHAT == AgencyType.getType(payResDetail.getAgencyCode())) {
                Date sucDate = new Date();
                refundService.updateRefundSuccess(refundId, sucDate);
                PayCheckWaiting payCheckWaiting = new PayCheckWaiting();
                payCheckWaiting.setCreateTime(sucDate);
                payCheckWaiting.setModifyTime(sucDate);
                payCheckWaiting.setVersion((short) 0);
                payCheckWaiting.setInstructId(refundId);
                payCheckWaiting.setBizCode(CheckType.REFUND.getValue());
                payCheckWaiting.setOutTransTime(sucDate);
                payCheckWaiting.setOutOrderId((String) refundResult.getData().get("third_refund_id"));
                payCheckWaiting.setBizAmt(payResDetail.getTrueMoney());
                payCheckWaiting.setCommissionFeeAmt(ZERO);
                payCheckWaiting.setStatus(CheckStatus.INIT.value());
                payCheckWaiting.setAccessPlatform(payResDetail.getAccessPlatform());
                payCheckWaiting.setAppId(model.getAppId());
                payCheckWaiting.setCheckDate(DateUtil.format(sucDate, DateUtil.DATE_FORMAT_DAY_SHORT));
                payCheckWaiting.setAgencyCode(agencyCode);
                payCheckWaiting.setMerchantNo(merchantNo);
                payCheckWaiting.setFeeRate(payResDetail.getFeeRate());
                payCheckWaiting.setCommissionFeeAmt(payResDetail.getPayFee().multiply(new BigDecimal(-1)));
                payCheckWaiting.setBankCode(payResDetail.getBankCode());//银行编码
                payCheckWaiting.setPayType(payResDetail.getPayFeeType());
                payCheckWaitingService.insert(payCheckWaiting);
            }
            return ResultMap.build();
        } catch (Exception e) {
            logger.error("FairAccountRefund Request Error,params :" + model.getPayDetailId() + "error：" + e);
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }
    }
}
