package com.sogou.pay.service.dao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.sogou.pay.enums.AccessPlatform;
import com.sogou.pay.manager.model.PayCheckUpdateModel;
import com.sogou.pay.service.enums.AgencyCode;
import com.sogou.pay.service.enums.OrderType;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.PayCheckWaiting;

/**
 * @Author qibaichao
 * @ClassName PayCheckWaitingDaoTest
 * @Date 2015年2月28日
 * @Description:
 */
public class PayCheckWaitingDaoTest extends BaseTest {


    @Autowired
    private PayCheckWaitingDao payCheckWaitingDao;


    @Autowired
    private SequenceFactory sequencerGenerator;

    @Test
    public void insert() {
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        Random rand = new Random();
        try {

            String merchantNo="sogoucaipiao";
            String checkDate ="20150303";
            String agencycode= AgencyCode.ALIPAY.name();

            PayCheckWaiting payCheckWaiting = new PayCheckWaiting();
            String instructId = sequencerGenerator.getPayDetailId();
            payCheckWaiting.setInstructId(instructId);
            payCheckWaiting.setOutOrderId(instructId);
            payCheckWaiting.setCheckType(OrderType.PAY.getValue());
            payCheckWaiting.setOutTransTime(new Date());
            float f = rand.nextFloat() * 1000;
            payCheckWaiting.setBizAmt(new BigDecimal(dcmFmt.format(f)));
            payCheckWaiting.setFeeRate(BigDecimal.valueOf(0.001));
            payCheckWaiting.setCommissionFeeAmt(BigDecimal.valueOf(1));
            payCheckWaiting.setAccessPlatform(AccessPlatform.ACCESSPLATFORM_PC);
            payCheckWaiting.setAppId(1);
            payCheckWaiting.setPayType(1);
            payCheckWaiting.setBankCode("abc");
            payCheckWaiting.setCheckDate(checkDate);
            payCheckWaiting.setAgencyCode(agencycode);
            payCheckWaiting.setMerchantNo(merchantNo);
            payCheckWaitingDao.insert(payCheckWaiting);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void batchInsert() {
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        Random rand = new Random();
        try {

            String merchantNo="sogoucaipiao";

            List<PayCheckWaiting> list = new ArrayList<PayCheckWaiting>();
            PayCheckWaiting payCheckWaiting = null;
            for (int i = 0; i < 100; i++) {
                payCheckWaiting = new PayCheckWaiting();
                String instructId = sequencerGenerator.getPayDetailId();
                payCheckWaiting.setInstructId(instructId);
                payCheckWaiting.setOutOrderId(instructId);
                payCheckWaiting.setCheckType(OrderType.PAY.getValue());
                payCheckWaiting.setOutTransTime(new Date());
                float f = rand.nextFloat() * 1000;
                payCheckWaiting.setBizAmt(new BigDecimal(dcmFmt.format(f)));
                payCheckWaiting.setCommissionFeeAmt(BigDecimal.valueOf(1));
                payCheckWaiting.setCheckDate("20150303");
                payCheckWaiting.setAgencyCode(AgencyCode.ALIPAY.name());
                payCheckWaiting.setAccessPlatform(AccessPlatform.ACCESSPLATFORM_PC);
                payCheckWaiting.setAppId(1);
                payCheckWaiting.setMerchantNo(merchantNo);
                list.add(payCheckWaiting);
                if (i % 1000 == 0) {
                    payCheckWaitingDao.batchInsert(list);
                    list.clear();
                }
            }
            if (list.size() != 0) {
                payCheckWaitingDao.batchInsert(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getByPayDetailId() {

        try {
            String instructId = "ZF20150309143051866001";
            PayCheckWaiting payCheckWaiting = payCheckWaitingDao.getByInstructId(instructId);
            System.out.println(payCheckWaiting);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void batchUpdateStatus() {

        try {
            List<PayCheckUpdateModel> list = new ArrayList<PayCheckUpdateModel>();
            PayCheckUpdateModel payCheckUpdateVo = new PayCheckUpdateModel();
            payCheckUpdateVo.setInstructId("111");
            payCheckUpdateVo.setPayCheckWaitingStatus(3);
            list.add(payCheckUpdateVo);
            PayCheckUpdateModel payCheckUpdateVo2 = new PayCheckUpdateModel();
            payCheckUpdateVo2.setInstructId("222");
            payCheckUpdateVo2.setPayCheckWaitingStatus(1);
            list.add(payCheckUpdateVo2);
            payCheckWaitingDao.batchUpdateStatus(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
