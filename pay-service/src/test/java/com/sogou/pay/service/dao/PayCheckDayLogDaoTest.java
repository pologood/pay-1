package com.sogou.pay.service.dao;

import java.util.Date;

import com.sogou.pay.service.enums.AgencyType;
import com.sogou.pay.service.enums.OperationLogStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.service.BaseTest;
import com.sogou.pay.service.entity.PayCheckDayLog;

/**
 * @Author qibaichao
 * @ClassName PayCheckDayLogDaoTest
 * @Date 2015年2月28日
 * @Description:
 */
public class PayCheckDayLogDaoTest extends BaseTest {

    @Autowired
    private PayCheckDayLogDao payCheckDayLogDao;

    @Test
    public void insert() {

        try {
            String agencyCode = AgencyType.ALIPAY.name();
            String checkDate = "20150303";
            String merchantNo = "sogoucaipiao";
            PayCheckDayLog payCheckDayLog = new PayCheckDayLog();
            payCheckDayLog.setCreateTime(new Date());
            payCheckDayLog.setModifyTime(new Date());
            payCheckDayLog.setCheckDate(checkDate);
            payCheckDayLog.setAgencyCode(agencyCode);
            payCheckDayLogDao.insert(payCheckDayLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getByCheckDateAndAgency() {

        try {
            String agencyCode = AgencyType.ALIPAY.name();
            String checkDate = "20150303";
            PayCheckDayLog payCheckDayLog = payCheckDayLogDao
                    .getByCheckDateAndAgency(checkDate, agencyCode);
            System.out.print("success" + payCheckDayLog);
            System.out.print("success" + payCheckDayLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateStatus() {

        try {
            long id = 9;
            int status = OperationLogStatus.SUCCESS.value();
            int version = 0;
            String remark = "aaa";
            int num = payCheckDayLogDao.updateStatus(id, status, version,
                    remark);
            System.out.print("修改成功" + num);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
