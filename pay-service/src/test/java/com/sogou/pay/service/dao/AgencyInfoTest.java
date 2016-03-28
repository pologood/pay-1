package com.sogou.pay.service.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.BaseTest;
import com.sogou.pay.service.entity.AgencyInfo;
import com.sogou.pay.service.payment.AgencyInfoService;

/**
 * Created by wujingpan on 2015/3/2.
 */
public class AgencyInfoTest extends BaseTest {

    @Autowired
    AgencyInfoDao dao;
    @Autowired
    AgencyInfoService service;

    @Test
    public void testQueryAll() {
        List<AgencyInfo> list = null;
        System.out.print(dao.getAgencyInfoList());
    }

    @Test
    public void testSelectAgencyInfo(){

        String agencyCode = "ALIPAY";
        AgencyInfo info = dao.getAgencyInfoByCode(agencyCode, "99", "99");
        //assertEquals("支付宝",info.getAgencyName());
        System.out.println(info.toString());
    }

    @Test
    public void testGetOne(){

        try {
            System.out.println(service.getById(1));
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
