package com.sogou.pay.web.api;

import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.service.payment.PayReqDetailService;
import com.sogou.pay.service.payment.RefundService;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.controller.notify.PayNotifyController;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/4/16 11:11
 */
public class RefundNotifyControllerTest extends BaseTest {
    private static Logger log = LoggerFactory.getLogger(PayNotifyController.class);

    @Autowired
    private RefundService refundService;
    @Autowired
    private PayReqDetailService payReqDetailService;
    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;

    private ResultMap getSecretKey(String merchantId) {
        //查询支付流水单
        try {
            PayAgencyMerchant payAgencyMerchant = payAgencyMerchantService.selectPayAgencyMerchantById(Integer.parseInt(merchantId));
            if (payAgencyMerchant == null) {
                log.error("[getSecretKey] 查询商户信息失败, merchantId=" + merchantId);
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
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }


    @Test
    public void testRefundAlipay() {
        String merchantId = "30";
        String url = "/notify/refund/alipay/" + merchantId;
        String reqId = "20160127164159104001";
        String refund_id = "123456789";
        String refund_money = "0.01";
        String refund_status = "SUCCESS";
        String result_details = refund_id + "^" + refund_money + "^" + refund_status;
        result_details = result_details + "$" + result_details;
        result_details = result_details + "#" + result_details;

        ResultMap result = getSecretKey(merchantId);
        if (!Result.isSuccess(result)) {
            log.error("[testRefundAlipay] 获取密码失败, merchantId=" + merchantId);
            return;
        }
        String md5securityKey = (String) result.getItem("md5securityKey");
        PMap requestPMap = new PMap();
        requestPMap.put("batch_no", reqId);
        requestPMap.put("result_details", result_details);
        String sign = SecretKeyUtil.aliMD5Sign(requestPMap, md5securityKey, null);
        requestPMap.put("sign", sign);
        testGet(url, requestPMap);
    }

    @Test
    public void testRefundTenpay() {
        String merchantId = "2";
        String url = "/notify/refund/tenpay/" + merchantId;
        String reqId = "20160219151702157001";
        String refund_id = "123456789";
        ResultMap result = getSecretKey(merchantId);
        if (!Result.isSuccess(result)) {
            log.error("[testRefundTenpay] 获取密码失败, merchantId=" + merchantId);
            return;
        }
        String md5securityKey = (String) result.getItem("md5securityKey");
        PMap requestPMap = new PMap();
        requestPMap.put("out_refund_no", reqId);
        requestPMap.put("refund_id", refund_id);
        requestPMap.put("refund_fee", "1");
        requestPMap.put("refund_status", "4");
        String sign = SecretKeyUtil.tenMD5Sign(requestPMap, md5securityKey, null);
        requestPMap.put("sign", sign);
        testGet(url, requestPMap);
    }
}
