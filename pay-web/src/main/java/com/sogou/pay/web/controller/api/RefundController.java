package com.sogou.pay.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.Model.RefundResult;
import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.manager.model.RefundModel;
import com.sogou.pay.manager.payment.RefundManager;
import com.sogou.pay.manager.notify.RefundNotifyManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.web.form.RefundParams;
import com.sogou.pay.web.utils.ControllerUtil;

import com.sogou.pay.web.utils.ServletUtil;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * 业务线申请订单退款接口入口
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/2 10:08
 */
@Controller
//@RequestMapping("/refund")
public class RefundController {

    private static final Logger logger = LoggerFactory.getLogger(RefundController.class);

    @Autowired
    private RefundManager refundManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private RefundNotifyManager refundNotifyManager;

    @Profiled(el = true, logger = "webTimingLogger", tag = "/api/refund",
            timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("/api/refund")
    @ResponseBody
    public Object refund(RefundParams params, HttpServletRequest request) {
        // 0.记录请求日志
        String ip = ServletUtil.getRealIp(request);
        logger.info("Refund Request Start!Ip：" + ip + "params:" + JsonUtil.beanToJson(params));
        ResultMap result = ResultMap.build();
        // 1.检查参数的完整性和合法性
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() != 0) {
            result.withError(ResultStatus.REFUND_PARAM_ERROR);
            logger.error("Refund Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(result));
            return JSONObject.toJSONString(result);
        }
        // 2.检查商户签名
        Result secResult = secureManager.verifyAppSign(params);
        if (!Result.isSuccess(secResult)) {
            result.withError(ResultStatus.REFUND_SIGN_ERROR);
            logger.error("Refund Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(result));
            return JSONObject.toJSONString(result);
        }
        // 3.组装参数,处理退款订单
        RefundModel refundModel = new RefundModel();
        refundModel.setAppId(Integer.parseInt(params.getAppId()));              //业务线id
        refundModel.setOrderId(params.getOrderId());                            //订单id
        refundModel.setRefundAmount(new BigDecimal(params.getRefundAmount()));  //订单退款金额
        refundModel.setBgurl(params.getBgUrl());                                //回调url
        ResultMap refResult = refundManager.refund(refundModel);
        logger.info("Refund Request,RefundManager.refund Returns Result:" + JsonUtil.beanToJson(refResult));
        if (!Result.isSuccess(refResult)) {
            result.withError(refResult.getStatus());
            if (null != refResult.getData().get("error_code")) {
                result.addItem("errorCode",refResult.getData().get("error_code").toString());
            }
            if (null != refResult.getData().get("error_info")) {
                result.addItem("errorMsg",refResult.getData().get("error_info").toString());
            }
            logger.error("Refund Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(result));
            return JSONObject.toJSONString(result);
        }
        // 4.只针对微信、快钱退款无异步回调处理处理，如果有Return Value，则在此步骤回调商户
        Object retValue = refResult.getReturnValue();
        if (retValue != null) {
            //对回调业务线参数进行加密
            Result secureResult = secureManager.appSign(retValue);
            refResult.withReturn(secureResult.getReturnValue());
            refundNotifyManager.notifyApp((ResultMap) refResult);
        }
        logger.info("Refund Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(result));
        return JSONObject.toJSONString(result);
    }

    @Profiled(el = true, logger = "webTimingLogger", tag = "/refund",
            timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("/refund")
    @ResponseBody
    public Object refund_deprecated(RefundParams params, HttpServletRequest request) {
        // 0.记录请求日志
        String ip = ServletUtil.getRealIp(request);
        logger.info("Refund Request Start!Ip：" + ip + "params:" + JsonUtil.beanToJson(params));
        RefundResult refundResult = new RefundResult();
        // 1.检查参数的完整性和合法性
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() != 0) {
            refundResult.setStatus(ResultStatus.REFUND_PARAM_ERROR.toString());
            refundResult.setMessage(ResultStatus.REFUND_PARAM_ERROR.getMessage());
            logger.error("Refund Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(refundResult));
            return JSONObject.toJSONString(refundResult);
        }
        // 2.检查商户签名
        Result secResult = secureManager.verifyAppSign(params);
        if (!Result.isSuccess(secResult)) {
            refundResult.setStatus(ResultStatus.REFUND_SIGN_ERROR.toString());
            refundResult.setMessage(ResultStatus.REFUND_SIGN_ERROR.getMessage());
            logger.error("Refund Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(refundResult));
            return JSONObject.toJSONString(refundResult);
        }
        // 3.组装参数,处理退款订单
        RefundModel refundModel = new RefundModel();
        refundModel.setAppId(Integer.parseInt(params.getAppId()));              //业务线id
        refundModel.setOrderId(params.getOrderId());                            //订单id
        refundModel.setRefundAmount(new BigDecimal(params.getRefundAmount()));  //订单退款金额
        refundModel.setBgurl(params.getBgUrl());                                //回调url
        ResultMap refResult = refundManager.refund(refundModel);
        logger.info("Refund Request,RefundManager.refund Returns Result:" + JsonUtil.beanToJson(refResult));
        if (!Result.isSuccess(refResult)) {
            refundResult.setStatus(refResult.getStatus().toString());
            refundResult.setMessage(refResult.getMessage());
            if (null != refResult.getData().get("error_code")) {
                refundResult.setErrorCode(refResult.getData().get("error_code").toString());
            }
            if (null != refResult.getData().get("error_info")) {
                refundResult.setErrorMsg(refResult.getData().get("error_info").toString());
            }
            logger.error("Refund Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(refundResult));
            return JSONObject.toJSONString(refundResult);
        }
        // 4.只针对微信、快钱退款无异步回调处理处理，如果有Return Value，则在此步骤回调商户
        Object retValue = refResult.getReturnValue();
        if (retValue != null) {
            //对回调业务线参数进行加密
            Result secureResult = secureManager.appSign(retValue);
            refResult.withReturn(secureResult.getReturnValue());
            refundNotifyManager.notifyApp((ResultMap) refResult);
        }
        refundResult.setStatus(ResultStatus.SUCCESS.toString());
        refundResult.setMessage(ResultStatus.SUCCESS.getMessage());
        logger.info("Refund Request End!Ip：" + ip + "Result:" + JsonUtil.beanToJson(refundResult));
        return JSONObject.toJSONString(refundResult);
    }

}
