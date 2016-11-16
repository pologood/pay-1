package com.sogou.pay.common.utils;

import com.sogou.pay.common.BaseTest;

import com.sogou.pay.common.types.PMap;
import org.jdom.JDOMException;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by hujunfei Date: 14-12-30 Time: 下午3:19
 */
public class XMlUtilTest extends BaseTest {

    @Test
    public void testmapToXmlString() {
        PMap ss = new PMap();
        ss.put("sdf", "111");
        ss.put("s1df", "112341");
        ss.put("s1df1", "1112");
        ss.put("s1111df", "13451111");
        String xmlwss = XMLUtil.Map2XML("xml", ss);
        System.out.println(xmlwss);
    }

    @Test
    public void testXmpToMap() throws JDOMException, IOException {
        String str = "<?xml version=\"1.0\" encoding=\"gb2312\"?>\n" +
                "<root>\n" +
                "    <op_code>1014</op_code>\n" +
                "    <op_name>batch_draw_query</op_name>\n" +
                "    <op_user>提交人ID</op_user>\n" +
                "    <op_time>操作时间（yyyyMMddHHmmssSSS）</op_time>\n" +
                "    <package_id>包序列ID（YYYYMMDDXXX）</package_id>\n" +
                "    <retcode>返回码：0或00-查询成功</retcode>\n" +
                "    <retmsg>错误内容描述</retmsg>\n" +
                "    <result>" +
                "        <trade_state>批次状态（1初始状态，2待审核，3可付款，4付款失败，5处理中，6受理完成,7已取消）</trade_state>\n" +
                "        <total_count>总笔数</total_count>\n" +
                "        <total_fee>总金额</total_fee>\n" +
                "        <succ_count>成功笔数</succ_count>\n" +
                "        <succ_fee>成功金额</succ_fee>\n" +
                "        <fail_count>失败笔数</fail_count>\n" +
                "        <fail_fee>失败金额</fail_fee>\n" +
                "        <origin_set>\n" +
                "            <origin_total>初始状态条数</origin_total>\n" +
                "            <origin_rec>\n" +
                "                <serial>单笔序列号</serial>\n" +
                "                <rec_bankacc>收款方银行帐号</rec_bankacc>\n" +
                "                <bank_type>银行类型</bank_type>\n" +
                "                <rec_name>收款方真实姓名</rec_name>\n" +
                "                <pay_amt>付款金额(以分为单位)</pay_amt>\n" +
                "                <acc_type>账户类型&gt;</acc_type>\n" +
                "                <area>开户地区</area>\n" +
                "                <city>开户城市</city>\n" +
                "                <subbank_name>支行名称</subbank_name>\n" +
                "                <desc>付款说明</desc>\n" +
                "                <modify_time>最后修改时间，格式：yyyy-MM-dd HH:mm:ss</modify_time>\n" +
                "            </origin_rec>\n" +
                "        </origin_set>\n" +
                "        <success_set>\n" +
                "            <suc_total>成功条数</suc_total>\n" +
                "            <suc_rec>\n" +
                "                <serial>单笔序列号</serial>\n" +
                "                <rec_bankacc>收款方银行帐号</rec_bankacc>\n" +
                "                <bank_type>银行类型</bank_type>\n" +
                "                <rec_name>收款方真实姓名</rec_name>\n" +
                "                <pay_amt>付款金额(以分为单位)</pay_amt>\n" +
                "                <acc_type>账户类型&gt;</acc_type>\n" +
                "                <area>开户地区</area>\n" +
                "                <city>开户城市</city>\n" +
                "                <subbank_name>支行名称</subbank_name>\n" +
                "                <desc>付款说明</desc>\n" +
                "                <modify_time>最后修改时间，格式：yyyy-MM-dd HH:mm:ss</modify_time>\n" +
                "            </suc_rec>\n" +
                "        </success_set>\n" +
                "        <tobank_set>\n" +
                "            <tobank_total>已提交银行条数</tobank_total>\n" +
                "            <tobank_rec>\n" +
                "                <serial>单笔序列号</serial>\n" +
                "                <rec_bankacc>收款方银行帐号</rec_bankacc>\n" +
                "                <bank_type>银行类型</bank_type>\n" +
                "                <rec_name>收款方真实姓名</rec_name>\n" +
                "                <pay_amt>付款金额(以分为单位)</pay_amt>\n" +
                "                <acc_type>账户类型&gt;</acc_type>\n" +
                "                <area>开户地区</area>\n" +
                "                <city>开户城市</city>\n" +
                "                <subbank_name>支行名称</subbank_name>\n" +
                "                <desc>付款说明</desc>\n" +
                "                <modify_time>最后修改时间，格式：yyyy-MM-dd HH:mm:ss</modify_time>\n" +
                "            </tobank_rec>\n" +
                "        </tobank_set>\n" +
                "        <fail_set>\n" +
                "            <fail_total>失败条数</fail_total>\n" +
                "            <fail_rec>\n" +
                "                <serial>单笔序列号</serial>\n" +
                "                <rec_bankacc>收款方银行帐号</rec_bankacc>\n" +
                "                <bank_type>银行类型</bank_type>\n" +
                "                <rec_name>收款方真实姓名</rec_name>\n" +
                "                <pay_amt>付款金额(以分为单位)</pay_amt>\n" +
                "                <acc_type>账户类型&gt;</acc_type>\n" +
                "                <area>开户地区</area>\n" +
                "                <city>开户城市</city>\n" +
                "                <subbank_name>支行名称</subbank_name>\n" +
                "                <desc>失败原因</desc>\n" +
                "                <err_code>付款失败错误码，参考5.12章节</err_code>\n" +
                "                <err_msg>付款失败中文描述</err_msg>\n" +
                "                <modify_time>最后修改时间，格式：yyyy-MM-dd HH:mm:ss</modify_time>\n" +
                "            </fail_rec>\n" +
                "        </fail_set>\n" +
                "        <handling_set>\n" +
                "            <handling_total>处理中条数</handling_total>\n" +
                "            <handling_rec>\n" +
                "                <serial>单笔序列号</serial>\n" +
                "                <rec_bankacc>收款方银行帐号</rec_bankacc>\n" +
                "                <bank_type>银行类型</bank_type>\n" +
                "                <rec_name>收款方真实姓名</rec_name>\n" +
                "                <pay_amt>付款金额(以分为单位)</pay_amt>\n" +
                "                <acc_type>账户类型&gt;</acc_type>\n" +
                "                <area>开户地区</area>\n" +
                "                <city>开户城市</city>\n" +
                "                <subbank_name>支行名称</subbank_name>\n" +
                "                <desc>付款说明</desc>\n" +
                "                <modify_time>最后修改时间，格式：yyyy-MM-dd HH:mm:ss</modify_time>\n" +
                "            </handling_rec>\n" +
                "        </handling_set>\n" +
                "        <return_ticket_set>\n" +
                "            <ret_ticket_total>退票条数</ret_ticket_total>\n" +
                "            <ret_ticket_rec>　　 \n" +
                "            <serial>单笔序列号</serial>　　 \n" +
                "            <rec_bankacc>收款方银行帐号</rec_bankacc>　　 \n" +
                "            <bank_type>银行类型</bank_type>　　 \n" +
                "            <rec_name>收款方真实姓名</rec_name>　　 \n" +
                "            <pay_amt>付款金额(以分为单位)</pay_amt>　　 \n" +
                "            <acc_type>账户类型&gt;</acc_type>　　 \n" +
                "            <area>开户地区</area>　　 \n" +
                "            <city>开户城市</city>　　 \n" +
                "            <subbank_name>支行名称</subbank_name>　　 \n" +
                "            <desc>付款说明</desc>\n" +
                "            <modify_time>最后修改时间，格式：yyyy-MM-dd HH:mm:ss</modify_time></ret_ticket_rec>\n" +
                "        </return_ticket_set>\n" +
                "    </result>\n" +
                "</root>";
        PMap xmlwss = XMLUtil.XML2PMap(str);
        String resultxml = xmlwss.getString("result");
        resultxml = "<result>"+resultxml+"</result>";
        PMap result = XMLUtil.XML2PMap(resultxml);
        System.out.println(xmlwss);
    }
}
