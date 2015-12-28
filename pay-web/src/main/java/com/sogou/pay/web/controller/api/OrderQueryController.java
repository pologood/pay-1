package com.sogou.pay.web.controller.api;

import java.util.List;

import com.sogou.pay.common.Model.QueryOrderResult;
import com.sogou.pay.common.Model.QueryRefundResult;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.manager.model.QueryRefundModel;
import com.sogou.pay.manager.payment.QueryRefundManager;
import com.sogou.pay.web.form.QueryRefundParams;
import com.sogou.pay.web.utils.ServletUtil;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.manager.model.PayOrderQueryModel;
import com.sogou.pay.manager.payment.OrderQueryManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.web.form.PayOrderQueryParams;
import com.sogou.pay.web.utils.ControllerUtil;

import javax.servlet.http.HttpServletRequest;


/**
 * 业务线查询订单退款状态入口
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/16 10:08
 */
@Controller
@RequestMapping("/orderQuery")
public class OrderQueryController {

    private static final Logger logger = LoggerFactory.getLogger(OrderQueryController.class);

    @Autowired
    private QueryRefundManager queryRefundManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private OrderQueryManager orderQueryManager;

    @Profiled(el = true, logger = "webTimingLogger", tag = "/orderQuery/pay",
            timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("pay")
    @ResponseBody
    public String pay(PayOrderQueryParams params, HttpServletRequest request) {
        ResultMap result = ResultMap.build();
        //QueryOrderResult queryOrderResult = new QueryOrderResult();
        // 0.记录请求日志
        String ip = ServletUtil.getRealIp(request);
        logger.info("Query Order Request Start!Ip：" + ip + "params:" + params);
        // 1.检查参数的完整性和合法性
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() != 0) {
            result.withError(ResultStatus.QUERY_ORDER_PARAM_ERROR);
            logger.error("Query Order Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(result));
            return JSONObject.toJSONString(result);
        }

        // 2.检查商户签名
        Result secResult = secureManager.verifyAppSign(params);
        if (!Result.isSuccess(secResult)) {
            result.withError(ResultStatus.QUERY_ORDER_SIGN_ERROR);
            logger.error("Query Order Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(result));
            return JSONObject.toJSONString(result);
        }
        // 3.处理支付订单查询
        PayOrderQueryModel payOrderModel = new PayOrderQueryModel();
        payOrderModel.setAppId(Integer.parseInt(params.getAppId()));
        payOrderModel.setOrderId(params.getOrderId());
        ResultMap queryResult = orderQueryManager.queryPayOrder(payOrderModel);
        logger.info("orderQueryManager.queryPayOrder Returns the result:" + queryResult.toString());
        if (!Result.isSuccess(queryResult)) {
            logger.error("Query Order Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(queryResult));
            return JSONObject.toJSONString(queryResult);
        }
        result.addItem("payStatus",queryResult.getReturnValue());
        logger.info("Query Order Request End!Ip：" + ip + "Result:" + JSONObject.toJSONString(result));
        return JSONObject.toJSONString(result);
    }

    /**
     * 业务线查询订单退款状态入口
     *
     * @author 用户平台事业部---高朋辉
     * @version 1.0
     * @date 2015/4/16 10:08
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/orderQuery/refund",
            timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    @ResponseBody
    @RequestMapping("/refund")
    public String queryRefund(QueryRefundParams params) {
        ResultMap result = ResultMap.build();
        logger.info("Query Order Refund Request Start!params:" + params);
        // 1.检查参数的完整性和合法性
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() != 0) {
            result.withError(ResultStatus.THIRD_Q_RF_PARAM_ERROR);
            logger.info("Query Order Refund Request End!Result:" + JsonUtil.beanToJson(result));
            return JSONObject.toJSONString(result);
        }
        // 2.检查商户签名
        Result secResult = secureManager.verifyAppSign(params);
        if (!Result.isSuccess(secResult)) {
            result.withError(ResultStatus.SIGNATURE_ERROR);
            logger.error("Query Order Refund Request End!Result:" + JsonUtil.beanToJson(result));
            return JSONObject.toJSONString(result);
        }
        // 3.处理退款订单查询
        QueryRefundModel model = new QueryRefundModel();
        model.setAppId(Integer.parseInt(params.getAppId())); //业务线id
        model.setOrderId(params.getOrderId());               //订单id
        model.setSign(params.getSign());                     //签名
        model.setSignType(params.getSignType());            //签名类型
        ResultMap queryRefundMap = queryRefundManager.queryRefund(model);
        logger.info("Query Order Refund Request,RefundManager.refund Returns the result:" + JSONObject.toJSONString(queryRefundMap));
        if (!Result.isSuccess(queryRefundMap)) {
            result.withError(queryRefundMap.getStatus());
            logger.error("Query Order Refund Request End!Result:" + JsonUtil.beanToJson(result));
            return JSONObject.toJSONString(result);
        }
        result.addItem("refundStatus", queryRefundMap.getReturnValue());
        logger.info("Query Order Refund Request End!Back Params:" + JSONObject.toJSONString(result));
        return JSONObject.toJSONString(result);

    }
}
