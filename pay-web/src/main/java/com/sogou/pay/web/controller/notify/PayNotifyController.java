package com.sogou.pay.web.controller.notify;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.utils.XMLUtil;
import com.sogou.pay.service.model.PayNotifyModel;
import com.sogou.pay.web.manager.notify.PayNotifyManager;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.entity.PayReqDetail;
import com.sogou.pay.service.service.PayAgencyMerchantService;
import com.sogou.pay.service.service.PayOrderRelationService;
import com.sogou.pay.service.service.PayOrderService;
import com.sogou.pay.service.service.PayReqDetailService;
import com.sogou.pay.web.portal.PayPortal;
import com.sogou.pay.web.controller.BaseController;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiepeidong on 2016/3/4.
 */


@Controller
@RequestMapping(value = "/notify")
public class PayNotifyController extends BaseController {
  private static final Logger log = LoggerFactory.getLogger(PayNotifyController.class);

  @Autowired
  private PayPortal payPortal;

  @Autowired
  private PayReqDetailService payReqDetailService;

  @Autowired
  private PayAgencyMerchantService payAgencyMerchantService;

  @Autowired
  private PayOrderRelationService payOrderRelationService;
  @Autowired
  private PayNotifyManager payNotifyManager;
  @Autowired
  private PayOrderService payOrderService;


  //从第三方同步通知中提取payDetailId等关键参数，同时做签名校验
  private ResultMap parseNotifyParams(String notifyType,
                                      String agencyCode,
                                      Map params) {

    PMap requestPMap = new PMap();
    requestPMap.put("agencyCode", agencyCode.toUpperCase());
    requestPMap.put("notifyType", notifyType.toUpperCase());
    requestPMap.put("data", new PMap(params));

    //提取payDetailId
    ResultMap result = payPortal.getReqIDFromNotify(requestPMap);
    if (!Result.isSuccess(result)) {
      log.error("[parseNotifyParams] 从notify中获取payDetailId失败, 参数: {}", requestPMap);
      return result;
    }
    String reqId = (String) result.getItem("reqId");
    //String merchantNo = (String) result.getItem("merchantNo");

    //查询支付流水单
    PayReqDetail payReqDetail = payReqDetailService.selectPayReqDetailById(reqId);
    if (null == payReqDetail) {
      log.error("[parseNotifyParams] 查询支付流水信息失败, reqId={}", reqId);
      return ResultMap.build(ResultStatus.REQ_DETAIL_NOT_EXIST);
    }
    String merchantNo = payReqDetail.getMerchantNo();
    PayAgencyMerchant payAgencyMerchant = payAgencyMerchantService.selectByAgencyAndMerchant(agencyCode, merchantNo);
    if (payAgencyMerchant == null) {
      log.error("[parseNotifyParams] 查询商户信息失败, agencyCode={}, merchantNo={}", agencyCode, merchantNo);
      return ResultMap.build(ResultStatus.THIRD_MERCHANT_NOT_EXIST);
    }
    //获取签名key
    String md5securityKey = payAgencyMerchant.getEncryptKey();
    String publicCertFilePath = payAgencyMerchant.getPubKeypath();
    requestPMap.put("md5securityKey", md5securityKey);
    requestPMap.put("publicCertFilePath", publicCertFilePath);

    //验证签名，提取参数
    result = payPortal.handleNotify(requestPMap);
    if (!Result.isSuccess(result)) {
      log.error("[parseNotifyParams] 验证签名、提取参数失败, 参数: {}", requestPMap);
      return result;
    }
    result.addItem("payFeeType", payReqDetail.getPayFeeType());
    return result;
  }


  //处理页面跳转同步通知的公共函数
  private ModelAndView handleNotifySync(String platform,
                                        String agencyCode,
                                        Map params) throws ServiceException {
    log.info("[handleNotifySync] 处理第三方同步通知, platform={}, agencyCode={}", platform, agencyCode);

    String notifyType = platform + "_sync";

    ResultMap result = parseNotifyParams(notifyType, agencyCode, params);
    if (!Result.isSuccess(result)) {
      return setErrorPage(result.getStatus(), platform);
    }

    PMap resultPMap = result.getData();
    String payStatus = resultPMap.getString("payStatus");
    if (!payStatus.equals("SUCCESS")) {
      log.error("[handleNotifySync] 交易失败, 参数: {}", params);
      return setErrorPage(ResultStatus.ORDER_FAILED, platform);
    }

    String reqId = resultPMap.getString("reqId");

    //1.根据payDetailId查询payId
    PayOrderRelation paramRelation = new PayOrderRelation();
    paramRelation.setPayDetailId(reqId);
    List<PayOrderRelation> relationList = payOrderRelationService.selectPayOrderRelation(paramRelation);
    if (null == relationList || relationList.size() == 0) {
      log.error("[handleNotifySync] 查询订单流水单关联表失败, reqId={}", reqId);
      return setErrorPage(ResultStatus.ORDER_RELATION_NOT_EXIST, platform);
    }
    //2.根据payIdList查询payOrder信息
    List<PayOrderInfo> payOrderInfos = payOrderService.selectPayOrderByPayIdList(relationList);
    PayOrderInfo payOrderInfo = null;
    if (payOrderInfos == null) {
      log.error("[handleNotifySync] 查询订单表失败, reqId={}", reqId);
      return setErrorPage(ResultStatus.ORDER_NOT_EXIST, platform);
    }
    payOrderInfo = payOrderInfos.get(0);

    //获得业务线页面通知地址
    String appPageUrl = payOrderInfo.getAppPageUrl();
    //获得通知所需的参数
    ResultMap resultNotify = payNotifyManager.syncNotifyApp(payOrderInfo);
    if (!Result.isSuccess(resultNotify)) {
      log.error("[handleNotifySync] 获取通知参数失败, reqId={}", reqId);
      return setErrorPage(resultNotify.getStatus(), platform);
    }

    String viewName = platform + "Notify";
    ModelAndView view = new ModelAndView(viewName);
    view.addObject("payFeeType", resultPMap.getString("payFeeType"));
    view.addObject("errorCode", 0);
    view.addObject("appUrl", appPageUrl);
    view.addObject("returnMap", resultNotify.getReturnValue());
    return view;
  }


