package com.sogou.pay.web.controller.notify;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.manager.notify.RefundNotifyManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.web.form.notify.AliRefundNotifyParams;
import com.sogou.pay.web.form.notify.TenRefundNotifyParams;
import com.sogou.pay.web.utils.ControllerUtil;
import org.dom4j.DocumentException;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * User: hujunfei Date: 2015-03-02 18:39 支付宝异步回调处理
 */
@Controller
@RequestMapping("/notify/refund")
public class RefundNotifyController {

    private static final Logger logger = LoggerFactory.getLogger(RefundNotifyController.class);

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private RefundNotifyManager refundNotifyManager;

    /**
     * 支付宝退款异步回调处理入口
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/refund/alipay",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("/alipay/{mid}")
    @ResponseBody
    public String handleAliNotify(@PathVariable("mid") String mid, AliRefundNotifyParams params, HttpServletRequest request) {
        logger.info("AliPay Refund Notify Start!Params：" + JSONUtil.Bean2JSON(params));
        // 1.验证参数
        int merchantid;
        try {
            merchantid = Integer.parseInt(mid);
        } catch (Exception e) {
            logger.error("AliPay Refund Notify, Url Warn: " + mid, e);
            return "success";
        }
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() != 0) {
            logger.error("AliPay Refund Notify, Validate Warn: " + JSONUtil.Bean2JSON(params));
            return "success";
        }
        // 2.验证签名
        Result secResult = secureManager.verifyThirdSign(params, merchantid);
        if (!Result.isSuccess(secResult)) {
            logger.error("AliPay Refund Notify, Verify Error: " + JSONUtil.Bean2JSON(params));
            return "success";
        }
        // 3.处理第三方回调信息逻辑
        ResultMap handleNotifyResult =
                (ResultMap) refundNotifyManager.handleAliNotify(BeanUtil.Bean2PMap(params));
        //第三方回调信息逻辑失败之后返回success，并且打印失败日志
        if (!Result.isSuccess(handleNotifyResult)) {
            logger.warn("AliPay Refund Notify," + handleNotifyResult.getMessage() + ":" + JSONUtil.Bean2JSON(params));
            return "success";
        }
        Object retValue = handleNotifyResult.getReturnValue();
        if (retValue == null) {
            // 无回调数据，业务处理错误，设置Error级别
            logger.error("AliPay Refund Notify, Handle Error: " + JSONUtil.Bean2JSON(params));
            return "success";
        }
        //平账退款不通知
        if (retValue.equals(9)) {
            return "success";
        }
        // 4.处理应用回调请求
        logger.info("AliPay Refund Notify,The Application Of The Callback Request Start!");
        Result secureResult = secureManager.appSign(retValue);
        handleNotifyResult.withReturn(secureResult.getReturnValue());
        refundNotifyManager.notifyApp(handleNotifyResult);
        logger.info("AliPay Refund Notify End!");
      return "success";
    }

    /**
     * 财付通退款异步回调处理入口
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/refund/tenpay",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("/tenpay/{mid}")
    @ResponseBody
    public String handleTenNotify(@PathVariable("mid") String mid, TenRefundNotifyParams params, HttpServletRequest request) throws ServiceException, IOException, DocumentException {
        logger.info("TenPay Refund Notify Start!Params：" + JSONUtil.Bean2JSON(params));
        // 1.验证参数
        int merchantid;
        try {
            merchantid = Integer.parseInt(mid);
        } catch (Exception e) {
            logger.error("TenPay Refund Notify, Url Error: " + mid, e);
            return "success";
        }
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() != 0) {
            logger.error("TenPay Refund Notify, Validate Error: " + JSONUtil.Bean2JSON(params));
            return "success";
        }
        // 2.验证签名
        Result secResult = secureManager.verifyThirdSign(params, merchantid);
        if (!Result.isSuccess(secResult)) {
            logger.error("TenPay Refund Notify, Verify Error: " + JSONUtil.Bean2JSON(params));
            return "success";
        }
        // 3.处理第三方回调信息逻辑
        ResultMap handleNotifyResult =
                (ResultMap) refundNotifyManager.handleTenNotify(BeanUtil.Bean2PMap(params));
        //第三方回调信息逻辑失败之后返回success，并且打印失败日志
        if (!Result.isSuccess(handleNotifyResult)) {
            logger.warn("TenPay Refund Notify," + handleNotifyResult.getMessage() + ":" + JSONUtil.Bean2JSON(params));
            return "success";
        }
        Object retValue = handleNotifyResult.getReturnValue();
        if (retValue == null) {
            // 无回调数据，业务处理错误，设置Error级别
            logger.error("TenPay Refund Notify, Handle Error: " + JSONUtil.Bean2JSON(params));
            return "success";
        }
        if (retValue.equals(9)) {
            return "success";
        }
        // 4.处理应用回调请求
        logger.info("TenPay Refund Notify,The Application Of The Callback Request Start!");
        Result secureResult = secureManager.appSign(retValue);
        handleNotifyResult.withReturn(secureResult.getReturnValue());
        refundNotifyManager.notifyApp(handleNotifyResult);
        logger.info("TenPay Refund Notify End!");
        return "success";
    }

}
