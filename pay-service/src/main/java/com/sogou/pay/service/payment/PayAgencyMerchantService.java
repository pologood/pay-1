package com.sogou.pay.service.payment;

import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.entity.PayBankRouter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @User: huangguoqing
 * @Date: 2015/03/02
 * @Description: 机构商户服务
 */
public interface PayAgencyMerchantService {

    /**
     * 根据条件查询第三方机构商户信息
     * @param *机构商户实体
     * @return 机构商户实体
     */
    public PayAgencyMerchant selectPayAgencyMerchant(PayAgencyMerchant payAgencyMerchant);

    /**
     * 根据id查询第三方机构商户信息
     * @param id 主键ID
     * @return 机构商户实体
     */
    public PayAgencyMerchant selectPayAgencyMerchantById(int id);

    /**
     * 对账使用
     * 根据支付机构编码查询信息list
     * @param agencyCode
     * @return
     */
    public List<PayAgencyMerchant> selectPayAgencyMerchants(String agencyCode);

    /**
     * 对账使用
     * @param agencyCode
     * @param merchantNo
     * @return
     */
    public PayAgencyMerchant selectByAgencyAndMerchant( String agencyCode,String merchantNo);


}
