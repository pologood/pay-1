package com.sogou.pay.thirdpay.api;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.thirdpay.biz.BillPayService;
import com.sogou.pay.thirdpay.biz.enums.AgencyType;
import com.sogou.pay.thirdpay.service.Alipay.AlipayService;
import com.sogou.pay.thirdpay.service.Tenpay.TenpayService;
import com.sogou.pay.thirdpay.service.ThirdpayService;
import com.sogou.pay.thirdpay.service.Wechat.WechatService;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by xiepeidong on 2016/1/19.
 */
@Component
public class PayPortal {

    private static Logger log = LoggerFactory.getLogger(PayPortal.class);

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private TenpayService tenpayService;

    @Autowired
    private BillPayService billPayService;

    @Autowired
    private WechatService wechatService;

    private HashMap<String, ThirdpayService> serviceHashMap;

    @Autowired
    public void init() {
        serviceHashMap = new HashMap<>();
        serviceHashMap.put(AgencyType.ALIPAY.name(), alipayService);
        serviceHashMap.put(AgencyType.TENPAY.name(), tenpayService);
        serviceHashMap.put(AgencyType.WECHAT.name(), wechatService);
    }

    public ResultMap<String> preparePay(PMap params) {

        ResultMap result = ResultMap.build();

        //1.验证共同参数是否为空
        if (StringUtil.isEmpty(params.getString("agencyCode"), params.getString("payChannle"),
                params.getString("orderAmount"), params.getString("md5securityKey"))) {
            result.withError(ResultStatus.THIRD_PAY_PARAM_ERROR);
            return result;
        }
        //2.验证金额是否大于0
        BigDecimal oAmount = new BigDecimal(params.getString("orderAmount"));
        if (oAmount.compareTo(new BigDecimal(0)) != 1) {
            result.withError(ResultStatus.THIRD_PAY_PARAM_ERROR);
            return result;
        }

        String agencyCode = params.getString("agencyCode");
        ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
        //2.根据不同支付机构请求不接口获取支付参数
        String payChannle = params.getString("payChannle");
        try {
            switch (payChannle) {
                //账户支付
                case "PC_ACCOUNT":
                    result = thirdpayService.preparePayInfoAccount(params);
                    break;
                //网关支付
                case "PC_GATEWAY":
                    result = thirdpayService.preparePayInfoGatway(params);
                    break;
                //扫码支付
                case "PC_QRCODE":
                    result = thirdpayService.preparePayInfoQRCode(params);
                    break;
                //手机客户端支付
                case "MOBILE_CLIENT":
                    result = thirdpayService.preparePayInfoSDK(params);
                    break;
                //WAP支付
                case "MOBILE_WAP":
                    result = thirdpayService.preparePayInfoWap(params);
                    break;
                //快钱支付（包括B2C和B2C）
                case "PC_99BILL":
                    result = billPayService.preparePayInfo(params);
                    break;
                default:
                    log.error("Payment Gateway:Does Not Support Pay Channel，Parameters:" + JSONUtil.Bean2JSON(params));
                    result.build(ResultStatus.THIRD_PAY_CHANNEL_NOT_EXIST);
            }
        } catch (ServiceException se) {
            log.error("Payment Gateway:Get Pay Parameters Unusually，ServiceException", se.toString() + "Parameters:" + JSONUtil.Bean2JSON(params));
            result.withError(se.getStatus());
        } catch (Exception e) {
            log.error("Payment Gateway:Get Pay Parameters Unusually，Exception", e.toString() + "Parameters:" + JSONUtil.Bean2JSON(params));
            result.withError(ResultStatus.THIRD_PAY_ERROR);
        }
        return result;
    }