  //服务器端异步通知的公共函数
  private ResultMap handleNotifyAsync(String platform,
                                      String agencyCode,
                                      Map params) throws ServiceException {
    log.info("[handleNotifyAsync] 处理第三方异步通知, platform={}, agencyCode={}", platform, agencyCode);

    String notifyType = platform + "_async";

    ResultMap result = parseNotifyParams(notifyType, agencyCode, params);
    if (!Result.isSuccess(result)) {
      return result;
    }

    PMap resultPMap = result.getData();
    String payStatus = resultPMap.getString("payStatus");
    if (!payStatus.equals("SUCCESS")) {
      log.error("[handleNotifyAsync] 交易失败, 参数:" + params);
      return ResultMap.build(ResultStatus.ORDER_FAILED);
    }

    PayNotifyModel payNotifyModel = new PayNotifyModel();
    payNotifyModel.setPayDetailId(resultPMap.getString("reqId"));
    payNotifyModel.setAgencyOrderId(resultPMap.getString("agencyPayId"));
    payNotifyModel.setChannelType(resultPMap.getString("channelType"));
    payNotifyModel.setAgencyPayTime(DateUtil.parse(resultPMap.getString("agencyPayTime")));
    payNotifyModel.setTrueMoney(new BigDecimal(resultPMap.getString("payMoney")));

    return payNotifyManager.handlePayNotify(payNotifyModel);
  }


  @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/websync",
          timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/websync/{agencyCode}"})
  public ModelAndView handleNotifyWebSync(@PathVariable("agencyCode") String agencyCode,
                                          @RequestParam Map params) throws ServiceException {
    return handleNotifySync("web", agencyCode, params);
  }


  @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/wapsync",
          timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/wapsync/{agencyCode}"})
  public ModelAndView handleNotifyWapSync(@PathVariable("agencyCode") String agencyCode,
                                          @RequestParam Map params) throws ServiceException {
    return handleNotifySync("wap", agencyCode, params);
  }


  @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/webasync",
          timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/webasync/{agencyCode}"})
  @ResponseBody
  public String handleNotifyWebAsync(@PathVariable("agencyCode") String agencyCode,
                                     @RequestParam Map params) throws ServiceException {
    handleNotifyAsync("web", agencyCode, params);
    return "success";  // 返回结果只是表示收到回调
  }


  @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/wapasync",
          timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/wapasync/{agencyCode}"})
  @ResponseBody
  public String handleNotifyWapAsync(@PathVariable("agencyCode") String agencyCode,
                                     @RequestParam Map params) throws ServiceException {
    handleNotifyAsync("wap", agencyCode, params);
    return "success";  // 返回结果只是表示收到回调
  }

  @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/sdkasync",
          timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/sdkasync/{agencyCode}"})
  @ResponseBody
  public String handleNotifySDKAsync(@PathVariable("agencyCode") String agencyCode,
                                     @RequestParam Map params) throws ServiceException {
    handleNotifyAsync("sdk", agencyCode, params);
    return "success";  // 返回结果只是表示收到回调
  }

  @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/webasync/wechat",
          timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/webasync/{test_}wechat"})
  @ResponseBody
  public String handleNotifyWebAsyncWechat(@PathVariable("test_") String test_,
                                           @RequestBody String body) throws ServiceException {
    String agencyCode = test_ + "wechat";
    Map params = null;
    try {
      params = XMLUtil.XML2Map(body);
    } catch (Exception e) {
      log.error("[handleNotifyWebAsyncWechat] 解析xml失败");
      return "success";
    }
    handleNotifyAsync("web", agencyCode, params);
    Map<String, String> result = new HashMap<>();
    result.put("return_code", "SUCCESS");
    return XMLUtil.Map2XML("xml", result);
  }

  @RequestMapping("/testBgUrl")
  @ResponseBody
  public String testBgUrl(@RequestParam Map params) {
    log.info("***********success testBgUrl***********" + JSONUtil.Bean2JSON(params));
    return "success";
  }


}
