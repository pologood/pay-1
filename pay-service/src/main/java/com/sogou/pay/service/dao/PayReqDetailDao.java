package com.sogou.pay.service.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.entity.PayReqDetail;

/**
 * @User: huangguoqing
 * @Date: 2015/03/03
 * @Description: 支付流水信息Dao
 */
@Repository
public interface PayReqDetailDao {

    public int insertPayReqDetail(PayReqDetail payReqDetail);

    public PayReqDetail selectPayReqDetailById(String payReqId);
    
    public List<PayReqDetail> selectPayReqByReqIdList(List<PayOrderRelation> payOrderRelationList);
}