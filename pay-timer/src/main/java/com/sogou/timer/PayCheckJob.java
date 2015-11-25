package com.sogou.timer;

import com.sogou.pay.PayPlatformBizServiceLocator;
import com.sogou.pay.common.annotation.Load;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.manager.payment.PayCheckManager;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.enums.AgencyType;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.service.payment.PayCheckResultService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by qibaichao on 2015/3/11.
 */
public class PayCheckJob {

    private static final Logger logger = LoggerFactory.getLogger(PayCheckJob.class);

    private static String DATE_FORMAT = "yyyyMMdd";

    private List<String> agencyCodeList = new ArrayList<String>();

    {
        agencyCodeList.add(AgencyType.ALIPAY.name());
        agencyCodeList.add(AgencyType.TENPAY.name());
        agencyCodeList.add(AgencyType.WECHAT.name());
        agencyCodeList.add(AgencyType.BILL99.name());
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
        String checkDate = getYesterday();
        logger.info("alipay check job start for checkDate:" + checkDate);
        String agencyCode = AgencyType.ALIPAY.name();
        payCheckManager.downloadCheckData(checkDate, agencyCode);
        payCheckManager.checkData(checkDate, agencyCode);
        logger.info("alipay check job end for checkDate:" + checkDate);
    }

    /**
     * 快钱对账任务
     * 每日凌晨3点15执行一次
     */
    public void doBill99Job() {
        // 取前一天日期
        String checkDate = getYesterday();
        logger.info("bill99 check job start for checkDate:" + checkDate);
        String agencyCode = AgencyType.BILL99.name();
        payCheckManager.downloadCheckData(checkDate, agencyCode);
        payCheckManager.checkData(checkDate, agencyCode);
        logger.info("bill99 check job end for checkDate:" + checkDate);
    }

    /**
     * 财付通对账任务
     * 7点以后进行
     * 每日7点30执行一次
     */
    public void doTenpayJob() {
        // 取前一天日期
        String checkDate = getYesterday();
        logger.info("tenpay check job start for checkDate:" + checkDate);
        String agencyCode = AgencyType.TENPAY.name();
        payCheckManager.downloadCheckData(checkDate, agencyCode);
        payCheckManager.checkData(checkDate, agencyCode);
        logger.info("tenpay check job end for checkDate:" + checkDate);
    }

    /**
     * 微信对账任务
     * 微信在次日9点启动生成前一天的对账单，10点后再获取
     * 每日10点30执行一次
     */
    public void doWechatJob() {
        // 取前一天日期
        String checkDate = getYesterday();
        logger.info("wechat check job start for checkDate:" + checkDate);
        String agencyCode = AgencyType.WECHAT.name();
        payCheckManager.downloadCheckData(checkDate, agencyCode);
        payCheckManager.checkData(checkDate, agencyCode);
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
            String checkDate = DateUtils.formatDate(ca.getTime(), DATE_FORMAT);
            for (String agencyCode : agencyCodeList) {
                int count = payCheckResultService.queryCountByDateAndAgency(checkDate, agencyCode);
                //对账结果数据为0，重新比对
                if (count == 0) {
                    payCheckManager.downloadCheckData(checkDate, agencyCode);
                    payCheckManager.checkData(checkDate, agencyCode);
                }
            }
        }
        logger.info("recheckData job end...");
    }

    private String getYesterday() {

        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, -1);
        String yesterday = DateUtil.format(ca.getTime(), DATE_FORMAT);
        return yesterday;
    }

}
