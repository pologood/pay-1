package com.sogou.pay.wallet.controller;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.ConvertUtil;
import com.sogou.pay.wallet.service.entity.WalletUser;
import com.sogou.pay.wallet.manager.BalanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;


/**
 * Created by xiepeidong on 2016/2/23.
 */
@Controller
@RequestMapping("/balance")
public class BalanceController {

    private static Logger log = LoggerFactory.getLogger(BalanceController.class);

    @Autowired
    BalanceManager balanceManager;

    private WalletUser getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        WalletUser user = (WalletUser) session.getAttribute("user");
        return user;
    }

    @RequestMapping(value = {"/topup"}/*, method = RequestMethod.POST*/)
    @ResponseBody
    public ResultMap topup(@RequestParam Map params, HttpServletRequest request) {
        WalletUser user = getLoginUser(request);
        if (user == null) {
            return ResultMap.build(ResultStatus.WALLET_USER_NOT_LOGIN);
        }
        int uid = user.getUid();
        BigDecimal money = new BigDecimal((String)params.get("money"));
        String channel_code = (String)params.get("channel_code");
        ResultMap result = balanceManager.doTopup(uid, money, channel_code);
        return result;
    }

    @RequestMapping(value = {"/withdraw"}/*, method = RequestMethod.POST*/)
    @ResponseBody
    public ResultMap withdraw(@RequestParam Map params, HttpServletRequest request) {
        WalletUser user = getLoginUser(request);
        if (user == null) {
            return ResultMap.build(ResultStatus.WALLET_USER_NOT_LOGIN);
        }
        int uid = user.getUid();
        BigDecimal money = new BigDecimal((String)params.get("money"));
        String channel_code = (String)params.get("channel_code");
        //检查支付密码
        String payPasswd_md5 = (String)params.get("paypwd");
        if (!payPasswd_md5.equals(user.getPayPasswd())) {
            return ResultMap.build(ResultStatus.WALLET_PAY_PASSWD_WRONG);
        }
        ResultMap result = balanceManager.doWithdraw(uid, money, channel_code);
        return result;
    }

    @RequestMapping(value = {"/transfer"}/*, method = RequestMethod.POST*/)
    @ResponseBody
    public ResultMap transfer(@RequestParam Map params, HttpServletRequest request) {
        WalletUser user = getLoginUser(request);
        if (user == null) {
            return ResultMap.build(ResultStatus.WALLET_USER_NOT_LOGIN);
        }
        int uid = user.getUid();
        BigDecimal money = new BigDecimal((String)params.get("money"));
        int payeeid = ConvertUtil.toInt(params.get("payeeid"));
        String payPasswd_md5 = (String)params.get("paypwd");
        //检查支付密码
        if (!payPasswd_md5.equals(user.getPayPasswd())) {
            return ResultMap.build(ResultStatus.WALLET_PAY_PASSWD_WRONG);
        }
        ResultMap result = balanceManager.doTransfer(uid, money, payeeid);
        return result;
    }


}
