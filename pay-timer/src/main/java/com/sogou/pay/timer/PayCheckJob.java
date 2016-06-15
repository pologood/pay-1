package com.sogou.pay.timer;

import com.sogou.pay.PayPlatformBizServiceLocator;
import com.sogou.pay.common.annotation.Load;
import com.sogou.pay.manager.payment.PayCheckManager;
import com.sogou.pay.service.enums.AgencyCode;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.service.payment.PayCheckResultService;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by qibaichao on 2015/3/11.
 */
public class PayCheckJob {

    private static final Logger logger = LoggerFactory.getLogger(PayCheckJob.class);

    private static String DATE_FORMAT = "yyyyMMdd";

    private List<String> agencyCodeList = new ArrayList<String>();

    {
        agencyCodeList.add(AgencyCode.ALIPAY.name());
        agencyCodeList.add(AgencyCode.TENPAY.name());
        agencyCodeList.add(AgencyCode.WECHAT.name());
    }

    @Load(locator = PayPlatformBizServiceLocator.class)
    private PayCheckManager payCheckManager;

    @Load(locator = PayPlatformBizServiceLocator.class)
    private PayAgencyMerchantService payAgencyMerchantService;

    @Load(locator = PayPlatformBizServiceLocator.class)
    private PayCheckResultService payCheckResultService;

    /**
     * 支付宝对账任务
     * 每日凌晨2点15执行一次
     */
    public void doAlipayJob() {
        // 取前一天日期
        Date checkDate = getYesterday();
        logger.info("alipay check job start for checkDate:" + checkDate);
        String agencyCode = AgencyCode.ALIPAY.name();
        payCheckManager.downloadOrderData(checkDate, agencyCode);
        payCheckManager.checkOrderData(checkDate, agencyCode);
        logger.info("alipay check job end for checkDate:" + checkDate);
    }


    /**
     * 财付通对账任务
     * 7点以后进行
     * 每日7点30执行一次
     */
    public void doTenpayJob() {
        // 取前一天日期
        Date checkDate = getYesterday();
        logger.info("tenpay check job start for checkDate:" + checkDate);
        String agencyCode = AgencyCode.TENPAY.name();
        payCheckManager.downloadOrderData(checkDate, agencyCode);
        payCheckManager.checkOrderData(checkDate, agencyCode);
        logger.info("tenpay check job end for checkDate:" + checkDate);
    }

    /**
     * 微信对账任务
     * 微信在次日9点启动生成前一天的对账单，10点后再获取
     * 每日10点30执行一次
     */
    public void doWechatJob() {
        // 取前一天日期
        Date checkDate = getYesterday();
        logger.info("wechat check job start for checkDate:" + checkDate);
        String agencyCode = AgencyCode.WECHAT.name();
        payCheckManager.downloadOrderData(checkDate, agencyCode);
        payCheckManager.checkOrderData(checkDate, agencyCode);
        logger.info("wechat check job end for checkDate:" + checkDate);
    }

    /**
     * 取昨天开始，往前三天进行检查
     */
    public void doRecheckData() throws Exception {
        logger.info("recheckData job start...");
        Calendar ca = Calendar.getInstance();
        for (int i = 0; i < 3; i++) {
            ca.add(Calendar.DAY_OF_MONTH, -1);
            Date checkDate = ca.getTime();
            String checkDateStr = DateUtils.formatDate(checkDate, DATE_FORMAT);
            for (String agencyCode : agencyCodeList) {
                int count = payCheckResultService.queryCountByDateAndAgency(checkDateStr, agencyCode);
                //对账结果数据为0，重新比对
                if (count == 0) {
                    payCheckManager.downloadOrderData(checkDate, agencyCode);
                    payCheckManager.checkOrderData(checkDate, agencyCode);
                }
            }
        }
        logger.info("recheckData job end...");
    }

    private Date getYesterday() {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, -1);
        return ca.getTime();
    }

}
