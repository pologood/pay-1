package com.sogou.pay.web.api;

import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.entity.PayReqDetail;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.service.payment.PayReqDetailService;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.notify.PayNotifyController;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by xiepeidong on 2016/3/7.
 */
public class PayNotifyControllerTest extends BaseTest {

    private static Logger log = LoggerFactory.getLogger(PayNotifyController.class);

    @Autowired
    private PayReqDetailService payReqDetailService;
    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;

    private ResultMap getSecretKey(String agencyCode, String reqId) {
        //查询支付流水单
        PayReqDetail payReqDetail = payReqDetailService.selectPayReqDetailById(reqId);
        if (null == payReqDetail) {
            log.error("[getSecretKey] 查询支付流水信息失败, reqId=" + reqId + ", agencyCode=" + agencyCode);
            return ResultMap.build(ResultStatus.REQ_DETAIL_NOT_EXIST_ERROR);
        }
        String merchantNo = payReqDetail.getMerchantNo();
        PayAgencyMerchant payAgencyMerchant = payAgencyMerchantService.selectByAgencyAndMerchant(agencyCode, merchantNo);
        if (payAgencyMerchant == null) {
            log.error("[getSecretKey] 查询商户信息失败, agencyCode=" + agencyCode + ", merchantNo=" + merchantNo);
            return ResultMap.build(ResultStatus.THIRD_NOTIFY_SYNC_PARAM_ERROR);
        }
        //获取签名key
        String md5securityKey = payAgencyMerchant.getEncryptKey();
        String publicCertFilePath = payAgencyMerchant.getPubKeypath();
        String privateCertFilePath = payAgencyMerchant.getPrivateKeypath();

        ResultMap result = ResultMap.build();
        result.addItem("md5securityKey", md5securityKey);
        result.addItem("publicCertFilePath", publicCertFilePath);
        result.addItem("privateCertFilePath", privateCertFilePath);
        return result;
    }


