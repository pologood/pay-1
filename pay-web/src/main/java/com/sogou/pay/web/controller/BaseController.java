package com.sogou.pay.web.controller;

import com.google.common.collect.Sets;
import com.sogou.pay.common.types.ResultStatus;

import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BaseController {

  protected static Set<String> cashierSignExcludes = Sets.newHashSet("sign", "bankId", "accessPlatform");

  protected static Set<String> signExcludes = Sets.newHashSet("sign");

  protected ModelAndView setErrorPage(String message, int errorCode, String platform) {
    String viewName = String.format("common/%sError", platform);
    ModelAndView modelAndView = new ModelAndView(viewName);
    modelAndView.addObject("errorCode", errorCode);
    modelAndView.addObject("message", message);
    return modelAndView;
  }

  protected ModelAndView setErrorPage(ResultStatus status, String platform) {
    return setErrorPage(status.getMessage(), status.getCode(), platform);
  }
}
