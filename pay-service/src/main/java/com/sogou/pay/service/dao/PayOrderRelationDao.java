package com.sogou.pay.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayOrderRelation;

/**
 * @User: huangguoqing
 * @Date: 2015/03/03
 * @Description: 支付单与支付流水关联信息Dao
 */
@Repository
public interface PayOrderRelationDao {

    public int insertPayOrderRelation(PayOrderRelation payOrderRelation);

    public List<PayOrderRelation> selectPayOrderRelation(PayOrderRelation payOrderRelation);

    public String selectPayOrderId(String payDetailId);
    
    public int updatePayOrderRelation(@Param("status")Integer status,@Param("payDetailId")String payDetailId);
}
