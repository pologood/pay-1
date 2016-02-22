package com.sogou.pay.manager.notify.impl;

import com.sogou.pay.common.Model.AppRefundNotifyModel;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.utils.*;
import com.sogou.pay.manager.notify.RefundNotifyManager;
import com.sogou.pay.service.connect.QueueNotifyProducer;
import com.sogou.pay.service.entity.PayCheckWaiting;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayResDetail;
import com.sogou.pay.service.entity.RefundInfo;
import com.sogou.pay.thirdpay.biz.enums.AgencyType;
import com.sogou.pay.service.enums.CheckStatus;
import com.sogou.pay.service.payment.*;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: hujunfei
 * Date: 2015-03-04 17:48
 * 退款回调逻辑处理，验证签名放在SecureManager中处理
 */
@Component
public class RefundNotifyManagerImpl implements RefundNotifyManager {
    private static final Logger logger = LoggerFactory.getLogger(RefundNotifyManagerImpl.class);

    @Autowired
    private RefundService refundService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private PayCheckWaitingService payCheckWaitingService;
    @Autowired
    private PayResDetailService payResDetailService;
    @Autowired
    private PayOrderRelationService payOrderRelationService;
    @Autowired
    private QueueNotifyProducer queueNotifyProducer;

    // TODO: 不同支付机构回调参数是否转换为统一的Model？回调之后的逻辑是否一样？
    @Override
    public ResultMap handleAliNotify(PMap<String, Object> params) {
        logger.info("Refund HandleAliNotify Start!Params" + JSONUtil.Bean2JSON(params));
        try {
            // 1.提取信息，一笔退款批次号对应一笔支付流水
            String refundId = params.getString("batch_no");                //退款单号
            String detailResult = params.getString("result_details");      //退款明细信息
            String[] details = detailResult.split("#");
            if (details.length != 1) {
                // 只能有一笔交易，即一笔退款批次对应一笔支付订单
                logger.error("Refund Notify Error: " + JSONUtil.Bean2JSON(params));
                return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_PARAM_ERROR);
            }
            String detail = details[0];
            String[] dealItems = detail.split("\\^");
            if (dealItems.length < 3) {
                logger.error("Refund Notify Error: " + JSONUtil.Bean2JSON(params));
                return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_PARAM_ERROR);
            }
            BigDecimal dealRefund = new BigDecimal(dealItems[1]); //退款金额
            String agencyRefundStatus = dealItems[2];             //退款状态
            // 2.查询退款订单
            RefundInfo refundInfo = refundService.selectByRefundId(refundId);
            if (refundInfo == null) {
                // 退款单不存在
                logger.error("Refund Notify Error: No Such RefundId, " + JSONUtil.Bean2JSON(params));
                return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_PARAM_ERROR);
            }
            if (refundInfo.getRefundStatus() == RefundService.REFUND_SUCCESS) {
                // 重复通知
                logger.warn("Refund Notify Warn: Notify More Times");
                return ResultMap.build();
            }
            // 3.判断退款状态和退款金额，更新退款表错误信息
            if (!"SUCCESS".equals(agencyRefundStatus)) {
                // 失败状态
                refundService.updateRefundFail(refundId, agencyRefundStatus, null);
                return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_PARAM_ERROR);
            } else if (dealRefund.compareTo(refundInfo.getRefundMoney()) != 0) {
                // 金额不一致
                logger.error("Refund Notify Error: Amount Not Fit, " + JSONUtil.Bean2JSON(params));
                return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_PARAM_ERROR);
            }

            // 4.退款成功，更新支付单退款金额->退款单退款成功状态，防止更新支付单退款金额失败导致退款超限
            return handleNotifySuccess(refundInfo, null);
        } catch (Exception e) {
            logger.error("Refund Notify Error: " + JSONUtil.Bean2JSON(params), e);
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }
    }

    @Override
    public Result handleTenNotify(PMap<String, Object> params) {
        logger.info("Refund HandleAliNotify Start!Params" + JSONUtil.Bean2JSON(params));
        try {
            // 签名校验放在SecureManager中处理
            // 1.提取信息，一笔退款批次号对应一笔支付流水
            String refundId = params.getString("out_refund_no");
            String thirdRefundId = params.getString("refund_id");
            BigDecimal refundFee = new BigDecimal(params.getString("refund_fee"));
            BigDecimal refundAmount = refundFee.divide(new BigDecimal(100));    // 财付通单位为分
            int agencyRefundStatus = Integer.parseInt(params.getString("refund_status"));

            // 2.查询退款订单
            RefundInfo refundInfo = refundService.selectByRefundId(refundId);
            if (refundInfo == null) {
                // 退款单不存在
                logger.error("Refund Notify Error: No Such RefundId, " + JSONUtil.Bean2JSON(params));
                return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_PARAM_ERROR);
            }
            if (refundInfo.getRefundStatus() == RefundService.REFUND_SUCCESS) {
                // 重复通知
                logger.warn("Refund Notify Warn: Notify More Times");
                return ResultMap.build();
            }
            // 3.1.退款更新，失败则更新退款单状态，金额不一致记录错误日志，返回
            // 财付通：4/10 success, 3/5/6 fail, 8/9/11 处理中, 1/2 未确定需重新发起, 7 转入代发
            if (agencyRefundStatus == 3 || agencyRefundStatus == 5 || agencyRefundStatus == 6) {
                // 失败状态
                refundService.updateRefundFail(refundId, String.valueOf(agencyRefundStatus), null);
                return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_PARAM_ERROR);
            } else if (agencyRefundStatus == 4 || agencyRefundStatus == 10) {
                // 成功状态
                if (refundAmount.compareTo(refundInfo.getRefundMoney()) != 0) {
                    // 金额不一致
                    logger.error("Refund Notify Error: Amount Not Fit, " + JSONUtil.Bean2JSON(params));
                    return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_PARAM_ERROR);
                }
            } else if (agencyRefundStatus == 8 || agencyRefundStatus == 9 || agencyRefundStatus == 11) {
                // 处理中
                logger.warn("Refund Notify Warn: In Processing, " + JSONUtil.Bean2JSON(params));
                return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_ERROR);
            } else {
                // 处理中
                logger.warn("Refund Notify Error: Other Status, " + JSONUtil.Bean2JSON(params));
                return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_ERROR);
            }

            // 3.2.退款更新，成功则更新支付单退款金额->退款单退款成功状态，防止更新支付单退款金额失败导致退款超限
            return handleNotifySuccess(refundInfo, thirdRefundId);
        } catch (Exception e) {
            logger.error("Refund Notify Error: " + JSONUtil.Bean2JSON(params), e);
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }
    }

    @Override
    public Result notifyApp(ResultMap result) {
        try {
            String appBgUrl = (String) result.getData().get("appBgUrl");
            Object notifyData = result.getReturnValue();
            Map<String, String> map = new HashMap<String, String>();
            if (notifyData instanceof Map) {
                map = (Map) notifyData;
            } else {
                map = (Map) BeanUtil.Bean2Map(notifyData);
            }
            AppRefundNotifyModel appRefundNotifyModel = new AppRefundNotifyModel();
            appRefundNotifyModel.setNotifyUrl(appBgUrl);
            appRefundNotifyModel.setAppId(String.valueOf(map.get("appId")));
            appRefundNotifyModel.setRefundStatus("SUCCESS");
            appRefundNotifyModel.setRefundAmount(String.valueOf(map.get("refundAmount")));
            appRefundNotifyModel.setOrderId(map.get("orderId"));
            appRefundNotifyModel.setPayId(map.get("payId"));
            appRefundNotifyModel.setPayAmount(map.get("payAmount"));
            appRefundNotifyModel.setRefundSuccessTime((map.get("refundSuccessTime")));
            appRefundNotifyModel.setSign(map.get("sign"));
            appRefundNotifyModel.setSignType("0");
            queueNotifyProducer.sendRefundMessage(appRefundNotifyModel);
            return ResultMap.build();
        } catch (Exception e) {
            logger.error("Refund Notify Handle Error: " + result.toString(), e);
            return ResultMap.build(ResultStatus.REFUND_SYSTEM_ERROR);
        }
    }

    @Override
    public Result repairRefundOrder(String refundId, String thirdRefundId) {
        try {
            // 2.查询退款订单
            RefundInfo refundInfo = refundService.selectByRefundId(refundId);
            if (refundInfo == null) {
                // 退款单不存在
                logger.error("RepairRefundOrder Error: No Such RefundId, refundId：" + refundId);
                return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_PARAM_ERROR);
            }
            if (refundInfo.getRefundStatus() == RefundService.REFUND_SUCCESS) {
                // 重复通知
                logger.error("RepairRefundOrder Error: Refund Status Already SUCCESS");
                return ResultMap.build();
            }
            return handleNotifySuccess(refundInfo, thirdRefundId);
        } catch (Exception e) {
            logger.error("RepairRefundOrder Error: " + "refundId" + refundId, e);
            return ResultMap.build(ResultStatus.REFUND_SYSTEM_ERROR);
        }
    }

    private ResultMap handleNotifySuccess(RefundInfo refundInfo, String thirdRefundId) {
        try {
            // 1.获取支付单信息
            String refundId = refundInfo.getRefundId();
            PayOrderInfo payOrderInfo = payOrderService.selectPayOrderById(refundInfo.getPayId());
            BigDecimal orderMoney = payOrderInfo.getOrderMoney();
            BigDecimal refundedMoney = payOrderInfo.getRefundMoney();
            BigDecimal allRefundMoney = refundedMoney.add(refundInfo.getRefundMoney());
            //2.对比退款金额与支付单金额
            int payRefundFlag = 2;
            if (allRefundMoney.compareTo(orderMoney) == 0) {
                payRefundFlag = 3;
            }
            //3.判断是否平账退款
            boolean isFairAcc = false;
            if (refundInfo.getPayFeeType() == RefundService.PAY_FEE_TYPE) {
                isFairAcc = true;
            }
            Date sucDate = new Date();
            //4.如果为平账退款则不更新支付单信息
            if (!isFairAcc) {
                int payUpdateResult = payOrderService.updateAddRefundMoney(refundInfo.getPayId(), refundInfo.getRefundMoney(), payRefundFlag);
                if (payUpdateResult != 1) {
                    // 支付单退款状态修改错误
                    logger.error("Refund Notify Error: Pay Order Update Fail, " + JSONUtil.Bean2JSON(refundInfo));
                    return ResultMap.build(ResultStatus.THIRD_REFUND_NOTIFY_ERROR);
                }
            }
            //5.更新退款单状态和退款成功返回时间
            refundService.updateRefundSuccess(refundId, sucDate);
            //6.查询支付回调流水表，组装对账数据
            PayResDetail payResDetail = payResDetailService.selectPayResById(refundInfo.getPayDetailId());
            String agencyCode = refundInfo.getAgencyCode();
            String merchantNo = refundInfo.getMerchantNo();
            PayCheckWaiting payCheckWaiting = new PayCheckWaiting();
            payCheckWaiting.setCreateTime(sucDate);
            payCheckWaiting.setModifyTime(sucDate);
            payCheckWaiting.setVersion((short) 0);
            payCheckWaiting.setInstructId(refundId);
            payCheckWaiting.setBizCode(CheckType.REFUND.getValue());
            payCheckWaiting.setOutTransTime(sucDate);
            payCheckWaiting.setOutOrderId(thirdRefundId);   // 第三方支付/退款订单号
            payCheckWaiting.setBizAmt(refundInfo.getRefundMoney());
            payCheckWaiting.setStatus(CheckStatus.INIT.value());
            payCheckWaiting.setAccessPlatform(payResDetail.getAccessPlatform());
            payCheckWaiting.setAppId(payOrderInfo.getAppId());
            payCheckWaiting.setCheckDate(DateUtil.format(sucDate, DateUtil.DATE_FORMAT_DAY_SHORT));
            payCheckWaiting.setAgencyCode(agencyCode);
            payCheckWaiting.setMerchantNo(merchantNo);
            //支付宝退款手续费为0
            if (refundInfo.getAgencyCode().equals(AgencyType.ALIPAY.name())) {
                payCheckWaiting.setFeeRate(BigDecimal.ZERO);
                payCheckWaiting.setCommissionFeeAmt(BigDecimal.ZERO);
            } else {
                payCheckWaiting.setFeeRate(payResDetail.getFeeRate());
                payCheckWaiting.setCommissionFeeAmt(payResDetail.getPayFee().multiply(new BigDecimal(-1)));
            }
            payCheckWaiting.setBankCode(payResDetail.getBankCode());//银行编码
            payCheckWaiting.setPayType(payResDetail.getPayFeeType());
            payCheckWaitingService.insert(payCheckWaiting);

            // 7.发送退款请求通知入队列
            ResultMap resultMaps = ResultMap.build();
            //如果为平账退款则不不通知
            if (isFairAcc) {
                return (ResultMap) resultMaps.withReturn(9);
            }
            ResultMap<Map<String, String>> resultMap = ResultMap.build();
            String notifyUrl = refundInfo.getAppBgUrl();
            if (!StringUtil.isBlank(notifyUrl)) {
                Map<String, String> data = new HashMap<>();
                data.put("orderId", payOrderInfo.getOrderId());
                data.put("payId", payOrderInfo.getPayId());
                data.put("payAmount", String.valueOf(payOrderInfo.getOrderMoney().doubleValue()));
                data.put("refundAmount", String.valueOf(refundInfo.getRefundMoney().doubleValue()));
                data.put("refundSuccessTime", DateUtil.format(sucDate, DateUtil.DATE_FORMAT_SECOND_SHORT));
                data.put("appId", String.valueOf(refundInfo.getAppId()));
                data.put("refundStatus","SUCCESS");
                data.put("signType", "0"); //签名类型
                return (ResultMap) resultMap.addItem("appBgUrl", refundInfo.getAppBgUrl())
                        .addItem("appId", refundInfo.getAppId())
                        .withReturn(data);
            }

            return ResultMap.build();
        } catch (Exception e) {
            logger.error("Refund Notify Error: " + JSONUtil.Bean2JSON(refundInfo), e);
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }
    }
}
