package com.sogou.pay.manager.payment.impl;

import java.util.ArrayList;
import java.util.List;

import com.sogou.pay.service.enums.PayOrderStatus;
import com.sogou.pay.thirdpay.api.PayPortal;
import com.sogou.pay.thirdpay.biz.enums.OrderState;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.model.PayOrderQueryModel;
import com.sogou.pay.manager.payment.OrderQueryManager;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.entity.PayReqDetail;
import com.sogou.pay.service.payment.AppService;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.service.payment.PayOrderRelationService;
import com.sogou.pay.service.payment.PayOrderService;
import com.sogou.pay.service.payment.PayReqDetailService;
import com.sogou.pay.service.utils.Constant;
//import com.sogou.pay.thirdpay.api.QueryApi;

@Component
public class OrderQueryManagerImpl implements OrderQueryManager {

    private static final Logger logger = LoggerFactory.getLogger(OrderQueryManagerImpl.class);
    
    //@Autowired
    //private QueryApi qeuryApi;

    @Autowired
    private PayPortal payPortal;
    @Autowired
    private AppService appService;
    
    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;
    
    @Autowired
    private PayReqDetailService payReqDetailService;
    
    @Autowired
    private PayOrderRelationService payOrderRelationService;
    @Autowired
    private PayOrderService payOrderService;

    @Profiled(el = true, logger = "dbTimingLogger", tag = "OrderQueryManager_queryPayOrder",
            timeThreshold = 50, normalAndSlowSuffixesEnabled = true)
    @Override
    public ResultMap queryPayOrder(PayOrderQueryModel model) {
        ResultMap result = ResultMap.build();
        //获取商户订单ID
        App app = appService.selectApp(model.getAppId());
        if(null == app){
            logger.error("appId is not found.appId = " + model.getAppId());
            result.withError(ResultStatus.PAY_APP_NOT_EXIST);
            return result;
        }
        //根据订单查询支付回调信息
        List<String> resultStatusList = new ArrayList<String>();
        List<PayReqDetail> payReqDetailList = null;
        try {
            //1.根据orderId和appId查询订单信息
            PayOrderInfo payOrderInfo = payOrderService.selectPayOrderInfoByOrderId(model.getOrderId(), app.getAppId());
            if(null == payOrderInfo){
                logger.error("【OrderQuery】payOrderInfo is not found.orderId = " + model.getOrderId()+",appId = " + app.getAppId());
                result.withError(ResultStatus.PAY_ORDER_NOT_EXIST);
                return result;
            }
            // 2.检查支付单里的支付状态是否成功，成功则返回
            if (payOrderInfo.getPayOrderStatus() == PayOrderStatus.SUCCESS.getValue()) {
                result.withReturn(OrderState.SUCCESS);
               // return result;
            }
            //2.根据payId查询关联表
            PayOrderRelation paramRelation = new PayOrderRelation();
            paramRelation.setPayId(payOrderInfo.getPayId());
            List<PayOrderRelation> relationList = payOrderRelationService.selectPayOrderRelation(paramRelation);
            if(null == relationList || relationList.size() == 0){
                logger.error("PayOrderRelation is not found.payId = " + payOrderInfo.getPayId());
                result.withError(ResultStatus.PAY_ORDER_RELATION_NOT_EXIST);
                return result;
            }
            //3.查询支付单流水信息
            payReqDetailList = payReqDetailService.selectPayReqByReqIdList(relationList);
            if(null == payReqDetailList){
                logger.error("payReqDetail is not found.orderId = " + model.getOrderId()+",appId = " + app.getAppId());
                result.withError(ResultStatus.PAY_ORDER_NOT_EXIST);
                return result;
            }
            for(PayReqDetail payReqDetail : payReqDetailList){
                PayAgencyMerchant merchant = new PayAgencyMerchant();
                merchant.setAgencyCode(payReqDetail.getAgencyCode());
                merchant.setAppId(app.getAppId());
                merchant.setCompanyCode(app.getBelongCompany());
                PayAgencyMerchant merchantQuery = payAgencyMerchantService.selectPayAgencyMerchant(merchant);
                if(null == merchantQuery){
                    logger.error("payAgencyMerchant is not found.agencyCode = " + payReqDetail.getAgencyCode()
                            +"appId = " + app.getAppId() + "companyCode = " + app.getBelongCompany());
                    result.withError(ResultStatus.PAY_MERCHANT_NOT_EXIST);
                    return result;
                }
                //组装网关参数
                PMap map = new PMap();
                map.put("agencyCode", merchantQuery.getAgencyCode());
                map.put("merchantNo", merchantQuery.getMerchantNo());
                map.put("sellerEmail", merchantQuery.getSellerEmail());
                map.put("md5securityKey", merchantQuery.getEncryptKey());
                map.put("queryUrl", Constant.QUERY_URL_MAP.get(merchantQuery.getAgencyCode()));
                map.put("serialNumber", payReqDetail.getPayDetailId());
                logger.info("queryAPI params : " + map.toString());
                //ResultMap apiResult = qeuryApi.queryOrder(map);
                ResultMap apiResult = payPortal.queryOrder(map);
                if(!Result.isSuccess(apiResult)){
                    logger.error("错误码："+apiResult.getStatus().getCode()+"，错误信息" + apiResult.getStatus().getMessage());
                    result.withError(apiResult.getStatus());
                    return result;
                }
                resultStatusList.add(apiResult.getData().get("order_state").toString());
            }
            result.withReturn(resultStatusList);
        } catch (Exception e) {
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }

}
