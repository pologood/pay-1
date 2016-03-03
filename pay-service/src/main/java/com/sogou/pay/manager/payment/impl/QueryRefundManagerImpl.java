package com.sogou.pay.manager.payment.impl;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.model.QueryRefundModel;
import com.sogou.pay.manager.payment.QueryRefundManager;
import com.sogou.pay.service.entity.*;
import com.sogou.pay.thirdpay.api.PayPortal;
import com.sogou.pay.thirdpay.biz.enums.AgencyType;
import com.sogou.pay.service.payment.*;
//import com.sogou.pay.thirdpay.api.QueryRefundApi;
import com.sogou.pay.common.enums.OrderRefundStatus;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/16 11:11
 */

@Component
public class QueryRefundManagerImpl implements QueryRefundManager {
    private static final Logger logger = LoggerFactory.getLogger(QueryRefundManagerImpl.class);

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Autowired
    private AppService appService;
//    @Autowired
//    private QueryRefundApi queryRefundApi;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private AgencyInfoService agencyInfoService;
    @Autowired
    private PayPortal payPortal;

    @Override
    public ResultMap queryRefund(QueryRefundModel queryRefundModel) {
        ResultMap result = ResultMap.build();
        try {
            String orderId = queryRefundModel.getOrderId();
            // 1.检查是否有支付单，没有支付订单返回
            PayOrderInfo payOrderInfo = payOrderService.selectPayOrderInfoByOrderId(orderId, queryRefundModel.getAppId());
            if (null == payOrderInfo) {
                logger.error("Query Refund Request ,Does Not Exist Order,Params :" + JSONUtil.Bean2JSON(queryRefundModel));
                return ResultMap.build(ResultStatus.QUERY_REFUND_ORDER_NOT_EXIST);
            }
            // 2.检查支付单里的退款状态是否退款成功，退款成功则返回
            if (payOrderInfo.getRefundFlag() == 3) {
                result.withReturn(OrderRefundStatus.SUCCESS);
                return result;
            }
            // 3.检查是否有退款单
            List<RefundInfo> refundInfoList = refundService.selectRefundByOrderIdAndTimeDesc(orderId);
            if (CollectionUtils.isEmpty(refundInfoList)) {
                logger.error("Query Refund Request ,Does Not Exist Order Refund,Params :" + JSONUtil.Bean2JSON(queryRefundModel));
                return ResultMap.build(ResultStatus.QUERY_REFUND_REFUND_NOT_EXIST);
            }
            // 4.检查退款单状态是否有退款成功的
            List refundStatusList = new ArrayList();
            for (RefundInfo refundInfo : refundInfoList) {
                refundStatusList.add(refundInfo.getRefundStatus());
            }
            if (refundStatusList.contains(3)) {
                result.withReturn(OrderRefundStatus.SUCCESS);
                return result;
            }
            RefundInfo refundInfo = refundInfoList.get(0);
            String agencyCode = refundInfo.getAgencyCode();
            //5.获得业务平台所属公司信息
            App app = appService.selectApp(queryRefundModel.getAppId());
            if (null == app) {
                logger.error("Query Refund Request ,Does Not Exist AppInfo,Params :" + JSONUtil.Bean2JSON(queryRefundModel));
                return ResultMap.build(ResultStatus.PAY_APP_NOT_EXIST);
            }
            //6.获得商户信息
            PayAgencyMerchant queryMerchant = new PayAgencyMerchant();
            queryMerchant.setAgencyCode(agencyCode);
            queryMerchant.setCompanyCode(app.getBelongCompany());
            queryMerchant.setAppId(queryRefundModel.getAppId());
            PayAgencyMerchant agencyMerchant = payAgencyMerchantService.selectPayAgencyMerchant(queryMerchant);
            if (null == agencyMerchant) {
                //支付机构商户不存在
                logger.error("Query Refund Request ,Does Not Exist Merchant Info,Params :" + JSONUtil.Bean2JSON(queryRefundModel));
                return ResultMap.build(ResultStatus.PAY_MERCHANT_NOT_EXIST);
            }
            //6.获得支付机构信息
            String agencyType = "5";
            String sellerEmail = null;
            if (agencyCode.equals(AgencyType.WECHAT.name())) {
                agencyType = "3";
                sellerEmail = agencyMerchant.getSellerEmail();
            }
            AgencyInfo agencyInfo = agencyInfoService
                    .getAgencyInfoByCode(agencyCode, String.valueOf(payOrderInfo.getAccessPlatForm()), agencyType);
            if (null == agencyInfo) {
                //支付机构不存在
                logger.error("Query Refund Request ,Does Not Exist Agency Info,Params :" + JSONUtil.Bean2JSON(queryRefundModel));
                return ResultMap.build(ResultStatus.PAY_AGENCY_NOT_EXIST);
            }
            //7.组装参数,调用退款网关
            PMap<String, Object> pMap = new PMap<>();
            pMap.put("agencyCode", agencyCode);
            pMap.put("merchantNo", agencyMerchant.getMerchantNo());
            pMap.put("md5securityKey", agencyMerchant.getEncryptKey());
            pMap.put("queryRefundUrl", agencyInfo.getQueryRefundUrl());
            pMap.put("out_refund_no", refundInfo.getRefundId());
            pMap.put("sellerEmail", sellerEmail);
            //ResultMap refundResult = queryRefundApi.queryRefund(pMap);
            ResultMap refundResult = payPortal.queryRefund(pMap);
            // 8.根据Refund request 结果决定是否更新失败状态,成功状态根据回调结果来更新
            if (!Result.isSuccess(refundResult)) {
                return ResultMap.build(ResultStatus.THIRD_QUERY_REFUND_ERROR);
            }
            result.withReturn(refundResult.getData().get("refund_status"));
            return result;
        } catch (Exception e) {
            logger.error("Query Refund request error,params :" + JSONUtil.Bean2JSON(queryRefundModel) + "error：" + e);
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }
    }
}
