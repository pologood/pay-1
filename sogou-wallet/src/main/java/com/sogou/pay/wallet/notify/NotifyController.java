package com.sogou.pay.wallet.notify;

import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.ConvertUtil;
import com.sogou.pay.wallet.enums.TranscStatus;
import com.sogou.pay.wallet.service.entity.WalletTranscTopup;
import com.sogou.pay.wallet.service.AccountService;
import com.sogou.pay.wallet.service.TransactionService;
import com.sogou.pay.wallet.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.ResourceBundle;


/**
 * Created by xiepeidong on 2016/2/23.
 */
@Controller
@RequestMapping("/notify")
public class NotifyController {

    private static Logger log = LoggerFactory.getLogger(NotifyController.class);

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;

    @RequestMapping(value = {"/sync/balance/topup"}, method = RequestMethod.POST)
    public ModelAndView syncNotifyTopup(PMap params) {
        ModelAndView view = new ModelAndView("result");
        String key = ResourceBundle.getBundle("config").getString("wallet.key");
        int this_appId = ConvertUtil.toInt(ResourceBundle.getBundle("config").getString("wallet.appid"));
        String sign = params.getString("sign");
        params.remove("sign");
        String this_sign = Util.signMD5(params, key);
        if (!sign.equals(this_sign)) {
            log.error("[syncNotifyTopup] 支付回调失败, 签名错误, 返回:" + params);
            view.addObject("success", false);
            view.addObject("error", ResultStatus.SIGNATURE_ERROR);
            return view;
        }

        int appId = ConvertUtil.toInt(params.getString("appId"));
        String orderId = params.getString("orderId");
        String payId = params.getString("payId");
        String isSuccess = params.getString("isSuccess");

        if (appId == this_appId) {
            log.error("[syncNotifyTopup] 支付回调失败, appid非法, 返回:" + params);
            view.addObject("success", false);
            view.addObject("error", ResultStatus.SYSTEM_ERROR);
            return view;
        }

        if (isSuccess.equals("T")) {
            view.addObject("success", true);
        } else {
            log.error("[syncNotifyTopup] 支付回调失败, 返回:" + params);
            view.addObject("success", false);
            view.addObject("error", ResultStatus.SYSTEM_ERROR);
        }

        return view;
    }

    @RequestMapping(value = {"/async/balance/topup"}, method = RequestMethod.POST)
    @ResponseBody
    public String asyncNotifyTopup(PMap params) {

        String key = ResourceBundle.getBundle("config").getString("wallet.key");
        int this_appId = ConvertUtil.toInt(ResourceBundle.getBundle("config").getString("wallet.appid"));
        String sign = params.getString("sign");
        params.remove("sign");
        String this_sign = Util.signMD5(params, key);
        if (!sign.equals(this_sign)) {
            log.error("[asyncNotifyTopup] 支付回调失败, 签名错误, 参数:" + params);
            return "FAIL";
        }

        int appId = ConvertUtil.toInt(params.getString("appId"));
        String orderId = params.getString("orderId");
        String payId = params.getString("payId");
        BigDecimal orderMoney = new BigDecimal(params.getString("orderMoney"));
        OrderStatus orderStatus = OrderStatus.get(params.getString("tradeStatus"));
        String isSuccess = params.getString("isSuccess");

        if(orderStatus == null){
            log.error("[asyncNotifyTopup] 支付回调失败, tradeStatus非法, 参数:" + params);
            return "FAIL";
        }
        int ret = transactionService.insertTopupResponse(appId, orderId, payId, orderMoney, orderStatus);
        if(ret <= 0){
            log.error("[asyncNotifyTopup] 添加支付回调记录失败, 参数:" + params);
            return "FAIL";
        }

        if (appId == this_appId) {
            log.error("[asyncNotifyTopup] 支付回调失败, appid非法, 参数:" + params);
            return "FAIL";
        }

        WalletTranscTopup transc = transactionService.queryTopup(orderId);
        if (transc.getStatus() == TranscStatus.SUCCESS) {
            log.info("[asyncNotifyTopup] 已经支付成功, 重复通知, 参数:" + params);
            return "SUCCESS";
        }
        if (!transc.getMoney().equals(orderMoney)) {
            log.error("[asyncNotifyTopup] 支付金额不等, 参数:" + params);
            return "FAIL";
        }

        ret = transactionService.updateTopupStatus(orderId, TranscStatus.SUCCESS);
        if (ret == 0) {
            log.warn("[asyncNotifyTopup] 并发修改支付状态, 参数:" + params);
            return "SUCCESS";
        } else if (ret < 0) {
            log.error("[asyncNotifyTopup] 修改支付状态失败, 参数:" + params);
            return "FAIL";
        }
        ret = accountService.increaseBalance(transc.getUid(), transc.getMoney());
        if (ret <= 0) {
            log.error("[asyncNotifyTopup] 修改余额失败, 参数:" + params);
            return "SUCCESS";
        }

        return "SUCCESS";
    }


}
