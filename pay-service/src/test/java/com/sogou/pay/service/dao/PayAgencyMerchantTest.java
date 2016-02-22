package com.sogou.pay.service.dao;

import com.sogou.pay.service.BaseTest;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.thirdpay.biz.enums.AgencyType;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
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
        payAgencyMerchant.setCompanyCode(1);
        payAgencyMerchant.setAppId(1999);
        PayAgencyMerchant result = payAgencyMerchantService.selectPayAgencyMerchant(payAgencyMerchant);
        assertEquals("1234274801",result.getMerchantNo());
    }

    @Test
    public void  selectPayAgencyMerchants(){

        try {
            String agencyCode = AgencyType.ALIPAY.name();
            List<PayAgencyMerchant>  list =  payAgencyMerchantService.selectPayAgencyMerchants(agencyCode);
            System.out.println( list);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
