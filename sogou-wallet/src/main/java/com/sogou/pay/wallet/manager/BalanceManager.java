package com.sogou.pay.wallet.manager;

import com.sogou.pay.common.http.client.HttpService;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.wallet.service.AccountService;
import com.sogou.pay.wallet.service.TransactionService;
import com.sogou.pay.wallet.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ResourceBundle;


/**
 * Created by xiepeidong on 2016/2/23.
 */
@Component
public class BalanceManager {
    private static Logger log = LoggerFactory.getLogger(BalanceManager.class);

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;

    public ResultMap doTopup(int uid, BigDecimal money, String channel_code) {
        if (!channel_code.equals("ALIPAY")) {
            log.error("[doTopup] 充值失败, 充值渠道非法:", uid, money, channel_code);
            return ResultMap.build(ResultStatus.WALLET_PAY_CHANNEL_INVALID);
        }
        String orderId = Util.getOrderNo();
        //创建交易记录
        int ret = addTranscTopup(uid, money, channel_code, orderId);
        if (ret <= 0) {
            log.error("[doTopup] 充值失败, 创建交易记录失败:", uid, money, channel_code);
            return ResultMap.build(ResultStatus.SYSTEM_DB_ERROR);
        }
        //组装外部支付请求
        ResultMap result = createExternalPayRequest(uid, orderId, money, channel_code);
        return result;
    }

    public ResultMap doWithdraw(int uid, BigDecimal money, String channel_code) {
        if (!channel_code.equals("ALIPAY")) {
            log.error("[doWithdraw] 提现失败, 提现渠道非法:", uid, money, channel_code);
            return ResultMap.build(ResultStatus.WALLET_PAY_CHANNEL_INVALID);
        }
        String orderId = Util.getTransferNo();
        //创建交易记录
        int ret = addTranscWithdraw(uid, money, channel_code, orderId);
        if (ret <= 0) {
            log.error("[doWithdraw] 提现失败, 创建交易记录失败:", uid, money, channel_code);
            return ResultMap.build(ResultStatus.SYSTEM_DB_ERROR);
        }
        //发送外部转账请求
        ResultMap result = submitExternalTransferRequest(uid, orderId, money, channel_code);
        return result;
    }

    public ResultMap doTransfer(int uid, BigDecimal money, int payeeid) {
        //创建交易记录
        int ret = addTranscTransfer(uid, money, payeeid);
        if (ret <= 0) {
            log.error("[doTransfer] 转账失败, 创建交易记录失败:", uid, money, payeeid);
            return ResultMap.build(ResultStatus.SYSTEM_DB_ERROR);
        }
        //更新双方余额
        ret = accountService.transferBalance(uid, money, payeeid);
        if (ret <= 0) {
            log.error("[doTransfer] 转账失败, 更新双方余额失败:", uid, money, payeeid);
            return ResultMap.build(ResultStatus.SYSTEM_DB_ERROR);
        }
        return ResultMap.build();
    }


    private int addTranscTopup(int uid, BigDecimal money, String channel_code, String orderId) {
        return transactionService.insertTopup(uid, money, channel_code, orderId);
    }

    private int addTranscWithdraw(int uid, BigDecimal money, String channel_code, String orderId) {
        return transactionService.insertWithdraw(uid, money, channel_code, orderId);
    }

    private int addTranscTransfer(int uid, BigDecimal money, int payeeId) {
        return transactionService.insertTransfer(uid, money, payeeId);
    }


    private ResultMap createExternalPayRequest(int uid, String orderId, BigDecimal money, String channel_code) {

        String key = ResourceBundle.getBundle("config").getString("wallet.key");
        String appId = ResourceBundle.getBundle("config").getString("wallet.appid");
        String payUrl = ResourceBundle.getBundle("config").getString("payweb.payurl");
        String pageUrl = ResourceBundle.getBundle("config").getString("wallet.pageurl");
        String bgUrl = ResourceBundle.getBundle("config").getString("wallet.bgurl");

        ResultMap result = ResultMap.build();
        result.addItem("version", "v1.0");
        result.addItem("pageUrl", pageUrl);
        result.addItem("bgUrl", bgUrl);
        result.addItem("orderId", orderId);
        result.addItem("orderAmount", money);
        result.addItem("orderTime", DateUtil.format(new Date(), DateUtil.DATE_FORMAT_SECOND_SHORT));
        result.addItem("productName", "搜狗钱包充值");
        result.addItem("productNum", "1");
        result.addItem("bankId", channel_code);
        result.addItem("appId", appId);
        result.addItem("accessPlatform", "2");
        result.addItem("signType", "0");

        String sign = Util.signMD5(result.getData(), key);
        result.addItem("sign", sign);
        result.addItem("payurl", payUrl);

        return result;
    }

    private ResultMap submitExternalTransferRequest(int uid, String orderId, BigDecimal money, String channel_code) {

        ResultMap result = ResultMap.build();
        String key = ResourceBundle.getBundle("config").getString("wallet.key");
        String appId = ResourceBundle.getBundle("config").getString("wallet.appid");
        String withdrawUrl = ResourceBundle.getBundle("config").getString("payweb.withdrawurl");
        String bgUrl = ResourceBundle.getBundle("config").getString("wallet.bgurl");


        PMap requestPMap = new PMap();
        requestPMap.put("version", "v1.0");
        requestPMap.put("bgUrl", bgUrl);
        requestPMap.put("orderId", orderId);
        requestPMap.put("orderAmount", money);
        requestPMap.put("orderTime", DateUtil.format(new Date(), DateUtil.DATE_FORMAT_SECOND_SHORT));
        requestPMap.put("productName", "搜狗钱包提现");
        requestPMap.put("bankId", channel_code);
        requestPMap.put("appId", appId);
        requestPMap.put("signType", "0");

        String sign = Util.signMD5(requestPMap, key);
        requestPMap.put("sign", sign);

        Result httpResponse = HttpService.getInstance().doPost(withdrawUrl, requestPMap, "UTF-8", null);
        if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
            log.error("[submitExternalTransferRequest] Http请求失败, 返回:" + httpResponse);
            return (ResultMap) result.withError(ResultStatus.SYSTEM_ERROR);
        }
        String resContent = (String) httpResponse.getReturnValue();
        PMap resMap = JSONUtil.JSON2PMap(resContent);
        String status = resMap.getString("status");
        if (!status.equals("SUCCESS")) {
            log.error("[submitExternalTransferRequest] 转账失败, 返回:" + httpResponse);
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }

    public ResultMap queryBalance(int uid){
        BigDecimal balance = accountService.queryBalance(uid);
        if(balance==null){
            return ResultMap.build(ResultStatus.WALLET_USER_NOT_EXIST);
        }
        ResultMap result = ResultMap.build();
        result.withReturn(balance);
        return result;
    }
}
