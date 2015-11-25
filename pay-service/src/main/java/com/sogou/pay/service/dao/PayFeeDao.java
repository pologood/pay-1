package com.sogou.pay.service.dao;

import com.sogou.pay.service.entity.PayFee;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by wujingpan on 2015/3/5.
 */
@Repository
public interface PayFeeDao {

    /**
     * 根据商户和付款类型获取支付手续费
     * @param merchantNo 商户号
     * @param payFeeType 1，网银 2，第三方  3，扫码支付，4.SDK
     * @param accessPlatform 1，PC 2，WAP  3，SDK，99.不区分
     * @return 手续费entity
     */
    public PayFee getPayFee(@Param("merchantNo")String merchantNo,@Param("payFeeType")Integer payFeeType,@Param("accessPlatform")Integer accessPlatform);

}
