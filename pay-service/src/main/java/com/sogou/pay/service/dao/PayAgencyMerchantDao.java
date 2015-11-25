package com.sogou.pay.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayAgencyMerchant;

/**
 * @User: huangguoqing
 * @Date: 2015/03/02
 * @Description: 机构商户Dao
 */
@Repository
public interface PayAgencyMerchantDao {

    public PayAgencyMerchant selectPayAgencyMerchant(PayAgencyMerchant payAgencyMerchant);

    public PayAgencyMerchant selectPayAgencyMerchantById(int id);

    /**
     * 对账使用
     * 根据支付机构编码查询信息list
     *
     * @param agencyCode
     * @return
     */
    public List<PayAgencyMerchant> selectPayAgencyMerchants(@Param("agencyCode") String agencyCode);

    /**
     * 对账使用
     * 根据 机构编码 和商户号查询
     * @param agencyCode
     * @param merchantNo
     * @return
     */
    public PayAgencyMerchant selectByAgencyAndMerchant(@Param("agencyCode") String agencyCode, @Param("merchantNo") String merchantNo);
}
