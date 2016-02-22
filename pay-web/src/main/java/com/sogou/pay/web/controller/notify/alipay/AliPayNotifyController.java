package com.sogou.pay.web.controller.notify.alipay;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.sogou.pay.common.utils.*;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.model.notify.PayNotifyModel;
import com.sogou.pay.manager.model.thirdpay.FairAccRefundModel;
import com.sogou.pay.manager.notify.PayNotifyManager;
import com.sogou.pay.manager.payment.RefundManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.entity.PayReqDetail;
import com.sogou.pay.thirdpay.biz.enums.AgencyType;
import com.sogou.pay.service.payment.PayOrderRelationService;
import com.sogou.pay.service.payment.PayOrderService;
import com.sogou.pay.service.payment.PayReqDetailService;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.form.notify.AliPayWapNotifyParams;
import com.sogou.pay.web.form.notify.AliPayWebNotifyParams;
import com.sogou.pay.web.utils.ControllerUtil;

/**
 * User: Liwei
 * Date: 15/3/3
 * Time: 下午2:56
 * Description:
 * 该Controller用于阿里支付回调，原则上Controller只做请求参数解析、合法性校验、转换，涉及到数据库操作的都传给Manager层做处理
 * 在该类内部通过/web, /sdk, /wap 等来路由不同平台的支付回调
 * 虽然可对同一支付机构不同平台回调归一到一个入口处理，但是考虑到个别重要参数差异或后续版本升级可能导致的差异，最好还是分开
 */

