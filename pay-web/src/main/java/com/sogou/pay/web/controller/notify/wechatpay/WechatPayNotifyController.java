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
    private PayNotifyManager payNotifyManager;

    @Autowired
    private PayPortal payPortal;
    @Autowired
    private RedisUtils redisUtils;


    @RequestMapping(value = "/getWechatStatus", produces = "text/plain; charset=utf-8")
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
        ResultMap wechatResult = payPortal.queryOrder(new PMap(queryParam));
        if(!ResultMap.isSuccess(wechatResult)){
            result.withError(ResultStatus.SYSTEM_ERROR);
            return JSONUtil.Bean2JSON(result);
        }
        result.addItem("payStatus", wechatResult.getData().get("order_state"));
        return JSONUtil.Bean2JSON(result);
    }

}
