package com.sogou.pay.web.controller.api;

import com.sogou.pay.common.http.utils.MyThread;
import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.utils.*;
import com.sogou.pay.manager.model.PayOrderQueryModel;
import com.sogou.pay.manager.payment.*;
import com.sogou.pay.service.entity.*;
import com.sogou.pay.service.enums.PayOrderStatus;
import com.sogou.pay.service.payment.*;
import com.sogou.pay.service.utils.Constant;
import com.sogou.pay.service.utils.DataSignUtil;
import com.sogou.pay.service.utils.email.EmailSender;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.form.PayOrderQueryParams;
import com.sogou.pay.web.utils.ServletUtil;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author 高朋辉
 * @ClassName KpiController
 * @Date 2015年10月14日
 * @Description: KpiController
 */
@Controller
@RequestMapping(value = "/kpiQuery")
public class KpiQueryController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(KpiQueryController.class);


    @Autowired
    private AppManager appManager;

    @Autowired
    private AppService appService;

    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;

    @Autowired
    private PayReqDetailService payReqDetailService;

    @Autowired
    private PayOrderRelationService payOrderRelationService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private EmailSender emailSender;

    /**
     * 测试查询订单状态KPI
     *
     * @param request
     * @return
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/kpiQuery/Query",
            timeThreshold = 10, normalAndSlowSuffixesEnabled = true)
    @RequestMapping
    @ResponseBody
    public ModelAndView Query(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("kpiSuccess");
        //1.获得用户IP
        String ip = ServletUtil.getRealIp(request);
        //2.设置KPI参数
        MyThread kpiQuery = new MyThread(ip) {
            Long start = System.currentTimeMillis();
            final String subject = "【KPI报警】查询订单报警";

            public void run() {
                Long start = System.currentTimeMillis();
                String ip = super.getIp();
                PayOrderQueryParams params = new PayOrderQueryParams();
                params.setOrderId("OD20151018112506159");
//                params.setOrderId("OD20150512171256862");
                params.setAppId("1999");
                params.setSignType("0");
                params.setSign(querySignData(params));
                logger.info("[查询订单状态KPI开始]!,ip=" + ip + ",订单号:" + params.getOrderId());
                // 4.处理支付订单查询
                PayOrderQueryModel model = new PayOrderQueryModel();
                model.setAppId(Integer.parseInt(params.getAppId()));
                model.setOrderId(params.getOrderId());
                // 5.获取商户订单ID
                App app = appService.selectApp(model.getAppId());
                if (null == app) {
                    sendEmail(subject, "业务线信息为空！");
                    logger.info("[查询订单状态KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                    return;
                }
                // 6.根据订单查询支付回调信息
                List<PayReqDetail> payReqDetailList = null;
                try {
                    //6.1.根据orderId和appId查询订单信息
                    PayOrderInfo payOrderInfo = payOrderService.selectPayOrderInfoByOrderId(model.getOrderId(), app.getAppId());
                    if (null == payOrderInfo) {
                        sendEmail(subject, "订单信息为空！");
                        logger.info("[查询订单状态KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                        return;
                    }
                    // 6.2.检查支付单里的支付状态是否成功，成功则返回
                    if (payOrderInfo.getPayOrderStatus() == PayOrderStatus.SUCCESS.getValue()) {
//                        sendEmail(subject, "支付单已支付！");
                        logger.info("[查询订单状态KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                        return;
                    }
                    // 6.3.根据payId查询关联表
                    PayOrderRelation paramRelation = new PayOrderRelation();
                    paramRelation.setPayId(payOrderInfo.getPayId());
                    List<PayOrderRelation> relationList = payOrderRelationService.selectPayOrderRelation(paramRelation);
                    if (null == relationList || relationList.size() == 0) {
                        sendEmail(subject, "订单关联表信息为空！");
                        logger.info("[查询订单状态KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                        return;
                    }
                    // 6.4.查询支付单流水信息
                    payReqDetailList = payReqDetailService.selectPayReqByReqIdList(relationList);
                    if (null == payReqDetailList) {
                        sendEmail(subject, "支付流水信息为空！");
                        logger.info("[查询订单状态KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                        return;
                    }
                    for (PayReqDetail payReqDetail : payReqDetailList) {
                        PayAgencyMerchant merchant = new PayAgencyMerchant();
                        merchant.setAgencyCode(payReqDetail.getAgencyCode());
                        merchant.setAppId(app.getAppId());
                        merchant.setCompanyCode(app.getBelongCompany());
                        PayAgencyMerchant merchantQuery = payAgencyMerchantService.selectPayAgencyMerchant(merchant);
                        if (null == merchantQuery) {
                            sendEmail(subject, "支付商户机构信息为空！");
                            logger.info("[查询订单状态KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                            return;
                        }
                        //7.组装网关参数
                        PMap map = new PMap();
                        map.put("agencyCode", merchantQuery.getAgencyCode());
                        map.put("merchantNo", merchantQuery.getMerchantNo());
                        map.put("sellerEmail", merchantQuery.getSellerEmail());
                        map.put("md5securityKey", merchantQuery.getEncryptKey());
                        map.put("queryUrl", Constant.QUERY_URL_MAP.get(merchantQuery.getAgencyCode()));
                        map.put("serialNumber", payReqDetail.getPayDetailId());
                    }
                } catch (Exception e) {
                    sendEmail(subject, "系统异常！");
                    logger.info("[查询订单状态KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                    return;
                }
                if (System.currentTimeMillis() - start > 4000) {
                    sendEmail(subject, "系统超时！");
                }
                logger.info("[查询订单状态KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
            }
        };
        kpiQuery.start();
        return view;
    }

    private String querySignData(PayOrderQueryParams params) {
        Map paramMap = convertToMap(params);
        Result<App> appresult = appManager.selectAppInfo(Integer.parseInt(params.getAppId()));
        App app = appresult.getReturnValue();
        String key = app.getSignKey();
        String sign = DataSignUtil.sign(packParams(paramMap, key), "0");
        return sign;
    }

    private String packParams(Map paramMap, String secret) {
        if (paramMap == null) {
            return null;
        }
        List<String> keyList = new ArrayList<String>(paramMap.keySet());
        Collections.sort(keyList);
        //拼接k1=v1k2=v2
        StringBuilder paramStrBuilder = new StringBuilder();
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            Object value = paramMap.get(key);

            if (value != null) {
                paramStrBuilder.append(key).append("=").append(value.toString());
                if (i != keyList.size() - 1) {//拼接时，不包括最后一个&字符
                    paramStrBuilder.append("&");
                }
            }
        }
        //拼接secretKey
        paramStrBuilder.append(secret);
        return paramStrBuilder.toString();
    }

    private Map convertToMap(Object params) {
        if (params instanceof Map) {
            return MapUtil.dropNulls((Map) params);
        } else {
            return BeanUtil.beanToMapNotNull(params);
        }
    }

    private void sendEmail(String subject, String content) {
        emailSender.sendEmail("error.ftl", subject, content,
                "wujingpan@sogou-inc.com", "huangguoqing@sogou-inc.com",
                "qibaichao@sogou-inc.com", "gaopenghui@sogou-inc.com");
    }

}
