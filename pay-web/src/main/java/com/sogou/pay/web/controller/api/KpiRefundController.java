package com.sogou.pay.web.controller.api;

import com.sogou.pay.common.http.utils.MyThread;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.*;
import com.sogou.pay.manager.model.RefundModel;
import com.sogou.pay.manager.payment.*;
import com.sogou.pay.service.entity.*;
import com.sogou.pay.service.enums.PayOrderStatus;
import com.sogou.pay.service.enums.RefundFlag;
import com.sogou.pay.service.enums.RefundStatus;
import com.sogou.pay.service.enums.RelationStatus;
import com.sogou.pay.service.payment.*;
import com.sogou.pay.service.utils.DataSignUtil;
import com.sogou.pay.service.utils.email.EmailSender;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.form.RefundParams;
import com.sogou.pay.web.utils.ServletUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author 高朋辉
 * @ClassName KpiController
 * @Date 2015年10月14日
 * @Description: KpiController
 */
@Controller
@RequestMapping(value = "/kpiRefund")
public class KpiRefundController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(KpiRefundController.class);

    @Autowired
    private AppManager appManager;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private PayOrderRelationService payOrderRelationService;
    @Autowired
    private PayResDetailService payResDetailService;
    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private AgencyInfoService agencyInfoService;
    @Autowired
    private EmailSender emailSender;

    /**
     * 退款KPI
     *
     * @param request
     * @return
     */
    @RequestMapping
    @ResponseBody
    public ModelAndView Refund(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("kpiSuccess");
        //1.获得用户IP
        String ip = ServletUtil.getRealIp(request);
        //2.创建线程
        MyThread kpiRefund = new MyThread(ip) {
            Long start = System.currentTimeMillis();
            final String subject = "【KPI报警】退款报警";
            //2.1执行线程run方法
            public void run() {
                Long start = System.currentTimeMillis();
                String ip = super.getIp();
                //2.2创建KPI参数
                RefundParams params = new RefundParams();
//                params.setOrderId("OD20150629103321270");
                params.setOrderId("OD20151018112506159");
                params.setBgUrl("https://cash.sogou.com/notify/alipay/1");
                params.setAppId("1999");
                params.setRefundAmount("0.01");
                params.setSignType("0");
                params.setSign(refundSignData(params));
                logger.info("[退款KPI开始]!,ip=" + ip + ",订单号:" + params.getOrderId());
                // 2.3.组装参数,处理退款订单
                RefundModel model = new RefundModel();
                model.setAppId(Integer.parseInt(params.getAppId()));              //业务线id
                model.setOrderId(params.getOrderId());                            //订单id
                model.setRefundAmount(new BigDecimal(params.getRefundAmount()));  //订单退款金额
                model.setBgurl(params.getBgUrl());
                try {
                    //2.4.验证订单相关信息
                    ResultMap orderCheckMap = checkOrderInfo(model);
                    if (!Result.isSuccess(orderCheckMap)) {
//                        sendEmail(subject, orderCheckMap.getMessage());
                        logger.info("[退款KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                        return;
                    }
                    PayOrderInfo payOrderInfo = (PayOrderInfo) orderCheckMap.getReturnValue();
                    //2.5根据支付单payId查询退款表里面所有初始化的退款记录
                    List<RefundInfo> refundInfoList = refundService.selectByPayIdAndRefundStatus(payOrderInfo.getPayId(), RefundStatus.INIT.getValue());
                    if (CollectionUtils.isNotEmpty(refundInfoList)) {    // 已有退款单在执行中
//                        sendEmail(subject, "已有退款单在执行中");
                        logger.info("[退款KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                        return;
                    }
                    //2.6查订单与流水关联表
                    PayOrderRelation payOrderRelation = new PayOrderRelation();
                    payOrderRelation.setPayId(payOrderInfo.getPayId());
                    payOrderRelation.setInfoStatus(RelationStatus.SUCCESS.getValue());
                    List<PayOrderRelation> relations = payOrderRelationService.selectPayOrderRelation(payOrderRelation);
                    if (CollectionUtils.isEmpty(relations)) {
                        // 无Refund Request 单
//                        sendEmail(subject, "订单与流水关联表信息为空");
                        logger.info("[退款KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                        return;
                    }
                    //2.7.查支付回调流水表
                    payOrderRelation = relations.get(0);
                    PayResDetail payResDetail = payResDetailService.selectPayResById(payOrderRelation.getPayDetailId());
                    if (null == payResDetail) {
//                        sendEmail(subject, "支付回调流水表信息为空");
                        logger.info("[退款KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                        return;
                    }
                    //2.8.查支付商户信息
                    String agencyCode = payResDetail.getAgencyCode(); //支付机构编码
                    String merchantNo = payResDetail.getMerchantNo(); //支付机构商户号
                    PayAgencyMerchant agencyMerchant = payAgencyMerchantService
                            .selectByAgencyAndMerchant(agencyCode, merchantNo);
                    if (null == agencyMerchant) {
//                        sendEmail(subject, "支付商户信息表信息为空");
                        logger.info("[退款KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                        return;

                    }
                    //2.9.查支付机构信息
                    AgencyInfo agencyInfo = agencyInfoService
                            .getAgencyInfoByCode(agencyCode, String.valueOf(payResDetail.getAccessPlatform()), String.valueOf(payResDetail.getPayFeeType()));
                    if (null == agencyInfo) {
//                        sendEmail(subject, "支付机构信息表信息为空");
                        logger.info("[退款KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                        return;
                    }
                } catch (Exception e) {
//                    sendEmail(subject, "系统异常");
                    logger.info("[退款KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
                    return;
                }
                if (System.currentTimeMillis() - start > 4000) {
//                    sendEmail(subject, "系统超时！");
                }
                logger.info("[退款KPI结束]!,ip=" + ip + ",订单号:" + params.getOrderId());
            }
        };
        kpiRefund.start();
        return view;
    }

    /**
     * 验证订单的金额、支付状态、退款标识等信息
     *
     * @param model 退款请求参数
     * @author 用户平台事业部---高朋辉
     */
    private ResultMap checkOrderInfo(RefundModel model) {
        ResultMap result = ResultMap.build();
        try {
            //1.验证退款金额
            BigDecimal refundAmount = model.getRefundAmount(); //退款金额
            String orderId = model.getOrderId();               //业务线订单号
            int appId = model.getAppId();                      //业务线ID
            //2.根据业务线订单号、业务线ID查询唯一订单信息
            PayOrderInfo payOrderInfo = payOrderService.selectPayOrderInfoByOrderId(orderId, appId);
            if (null == payOrderInfo) {
                return ResultMap.build(ResultStatus.REFUND_ORDER_NOT_EXIST);
            }

            if (PayOrderStatus.SUCCESS.getValue() != payOrderInfo.getPayOrderStatus()) {
                return ResultMap.build(ResultStatus.REFUND_ORDER_NOT_PAY);
            }
            //3.检查支付订单是否已经退款
            if (RefundFlag.SUCCESS.getValue() == payOrderInfo.getRefundFlag()) {
                return ResultMap.build(ResultStatus.REFUND_REFUND_ALREADY_DONE);
            }
            //4.检查退款金额与支付金额是否相同
            BigDecimal payMoney = payOrderInfo.getOrderMoney();            //订单支付金额
            // BigDecimal notRefund = payMoney.subtract(payRefundMoney);   //没有退款的金额
            // BigDecimal payRefundMoney = payOrderInfo.getRefundMoney();  //订单退款金额
            if (refundAmount.compareTo(payMoney) != 0) {
                // 退款金额不等于余额
                return ResultMap.build(ResultStatus.REFUND_PARTIAL_REFUND);
            }
            result.withReturn(payOrderInfo);
            return result;
        } catch (Exception e) {
            return ResultMap.build(ResultStatus.SYSTEM_ERROR);
        }
    }

    private String refundSignData(RefundParams params) {
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
            return BeanUtil.Bean2MapNotNull(params);
        }
    }

    private void sendEmail(String subject, String content) {
        emailSender.sendEmail("error.ftl", subject, content,
                "gaopenghui@sogou-inc.com", "xiepeidong@sogou-inc.com");
    }
}
