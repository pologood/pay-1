package com.sogou.pay.service.dao;

import com.sogou.pay.service.BaseTest;
import com.sogou.pay.service.entity.PayOrderRelation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Author huangguoqing
 * @Date 2015/3/9 10:40
 * @Description:
 */
public class PayOrderRelationTest extends BaseTest{
    @Autowired
    PayOrderRelationDao dao;

    @Test
    public void insertTest(){
        PayOrderRelation relation = new PayOrderRelation();
        relation.setPayDetailId("ZF20150309103911149001");
        relation.setPayId("p1122222");
        relation.setInfoStatus(1);
        relation.setCreateTime(new Date());
        dao.insertPayOrderRelation(relation);
    }
    
    @Test
    public void updateTest(){
        dao.updatePayOrderRelation(3, "ZF20150309165115934001");
    }
}
