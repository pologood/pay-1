package com.sogou.pay.service.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.service.BaseTest;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.enums.OrderType;
import com.sogou.pay.service.enums.TerminalType;

/**
 * User: hujunfei
 * Date: 2015-03-04 10:55
 */
public class PayOrderDAOTest extends BaseTest {
    @Autowired
    private PayOrderDao payOrderDAO;

    @Test
    public void testA() {
        BigDecimal b = new BigDecimal(1000.03);
        BigDecimal c =b.add(new BigDecimal(299.96));
        BigDecimal d = b.add(new BigDecimal(299.97));

        System.out.println(String.valueOf(b.doubleValue()));
        System.out.println(String.valueOf(c.doubleValue()));
        System.out.println(String.valueOf(d.doubleValue()));
    }


    @Test
    public void testAll() {
        String payId = "test100001";
        PayOrderInfo order = new PayOrderInfo();
        order.setAppId(1000);
        order.setOrderId("1000001");
        order.setPayId(payId);
        order.setChannelCode("");
        order.setOrderType(OrderType.PAYCASH.getValue());
        order.setProductInfo("test");
        order.setBuyHomeIp("127.0.0.1");
        order.setOrderMoney(new BigDecimal(2000.05));
        order.setAccessPlatForm(TerminalType.WEB.getValue());
        order.setPayOrderStatus(3);
        order.setRefundMoney(new BigDecimal(0));
        order.setRefundFlag(1);

        testInsert(order);
        testUpdateRefund(order);
    }

    @Test
    public void selectPayOrderByReqId(){
        String reqId = "ZF20150309161417272001";
        List<PayOrderRelation> list1 = new ArrayList();
        PayOrderRelation r1 = new PayOrderRelation();
        r1.setPayId("ZFD20150413173930414001");
        PayOrderRelation r2 = new PayOrderRelation();
        r2.setPayId("ZFD20150413174438698001");
        list1.add(r1);
        list1.add(r2);
        List<PayOrderInfo> list = payOrderDAO.selectPayOrderByPayIdList(list1);
        System.out.println(list);
    }
    private void testInsert(PayOrderInfo payOrderInfo) {
        assertEquals(payOrderDAO.insertPayOrder(payOrderInfo), 1);
    }

    private void testUpdateRefund(PayOrderInfo order) {
        String payId = order.getPayId();

        assertEquals(payOrderDAO.updateAddRefundMoney(payId, new BigDecimal(1000.01), 2), 1);
        assertEquals(payOrderDAO.updateAddRefundMoney(payId, new BigDecimal(1000.08), 2), 0);
        assertEquals(payOrderDAO.updateAddRefundMoney(payId, new BigDecimal(1000.03), 2), 1);
    }
    
    @Test
    public void testUpdate(){
        try{
//            payOrderDAO.updatePayOrderByReqId("ZF20150312171544388002",1, new Date());
            }
        catch (Exception e){
            e.printStackTrace();
        };
        
        System.out.println("ss");
    }
    
    @Test
    public void testUpdateNotifyByReqId(){
        payOrderDAO.updatePayOrderNotifyByReqId("ZF20150312143013997001");
    }
    
    @Test
    public void testSelectById(){
        System.out.println(payOrderDAO.selectPayOrderById("ddd"));
    }
    
    @Test
    public void testUpdateOrderInfo(){
        payOrderDAO.updatePayOrderByPayId("111111", "ALIPAY", 1, new Date());
    }
}
