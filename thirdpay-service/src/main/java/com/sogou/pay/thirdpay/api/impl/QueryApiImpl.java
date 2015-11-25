package com.sogou.pay.thirdpay.api.impl;


import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.thirdpay.api.QueryApi;
import com.sogou.pay.thirdpay.biz.AliPayService;
import com.sogou.pay.thirdpay.biz.BillPayService;
import com.sogou.pay.thirdpay.biz.TenPayService;
import com.sogou.pay.thirdpay.biz.WechatPayService;
import com.sogou.pay.thirdpay.biz.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/27 10:08
 */
@Component
public class QueryApiImpl implements QueryApi {

    private static final Logger log = LoggerFactory.getLogger(QueryApiImpl.class);
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private TenPayService tenPayService;
    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private BillPayService billPayService;

    @Override
    public ResultMap<String> queryOrder(PMap params) {
        log.info("Query Order Start!,Parameters：" + JsonUtil.beanToJson(params));
        ResultMap result = ResultMap.build();

        //1.验证参数合法性
        boolean isEmptyParams = verifyParams(params);
        if (!isEmptyParams) {
            log.error("Query Order:Lack Parameter Or Parameter Illegal,Parameters:" + JsonUtil.beanToJson(params));
            return result.build(ResultStatus.THIRD_QUERY_PARAM_ERROR);
        }
        //2.根据不同支付机构查询订单信息
        String payChannle = params.getString("agencyCode");
        try {
            switch (payChannle) {
                //1支付宝
                case "ALIPAY":
                    result = aliPayService.queryOrderInfo(params);
                    break;
                //2.财付通
                case "TENPAY":
                    result = tenPayService.queryOrderInfo(params);
                    break;
                //3.微信
                case "WECHAT":
                    result = wechatPayService.queryOrderInfo(params);
                    break;
                //4.快钱
                case "BILL99":
                    result = billPayService.queryOrderInfo(params);
                    break;
                default:
                    log.error("Query Order:Does Not Support Query Channel，Parameters:" + JsonUtil.beanToJson(params));
                    result.withError(ResultStatus.THIRD_QUERY_PARAM_ERROR);
            }
        } catch (ServiceException se) {
            log.error("Query Order:Get Query Parameters Unusually:", se + "Parameters:" + JsonUtil.beanToJson(params));
            return result.build(se.getStatus());
        } catch (Exception e) {
            log.error("Query Order:Get Query Parameters Unusually:", e + "Parameters:" + JsonUtil.beanToJson(params));
            return result.build(ResultStatus.THIRD_QUERY_SYSTEM_ERROR);
        }
        log.info("Query Order End!Parameters:" + JsonUtil.beanToJson(params) + "Return Result:" + JsonUtil.beanToJson(result));
        return result;
    }

    /**
     * 验证参数合法性
     */
    public boolean verifyParams(PMap params) {
        //1.验证共同参数是否为空
        if (Utils.isEmpty(
                params.getString("agencyCode"), params.getString("merchantNo")
                , params.getString("queryUrl"), params.getString("md5securityKey")
                , params.getString("serialNumber"))) {
            return false;
        } else {
            return true;
        }
    }
}

