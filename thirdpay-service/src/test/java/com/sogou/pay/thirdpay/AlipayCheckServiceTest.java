package com.sogou.pay.thirdpay;

import com.alibaba.fastjson.JSON;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.thirdpay.biz.AlipayCheckService;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.modle.OutCheckRecord;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by qibaichao on 2015/3/6.
 */
public class AlipayCheckServiceTest extends BaseTest {

    @Autowired
    private AlipayCheckService alipayCheckService;

    @Test
    public void doQuery() {

        try {
//            <rate>0.01</rate>
            CheckType checkType = CheckType.PAYCASH;
            String startTime = "2015-05-20 00:00:00";
            String endTime = "2015-05-20 23:59:59";
            String pageNo = "1";
            String pageSize = "1000";
//            String merchantNo = "2088811923135335";
//            String key = "20w5obaxam7keamcuzk7cfiu46j4htg0";

            String merchantNo = "10012138842";
            String key = "i7msh5zr39lvqbexsfgm5uh3wxzf5yy8";

            ResultMap resultMap = alipayCheckService.doQuery(merchantNo, checkType, startTime, endTime, pageNo, pageSize, key);

            List<OutCheckRecord> records = (List<OutCheckRecord>) resultMap.getData().get("records");
            System.out.println("records:"+JSON.toJSON(records));
            System.out.println("records size:"+records.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