@Controller
@RequestMapping(value = "/notify/ali/pay")
public class AliPayNotifyController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayNotifyController.class);

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private PayNotifyManager payNotifyManager;
    @Autowired
    private RefundManager refundManager;
    @Autowired
    private PayReqDetailService payReqDetailService;
    @Autowired
    private PayOrderRelationService payOrderRelationService;
    @Autowired
    private PayOrderService payOrderService;


    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/ali/pay/webAsync",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping(value = "/webAsync")
    @ResponseBody
    public String aliWebNotify(AliPayWebNotifyParams aliPayWebNotifyParams, HttpServletRequest request) throws ServiceException {
        //1. 签名校验
        PMap<String, String> pMap = ControllerUtil.getParamPMap(request);
        LOGGER.info("【webAsync:params】" + pMap);
        //调用签名公共服务进行签名校验
        String agency = AgencyType.ALIPAY.name();
        String reqDetailId = pMap.getString("out_trade_no");
        PayReqDetail payReqDetail = payReqDetailService.selectPayReqDetailById(reqDetailId);
        if (null == payReqDetail) {
            LOGGER.error("【支付回调】没有该支付流水信息，reqDetailId = " + payReqDetail);
            return "success";
        }
        String partner = payReqDetail.getMerchantNo();
        ResultMap sign = (ResultMap) secureManager.verifyNotifySign(pMap, agency, partner);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            LOGGER.error("【支付回调】md5签名异常，参数:" + pMap);
            return "success";
        }

        //2. 业务参数提取、校验、转换，主要是格式校验
        PayNotifyModel payNotifyModel = paramsConvert(aliPayWebNotifyParams);
        if (payNotifyModel == null) {
            return "success";
        }

        //3. 回调流水预处理，主要是业务逻辑校验
        ResultMap processResult = payNotifyManager.doProcess(payNotifyModel);
        if (Result.isSuccess(processResult) && 1 == (int) processResult.getReturnValue()) {
            //调用平账退款接口
            LOGGER.info("【平账退款】调用平账退款接口参数：" + JSONUtil.Bean2JSON(processResult.getData().get("fairAccRefundModel")));
            refundManager.fairAccountRefund((FairAccRefundModel) processResult.getData().get("fairAccRefundModel"));
            LOGGER.info("【平账退款】平账退款成功！");
        }
        return "success";  // 返回结果只是表示收到回调
    }

    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/ali/pay/webSync",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping(value = "/webSync")
    public ModelAndView aliWebSyncNotify(HttpServletRequest request) throws ServiceException, IOException {
        ModelAndView view = new ModelAndView("notifySync");
        /******1. 签名校验*******/
        PMap<String, String> pMap = ControllerUtil.getParamPMap(request);
        LOGGER.info("【webSync:params】" + pMap);
        //Todo 调用签名公共服务进行签名校验
        String agency = AgencyType.ALIPAY.name();
        String reqDetailId = pMap.getString("out_trade_no");
        PayReqDetail payReqDetail = payReqDetailService.selectPayReqDetailById(reqDetailId);
        if(null == payReqDetail){
            LOGGER.error("【支付回调】没有该支付流水信息！reqDetailId=" + reqDetailId);
            view.addObject("errorCode", ResultStatus.REQ_INFO_NOT_EXIST_ERROR.getCode());
            view.addObject("errorMessage", ResultStatus.REQ_INFO_NOT_EXIST_ERROR.getMessage());
            return view;
        }
        String partner = payReqDetail.getMerchantNo();
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
        if(null == relationList || relationList.size() == 0){
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
            view.addObject("errorCode", ResultStatus.PAY_ORDER_NOT_EXIST.getCode());
            view.addObject("errorMessage", ResultStatus.PAY_ORDER_NOT_EXIST.getMessage());
            return view;
        }
        //获得通知参数
        ResultMap resultNotify = payNotifyManager.getNotifyMap(payOrderInfo);
        if(!Result.isSuccess(resultNotify)){
            view.addObject("errorCode", resultNotify.getStatus().getCode());
            view.addObject("errorMessage", resultNotify.getStatus().getMessage());
            return view;
        }
        view.addObject("payFeeType", payReqDetail.getPayFeeType());
        view.addObject("errorCode", 0);
        view.addObject("appUrl", url);
        view.addObject("returnMap", resultNotify.getReturnValue());
        return view;
    }

    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/ali/pay/wapAsync",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping(value = "/wapAsync")
    @ResponseBody
    public String aliWapNotify(AliPayWapNotifyParams aliPayWapNotifyParams, HttpServletRequest request) throws ServiceException {
        //1. 签名校验
        PMap<String, String> pMap = ControllerUtil.getParamPMap(request);
        LOGGER.info("【wapAsync:params】" + pMap);
        //将notify_data解析存入pMap中
        PMap notifyMap;
        try {
            notifyMap = XMLUtil.XML2PMap(pMap.get("notify_data"));
            pMap.putAll(notifyMap);
        } catch (Exception e) {
            LOGGER.error("【支付回调】解析请求数据出错！");
            return "success";
        }
        //调用签名公共服务进行签名校验
        String agency = AgencyType.ALIPAY.name();
        String reqDetailId = pMap.getString("out_trade_no");
        PayReqDetail payReqDetail = payReqDetailService.selectPayReqDetailById(reqDetailId);
        if (null == payReqDetail) {
            LOGGER.error("【支付回调】没有该支付流水信息，reqDetailId = " + payReqDetail);
            return "success";
        }
        String partner = payReqDetail.getMerchantNo();
        /**美颜测试开始**/
//        ResultMap sign = (ResultMap) secureManager.verifyNotifySign(pMap, agency, partner,"wap");
//        if (sign.getStatus() != ResultStatus.SUCCESS) {
//            LOGGER.error("【支付回调】md5签名异常，参数:" + pMap);
//            return "success";
//        }
        /**美颜测试结束**/
        //2. 业务参数提取、校验、转换，主要是格式校验
        PayNotifyModel payNotifyModel = paramsConvert(pMap);
        if (payNotifyModel == null) {
            return "success";
        }

        //3. 回调流水预处理，主要是业务逻辑校验
        ResultMap processResult = payNotifyManager.doProcess(payNotifyModel);
        if (Result.isSuccess(processResult) && 1 == (int) processResult.getReturnValue()) {
            //调用平账退款接口
            LOGGER.info("【平账退款】调用平账退款接口参数：" + JSONUtil.Bean2JSON(processResult.getData().get("fairAccRefundModel")));
            refundManager.fairAccountRefund((FairAccRefundModel) processResult.getData().get("fairAccRefundModel"));
            LOGGER.info("【平账退款】平账退款成功！");
        }
        return "success";  // 返回结果只是表示收到回调
    }

    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/ali/pay/wapSync",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping(value = "/wapSync")
    public ModelAndView aliWapSyncNotify(HttpServletRequest request) throws ServiceException, IOException {
        ModelAndView view = new ModelAndView("notifySync");
        /******1. 签名校验*******/
        PMap<String, String> pMap = ControllerUtil.getParamPMap(request);
        LOGGER.info("【wapSync:params】" + pMap);
        //Todo 调用签名公共服务进行签名校验
        String agency = AgencyType.ALIPAY.name();
        String reqDetailId = pMap.getString("out_trade_no");
        PayReqDetail payReqDetail = payReqDetailService.selectPayReqDetailById(reqDetailId);
        if(null == payReqDetail){
            LOGGER.error("【支付回调】没有该支付流水信息！reqDetailId=" + reqDetailId);
            return setWapErrorPage(ResultStatus.REQ_INFO_NOT_EXIST_ERROR.getMessage(),
                    ResultStatus.REQ_INFO_NOT_EXIST_ERROR.getCode());
        }
        String partner = payReqDetail.getMerchantNo();
        ResultMap sign = (ResultMap) secureManager.verifyNotifySign(pMap, agency, partner);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            LOGGER.error("md5签名异常，参数:" + pMap);
            return setWapErrorPage(sign.getStatus().getMessage(),
                    sign.getStatus().getCode());
        }

        String url = null;
        //1.根据reqId查询payId
        PayOrderRelation paramRelation = new PayOrderRelation();
        paramRelation.setPayDetailId(pMap.getString("out_trade_no"));
        List<PayOrderRelation> relationList = payOrderRelationService.selectPayOrderRelation(paramRelation);
        if(null == relationList || relationList.size() == 0){
            LOGGER.error("There is no PayOrderRelation from reqId={}", pMap.getString("out_trade_no"));
            return setWapErrorPage(ResultStatus.PAY_ORDER_RELATION_NOT_EXIST.getMessage(),
                    ResultStatus.PAY_ORDER_RELATION_NOT_EXIST.getCode());
        }
        //2.根据payIdList查询payOrder信息
        List<PayOrderInfo> payOrderInfos = payOrderService.selectPayOrderByPayIdList(relationList);
        PayOrderInfo payOrderInfo = null;
        if (payOrderInfos != null) {
            payOrderInfo = payOrderInfos.get(0);
            url = payOrderInfo.getAppPageUrl();
        } else {
            LOGGER.error("There is no orderinfo from reqId={}", pMap.getString("out_trade_no"));
            return setWapErrorPage(ResultStatus.PAY_ORDER_NOT_EXIST.getMessage(),
                    ResultStatus.PAY_ORDER_NOT_EXIST.getCode());
        }
        //获得通知参数
        ResultMap resultNotify = payNotifyManager.getNotifyMap(payOrderInfo);
        if(!Result.isSuccess(resultNotify)){
            return setWapErrorPage(resultNotify.getStatus().getMessage(),
                    resultNotify.getStatus().getCode());
        }
        view.addObject("payFeeType", payReqDetail.getPayFeeType());
        view.addObject("errorCode", 0);
        view.addObject("appUrl", url);
        view.addObject("returnMap", resultNotify.getReturnValue());
        return view;
    }

    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/ali/pay/sdkAsync",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping(value = "/sdkAsync")
    @ResponseBody
    public String aliSdkNotify(AliPayWebNotifyParams aliPayWebNotifyParams, HttpServletRequest request) throws ServiceException {
        //1. 签名校验
        PMap<String, String> pMap = ControllerUtil.getParamPMap(request);
        LOGGER.info("【sdkAsync:params】" + pMap);
        //调用签名公共服务进行签名校验
        String agency = AgencyType.ALIPAY.name();
        String reqDetailId = pMap.getString("out_trade_no");
        PayReqDetail payReqDetail = payReqDetailService.selectPayReqDetailById(reqDetailId);
        if (null == payReqDetail) {
            LOGGER.error("【支付回调】没有该支付流水信息，reqDetailId = " + payReqDetail);
            return "success";
        }
        String partner = payReqDetail.getMerchantNo();
        ResultMap sign = (ResultMap) secureManager.verifyNotifySign(pMap, agency, partner,"sdk");
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            LOGGER.error("【支付回调】RSA签名异常，参数:" + pMap);
            return "success";
        }

        //2. 业务参数提取、校验、转换，主要是格式校验
        PayNotifyModel payNotifyModel = paramsConvert(aliPayWebNotifyParams);
        if (payNotifyModel == null) {
            return "success";
        }

        //3. 回调流水预处理，主要是业务逻辑校验
        ResultMap processResult = payNotifyManager.doProcess(payNotifyModel);
        if (Result.isSuccess(processResult) && 1 == (int) processResult.getReturnValue()) {
            //调用平账退款接口
            LOGGER.info("【平账退款】调用平账退款接口参数：" + JSONUtil.Bean2JSON(processResult.getData().get("fairAccRefundModel")));
            refundManager.fairAccountRefund((FairAccRefundModel) processResult.getData().get("fairAccRefundModel"));
            LOGGER.info("【平账退款】平账退款成功！");
        }
        return "success";  // 返回结果只是表示收到回调
    }

    private PayNotifyModel paramsConvert(Object notifyParams) {
        PMap paramMap = null;
        AliPayWebNotifyParams aliPayWebNotifyParams = null;
        if(notifyParams instanceof AliPayWebNotifyParams){
            aliPayWebNotifyParams = (AliPayWebNotifyParams)notifyParams;
            String orderStatus = aliPayWebNotifyParams.getTrade_status();
            if (!"TRADE_FINISHED".equals(orderStatus) && !"TRADE_SUCCESS".equals(orderStatus)) {
                return null;
            }

            paramMap = BeanUtil.Bean2PMap(aliPayWebNotifyParams); //因为aliPayWebNotifyParams作为controller封装对象都是String型的，不便于操作，此处转换为PMap便于处理
        } else if(notifyParams instanceof PMap)
            paramMap = (PMap)notifyParams;

        PayNotifyModel payNotifyModel = new PayNotifyModel();
        payNotifyModel.setPayDetailId(paramMap.getString("out_trade_no"));
        payNotifyModel.setAgencyOrderId(paramMap.getString("trade_no"));
        payNotifyModel.setChannelType(paramMap.getString("out_channel_type"));
        payNotifyModel.setAgencyPayTime(DateUtil.parse(paramMap.getString("gmt_payment"), DateUtil.DATE_FORMAT_SECOND));

        String totalFee = paramMap.getString("total_fee");
        BigDecimal true_money = new BigDecimal(totalFee);
        payNotifyModel.setTrueMoney(true_money);

        return payNotifyModel;
    }

    private PayNotifyModel sdkParamsConvert(PMap paramMap) {
        String orderStatus = paramMap.getString("trade_status");
        if (!"TRADE_FINISHED".equals(orderStatus) && !"TRADE_SUCCESS".equals(orderStatus)) {
            return null;
        }
        PayNotifyModel payNotifyModel = new PayNotifyModel();
        payNotifyModel.setPayDetailId(paramMap.getString("out_trade_no"));
        payNotifyModel.setAgencyOrderId(paramMap.getString("trade_no"));
        payNotifyModel.setChannelType(paramMap.getString("out_channel_type"));
        payNotifyModel.setAgencyPayTime(DateUtil.parse(paramMap.getString("notify_reg_time"), DateUtil.DATE_FORMAT_MILLS_SHORT_ALISDK));

        String totalFee = paramMap.getString("total_fee");
        BigDecimal true_money = new BigDecimal(totalFee);
        payNotifyModel.setTrueMoney(true_money);
        return payNotifyModel;
    }

    @RequestMapping("/testBgUrl")
    @ResponseBody
    public String testBgUrl(HttpServletRequest resquest) {

        LOGGER.info("***********success testBgUrl***********" + JSONUtil.Bean2JSON(resquest.getParameterMap()));
        return "success";
    }
}
