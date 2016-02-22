package com.sogou.pay.thirdpay;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;
import com.sogou.pay.thirdpay.service.Alipay.AlipayService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.StringWriter;

/**
 * Created by qibaichao on 2015/3/5.
 */
public class CheckApiTest  extends BaseTest{

    @Autowired
    private AlipayService alipayService;


    @Test
    public  void testCheckData(){

        try{
            CheckType checkType =CheckType.PAID;
            String merchantNo="123";
            String startTime="2015-03-05 00:00:00";
            String endTime="2015-03-05 23:59:59";
            String pageNo="1";
            String pageSize="500";
            String key="321";
            //ResultMap resultMap= alipayService.doQuery( merchantNo, checkType,   startTime,  endTime,  pageNo,  pageSize,  key);
            //System.out.println(JSON.toJSON(resultMap) );
        }catch (Exception e){


        }

    }

    @Test
    public  void testVerySign(){
        try{
            String xmlString ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<alipay> \n" +
                    "  <is_success>T</is_success>  \n" +
                    "  <response> \n" +
                    "    <account_page_query_result> \n" +
                    "      <account_log_list> \n" +
                    "        <AccountQueryAccountLogVO> \n" +
                    "          <balance>21640857.99</balance>  \n" +
                    "          <bank_account_name>中国工商银行</bank_account_name>  \n" +
                    "          <bank_account_no>6228098765432109877</bank_account_no>  \n" +
                    "          <bank_name>张三</bank_name>  \n" +
                    "          <buyer_account>20881010118937730156</buyer_account>  \n" +
                    "          <currency>156</currency>  \n" +
                    "          <deposit_bank_no>1407872714662639</deposit_bank_no>  \n" +
                    "          <goods_title>商品１</goods_title>  \n" +
                    "          <income>0.00</income>  \n" +
                    "          <iw_account_log_id>340005462311</iw_account_log_id>  \n" +
                    "          <memo>交易付款</memo>  \n" +
                    "          <merchant_out_order_no>8914444063140954</merchant_out_order_no>  \n" +
                    "          <other_account_email>other@alipay.com</other_account_email>  \n" +
                    "          <other_account_fullname>xx公司</other_account_fullname>  \n" +
                    "          <other_user_id>2088001368431897</other_user_id>  \n" +
                    "          <outcome>500.00</outcome>  \n" +
                    "          <partner_id>2088101118137074</partner_id>  \n" +
                    "          <seller_account>20881010118937520156</seller_account>  \n" +
                    "          <seller_fullname>xx商户</seller_fullname>  \n" +
                    "          <service_fee>1.00</service_fee>  \n" +
                    "          <service_fee_ratio>0.02</service_fee_ratio>  \n" +
                    "          <total_fee>30.00</total_fee>  \n" +
                    "          <trade_no>2012050726014177</trade_no>  \n" +
                    "          <trade_refund_amount>1.00</trade_refund_amount>  \n" +
                    "          <trans_account>20881010118937220156</trans_account>  \n" +
                    "          <trans_code_msg>转账</trans_code_msg>  \n" +
                    "          <trans_date>2010-09-25 17:27:30</trans_date>  \n" +
                    "          <trans_out_order_no>2012050726014177</trans_out_order_no>  \n" +
                    "          <sub_trans_code_msg>冻结余额</sub_trans_code_msg>  \n" +
                    "          <sign_product_name>高级即时到账</sign_product_name>  \n" +
                    "          <rate>0.003</rate> \n" +
                    "        </AccountQueryAccountLogVO> \n" +
                    "      </account_log_list>  \n" +
                    "      <has_next_page>F</has_next_page>  \n" +
                    "      <page_no>1</page_no>  \n" +
                    "      <page_size>5000</page_size> \n" +
                    "    </account_page_query_result> \n" +
                    "  </response>  \n" +
                    "  <sign>326e59040fb8d21149daf43e88e4c12a</sign>  \n" +
                    "  <sign_type>MD5</sign_type> \n" +
                    "</alipay>\n" +
                    "\n";
            //第一种方式 从xml 字符串中获取
//            String  xml = IOUtils.toString(new FileInputStream("E:\\alipay\\test.xml"));
//            System.out.println(xml);
            Document documentaa = DocumentHelper.parseText(xmlString);
            //第二种方式从xml文件中获取
            SAXReader reader = new SAXReader();
            Document document = reader.read(new FileInputStream("E:\\alipay\\test.xml"),"utf-8");
            Element wrapper = (Element)document.getRootElement().selectSingleNode("/alipay/response/account_page_query_result");
            String hasNextPage = wrapper.elementText("has_next_page");
            String pageNo = wrapper.elementText("page_no");
            String pageSize = wrapper.elementText("page_size");
            Element accountLogList = wrapper.element("account_log_list");

            OutputFormat format = new OutputFormat();
            // expand <xxx /> to <xxx></xxx>
            format.setExpandEmptyElements(true); //一定要设置
            StringWriter accountLogListContent = new StringWriter();
            XMLWriter writer = new XMLWriter(accountLogListContent, format);
            writer.write(accountLogList);

            System.out.println(accountLogListContent);

//            String payload = String.format(
//                    "account_log_list=%s&has_next_page=%s&page_no=%s&page_size=%s",
//                    accountLogListContent, hasNextPage, pageNo, pageSize);

            PMap requestPMap = new PMap();
//            requestPMap.put("hasNextPage",hasNextPage);
//            requestPMap.put("pageNo",hasNextPage);
//            requestPMap.put("pageSize",hasNextPage);
            requestPMap.put("accountLogList",accountLogListContent);

            String key = "xxxxx";
            System.out.println( SecretKeyUtil.aliMd5sign(requestPMap, key, "utf-8"));
        }catch(Exception e){
            e.printStackTrace();
        }

    }


}