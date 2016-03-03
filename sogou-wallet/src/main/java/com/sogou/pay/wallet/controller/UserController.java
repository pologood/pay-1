package com.sogou.pay.wallet.controller;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.wallet.manager.BalanceManager;
import com.sogou.pay.wallet.service.entity.WalletUser;
import com.sogou.pay.wallet.manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by xiepeidong on 2016/2/23.
 */
@Controller
public class UserController {
    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserManager userManager;
    @Autowired
    BalanceManager balanceManager;

    @RequestMapping(value = {"/user/logout"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultMap toLogout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        ResultMap result = ResultMap.build();
        return result;
    }

    @RequestMapping(value = {"/user/paypwd/reset"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultMap resetPayPasswd(@RequestParam Map params, HttpServletRequest request) {
        HttpSession session = request.getSession();
        WalletUser user = (WalletUser) session.getAttribute("user");
        if (user == null) {
            return ResultMap.build(ResultStatus.WALLET_USER_NOT_LOGIN);
        }
        int uid = user.getUid();
        String paypwd = (String) params.get("paypwd");
        String new_paypwd = (String) params.get("npaypwd");
        userManager.resetPayPasswd(uid, paypwd, new_paypwd);
        ResultMap result = ResultMap.build();
        return result;
    }

    @RequestMapping(value = {"/", "/index", "/user/login"}, method = RequestMethod.GET)
    public ModelAndView toIndex(HttpServletRequest request) {
        HttpSession session = request.getSession();
        WalletUser user = (WalletUser) session.getAttribute("user");
        if (user == null) {
            String loginurl = null;
            String ptoken = request.getParameter("ptoken");
            ResultMap result = userManager.userLogin(ptoken);
            if (result.getStatus() == ResultStatus.SUCCESS) {
                user = (WalletUser) result.getReturnValue();
                session.setAttribute("user", user);
                loginurl = "/";
            } else {
                String originalUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                try {
                    originalUrl = URLEncoder.encode(originalUrl, "UTF-8");
                } catch (Exception ex) {
                }
                loginurl = ResourceBundle.getBundle("config").getString("pandora.sso.loginUrl") +
                        "&redirect=" + originalUrl;
            }
            return new ModelAndView("redirect:" + loginurl);
        }
        ModelAndView view = new ModelAndView("index");
        view.addObject("openid", user.getOpenid());
        view.addObject("uname", user.getUname());
        ResultMap result = balanceManager.queryBalance(user.getUid());
        if(result.getStatus()==ResultStatus.SUCCESS){
            view.addObject("balance",(BigDecimal)result.getReturnValue());
        }
        return view;
    }


}
