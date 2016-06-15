package com.sogou.pay.portal;


import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.service.enums.AgencyCode;
import com.sogou.pay.thirdpay.service.Alipay.AlipayService;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayService;
import com.sogou.pay.thirdpay.service.ThirdpayService;
import com.sogou.pay.thirdpay.service.Wechat.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by qibaichao on 2015/3/4.
 */
@Component
public class CheckPortal {

    private static final Logger logger = LoggerFactory.getLogger(CheckPortal.class);

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private TenpayService tenpayService;


    @Autowired
    private WechatService wechatService;

    private HashMap<String, ThirdpayService> serviceHashMap;


    @Autowired
    public void init() {
        serviceHashMap = new HashMap<>();
        serviceHashMap.put(AgencyCode.ALIPAY.name(), alipayService);
        serviceHashMap.put(AgencyCode.TENPAY.name(), tenpayService);
        serviceHashMap.put(AgencyCode.WECHAT.name(), wechatService);
    }

    public ResultMap doQuery(PMap params) {
        try {
            String agencyCode = params.getString("agencyCode");
            ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
            return thirdpayService.downloadOrder(params);
        } catch (ServiceException se) {
            logger.warn("[doQuery] 下载对账单异常", se);
            return ResultMap.build(se.getStatus());
        } catch (Exception e) {
            logger.error("[doQuery] 下载对账单异常", e);
            return ResultMap.build(ResultStatus.THIRD_QUERY_ERROR);
        }
    }

}
