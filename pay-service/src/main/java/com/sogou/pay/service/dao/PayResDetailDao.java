package com.sogou.pay.service.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayResDetail;

/**
 * @User: liwei
 * @Date: 2015/03/06
 * @Description: 支付回调流水信息Dao
 */
@Repository
public interface PayResDetailDao {

    public PayResDetail selectByAgencyOrderId(String agencyOrderId);

    public int insertPayResDetail(PayResDetail payResDetail);

    public PayResDetail selectPayResById(String payDetailId);

    public int updatePayResPayfeeById(@Param("payFee") BigDecimal payFee,
            @Param("feeRate") BigDecimal feeRate, @Param("payDetailId") String payDetailId);

}
