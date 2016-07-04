package com.sogou.pay.web.controller;

import com.sogou.pay.common.types.ResultStatus;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BaseController {

  protected static List<String> cashierSignExcludes = Arrays.asList("sign", "bankId", "accessPlatform");

  protected static List<String> signExcludes = Arrays.asList("sign");

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
