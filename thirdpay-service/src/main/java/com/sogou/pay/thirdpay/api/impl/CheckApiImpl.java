package com.sogou.pay.thirdpay.api.impl;


import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.thirdpay.api.CheckApi;
import com.sogou.pay.thirdpay.biz.AlipayCheckService;
import com.sogou.pay.thirdpay.biz.BillCheckService;
import com.sogou.pay.thirdpay.biz.TenpayCheckService;
import com.sogou.pay.thirdpay.biz.WechatCheckService;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by qibaichao on 2015/3/4.
 */
@Component
public class CheckApiImpl implements CheckApi {

    private static final Logger logger = LoggerFactory.getLogger(CheckApiImpl.class);

    @Autowired
    private AlipayCheckService alipayCheckService;

    @Autowired
    private TenpayCheckService tenpayCheckService;

    @Autowired
    private WechatCheckService wechatCheckService;

    @Autowired
    private BillCheckService billCheckService;


    @Override
    public ResultMap doQueryAlipay(PMap params) {

        ResultMap result = ResultMap.build();
        try {
            String pageNo = params.getString("pageNo");
            String startTime = params.getString("startTime");
            String endTime = params.getString("endTime");
            CheckType checkType = (CheckType) params.get("checkType");
            String merchantNo = params.getString("merchantNo");
            String key = params.getString("key");
            String pageSize = params.getString("pageSize");
            //参数校验
            if (Utils.isEmpty(pageNo, startTime, endTime, merchantNo, key, pageSize)) {
                logger.error("缺少必选参数或存在非法参数，参数：" + params);
                result.withError(ResultStatus.PARAM_ERROR);
                return result;
            }
            result = alipayCheckService.doQuery(merchantNo, checkType, startTime, endTime, pageNo, pageSize, key);
        } catch (ServiceException se) {
            logger.warn("支付宝对账查询异常: ", se);
            result.withError(se.getStatus());
        } catch (Exception e) {
            logger.error("支付宝对账查询异常: ", e);
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public ResultMap doQueryTenpay(PMap params) {

        ResultMap result = ResultMap.build();
        try {
            String merchantNo = params.getString("merchantNo");
            CheckType checkType = (CheckType) params.get("checkType");
            String checkDate = params.getString("checkDate");
            String key = params.getString("key");
            //参数校验
            if (Utils.isEmpty(checkDate, merchantNo, merchantNo, key)) {
                logger.error("缺少必选参数或存在非法参数，参数：" + params);
                result.withError(ResultStatus.PARAM_ERROR);
                return result;
            }
            result = tenpayCheckService.doQuery(merchantNo, checkType, checkDate, key);
        } catch (ServiceException se) {
            logger.warn("财付通对账查询异常: ", se);
            result.withError(se.getStatus());
        } catch (Exception e) {
            logger.error("财付通对账查询异常: ", e);
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public ResultMap doQueryWechat(PMap params) {

        ResultMap result = ResultMap.build();
        try {
            String appId = params.getString("appId");
            String merchantNo = params.getString("merchantNo");
            CheckType checkType = (CheckType) params.get("checkType");
            String checkDate = params.getString("checkDate");
            String key = params.getString("key");
            //参数校验
            if (Utils.isEmpty(checkDate, merchantNo, merchantNo, key)) {
                logger.error("缺少必选参数或存在非法参数，参数：" + params);
                result.withError(ResultStatus.PARAM_ERROR);
                return result;
            }
            result = wechatCheckService.doQuery(appId, merchantNo, checkType, checkDate, key);
        } catch (ServiceException se) {
            logger.warn("微信对账查询异常: ", se);
            result.withError(se.getStatus());
        } catch (Exception e) {
            logger.error("微信对账查询异常: ", e);
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
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
        if (Utils.isEmpty(pageNo, startTime, endTime, merchantNo, key)) {
            logger.error("缺少必选参数或存在非法参数，参数：" + params);
            result.withError(ResultStatus.PARAM_ERROR);
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
        if (Utils.isEmpty(pageNo, startTime, endTime, merchantNo, key)) {
            logger.error("缺少必选参数或存在非法参数，参数：" + params);
            result.withError(ResultStatus.PARAM_ERROR);
            return result;
        }
        // 退款订单
        result = billCheckService.doRefundQuery(merchantNo, startTime, endTime, pageNo, key);
        return result;
    }

}
