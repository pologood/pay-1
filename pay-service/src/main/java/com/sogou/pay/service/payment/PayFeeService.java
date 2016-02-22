package com.sogou.pay.service.payment;

import java.math.BigDecimal;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.PMap;

/**
 * Created by wujingpan on 2015/3/5.
 */
public interface PayFeeService {

    /**
     * 计算手续费
     * @param payAmount  支付金额
     * @param merchantNo 商户号
     * @param payFeeType 1，网银 2，第三方  3，扫码支付，4.SDK
     * @param accessPlatform 1，PC 2，WAP  3，SDK，99.不区分
     * @return BigDecimal
     */
    public PMap<String,BigDecimal> getPayFee(BigDecimal payAmount,String merchantNo,Integer payFeeType,Integer accessPlatform) throws ServiceException;
}
