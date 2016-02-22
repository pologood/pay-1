package com.sogou.pay.thirdpay.api.impl;


import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.thirdpay.api.CheckApi;
import com.sogou.pay.thirdpay.biz.BillCheckService;
import com.sogou.pay.thirdpay.biz.enums.AgencyType;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
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
public class CheckApiImpl implements CheckApi {

    private static final Logger logger = LoggerFactory.getLogger(CheckApiImpl.class);

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private TenpayService tenpayService;

    @Autowired
    private BillCheckService billCheckService;

    @Autowired
    private WechatService wechatService;

    private HashMap<String, ThirdpayService> serviceHashMap;


    @Autowired
    public void init() {
        serviceHashMap = new HashMap<>();
        serviceHashMap.put(AgencyType.ALIPAY.name(), alipayService);
        serviceHashMap.put(AgencyType.TENPAY.name(), tenpayService);
        serviceHashMap.put(AgencyType.WECHAT.name(), wechatService);
    }

    @Override
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

    /**
     * 快钱支付对账数据查询
     *
     * @param params
     * @return
     */
    @Override
    public ResultMap doPayQueryBill99(PMap params) {
        ResultMap result = ResultMap.build();
        String pageNo = params.getString("pageNo");
        String startTime = params.getString("startTime");
        String endTime = params.getString("endTime");
        String merchantNo = params.getString("merchantNo");
        String key = params.getString("key");
        //参数校验
        if (StringUtil.isEmpty(pageNo, startTime, endTime, merchantNo, key)) {
            logger.error("缺少必选参数或存在非法参数，参数：" + params);
            result.withError(ResultStatus.THIRD_QUERY_PARAM_ERROR);
            return result;
        }
        // 成功支付的订单
        result = billCheckService.doPayQuery(merchantNo, startTime, endTime, pageNo, key);
        return result;
    }

    /**
     * 快钱支付退款数据查询
     *
     * @param params
     * @return
     */
    @Override
    public ResultMap doRefundQueryBill99(PMap params) {
        ResultMap result = ResultMap.build();
        String pageNo = params.getString("pageNo");
        String startTime = params.getString("startTime");
        String endTime = params.getString("endTime");
        String merchantNo = params.getString("merchantNo");
        String key = params.getString("key");
        //参数校验
        if (StringUtil.isEmpty(pageNo, startTime, endTime, merchantNo, key)) {
            logger.error("缺少必选参数或存在非法参数，参数：" + params);
            result.withError(ResultStatus.THIRD_QUERY_PARAM_ERROR);
            return result;
        }
        // 退款订单
        result = billCheckService.doRefundQuery(merchantNo, startTime, endTime, pageNo, key);
        return result;
    }

}
