package com.sogou.pay.service.dao;

import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.service.BaseTest;
import com.sogou.pay.service.entity.RefundInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by hjf on 15-3-2.
 */
public class RefundInfoDAOTest extends BaseTest {
    @Autowired
    private RefundInfoDAO refundInfoDAO;

    @Test
    public void testAll() {
        String orderId = "test" + DateUtil.format(new Date(), DateUtil.DATE_FORMAT_SECOND_SHORT);
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setRefundId(orderId);
        refundInfo.setPayDetailId("2000001");
        refundInfo.setPayId("1000001");
        refundInfo.setOrderId("3000001");
        refundInfo.setAgencyCode("test");
        refundInfo.setMerchantNo("test");
        refundInfo.setPayFeeType(2);
        refundInfo.setBalanceRefund(new BigDecimal(500));
        refundInfo.setAppId(1000);
        refundInfo.setNetBalanceRefund(new BigDecimal(500.01));
        refundInfo.setOrderMoney(new BigDecimal(2000.03));
        refundInfo.setRefundMoney(new BigDecimal(1000.01));
        refundInfo.setRefundReqTime(new Date());
        refundInfo.setRefundResTime(new Date());

        testInsert(refundInfo);
//        testSelect(refundInfo);
        testUpdateStatus(refundInfo);
    }

    public void testInsert(RefundInfo refundInfo) {
        assertEquals(refundInfoDAO.insert(refundInfo), 1);
    }

    @Test
    public void testSelect() {
        RefundInfo result = refundInfoDAO.selectByRefundId("20150417153642747001");

        System.out.println(result);
    }

    public void testUpdateStatus(RefundInfo refundInfo) {
        String refundId = refundInfo.getRefundId();
        System.out.println(refundInfo.getRefundStatus());
        assertEquals(refundInfoDAO.updateRefundStatusOldToNew(refundId, refundInfo.getRefundStatus() + 1, refundInfo.getRefundStatus(), null, null, null), 1);

        RefundInfo result = refundInfoDAO.selectByRefundId(refundId);
        assertEquals(result.getRefundStatus(), refundInfo.getRefundStatus()+1);

        assertEquals(refundInfoDAO.updateRefundStatusOldToNew(refundId, refundInfo.getRefundStatus() + 1, refundInfo.getRefundStatus(), null, null, null), 0);

        RefundInfo result1 = refundInfoDAO.selectByRefundId(refundId);
        assertEquals(result1.getRefundStatus(), refundInfo.getRefundStatus()+1);
    }
}