    @Test
    public void testWebSyncAlipay() {
        String url = "/notify/websync/alipay";
        String agencyCode = "ALIPAY";
        String reqId = "ZF20160303200209140969243001";
        ResultMap result = getSecretKey(agencyCode, reqId);
        if (!Result.isSuccess(result)) {
            log.error("[testWebSyncAlipay] 获取密码失败, reqId=" + reqId + ", agencyCode=" + agencyCode);
            return;
        }
        String md5securityKey = (String) result.getItem("md5securityKey");
        PMap requestPMap = new PMap();
        requestPMap.put("is_success", "T");
        requestPMap.put("sign_type", "MD5");
        requestPMap.put("out_trade_no", reqId);
        requestPMap.put("trade_status", "TRADE_FINISHED");
        String sign = SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, null);
        requestPMap.put("sign", sign);
        testGet(url, requestPMap);
    }

    @Test
    public void testWebAsyncAlipay() {
        String url = "/notify/webasync/alipay";
        String agencyCode = "ALIPAY";
        String reqId = "ZF20160303200209140969243001";
        String trade_no = "123456789";
        String gmt_payment = DateUtil.formatTime(new Date());
        ResultMap result = getSecretKey(agencyCode, reqId);
        if (!Result.isSuccess(result)) {
            log.error("[testWebAsyncAlipay] 获取密码失败, reqId=" + reqId + ", agencyCode=" + agencyCode);
            return;
        }
        String md5securityKey = (String) result.getItem("md5securityKey");
        PMap requestPMap = new PMap();
        requestPMap.put("sign_type", "MD5");
        requestPMap.put("out_trade_no", reqId);
        requestPMap.put("trade_no", trade_no);
        requestPMap.put("trade_status", "TRADE_FINISHED");
        requestPMap.put("out_channel_type", "BALANCE");
        requestPMap.put("gmt_payment", gmt_payment);
        requestPMap.put("total_fee", "10.00");
        String sign = SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, null);
        requestPMap.put("sign", sign);
        testGet(url, requestPMap);
    }

    @Test
    public void testWapSyncAlipay() {
        String url = "/notify/wapsync/alipay";
        String agencyCode = "ALIPAY";
        String reqId = "ZF20160303200209140969243001";
        ResultMap result = getSecretKey(agencyCode, reqId);
        if (!Result.isSuccess(result)) {
            log.error("[testWapSyncAlipay] 获取密码失败, reqId=" + reqId + ", agencyCode=" + agencyCode);
            return;
        }
        String md5securityKey = (String) result.getItem("md5securityKey");
        PMap requestPMap = new PMap();
        requestPMap.put("is_success", "T");
        requestPMap.put("sign_type", "MD5");
        requestPMap.put("out_trade_no", reqId);
        requestPMap.put("trade_status", "TRADE_FINISHED");
        String sign = SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, null);
        requestPMap.put("sign", sign);
        testGet(url, requestPMap);
    }

    @Test
    public void testWapAsyncAlipay() {
        String url = "/notify/wapasync/alipay";
        String agencyCode = "ALIPAY";
        String reqId = "ZF20160303200209140969243001";
        String trade_no = "123456789";
        String gmt_payment = DateUtil.formatTime(new Date());
        ResultMap result = getSecretKey(agencyCode, reqId);
        if (!Result.isSuccess(result)) {
            log.error("[testWapAsyncAlipay] 获取密码失败, reqId=" + reqId + ", agencyCode=" + agencyCode);
            return;
        }
        String md5securityKey = (String) result.getItem("md5securityKey");
        PMap requestPMap = new PMap();
        requestPMap.put("sign_type", "MD5");
        requestPMap.put("out_trade_no", reqId);
        requestPMap.put("trade_no", trade_no);
        requestPMap.put("trade_status", "TRADE_FINISHED");
        requestPMap.put("out_channel_type", "BALANCE");
        requestPMap.put("gmt_payment", gmt_payment);
        requestPMap.put("total_fee", "10.00");
        String sign = SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, null);
        requestPMap.put("sign", sign);
        testGet(url, requestPMap);
    }

    @Test
    public void testSDKAsyncAlipay() {
        String url = "/notify/sdkasync/alipay";
        String agencyCode = "ALIPAY";
        String reqId = "ZF20160303200209140969243001";
        String trade_no = "123456789";
        String gmt_payment = DateUtil.formatTime(new Date());
        ResultMap result = getSecretKey(agencyCode, reqId);
        if (!Result.isSuccess(result)) {
            log.error("[testSDKAsyncAlipay] 获取密码失败, reqId=" + reqId + ", agencyCode=" + agencyCode);
            return;
        }
        String privateCertFilePath = (String) result.getItem("privateCertFilePath");
        PMap requestPMap = new PMap();
        requestPMap.put("sign_type", "MD5");
        requestPMap.put("out_trade_no", reqId);
        requestPMap.put("trade_no", trade_no);
        requestPMap.put("trade_status", "TRADE_FINISHED");
        requestPMap.put("out_channel_type", "BALANCE");
        requestPMap.put("gmt_payment", gmt_payment);
        requestPMap.put("total_fee", "10.00");
        String sign = SecretKeyUtil.aliRSASign(requestPMap, privateCertFilePath, null);
        requestPMap.put("sign", sign);
        testGet(url, requestPMap);
    }

    @Test
    public void testWebSyncWechat() {
        String url = "/notify/websync/wechat";
        String agencyCode = "WECHAT";
        String reqId = "ZF20160303164930053805116001";
        ResultMap result = getSecretKey(agencyCode, reqId);
        if (!Result.isSuccess(result)) {
            log.error("[testWebSyncWechat] 获取密码失败, reqId=" + reqId + ", agencyCode=" + agencyCode);
            return;
        }
        String md5securityKey = (String) result.getItem("md5securityKey");
        PMap requestPMap = new PMap();
        requestPMap.put("out_trade_no", reqId);
        requestPMap.put("result_code", "SUCCESS");
        String sign = SecretKeyUtil.tenMD5Sign(requestPMap, md5securityKey, null);
        requestPMap.put("sign", sign);
        testGet(url, requestPMap);
    }

    @Test
    public void testWebAsyncWechat() {
        String url = "/notify/webasync/wechat";
        String agencyCode = "WECHAT";
        String reqId = "ZF20160303164930053805116001";
        String transaction_id = "123456789";
        String time_end = DateUtil.format(new Date(), DateUtil.DATE_FORMAT_SECOND_SHORT);
        ResultMap result = getSecretKey(agencyCode, reqId);
        if (!Result.isSuccess(result)) {
            log.error("[testWebAsyncWechat] 获取密码失败, reqId=" + reqId + ", agencyCode=" + agencyCode);
            return;
        }
        String md5securityKey = (String) result.getItem("md5securityKey");
        PMap requestPMap = new PMap();
        requestPMap.put("out_trade_no", reqId);
        requestPMap.put("transaction_id", transaction_id);
        requestPMap.put("return_code", "SUCCESS");
        requestPMap.put("result_code", "SUCCESS");
        requestPMap.put("time_end", time_end);
        requestPMap.put("total_fee", "1");
        String sign = SecretKeyUtil.tenMD5Sign(requestPMap, md5securityKey, null);
        requestPMap.put("sign", sign);
        testPost(url, requestPMap, "application/xml");
    }


    @Test
    public void testWebSyncTenpay() {
        String url = "/notify/websync/tenpay";
        String agencyCode = "TENPAY";
        String reqId = "ZF20160302142242140003";
        ResultMap result = getSecretKey(agencyCode, reqId);
        if (!Result.isSuccess(result)) {
            log.error("[testWebSyncTenpay] 获取密码失败, reqId=" + reqId + ", agencyCode=" + agencyCode);
            return;
        }
        String md5securityKey = (String) result.getItem("md5securityKey");
        PMap requestPMap = new PMap();
        requestPMap.put("out_trade_no", reqId);
        requestPMap.put("trade_state", "0");
        String sign = SecretKeyUtil.tenMD5Sign(requestPMap, md5securityKey, null);
        requestPMap.put("sign", sign);
        testGet(url, requestPMap);
    }

    @Test
    public void testWebAsyncTenpay() {
        String url = "/notify/webasync/tenpay";
        String agencyCode = "TENPAY";
        String reqId = "ZF20160302142242140003";
        String transaction_id = "123456789";
        String time_end = DateUtil.format(new Date(), DateUtil.DATE_FORMAT_SECOND_SHORT);
        ResultMap result = getSecretKey(agencyCode, reqId);
        if (!Result.isSuccess(result)) {
            log.error("[testWebAsyncTenpay] 获取密码失败, reqId=" + reqId + ", agencyCode=" + agencyCode);
            return;
        }
        String md5securityKey = (String) result.getItem("md5securityKey");
        PMap requestPMap = new PMap();
        requestPMap.put("out_trade_no", reqId);
        requestPMap.put("transaction_id", transaction_id);
        requestPMap.put("trade_state", "0");
        requestPMap.put("time_end", time_end);
        requestPMap.put("total_fee", "1");
        String sign = SecretKeyUtil.tenMD5Sign(requestPMap, md5securityKey, null);
        requestPMap.put("sign", sign);
        testGet(url, requestPMap);
    }


}
