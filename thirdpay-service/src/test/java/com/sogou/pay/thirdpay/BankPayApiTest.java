package com.sogou.pay.thirdpay;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.thirdpay.api.BankPayApi;
import com.sogou.pay.thirdpay.biz.model.Record;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/28 15:41
 */
public class BankPayApiTest extends BaseTest {

    @Autowired
    private BankPayApi bankPayApi;

    @Test
    public void testBankPaySusbmit() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("bankPaySubUrl", "https://mch.tenpay.com/cgi-bin/mchbatchtransfer.cgi");
        pmap.put("merchantNo", "1234639901");
        pmap.put("refundUrl", "https://mch.tenpay.com/refundapi/gateway/refund.xml");
        pmap.put("refundNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("md5securityKey", "dcfe3c50f2fa354351333aa9622b9f95");
        pmap.put("publicCertFilePath", "/pay_key/tenpay_keji/cacert.pem");
        pmap.put("privateCertFilePath", "/pay_key/tenpay_keji/1234639901_20150319170653.pfx");
        pmap.put("op_code", "1013");
        pmap.put("op_name", "batch_draw");
        pmap.put("op_user", "1234639901");
        pmap.put("op_passwd", "3edcvfr4");
        pmap.put("op_time", "201506031527232");
        pmap.put("sp_id", "1234639901");
        pmap.put("package_id", "1111111111");
        pmap.put("total_num", "2");
        pmap.put("total_amt", "12");
        pmap.put("client_ip", "127.0.01");
        Record record = new Record();
        record.setSerial("1111");
        record.setRec_bankacc(" 234234234 ");
        record.setBank_type("1 ");
        record.setRec_name("SDF ");
        record.setPay_amt("1 ");
        record.setAcc_type(" 1 ");
        record.setArea("1 ");
        record.setCity(" 1 ");
        record.setSubbank_name(" 1 ");
        record.setDesc(" 1 ");
        record.setRecv_mobile("15010198272 ");
        Record record2 = new Record();
        record2.setSerial("2");
        record2.setRec_bankacc(" 234 ");
        record2.setBank_type("1 ");
        record2.setRec_name("123 ");
        record2.setPay_amt("1 ");
        record2.setAcc_type(" 1 ");
        record2.setArea("1 ");
        record2.setCity(" 1 ");
        record2.setSubbank_name(" 1 ");
        record2.setDesc(" 1 ");
        record2.setRecv_mobile("15010198272 ");
        List record_set = new ArrayList();
        record_set.add(record);
        record_set.add(record2);
        pmap.put("record_set", record_set);
        result = bankPayApi.paySubmit(pmap);


        System.out.print("result" + result);
    }
    @Test
    public void testBankPayQuery() {
        PMap pmap = new PMap();
        ResultMap result = ResultMap.build();
        pmap.put("bankPaySubUrl", "https://mch.tenpay.com/cgi-bin/mchbatchtransfer.cgi");
        pmap.put("merchantNo", "1234639901");
        pmap.put("refundUrl", "https://mch.tenpay.com/refundapi/gateway/refund.xml");
        pmap.put("refundNotifyUrl", "http://sogou.pay/refundnotify.jsp");
        pmap.put("md5securityKey", "dcfe3c50f2fa354351333aa9622b9f95");
        pmap.put("publicCertFilePath", "/pay_key/tenpay_keji/cacert.pem");
        pmap.put("privateCertFilePath", "/pay_key/tenpay_keji/1234639901_20150319170653.pfx");
        pmap.put("op_code", "1013");
        pmap.put("op_name", "batch_draw");
        pmap.put("op_user", "1234639901");
        pmap.put("op_passwd", "3edcvfr4");
        pmap.put("op_time", "201506031527232");
        pmap.put("sp_id", "1234639901");
        pmap.put("package_id", "1111111111");
        pmap.put("total_num", "2");
        pmap.put("total_amt", "12");
        pmap.put("client_ip", "127.0.01");
        result = bankPayApi.payQuery(pmap);


        System.out.print("result" + result);
    }


    public static String toXml(Record obj) {
        XStream xstream = new XStream(new DomDriver("utf8"));
        xstream.processAnnotations(obj.getClass()); // 识别obj类中的注解
        return xstream.toXML(obj);
    }
}
