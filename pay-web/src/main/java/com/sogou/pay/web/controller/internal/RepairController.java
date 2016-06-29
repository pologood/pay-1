package com.sogou.pay.web.controller.internal;

import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.manager.model.PayNotifyModel;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.manager.notify.PayNotifyManager;
import com.sogou.pay.web.manager.notify.RefundNotifyManager;
import com.sogou.pay.web.manager.notify.WithdrawNotifyManager;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @Author xiepeidong
 * @ClassName RepairController
 * @Date 2016年6月29日
 * @Description: 内部接口: 对账时补单
 */
@Controller
@RequestMapping(value = "/internal")
public class RepairController extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(RepairController.class);

  @Autowired
  private PayNotifyManager payNotifyManager;

  @Autowired
  private RefundNotifyManager refundNotifyManager;

  @Autowired
  private WithdrawNotifyManager withdrawNotifyManager;


  //支付补单
  @Profiled(el = true, logger = "webTimingLogger", tag = "/internal/repair/pay",
          timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/repair/pay"}, method = RequestMethod.POST,
          produces = "application/json; charset=utf-8")
  @ResponseBody
  public ResultMap repairPay(PayNotifyModel params) {
    return payNotifyManager.handlePayNotify(params);
  }

  //退款补单
  @Profiled(el = true, logger = "webTimingLogger", tag = "/internal/repair/refund",
          timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/repair/refund"}, method = RequestMethod.POST,
          produces = "application/json; charset=utf-8")
  @ResponseBody
  public ResultMap repairRefund(@RequestParam Map params) {
    return refundNotifyManager.handleRefundNotify(new PMap<>(params));
  }

  //提现补单
  @Profiled(el = true, logger = "webTimingLogger", tag = "/internal/repair/withdraw",
          timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/repair/withdraw"}, method = RequestMethod.POST,
          produces = "application/json; charset=utf-8")
  @ResponseBody
  public ResultMap repairWithdraw(@RequestParam Map params) {
    return withdrawNotifyManager.handleWithdrawNotify(new PMap<>(params));
  }

}
