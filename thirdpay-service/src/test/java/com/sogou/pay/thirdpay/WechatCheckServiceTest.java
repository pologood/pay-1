package com.sogou.pay.thirdpay;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.thirdpay.biz.WechatCheckService;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by qibaichao on 2015/3/9.
 */
public class WechatCheckServiceTest extends BaseTest {

    @Autowired
    private WechatCheckService wechatCheckService;


    @Test
    public void doQuery() {

        try {

            //公众号
            String appId = "wx14cdf1737b024a16";
            //商户号
            String merchantNo = "1234469202";
            //对账类型
            CheckType checkType = CheckType.PAID;
            //对账日期
            String checkDate = "20151110";
            //加密密钥
            String key = "1hu8aa7dbnldi012y984klo28uom5r42";
            ResultMap resultMap = wechatCheckService.doQuery(appId, merchantNo, checkType, checkDate, key);
            System.out.println(JSON.toJSON(resultMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
