package com.sogou.pay.service.payment.manager;

import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.manager.payment.PayCheckManager;
import com.sogou.pay.BaseTest;
import com.sogou.pay.service.dao.PayCheckDao;
import com.sogou.pay.service.dao.PayCheckDayLogDao;
import com.sogou.pay.service.dao.PayCheckWaitingDao;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.entity.PayCheck;
import com.sogou.pay.service.entity.PayCheckDayLog;
import com.sogou.pay.service.entity.PayCheckWaiting;
import com.sogou.pay.thirdpay.biz.enums.AgencyType;
import com.sogou.pay.service.enums.OperationLogStatus;
import com.sogou.pay.service.enums.OrderType;
import com.sogou.pay.service.enums.TerminalType;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by qibaichao on 2015/3/10.
 */
public class PayCheckManagerTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(PayCheckManagerTest.class);

    @Autowired
    private PayCheckManager payCheckManager;

    @Autowired
    private PayCheckDao payCheckDao;

    @Autowired
    private PayCheckWaitingDao payCheckWaitingDao;

    @Autowired
    private PayCheckDayLogDao payCheckDayLogDao;

    @Autowired
    private SequenceFactory sequencerGenerator;

    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;


    /**
     * 模拟数据
     */
    @Test
    public void mockDownloadData() {
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        Random rand = new Random();
        String checkDate = "20150303";
        String merchantNo = "sogoucaipiao";
        String agencyCode = AgencyType.ALIPAY.name();
        PayCheckDayLog payCheckDayLog = null;
        try {
            //插入一条log
            payCheckDayLog = payCheckDayLogDao.getByCheckDateAndAgency(checkDate, agencyCode);

            if (payCheckDayLog == null) {
                payCheckDayLog = new PayCheckDayLog();
                payCheckDayLog.setCreateTime(new Date());
                payCheckDayLog.setModifyTime(new Date());
                payCheckDayLog.setCheckDate(checkDate);
                payCheckDayLog.setAgencyCode(agencyCode);
                payCheckDayLogDao.insert(payCheckDayLog);
                payCheckDayLog = payCheckDayLogDao.getByCheckDateAndAgency(checkDate, agencyCode);
            }

            //批量插入数据
            List<PayCheckWaiting> list = new ArrayList<PayCheckWaiting>();
            List<PayCheck> payCheckList = new ArrayList<PayCheck>();
            PayCheck payCheck = null;
            PayCheckWaiting payCheckWaiting = null;
            for (int i = 0; i < 10000; i++) {

                payCheck = new PayCheck();
                String instructId = sequencerGenerator.getPayDetailId();
                payCheck.setInstructId(instructId);
                payCheck.setOutOrderId(instructId);
                payCheck.setBizCode(OrderType.PAYCASH.getValue());
                payCheck.setOutTransTime(new Date());
                float f = rand.nextFloat() * 1000;
                payCheck.setBizAmt(new BigDecimal(dcmFmt.format(f)));
                payCheck.setCommissionFeeAmt(BigDecimal.valueOf(0));
                payCheck.setCheckDate(checkDate);
                payCheck.setAgencyCode(agencyCode);
                payCheck.setMerchantNo(merchantNo);
                payCheckList.add(payCheck);

                //插入waiting 表
                payCheckWaiting = new PayCheckWaiting();
                payCheckWaiting.setInstructId(instructId);
                payCheckWaiting.setOutOrderId(instructId);
                payCheckWaiting.setBizCode(OrderType.PAYCASH.getValue());
                payCheckWaiting.setOutTransTime(new Date());
                payCheckWaiting.setBizAmt(new BigDecimal(dcmFmt.format(f)));
                payCheckWaiting.setCommissionFeeAmt(BigDecimal.valueOf(0));
                payCheckWaiting.setCheckDate(checkDate);
                payCheckWaiting.setAgencyCode(AgencyType.ALIPAY.name());
                payCheckWaiting.setAccessPlatform(TerminalType.WEB.getValue());
                payCheckWaiting.setAppId(1);
                payCheckWaiting.setMerchantNo(merchantNo);
                list.add(payCheckWaiting);
                if (i % 1000 == 0) {
                    payCheckDao.batchInsert(payCheckList);
                    payCheckWaitingDao.batchInsert(list);
                    payCheckList.clear();
                    list.clear();
                }
            }
            if (list.size() != 0) {
                payCheckDao.batchInsert(payCheckList);
                payCheckWaitingDao.batchInsert(list);
            }
            payCheckDayLog = payCheckDayLogDao.getByCheckDateAndAgency(checkDate, agencyCode);
            //修改为下载成功
            payCheckDayLogDao.updateStatus(payCheckDayLog.getId(), OperationLogStatus.DOWNLOADSUCCESS.value(),
                    payCheckDayLog.getVersion(), OperationLogStatus.DOWNLOADSUCCESS.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟数据
     */
    @Test
    public void downloadData() {
//        Date checkDate = DateUtil.parse("20151130");
//        String agencyCode = AgencyType.ALIPAY.name();
//        Date checkDate = DateUtil.parse("20160126");
//        String agencyCode = AgencyType.TENPAY.name();
        Date checkDate = DateUtil.parse("20160216");
        String agencyCode = AgencyType.WECHAT.name();
        try {
            payCheckManager.downloadOrderData(checkDate, agencyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void selectByAgencyAndMerchant() {
        try {
            String merchantNo = "1900000109";
            String agencyCode = AgencyType.TENPAY.name();
            PayAgencyMerchant payAgencyMerchant = payAgencyMerchantService.selectByAgencyAndMerchant(agencyCode, merchantNo);
            System.out.println(payAgencyMerchant);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkData() {
        //财付通对账
        Date checkDate = DateUtil.parse("20151101");
        String agencyCode = AgencyType.TENPAY.name();
        try {
            payCheckManager.downloadOrderData(checkDate, agencyCode);
            payCheckManager.checkOrderData(checkDate, agencyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //微信对账
        agencyCode = AgencyType.WECHAT.name();
        try {
            payCheckManager.downloadOrderData(checkDate, agencyCode);
            payCheckManager.checkOrderData(checkDate, agencyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        agencyCode = AgencyType.ALIPAY.name();
        try {
            payCheckManager.downloadOrderData(checkDate, agencyCode);
            payCheckManager.checkOrderData(checkDate, agencyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void checkAlipay() {
        Date checkDate = DateUtil.parse("20150413");
        String agencyCode = AgencyType.ALIPAY.name();
        try {
            payCheckManager.downloadOrderData(checkDate, agencyCode);
            payCheckManager.checkOrderData(checkDate, agencyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkTenpay() {
        Date checkDate = DateUtil.parse("20150421");
        String agencyCode = AgencyType.TENPAY.name();
        try {
            payCheckManager.downloadOrderData(checkDate, agencyCode);
            payCheckManager.checkOrderData(checkDate, agencyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkWechat() {
        Date checkDate = DateUtil.parse("20151118");
        String agencyCode = AgencyType.WECHAT.name();
        try {
            payCheckManager.downloadOrderData(checkDate, agencyCode);
            payCheckManager.checkOrderData(checkDate, agencyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkBill99() {
        Date checkDate = DateUtil.parse("20150629");
        String agencyCode = AgencyType.BILL99.name();
        String merchantNo = "1234469202";
        try {
            payCheckManager.downloadOrderData(checkDate, agencyCode);
            payCheckManager.checkOrderData(checkDate, agencyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updatePayCheckDiff() {
        try {
            payCheckManager.updatePayCheckDiff();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updatePayCheckResult() {

        Date checkDate = DateUtil.parse("20150303");
        String agencyCode = AgencyType.ALIPAY.name();
        try {
            payCheckManager.updatePayCheckResult(checkDate, agencyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String args[]) {
//        if (!"TRADE_FINISHED".equals("TRADE_FINISHED") ||! "TRADE_SUCCESS".equals("TRADE_FINISHED")) {
//            System.out.println("eee");
//        }
//    }

}
