package com.sogou.pay.web.controller.notify.tenpay;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.DocumentException;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.common.utils.PMapUtil;
import com.sogou.pay.manager.model.notify.PayNotifyModel;
import com.sogou.pay.manager.model.thirdpay.FairAccRefundModel;
import com.sogou.pay.manager.notify.PayNotifyManager;
import com.sogou.pay.manager.payment.RefundManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.enums.AgencyType;
import com.sogou.pay.service.payment.PayOrderRelationService;
import com.sogou.pay.service.payment.PayOrderService;
import com.sogou.pay.web.form.notify.TenPayWebNotifyParams;
import com.sogou.pay.web.utils.ControllerUtil;

/**
 * User: Liwei
 * Date: 15/3/3
 * Time: 下午2:56
 * Description:
 * 该Controller用于财付通支付回调，原则上Controller只做请求参数解析、合法性校验、转换，涉及到数据库操作的都传给Manager层做处理
 * 在该类内部通过/web, /sdk, /wap 等来路由不同平台的支付回调
 * 虽然可对同一支付机构不同平台回调归一到一个入口处理，但是考虑到个别重要参数差异或后续版本升级可能导致的差异，最好还是分开
 */

@Controller
@RequestMapping(value = "/notify/ten/pay")
public class TenPayNotifyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenPayNotifyController.class);

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private PayNotifyManager payNotifyManager;
    @Autowired
    private RefundManager refundManager;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private PayOrderRelationService payOrderRelationService;

    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/ten/pay/webAsync",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping(value = "/webAsync")
    @ResponseBody
    public String tenWebNotify(TenPayWebNotifyParams tenPayWebNotifyParams, HttpServletRequest request) throws ServiceException, IOException, DocumentException {
        //1. 签名校验
        //PMap<String, String> pMap = ControllerUtil.getXmlParamPMap(request);
        LOGGER.info("Tenpay paynotify webAsync request info( {} )", HttpUtil.getRequestInfo(request));

        PMap<String, String> pMap = ControllerUtil.getParamPMap(request);
        LOGGER.info("Tenpay paynotify webAsync request pMap( {} )", pMap);

        //Todo 调用签名公共服务进行签名校验
        String agency = AgencyType.TENPAY.name();
        String partner = pMap.getString("partner");
        ResultMap sign = (ResultMap) secureManager.verifyNotifySign(pMap, agency, partner);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            LOGGER.error("md5签名异常，参数:" + pMap);
            return "success";
        }

        //2. 业务参数提取、校验、转换，主要是格式校验
        PayNotifyModel payNotifyModel = paramsConvert(tenPayWebNotifyParams);
        if (payNotifyModel == null) {
            return "success";
        }

        //3. 回调流水预处理，主要是业务逻辑校验
        ResultMap processResult = payNotifyManager.doProcess(payNotifyModel);
        if(Result.isSuccess(processResult) && 1 == (int)processResult.getReturnValue()){
            //调用平账退款接口
            refundManager.fairAccountRefund((FairAccRefundModel)processResult.getData().get("fairAccRefundModel"));
        }
        return "success";  // 返回结果只是表示收到回调

    }
    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/ten/pay/webSync",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping(value = "/webSync")
    public ModelAndView tenWebSyncNotify(TenPayWebNotifyParams tenPayWebNotifyParams, HttpServletRequest request) throws ServiceException, IOException {
        ModelAndView view = new ModelAndView("notifySync");
        /******1. 签名校验*******/
        PMap<String, String> pMap = ControllerUtil.getParamPMap(request);
        LOGGER.info("Tenpay paynotify webSync request info( {} )", HttpUtil.getRequestInfo(request));
        LOGGER.info("Tenpay paynotify webSync request pMap( {} )", pMap);

        //Todo test 调用签名公共服务进行签名校验
        String agency = AgencyType.TENPAY.name();
        String partner = pMap.getString("partner");
        ResultMap sign = (ResultMap) secureManager.verifyNotifySign(pMap, agency, partner);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            LOGGER.error("md5签名异常，参数:" + pMap);
            view.addObject("errorCode", sign.getStatus().getCode());
            view.addObject("errorMessage", sign.getStatus().getMessage());
            return view;
        }

        String url = null;
      //1.根据reqId查询payId
        PayOrderRelation paramRelation = new PayOrderRelation();
        paramRelation.setPayDetailId(pMap.getString("out_trade_no"));
        List<PayOrderRelation> relationList = payOrderRelationService.selectPayOrderRelation(paramRelation);
        if(null == relationList){
            LOGGER.error("There is no PayOrderRelation from reqId={}", pMap.getString("out_trade_no"));
            view.addObject("errorCode", ResultStatus.PAY_ORDER_RELATION_NOT_EXIST.getCode());
            view.addObject("errorMessage", ResultStatus.PAY_ORDER_RELATION_NOT_EXIST.getMessage());
            return view;
        }
        //2.根据payIdList查询payOrder信息
        List<PayOrderInfo> payOrderInfos = payOrderService.selectPayOrderByPayIdList(relationList);
        PayOrderInfo payOrderInfo = null;
        if (payOrderInfos != null) {
            payOrderInfo = payOrderInfos.get(0);
            url = payOrderInfo.getAppPageUrl();
        } else {
            LOGGER.error("There is no orderinfo from reqId={}", pMap.getString("out_trade_no"));
            view.addObject("errorCode", ResultStatus.RES_PAY_INFO_NOT_EXIST_ERROR.getCode());
            view.addObject("errorMessage", ResultStatus.RES_PAY_INFO_NOT_EXIST_ERROR.getMessage());
            return view;
        }
        //获得通知参数
        ResultMap resultNotify = payNotifyManager.getNotifyMap(payOrderInfo);
        if(!Result.isSuccess(resultNotify)){
            view.addObject("errorCode", resultNotify.getStatus().getCode());
            view.addObject("errorMessage", resultNotify.getStatus().getMessage());
            return view;
        }
        view.addObject("errorCode", 0);
        view.addObject("appUrl", url);
        view.addObject("returnMap", resultNotify.getReturnValue());
        return view;
    }

    private PayNotifyModel paramsConvert(TenPayWebNotifyParams tenPayWebNotifyParams) {
        String orderStatus = tenPayWebNotifyParams.getTrade_state();
        if (!"0".equals(orderStatus)) {
            return null;
        }

        PMap paramMap = PMapUtil.fromBean(tenPayWebNotifyParams); //因为aliPayWebNotifyParams作为controller封装对象都是String型的，不便于操作，此处转换为PMap便于处理

        String totalFee = paramMap.getString("total_fee");

        BigDecimal true_money = new BigDecimal(totalFee).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UP);

        PayNotifyModel payNotifyModel = new PayNotifyModel();
        payNotifyModel.setPayDetailId(paramMap.getString("out_trade_no"));
        payNotifyModel.setAgencyOrderId(paramMap.getString("transaction_id"));
//        payNotifyModel.setPayStatus(pay_status);
        payNotifyModel.setAgencyPayTime(paramMap.getDate("time_end"));
        payNotifyModel.setTrueMoney(true_money);

        return payNotifyModel;
    }

}
