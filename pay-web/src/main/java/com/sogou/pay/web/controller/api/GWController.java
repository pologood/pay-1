package com.sogou.pay.web.controller.api;

import com.sogou.pay.common.Model.StdPayRequest;
import com.sogou.pay.common.types.*;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.manager.model.PayChannelAdapts;
import com.sogou.pay.manager.model.PayChannelAdapt;
import com.sogou.pay.web.manager.ChannelAdaptManager;
import com.sogou.pay.service.payment.AppService;
import com.sogou.pay.web.manager.api.PayManager;
import com.sogou.pay.web.manager.SecureManager;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.web.portal.PayPortal;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.form.PayForm;
import com.sogou.pay.web.utils.ControllerUtil;
import com.sogou.pay.web.utils.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author xiepeidong
 * @ClassName GWController
 * @Date 2016年1月13日
 * @Description: 集成了所有的网关型接口：Web、Wap网页支付
 */
@Controller
@RequestMapping(value = "/gw")
public class GWController extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(GWController.class);

  @Autowired
  private AppService appService;

  @Autowired
  private PayManager payManager;

  @Autowired
  private ChannelAdaptManager channelAdaptMaanger;

  @Autowired
  private SecureManager secureManager;

  @Autowired
  private PayPortal payPortal;

  private static final HashMap<Integer, String> COMPANYMAP = new HashMap<Integer, String>() {
    {
      put(1, "北京搜狗网络技术有限公司");
      put(2, "北京搜狗科技发展有限公司");
    }
  };

  private static PMap cashierSignExcludes = new PMap();
  private static PMap signExcludes = new PMap();

  static {
    signExcludes.put("sign", true);
    cashierSignExcludes.put("sign", true);
    cashierSignExcludes.put("bankId", true);
    cashierSignExcludes.put("accessPlatform", true);
  }

  private ModelAndView createCashierView(PMap payParams, PMap queryParams, String platform) {
    //银行适配
    ResultBean<PayChannelAdapts> result = channelAdaptMaanger.getChannelAdapts(
            payParams.getInt("appId"), payParams.getInt("accessPlatform"));
    if (!Result.isSuccess(result)) {
      logger.error("[doPay][createCashierView][Failed] params={}, result={}", JSONUtil.Bean2JSON(payParams),
              JSONUtil.Bean2JSON(result));
      return setErrorPage(result.getStatus(), platform);
    }
    //获得支付渠道
    PayChannelAdapts adapts = result.getValue();
    //第三方支付列表
    List<PayChannelAdapt> thirdPayList = adapts.getThirdPayList();
    //扫码支付列表
    List<PayChannelAdapt> qrCodeList = adapts.getQrCodeList();
    //网银支付列表
    List<PayChannelAdapt> bankList = adapts.getBankDebitList();
    //B2B支付列表
    List<PayChannelAdapt> b2bList = adapts.getB2bList();

    if (bankList.isEmpty() && thirdPayList.isEmpty() && qrCodeList.isEmpty() && b2bList.isEmpty()) {
      logger.error("[doPay] PayChannelAdapts is empty, params={}", JSONUtil.Bean2JSON(payParams));
      return setErrorPage(ResultStatus.PAY_CHANNEL_NOT_EXIST, platform);
    }

    //获得收款方信息
    App app = appService.selectApp(payParams.getInt("appId"));
    if (app == null) {
      logger.error("[doPay][selectApp][Failed] params={}, result={}", JSONUtil.Bean2JSON(payParams),
              JSONUtil.Bean2JSON(result));
      return setErrorPage(ResultStatus.APPID_NOT_EXIST, platform);
    }

    //收银台页面
    ModelAndView view = new ModelAndView("cashier");
    view.addObject("payParams", payParams);
    view.addObject("queryParams", queryParams);
    view.addObject("companyName", COMPANYMAP.get(app.getBelongCompany()));
    view.addObject("thirdPayList", thirdPayList);
    view.addObject("qrCodeList", qrCodeList);
    view.addObject("bankList", bankList);
    view.addObject("b2bList", b2bList);
    return view;
  }

  private ModelAndView createAgencyView(PMap paramsMap, String platform) {
    ModelAndView view = new ModelAndView(String.format("%sForward", platform));
    view.addObject("payUrl", paramsMap.get("payUrl"));
    return view;
  }

  @RequestMapping(value = "/pay/{platform}", method = RequestMethod.POST)
  public ModelAndView doPay(@PathVariable(value = "platform") String platform,
                            PayForm payForm, HttpServletRequest request) {
    //验证参数
    List validateResult = ControllerUtil.validateParams(payForm);
    if (validateResult.size() > 0) {
      logger.error("[doPay][validateParams][Failed] params={}, result={}", JSONUtil.Bean2JSON(payForm),
              validateResult.toString());
      return setErrorPage(ResultStatus.PARAM_ERROR, platform);
    }
    //验证签名
    PMap payParamsMap = BeanUtil.Bean2PMap(payForm);
    //查询业务线信息
    int appId = payParamsMap.getInt("appId");
    App app = appService.selectApp(appId);
    if (app == null) {
      logger.error("[doPay][selectApp][Failed] appid not exists, params={}", JSONUtil.Bean2JSON(payParamsMap));
      return setErrorPage(ResultStatus.APPID_NOT_EXIST, platform);
    }
    //如果是收银台请求，则排除bankId, accessPlatform再验签
    boolean fromCashier = Objects.equals(payForm.getFromCashier(), "true");
    Result result = secureManager.verifyAppSign(payParamsMap, fromCashier ? cashierSignExcludes : signExcludes, app.getSignKey());
    if (!Result.isSuccess(result)) {
      logger.error("[doPay][verifyAppSign][Failed] params={}, result={}", JSONUtil.Bean2JSON(payParamsMap),
              JSONUtil.Bean2JSON(result));
      return setErrorPage(result.getStatus(), platform);
    }

    if (StringUtil.isBlank(payForm.getBankId())) {
      //未指定支付渠道，返回收银台页面
      //组装重新支付参数
      payParamsMap.put("fromCashier", "true");
      result = secureManager.doAppSign(payParamsMap, cashierSignExcludes, app.getSignKey());
      if (!Result.isSuccess(result)) {
        logger.error("[doPay][doAppSign][Failed] params={}, result={}", JSONUtil.Bean2JSON(payParamsMap),
                JSONUtil.Bean2JSON(result));
        return setErrorPage(result.getStatus(), platform);
      }
      //组装订单查询参数
      PMap queryParamsMap = new PMap();
      queryParamsMap.put("orderId", payForm.getOrderId());
      queryParamsMap.put("appId", payForm.getAppId());
      queryParamsMap.put("fromCashier", "true");
      result = secureManager.doAppSign(queryParamsMap, signExcludes, app.getSignKey());
      if (!Result.isSuccess(result)) {
        logger.error("[doPay][doAppSign][Failed] params={}, result={}", JSONUtil.Bean2JSON(queryParamsMap),
                JSONUtil.Bean2JSON(result));
        return setErrorPage(result.getStatus(), platform);
      }

      return createCashierView(payParamsMap, queryParamsMap, platform);
    } else {
      //指定了支付渠道
      payParamsMap.put("userIp", ServletUtil.getRealIp(request));
      payParamsMap.put("channelCode", payForm.getBankId());
      //创建支付订单
      Result result2 = payManager.createOrder(payParamsMap);
      if (!Result.isSuccess(result2)) {
        logger.error("[doPay][createOrder][Failed] params={}, result={}", JSONUtil.Bean2JSON(payParamsMap),
                JSONUtil.Bean2JSON(result2));
        return setErrorPage(result2.getStatus(), platform);
      }
      payParamsMap.put("payId", result2.getReturnValue());

      //组装支付网关参数
      ResultMap payResult = payManager.payOrder(payParamsMap);
      if (!Result.isSuccess(payResult)) {
        logger.error("[doPay][payOrder][Failed] params={}, result={}", JSONUtil.Bean2JSON(payParamsMap),
                JSONUtil.Bean2JSON(payResult));
        return setErrorPage(payResult.getStatus(), platform);
      }
      //调用支付网关
      StdPayRequest payGateParams = (StdPayRequest) payResult.getReturnValue();
      ResultMap<String> payGateResult = payPortal.preparePay(payGateParams);
      if (!Result.isSuccess(payGateResult)) {
        logger.error("[doPay][preparePay][Failed] params={}, result={}", JSONUtil.Bean2JSON(payGateParams),
                JSONUtil.Bean2JSON(payGateResult));
        return setErrorPage(payGateResult.getStatus(), platform);
      }
      payParamsMap.put("payUrl", payGateResult.getItem("returnUrl"));
      //返回支付页面
      return createAgencyView(payParamsMap, platform);
    }
  }
}
