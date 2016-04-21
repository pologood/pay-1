package com.sogou.pay.service.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.entity.PayReqDetail;

public class PayReqDetailDaoTest extends BaseTest{
    @Autowired
    private PayReqDetailDao dao;
    
    @Test
    public void selectTest(){
        PayReqDetail detail = dao.selectPayReqDetailById("ZF20150619174932402001");
        System.out.println(detail.toString());
    }
    
    @Test
    public void selectListTest(){
        List<PayOrderRelation> list = new ArrayList<PayOrderRelation>();
        PayOrderRelation param = new PayOrderRelation();
        param.setPayDetailId("ZF20150413173933896001");
        PayOrderRelation param2 = new PayOrderRelation();
        param2.setPayDetailId("ZF20150413174440045001");
        list.add(param);
        list.add(param2);
        System.out.println(dao.selectPayReqByReqIdList(list).size());
    }
            
}
