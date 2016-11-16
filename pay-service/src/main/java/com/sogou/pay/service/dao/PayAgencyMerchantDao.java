package com.sogou.pay.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayAgencyMerchant;

/**
 * 机构商户Dao
 */
@Repository
public interface PayAgencyMerchantDao {

    public PayAgencyMerchant getMerchant(PayAgencyMerchant payAgencyMerchant);

    public PayAgencyMerchant getMerchantById(@Param("merchantId") int merchantId);

    /**
     * 对账使用
     * 根据支付机构编码查询信息list
     */
    public List<PayAgencyMerchant> getMerchantsByAgencyCode(@Param("agencyCode") String agencyCode);

    /**
     * 对账使用
     * 根据 机构编码 和商户号查询
     */
    public PayAgencyMerchant getMerchantByAgencyCodeAndMerchantNo(@Param("agencyCode") String agencyCode, @Param("merchantNo") String merchantNo);

    public List<PayAgencyMerchant> routeMerchants(@Param("channelId")Integer channelId,
                                                  @Param("appId")Integer appId,
                                                  @Param("companyId")Integer companyId);
}
