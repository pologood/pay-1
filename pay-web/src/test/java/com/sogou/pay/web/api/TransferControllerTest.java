package com.sogou.pay.web.api;

import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.utils.SequenceGenerator;
import com.sogou.pay.web.form.TransferRecord;
import com.sogou.pay.service.utils.orderNoGenerator.PayTransferBatchNo;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;
import com.sogou.pay.web.controller.api.APIController;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.form.TransferForm;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 代付查询任务
 */
public class TransferControllerTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(TransferControllerTest.class);

    @Autowired
    APIController apiController;

    @Autowired
    SequenceFactory sequenceFactory;

    @Test
    public void testTransfer() {
        String url = "/api/transfer";
        TransferForm params = new TransferForm();
        params.setVersion("v1.0");
        params.setBatchNo(sequenceFactory.getTransferBatchNo());
        params.setAppId("1999");
        params.setCompanyName("搜狗科技");
        params.setDbtAcc("591902896010504");
        params.setBbkNbr("59");
        params.setMemo("代发");
        params.setSignType("1");

        List<TransferRecord> records = new ArrayList<>();
        for(int i=0;i<1;i++){
            TransferRecord record = new TransferRecord();
            record.setPayId(sequenceFactory.getTransferNo());
            record.setRecBankacc("6225885910000108");
            record.setRecName("Judy Zeng");
            record.setPayAmt("1.00");
            records.add(record);
        }
        params.setRecordList(JSONUtil.Bean2JSON(records));
        Map map = BeanUtil.Bean2Map(params);

        map.put("sign", apiController.signData(map));
        testGet(url, map);
    }

}
