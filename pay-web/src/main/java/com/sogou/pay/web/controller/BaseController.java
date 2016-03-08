package com.sogou.pay.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.sogou.pay.common.types.ResultStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

/**
 * 类BaseController.java
 * 
 * @author 黄国庆  2015年3月9日 下午4:2 8:43
 */
@Controller
@SuppressWarnings("all")
public class BaseController {
    
    /**
     * @param message:错误消息
     * @param modelAndView:页面跳转 common/error
     * @Description: 前端异常处理
     */
    protected ModelAndView setErrorPage(String message, int errorCode, String platform){
        String viewName = String.format("common/%sError", platform);
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("errorCode", errorCode);
        modelAndView.addObject("message",message);
        return modelAndView;
    }

    protected ModelAndView setErrorPage(ResultStatus status, String platform){
        return setErrorPage(status.getMessage(), status.getCode(), platform);
    }


    /**
     * @Description: 获取所有请求参数,不适用于参数中有数组的情况
     * @return Map<String,Object>
     */
    protected Map<String, String> getRequestParameterMap(
            HttpServletRequest request) {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap = request.getParameterMap();
        Map<String, String> returnMap = new HashMap<String, String>();
        if (!parameterMap.isEmpty())
            for (String key : parameterMap.keySet())
                returnMap.put(key, ((String[]) parameterMap.get(key))[0].trim());
        return returnMap;
    }
}
