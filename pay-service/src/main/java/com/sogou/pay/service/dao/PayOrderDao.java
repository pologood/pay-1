package com.sogou.pay.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayOrderRelation;

/**
 * @User: huangguoqing
 * @Date: 2015/03/03
 * @Description: 支付单信息Dao
 */
@Repository
public interface PayOrderDao {

    public int insertPayOrder(PayOrderInfo payOrderInfo);

    public PayOrderInfo selectPayOrderById(String payId);

    public List<PayOrderInfo> selectPayOrderByPayIdList(List<PayOrderRelation> relationList);

    public int updateAddRefundMoney(@Param("payId") String payId, @Param("refundAmount") BigDecimal refundAmount, @Param("refundFlag") int refundFlag);

    public int updatePayOrder(PayOrderInfo payOrderInfo);

    public int updatePayOrderByPayId(@Param("payId") String payId,@Param("bankCode") String bankCode,@Param("payOrderStatus") Integer payOrderStatus, @Param("successTime") Date successTime);
    
    public int updatePayOrderNotifyByReqId(String payReqId);
    
    public PayOrderInfo selectPayOrderByOrderId(@Param("orderId")String orderId,@Param("appId")Integer appId);
}