    public ResultMap<String> refundOrder(PMap params) {
        ResultMap result = ResultMap.build();

        //1.验证共同参数是否为空
        if (StringUtil.isEmpty(params.getString("agencyCode"),
                params.getString("md5securityKey"),params.getString("refundAmount"),
                params.getString("totalAmount"))) {
            result.withError(ResultStatus.THIRD_REFUND_PARAM_ERROR);
            return result;
        }
        //2.验证金额是否大于0
        BigDecimal refundAmount = new BigDecimal(params.getString("refundAmount"));
        BigDecimal totalAmount = new BigDecimal(params.getString("totalAmount"));
        int compareRefundAmount = refundAmount.compareTo(new BigDecimal(0));
        int compareTotalAmount = totalAmount.compareTo(new BigDecimal(0));
        if (compareRefundAmount != 1 || compareTotalAmount != 1) {
            result.withError(ResultStatus.THIRD_REFUND_PARAM_ERROR);
            return result;
        }

        //2.根据不同支付机构调用退款接口
        String agencyCode = params.getString("agencyCode");
        ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);

        try {
            if (agencyCode.equals(AgencyType.BILL99.name())) {
                result = billPayService.refundOrderInfo(params);
            } else {
                thirdpayService.refundOrder(params);
            }
        } catch (ServiceException se) {
            log.error("Refund Order:Get Refund Order Unusually:", se + "Parameters:" + JSONUtil.Bean2JSON(params));
            result.withError(se.getStatus());
            return result;
        } catch (Exception e) {
            log.error("Refund Order:Get Refund Order Unusually:", e + "Parameters:" + JSONUtil.Bean2JSON(params));
            result.withError(ResultStatus.THIRD_REFUND_ERROR);
            return result;
        }
        log.info("Refund Order End!Parameters:" + JSONUtil.Bean2JSON(params) + "Return Result:" + JSONUtil.Bean2JSON(result));
        return result;
    }


    public ResultMap<String> queryOrder(PMap params) {
        ResultMap result = ResultMap.build();

        if (StringUtil.isEmpty(params.getString("agencyCode"), params.getString("md5securityKey"))) {
            result.withError(ResultStatus.THIRD_QUERY_PARAM_ERROR);
            return result;
        }

        //2.根据不同支付机构调用退款接口
        String agencyCode = params.getString("agencyCode");
        ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
        try {
            if (agencyCode.equals(AgencyType.BILL99.name())) {
                result = billPayService.queryOrderInfo(params);
            } else {
                thirdpayService.queryOrder(params);
            }
        } catch (ServiceException se) {
            log.error("Query Order:Get Query Parameters Unusually:", se + "Parameters:" + JSONUtil.Bean2JSON(params));
            return result.build(se.getStatus());
        } catch (Exception e) {
            log.error("Query Order:Get Query Parameters Unusually:", e + "Parameters:" + JSONUtil.Bean2JSON(params));
            return result.build(ResultStatus.THIRD_QUERY_ERROR);
        }
        log.info("Query Order End!Parameters:" + JSONUtil.Bean2JSON(params) + "Return Result:" + JSONUtil.Bean2JSON(result));
        return result;

    }

    public ResultMap<String> queryRefund(PMap params) {
        ResultMap result = ResultMap.build();

        //1.验证共同参数是否为空
        if (StringUtil.isEmpty(params.getString("agencyCode"), params.getString("md5securityKey"))) {
            result.withError(ResultStatus.THIRD_QUERY_REFUND_PARAM_ERROR);
            return result;
        }

        //2.根据不同支付机构调用退款接口
        String agencyCode = params.getString("agencyCode");
        ThirdpayService thirdpayService = serviceHashMap.get(agencyCode);
        try {
            if (agencyCode.equals(AgencyType.BILL99.name())) {
                result = billPayService.queryRefundInfo(params);
            } else {
                thirdpayService.queryRefundOrder(params);
            }
        } catch (ServiceException se) {
            log.error("Query Refund Order: Unusually:", se + "Parameters:" + JSONUtil.Bean2JSON(params));
            return result.build(se.getStatus());
        } catch (Exception e) {
            log.error("Query Refund Order: Unusually:", e + "Parameters:" + JSONUtil.Bean2JSON(params));
            return result.build(ResultStatus.THIRD_QUERY_REFUND_ERROR);
        }
        log.info("Query Refund Order End!Parameters:" + JSONUtil.Bean2JSON(params) + "Return Result:" + JSONUtil.Bean2JSON(result));
        return result;
    }


}
