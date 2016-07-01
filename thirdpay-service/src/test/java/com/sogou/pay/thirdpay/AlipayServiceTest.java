package com.sogou.pay.thirdpay;

import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.thirdpay.biz.model.TransferRecord;
import com.sogou.pay.thirdpay.service.Alipay.AlipayService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by xiepeidong on 2016/2/25.
 */
public class AlipayServiceTest extends BaseTest {

    @Autowired
    AlipayService alipayService;

    @Test
    public void testPrepareTransferInfo(){
        PMap pMap = new PMap();
        pMap.put("merchantNo", "2088811923135335");
        pMap.put("serverNotifyUrl", "http://center.pay.sogou.com/notify/ali/pay/webAsync");
        pMap.put("serialNumber", "000000000000");
        pMap.put("orderAmount", "0.01");
        pMap.put("accountName", "北京搜狗网络技术有限公司");
        pMap.put("sellerEmail", "sogouwangluo@sogou-inc.com");
        pMap.put("md5securityKey", "9s12ckbjobqzv173ru1c3lwbudd900m0");
        pMap.put("payUrl", "https://mapi.alipay.com/gateway.do");
        TransferRecord record = new TransferRecord();
        record.setSerialNumber("000000");
        record.setAccountNo("15201294712");
        record.setAccountName("谢沛东");
        record.setTransferAmount("0.01");
        record.setMemo("测试");
        List<TransferRecord> records = new ArrayList<TransferRecord>();
        records.add(record);
        pMap.put("records", records);
        try {
            ResultMap result = alipayService.prepareTransferInfo(pMap);
            System.out.println(result.getItem("returnUrl"));
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Test
    public void testGetCertId(){
        try{
            FileInputStream fis = new FileInputStream("e:\\pay_key\\700000000000001_acp.pfx");
            KeyStore trustKeyStore = KeyStore.getInstance("PKCS12");
            trustKeyStore.load(fis, "000000".toCharArray());
            Enumeration<String> aliasenum = trustKeyStore.aliases();
            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                keyAlias = aliasenum.nextElement();
            }
            X509Certificate cert = (X509Certificate) trustKeyStore
                    .getCertificate(keyAlias);
            String certId= cert.getSerialNumber().toString();

            System.out.println(certId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
