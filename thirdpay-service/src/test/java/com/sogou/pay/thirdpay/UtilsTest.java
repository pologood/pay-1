package com.sogou.pay.thirdpay;


import com.sogou.pay.thirdpay.service.Tenpay.TenpayUtils;
import org.junit.Test;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/28 15:41
 */
public class UtilsTest extends BaseTest {
    @Test
    public void testUtilsTest() {
        String refundDate = TenpayUtils.getNonceStr();
        System.out.print("result"+refundDate);
    }
    @Test
    public void testStringTest() {
        String result_details = "01012300001^2010123016346858^0.02^SUCCESS$|";
        String[] details = result_details.split("#");
        String detail = details[0];
        int dol = detail.indexOf("$");
        // 交易退款数据集
        String deal = detail.substring(0, dol);
        String[] dealItems = deal.split("\\^");
        System.out.print("result"+dealItems);
    }



}
