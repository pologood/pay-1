package com.sogou.pay.web.controller.notify.wechatpay;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.thirdpay.api.PayPortal;
import org.dom4j.DocumentException;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sogou.pay.common.cache.RedisUtils;
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
import com.sogou.pay.thirdpay.biz.enums.AgencyType;
import com.sogou.pay.service.payment.PayOrderRelationService;
import com.sogou.pay.service.payment.PayOrderService;
//import com.sogou.pay.thirdpay.api.QueryApi;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.form.notify.WeChatPayWebNotifyParams;
import com.sogou.pay.web.utils.ControllerUtil;

/**
 * User: Liwei
 * Date: 15/3/3
 * Time: 下午2:58
 * Description:微信支付回调接口
 */
@Controller
@RequestMapping(value = "/notify/wechat/pay")
public class WechatPayNotifyController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatPayNotifyController.class);

    private static final String UNDERLINE = "_";
    
    private static final String WECHAT_QUERY_ORDER = "WECHAT_ORDER_QUERY";
    
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
    //@Autowired
    //private QueryApi queryApi;
    @Autowired
    private PayPortal payPortal;
    @Autowired
    private RedisUtils redisUtils;

    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/wechat/pay/webAsync",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping(value = "/webAsync")
    @ResponseBody
    public String weChatWebNotify(HttpServletRequest request) throws ServiceException, IOException, DocumentException {
        /******1. 签名校验*******/
        PMap<String, String> pMap = ControllerUtil.getXmlParamPMap(request);
        LOGGER.info("【webAsync:pMap】" + pMap);
        String agency = AgencyType.WECHAT.name();
        String partner = pMap.getString("mch_id");
        ResultMap sign = (ResultMap) secureManager.verifyNotifySign(pMap, agency, partner);
        if (sign.getStatus() != ResultStatus.SUCCESS) {
            LOGGER.error("md5签名异常，参数:" + pMap);
            return "success";
        }

        /*******2. 业务参数提取、校验、转换，主要是格式校验*******/
        WeChatPayWebNotifyParams weChatPayWebNotifyParams = new WeChatPayWebNotifyParams();
        weChatPayWebNotifyParams.setOut_trade_no(pMap.get("out_trade_no"));
        weChatPayWebNotifyParams.setTransaction_id(pMap.get("transaction_id"));
        weChatPayWebNotifyParams.setBank_billno(pMap.get("bank_billno"));
        weChatPayWebNotifyParams.setResult_code(pMap.get("result_code"));
        weChatPayWebNotifyParams.setTime_end(pMap.get("time_end"));
        weChatPayWebNotifyParams.setTotal_fee(pMap.get("total_fee"));
        PayNotifyModel payNotifyModel = paramsConvert(weChatPayWebNotifyParams);
        if (payNotifyModel == null) {
            return "success";
        }

        ResultMap processResult = payNotifyManager.doProcess(payNotifyModel);
        if(Result.isSuccess(processResult) && 1 == (int)processResult.getReturnValue()){
            //调用平账退款接口
            refundManager.fairAccountRefund((FairAccRefundModel)processResult.getData().get("fairAccRefundModel"));
        }
        return "success";  // 返回结果只是表示收到回调
    }

    private PayNotifyModel paramsConvert(WeChatPayWebNotifyParams weChatPayWebNotifyParams) {
        String orderStatus = weChatPayWebNotifyParams.getResult_code();
        if (!"SUCCESS".equals(orderStatus)) {
            return null;
        }

        PMap paramMap = BeanUtil.Bean2PMap(weChatPayWebNotifyParams); //因为aliPayWebNotifyParams作为controller封装对象都是String型的，不便于操作，此处转换为PMap便于处理

        String totalFee = paramMap.getString("total_fee");
        BigDecimal true_money = new BigDecimal(totalFee).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UP);

        PayNotifyModel payNotifyModel = new PayNotifyModel();
        payNotifyModel.setPayDetailId(paramMap.getString("out_trade_no"));
        payNotifyModel.setAgencyOrderId(paramMap.getString("transaction_id"));
        //payNotifyModel.setPayStatus(pay_status);
        payNotifyModel.setAgencyPayTime(paramMap.getDate("time_end"));
        payNotifyModel.setTrueMoney(true_money);

        return payNotifyModel;
    }

    @Profiled(el = true, logger = "webTimingLogger", tag = "/notify/wechat/pay/webSync",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping(value = "/webSync")
    public ModelAndView wechatWebSyncNotify(HttpServletRequest request) throws ServiceException, IOException {
        ModelAndView view = new ModelAndView("notifySync");
        String payReqId = request.getParameter("payReqId");
        String url = null;
        //1.根据reqId查询payId
        PayOrderRelation paramRelation = new PayOrderRelation();
        paramRelation.setPayDetailId(payReqId);
        List<PayOrderRelation> relationList = payOrderRelationService.selectPayOrderRelation(paramRelation);
        if(null == relationList || relationList.size() == 0){
            LOGGER.error("There is no PayOrderRelation from reqId={}", payReqId);
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
            LOGGER.error("There is no orderinfo from reqId={}", payReqId);
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
        view.addObject("errorCode", 0);
        view.addObject("appUrl", url);
        view.addObject("returnMap", resultNotify.getReturnValue());
        return view;
    }

    
    @RequestMapping(value = "/getWechatStatus")
    @ResponseBody
    public String getWechatStatus(HttpServletRequest request){
        ResultMap result = ResultMap.build();
        Map paraMap = ControllerUtil.getParamPMap(request);
        if(null == paraMap.get("payReqId")){
            LOGGER.error("【获取微信支付状态】payReqId为空！");
            result.withError(ResultStatus.PAY_PARAM_ERROR);
            return JSONUtil.Bean2JSON(result);
        }
        String payReqId = paraMap.get("payReqId").toString();
        String key = WECHAT_QUERY_ORDER + UNDERLINE + payReqId;
        Map<String,String> queryParam = (Map<String,String>)redisUtils.hGetAll(key);
        if(null == queryParam || queryParam.isEmpty()){
            ResultMap<PMap<String,String>> resultDb = payNotifyManager.getQueryOrderParam(paraMap);
            if(!ResultMap.isSuccess(resultDb)){
                LOGGER.error("【获取微信支付状态】获得商户信息失败！paraMap="+paraMap);
                result.withError(ResultStatus.SYSTEM_ERROR);
                return JSONUtil.Bean2JSON(result);
            }
            queryParam = resultDb.getReturnValue();
            try {
                redisUtils.hPutAll(WECHAT_QUERY_ORDER + UNDERLINE + payReqId, queryParam);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //请求微信接口
        //ResultMap wechatResult = queryApi.queryOrder(new PMap(queryParam));
        ResultMap wechatResult = payPortal.queryOrder(new PMap(queryParam));
        if(!ResultMap.isSuccess(wechatResult)){
            result.withError(ResultStatus.SYSTEM_ERROR);
            return JSONUtil.Bean2JSON(result);
        }
        result.addItem("payStatus", wechatResult.getData().get("order_state"));
        return JSONUtil.Bean2JSON(result);
    }

}
