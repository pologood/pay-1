package com.sogou.pay.timer;


import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.service.enums.AgencyCode;
import com.sogou.pay.thirdpay.service.Alipay.AlipayService;
import com.sogou.pay.thirdpay.service.CMBC.CMBCService;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayService;
import com.sogou.pay.thirdpay.service.ThirdpayService;
import com.sogou.pay.thirdpay.service.Wechat.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PayPortal {

    private static final Logger logger = LoggerFactory.getLogger(PayPortal.class);

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private TenpayService tenpayService;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private CMBCService cmbcService;

    private HashMap<String, ThirdpayService> serviceHashMap;

    @Autowired
    public void init() {
        serviceHashMap = new HashMap<>();
        serviceHashMap.put(AgencyCode.ALIPAY.name(), alipayService);
        serviceHashMap.put(AgencyCode.TENPAY.name(), tenpayService);
        serviceHashMap.put(AgencyCode.WECHAT.name(), wechatService);
        serviceHashMap.put(AgencyCode.CMBC.name(), cmbcService);
    }

    public ResultMap downloadOrder(PMap params) {
        try {
            String agencyCode = params.getString("agencyCode");
            ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
            return thirdpayService.downloadOrder(params);
        } catch (ServiceException se) {
            logger.warn("[downloadOrder] 下载对账单异常", se);
            return ResultMap.build(se.getStatus());
        } catch (Exception e) {
            logger.error("[downloadOrder] 下载对账单异常", e);
            return ResultMap.build(ResultStatus.THIRD_QUERY_ERROR);
        }
    }

    public ResultMap transfer(PMap params){
        try {
            String agencyCode = params.getString("agencyCode");
            ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
            return thirdpayService.prepareTransferInfo(params);
        } catch (ServiceException se) {
            logger.warn("[transfer] 发起代付异常", se);
            return ResultMap.build(se.getStatus());
        } catch (Exception e) {
            logger.error("[transfer] 发起代付异常", e);
            return ResultMap.build(ResultStatus.THIRD_QUERY_ERROR);
        }
    }

    public ResultMap queryTransfer(PMap params){
        try {
            String agencyCode = params.getString("agencyCode");
            ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
            return thirdpayService.queryTransfer(params);
        } catch (ServiceException se) {
            logger.warn("[queryTransfer] 查询代付异常", se);
            return ResultMap.build(se.getStatus());
        } catch (Exception e) {
            logger.error("[queryTransfer] 查询代付异常", e);
            return ResultMap.build(ResultStatus.THIRD_QUERY_ERROR);
        }
    }

    public ResultMap queryTransferRefund(PMap params){
        try {
            String agencyCode = params.getString("agencyCode");
            ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
            return thirdpayService.queryTransferRefund(params);
        } catch (ServiceException se) {
            logger.warn("[queryTransferRefund] 查询代付退票异常", se);
            return ResultMap.build(se.getStatus());
        } catch (Exception e) {
            logger.error("[queryTransferRefund] 查询代付退票异常", e);
            return ResultMap.build(ResultStatus.THIRD_QUERY_ERROR);
        }
    }

}
