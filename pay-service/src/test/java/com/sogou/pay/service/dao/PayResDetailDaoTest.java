package com.sogou.pay.service.dao;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.PayResDetail;
import com.sogou.pay.service.service.PayResDetailService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Liwei
 * Date: 15/3/6
 * Time: 下午7:00
 * Description:
 */
public class PayResDetailDaoTest extends BaseTest {
    @Autowired
    private PayResDetailService payResDetailService;

    @Test
    public void insertTest() throws ServiceException {
        PayResDetail payResDetail = new PayResDetail();

        payResDetail.setMerchantNo("aaaa");
        payResDetail.setBankCardType(1);
        payResDetail.setAgencyCode("aaaa");
        payResDetail.setBalance(BigDecimal.valueOf(10.00));
        payResDetail.setPayFeeType(1);
        payResDetail.setAccessPlatform(1);
        payResDetail.setAgencyPayTime(new Date());
        payResDetail.setBankCode("icbc");
        payResDetail.setAgencyOrderId("sddddd");
        payResDetail.setBankOrderId("hhhhhh");
        payResDetail.setAgencyPayTime(new Date());
        payResDetail.setPayDetailId("ppppp");
        payResDetail.setTrueMoney(BigDecimal.valueOf(10.00));
        payResDetail.setPayDetailId("dddd1");
        payResDetail.setPayFee(new BigDecimal("0.1"));
        payResDetail.setFeeRate(new BigDecimal("0.01"));
        
        int ret = payResDetailService.insertPayResDetail(payResDetail);
        assertTrue(ret == 1);

    }

    @Test
    public void selectTest () throws ServiceException {
        String payResId = "ZF20150309104706253001";
        PayResDetail payResDetail = payResDetailService.selectPayResById(payResId);
        System.out.println(payResDetail.getFeeRate());
    }

    @Test
    public void updatePayResPayfeeByIdTest() throws ServiceException {
        String payResId = "ZF20150309104706253001";
        BigDecimal fee = new BigDecimal("1.0001");
        BigDecimal feeRate = new BigDecimal("0.003");
        int ret = payResDetailService.updatePayResPayfeeById(fee,feeRate, payResId);
        assertTrue(ret == 1);
    }
}
