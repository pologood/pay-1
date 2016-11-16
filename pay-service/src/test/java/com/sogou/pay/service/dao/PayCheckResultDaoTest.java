package com.sogou.pay.service.dao;

import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.PayCheckResult;
import com.sogou.pay.service.enums.AgencyCode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author qibaichao
 * @Date 2015/3/3
 * @Time 15:01
 * @Description:
 */
public class PayCheckResultDaoTest extends BaseTest {

    @Autowired
    private PayCheckResultDao payCheckResultDao;

    @Test
    public void insert() {
        try {
            String checkDate = "20150303";
            String agencyCode = AgencyCode.ALIPAY.name();
            int number = payCheckResultDao.insert(checkDate,agencyCode);
            System.out.println(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delete() {

        try {
            String checkDate = "20150303";
            String agencyCode = AgencyCode.ALIPAY.name();
            int number = payCheckResultDao.delete(checkDate,agencyCode);
            System.out.println(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateAmtAndNumById() {

        try {
            long id = 1l;
            int status = 1;
            int number = payCheckResultDao.updateStatus(id, status);
            System.out.println(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queryByCheckDate() {
        try {
            String checkDate = "20150303";
            String agencyCode = AgencyCode.ALIPAY.name();
            List<PayCheckResult> list = payCheckResultDao.queryByDateAndAgency(checkDate,agencyCode);
            System.out.println(list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
