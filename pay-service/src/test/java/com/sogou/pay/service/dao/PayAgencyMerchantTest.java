package com.sogou.pay.service.dao;

import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.enums.AgencyCode;
import com.sogou.pay.service.service.PayAgencyMerchantService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author huangguoqing
 * @Date 2015/3/3 18:53
 * @Description:
 */
public class PayAgencyMerchantTest extends BaseTest {
    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;

    @Test
    public void selectPayAgencyMerchant(){
        PayAgencyMerchant payAgencyMerchant = new PayAgencyMerchant();
        payAgencyMerchant.setAgencyCode("TENPAY");
        payAgencyMerchant.setCompanyId(1);
        payAgencyMerchant.setAppId(1999);
        PayAgencyMerchant result = payAgencyMerchantService.getMerchant(payAgencyMerchant);
        assertEquals("1234274801",result.getMerchantNo());
    }

    @Test
    public void  selectPayAgencyMerchants(){

        try {
            String agencyCode = AgencyCode.ALIPAY.name();
            List<PayAgencyMerchant>  list =  payAgencyMerchantService.getMerchantsByAgencyCode(agencyCode);
            System.out.println( list);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
