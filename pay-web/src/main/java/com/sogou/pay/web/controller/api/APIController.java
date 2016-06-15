package com.sogou.pay.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.manager.model.PayOrderQueryModel;
import com.sogou.pay.manager.model.QueryRefundModel;
import com.sogou.pay.manager.model.RefundModel;
import com.sogou.pay.web.manager.api.RefundManager;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.payment.AppService;
import com.sogou.pay.web.form.QueryRefundParams;
import com.sogou.pay.web.manager.SecureManager;
import com.sogou.pay.web.portal.PayPortal;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.form.PayForm;
import com.sogou.pay.web.form.PayOrderQueryParams;
import com.sogou.pay.web.form.RefundParams;
import com.sogou.pay.web.utils.ControllerUtil;
import com.sogou.pay.web.utils.ServletUtil;
import com.sogou.pay.web.manager.api.PayManager;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author xiepeidong
 * @ClassName APIController
 * @Date 2016年1月13日
 * @Description: 集成了所有的Restful型接口：支付、退款、查询
 */
@Controller
@RequestMapping(value = "/api")
public class APIController extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(APIController.class);

  @Autowired
  private AppService appService;

  @Autowired
  private PayManager payManager;

  @Autowired
  private SecureManager secureManager;

  @Autowired
  private PayPortal payPortal;

  @Autowired
  private RefundManager refundManager;

  private static PMap cashierSignExcludes = new PMap();
  private static PMap signExcludes = new PMap();

  static {
    signExcludes.put("sign", true);
    cashierSignExcludes.put("sign", true);
    cashierSignExcludes.put("bankId", true);
    cashierSignExcludes.put("accessPlatform", true);
  }


  private ResultMap commonCheck(Object params) {
    ResultMap resultMap = ResultMap.build();
    //验证参数
    List validateResult = ControllerUtil.validateParams(params);
    if (validateResult.size() > 0) {
      logger.error("[doPay][validateParams][Failed]{}", validateResult.toString().substring(1, validateResult.toString().length() - 1));
      return (ResultMap) resultMap.withError(ResultStatus.PAY_PARAM_ERROR);
    }
    PMap paramsMap = BeanUtil.Bean2PMap(params);
    //查询业务线信息
    int appId = paramsMap.getInt("appId");
    App app = appService.selectApp(appId);
    if (app == null) {
      logger.error("[commonCheck] appid not exists, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) resultMap.withError(ResultStatus.PAY_APP_NOT_EXIST);
    }
    //验证签名
    //如果是收银台请求，则排除bankId, accessPlatform再验签
    boolean fromCashier = Objects.equals(paramsMap.get("fromCashier"), "true");
    String secret = app.getSignKey();
    Result result = secureManager.verifyAppSign(paramsMap, fromCashier ? cashierSignExcludes : signExcludes, secret);
    if (!Result.isSuccess(result)) {
      logger.info("[doPay][verifyAppSign][Failed]{}", result.getStatus());
      return (ResultMap) resultMap.withError(result.getStatus());
    }
    paramsMap.put("app", app);
    return (ResultMap) resultMap.withReturn(paramsMap);
  }


  //sdk支付、扫码支付
  @Profiled(el = true, logger = "webTimingLogger", tag = "/api/pay",
          timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/pay/sdk", "/pay/qrcode"}, method = RequestMethod.POST,
          produces = "application/json; charset=utf-8")
  @ResponseBody
  public ResultMap doPay(PayForm params, HttpServletRequest request) {
    ResultMap resultMap;

    resultMap = commonCheck(params);
    if (!Result.isSuccess(resultMap)) {
      return resultMap;
    }

    PMap paramsMap = (PMap) resultMap.getReturnValue();

    paramsMap.put("userIp", ServletUtil.getRealIp(request));
    paramsMap.put("channelCode", params.getBankId());

    //创建支付订单
    Result result = payManager.createOrder(paramsMap);
    if (!Result.isSuccess(result)) {
      logger.error("[doPay][createOrder][Failed] params={}, result={}", JSONUtil.Bean2JSON(paramsMap),
              JSONUtil.Bean2JSON(result));
      return (ResultMap) resultMap.withError(result.getStatus());
    }
    paramsMap.put("payId", result.getReturnValue());

    //组装支付网关参数
    ResultMap result2 = payManager.payOrder(paramsMap);
    if (!Result.isSuccess(result2)) {
      logger.error("[doPay][payOrder][Failed] params={}, result={}", JSONUtil.Bean2JSON(paramsMap),
              JSONUtil.Bean2JSON(result2));
      return (ResultMap) resultMap.withError(result2.getStatus());
    }
    //调用支付网关
    PMap payGateParams = (PMap) result2.getReturnValue();
    ResultMap<String> result3 = payPortal.preparePay(payGateParams);
    if (!Result.isSuccess(result3)) {
      logger.error("[doPay][preparePay][Failed] params={}, result={}", JSONUtil.Bean2JSON(payGateParams),
              JSONUtil.Bean2JSON(result3));
      return (ResultMap) resultMap.withError(ResultStatus.THIRD_PAY_ERROR);
    }

    //返回结果
    resultMap.addItems(result3.getData());
    return resultMap;
  }

  //订单查询
  @Profiled(el = true, logger = "webTimingLogger", tag = "/api/pay/query",
          timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = "/pay/query", method = RequestMethod.GET,
          produces = "application/json; charset=utf-8")
  @ResponseBody
  public ResultMap queryPay(PayOrderQueryParams params, HttpServletRequest request) {
    ResultMap resultMap;
    //检查参数
    resultMap = commonCheck(params);
    if (!Result.isSuccess(resultMap))
      return resultMap;

    PMap paramsMap = (PMap) resultMap.getReturnValue();

    //处理订单查询
    PayOrderQueryModel payOrderModel = new PayOrderQueryModel();
    payOrderModel.setApp((App) paramsMap.get("app"));
    payOrderModel.setOrderId(params.getOrderId());
    payOrderModel.setFromCashier(Objects.equals(params.getFromCashier(), "true"));
    ResultMap result = payManager.queryPayOrder(payOrderModel);
    if (!Result.isSuccess(result)) {
      logger.error("[queryPay][queryPayOrder][Failed] params={}, result={}", JSONUtil.Bean2JSON(payOrderModel),
              JSONUtil.Bean2JSON(result));
      return result;
    }
    resultMap.addItems(result.getData());
    return resultMap;
  }

  //订单退款
  @Profiled(el = true, logger = "webTimingLogger", tag = "/api/refund",
          timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = "/refund", method = RequestMethod.POST,
          produces = "application/json; charset=utf-8")
  @ResponseBody
  public ResultMap doRefund(RefundParams params, HttpServletRequest request) {
    String ip = ServletUtil.getRealIp(request);
    logger.debug("[doRefund] refund request coming, IP={}, params={}", ip, JSONUtil.Bean2JSON(params));
    ResultMap resultMap;
    //检查参数
    resultMap = commonCheck(params);
    if (!Result.isSuccess(resultMap))
      return resultMap;

    PMap paramsMap = (PMap) resultMap.getReturnValue();
    App app = (App) paramsMap.get("app");

    //处理订单退款
    RefundModel refundModel = new RefundModel();
    refundModel.setApp(app);              //业务线id
    refundModel.setOrderId(params.getOrderId());//订单id
    if (params.getRefundAmount() != null)//退款金额可选
      refundModel.setRefundAmount(new BigDecimal(params.getRefundAmount()));  //订单退款金额
    refundModel.setBgurl(params.getBgUrl());                                //回调url
    ResultMap refundResult = refundManager.refundOrder(refundModel);
    if (!Result.isSuccess(refundResult)) {
      logger.error("[doRefund] refund failed, IP={}, params={}, result={}", ip, JSONUtil.Bean2JSON(refundModel),
              JSONUtil.Bean2JSON(refundResult));
      resultMap.withError(refundResult.getStatus());
      if (null != refundResult.getData().get("error_code")) {
        resultMap.addItem("errorCode", refundResult.getData().get("error_code").toString());
      }
      if (null != refundResult.getData().get("error_info")) {
        resultMap.addItem("errorMsg", refundResult.getData().get("error_info").toString());
      }
    }
    logger.debug("[doRefund] refund request end, IP={}, result={}", ip, JSONUtil.Bean2JSON(resultMap));
    return resultMap;
  }

  @Profiled(el = true, logger = "webTimingLogger", tag = "/api/refund/query",
          timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = "/refund/query", method = RequestMethod.GET,
          produces = "application/json; charset=utf-8")
  @ResponseBody
  public ResultMap queryRefund(QueryRefundParams params) {
    logger.debug("[queryRefund] query refund request coming, params={}", JSONUtil.Bean2JSON(params));
    ResultMap resultMap;
    //检查参数
    resultMap = commonCheck(params);
    if (!Result.isSuccess(resultMap))
      return resultMap;

    PMap paramsMap = (PMap) resultMap.getReturnValue();
    App app = (App) paramsMap.get("app");

    //处理订单退款查询
    QueryRefundModel model = new QueryRefundModel();
    model.setApp(app); //业务线id
    model.setOrderId(params.getOrderId());               //订单id
    model.setSign(params.getSign());                     //签名
    model.setSignType(params.getSignType());            //签名类型
    ResultMap queryRefundMap = refundManager.queryRefund(model);
    if (!Result.isSuccess(queryRefundMap)) {
      logger.error("[queryRefund] queryRefund failed, params={}, result={}", JSONUtil.Bean2JSON(model),
              JSONUtil.Bean2JSON(queryRefundMap));
      resultMap.withError(queryRefundMap.getStatus());
      return resultMap;
    }
    resultMap.addItem("refundStatus", queryRefundMap.getReturnValue());
    logger.debug("[queryRefund] query refund request end, params={}", JSONUtil.Bean2JSON(resultMap));
    return resultMap;
  }


  @RequestMapping(value = "/pay/sign", method = RequestMethod.GET,
          produces = "application/json; charset=utf-8")
  @ResponseBody
  public String signData(@RequestParam Map<String, String> paramsMap) {
    int appId = Integer.parseInt(paramsMap.get("appId"));
    App app = appService.selectApp(appId);
    if (app == null) {
      logger.error("[signData] appid not exists, params={}", JSONUtil.Bean2JSON(paramsMap));
      return null;
    }
    Result result = secureManager.doAppSign(paramsMap, signExcludes, app.getSignKey());
    return JSONObject.toJSONString(paramsMap.get("sign"));
  }

}
