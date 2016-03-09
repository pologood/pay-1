package com.sogou.pay.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.manager.payment.AppManager;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.utils.DataSignUtil;
import com.sogou.pay.web.form.PayOrderQueryParams;
import com.sogou.pay.web.form.PayParams;
import com.sogou.pay.web.form.QueryRefundParams;
import com.sogou.pay.web.form.RefundParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 测试入口
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/09/01 10:08
 */
@Controller
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private AppManager appManager;

    @RequestMapping("/refund")
    public ModelAndView refund(PayParams params, HttpServletRequest request) {
        ModelAndView view = new ModelAndView("/common/refund");
        return view;
    }

    @RequestMapping("/queryRefund")
    public ModelAndView queryRefund(PayParams params, HttpServletRequest request) {
        ModelAndView view = new ModelAndView("/common/queryRefund");
        return view;
    }

    @RequestMapping("/queryOrder")
    public ModelAndView queryOrder(PayParams params, HttpServletRequest request) {
        ModelAndView view = new ModelAndView("/common/queryOrder");
        return view;
    }

    @RequestMapping(value = "getRefundSignData", produces = "text/plain; charset=utf-8")
    @ResponseBody
    public String signData(RefundParams params, HttpServletRequest request){
        Map paramMap = convertToMap(params);
        Result<App> appresult = appManager.selectAppInfo(Integer.parseInt(params.getAppId()));
        App app = appresult.getReturnValue();
        String key = app.getSignKey();
        String sign = DataSignUtil.sign(packParams(paramMap, key), "0");
        return JSONObject.toJSONString(sign);
    }

    @RequestMapping(value = "getQueryRefundSignData", produces = "text/plain; charset=utf-8")
    @ResponseBody
    public String getQueryRefundSignData(QueryRefundParams params, HttpServletRequest request){
        Map paramMap = convertToMap(params);
        Result<App> appresult = appManager.selectAppInfo(Integer.parseInt(params.getAppId()));
        App app = appresult.getReturnValue();
        String key = app.getSignKey();
        String sign = DataSignUtil.sign(packParams(paramMap, key), "0");
        return JSONObject.toJSONString(sign);
    }

    @RequestMapping(value = "getQueryOrderSignData", produces = "text/plain; charset=utf-8")
    @ResponseBody
    public String getQueryPaySignData(PayOrderQueryParams params, HttpServletRequest request){
        Map paramMap = convertToMap(params);
        Result<App> appresult = appManager.selectAppInfo(Integer.parseInt(params.getAppId()));
        App app = appresult.getReturnValue();
        String key = app.getSignKey();
        String sign = DataSignUtil.sign(packParams(paramMap, key), "0");
        return JSONObject.toJSONString(sign);
    }

    private Map convertToMap(Object params) {
        if (params instanceof Map) {
            return MapUtil.dropNulls((Map) params);
        } else {
            return BeanUtil.Bean2MapNotNull(params);
        }
    }

    private String packParams(Map paramMap, String secret) {
        if (paramMap == null) {
            return null;
        }
        List<String> keyList = new ArrayList<String>(paramMap.keySet());
        Collections.sort(keyList);

        //拼接k1=v1k2=v2
        StringBuilder paramStrBuilder = new StringBuilder();
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            Object value = paramMap.get(key);

            if (value != null) {
                paramStrBuilder.append(key).append("=").append(value.toString());
                if (i != keyList.size() - 1) {//拼接时，不包括最后一个&字符
                    paramStrBuilder.append("&");
                }
            }
        }

        //拼接secretKey
        paramStrBuilder.append(secret);
        return paramStrBuilder.toString();
        /*String encodeParam = "";
        try {
            encodeParam = URLEncoder.encode(paramStrBuilder.toString(), CommonConstant.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return encodeParam;*/
    }
}
