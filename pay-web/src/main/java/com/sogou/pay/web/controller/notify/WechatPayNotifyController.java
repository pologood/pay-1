package com.sogou.pay.web.controller.notify;

import java.util.Map;

import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.thirdpay.api.PayPortal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.pay.common.cache.RedisUtils;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.notify.PayNotifyManager;
//import com.sogou.pay.thirdpay.api.QueryApi;
import com.sogou.pay.web.controller.BaseController;

/**
 * User: Liwei
 * Date: 15/3/3
 * Time: 下午2:58
 * Description:微信支付回调接口
 */
@Controller
public class WechatPayNotifyController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(WechatPayNotifyController.class);

    private static final String UNDERLINE = "_";
    
    private static final String WECHAT_QUERY_ORDER = "WECHAT_ORDER_QUERY";

    @Autowired
    private PayNotifyManager payNotifyManager;

    @Autowired
    private PayPortal payPortal;
    @Autowired
    private RedisUtils redisUtils;


    @RequestMapping(value = "/notify/status/wechat", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getWechatStatus(@RequestParam Map params){
        ResultMap result = ResultMap.build();
        String payReqId = (String)params.get("payReqId");
        if(null == payReqId){
            log.error("[getWechatStatus] 轮询微信支付状态失败, payReqId为空, 参数:" + params);
            result.withError(ResultStatus.QUERY_ORDER_PARAM_ERROR);
            return JSONUtil.Bean2JSON(result);
        }
        String key = WECHAT_QUERY_ORDER + UNDERLINE + payReqId;
        Map<String,String> queryParam = (Map<String,String>)redisUtils.hGetAll(key);
        if(null == queryParam || queryParam.isEmpty()){
            ResultMap<PMap<String,String>> resultDb = payNotifyManager.getQueryOrderParam(params);
            if(!ResultMap.isSuccess(resultDb)){
                log.error("[getWechatStatus] 轮询微信支付状态失败, 无法获取订单信息, 参数:"+params);
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
            result.withError(ResultStatus.THIRD_QUERY_ERROR);
            return JSONUtil.Bean2JSON(result);
        }
        result.addItem("payReqId", payReqId);
        result.addItem("payStatus", wechatResult.getData().get("order_state"));
        return JSONUtil.Bean2JSON(result);
    }

}
