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
     */
    public PayFee getPayFee(@Param("merchantNo")String merchantNo,@Param("payFeeType")Integer payFeeType,@Param("accessPlatform")Integer accessPlatform);

}
