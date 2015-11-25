package com.sogou.pay.service.payment;

import com.sogou.pay.service.entity.PayBankAlias;

/**
 * @Author huangguoqing
 * @Date 2015/3/6 10:50
 * @Description: 银行别名服务
 */
public interface PayBankAliasService {

    /**
     * 获取银行别名
     * @param agencyCode 第三方支付机构编码
     * @param bankCode 银行编码
     * @return 银行别名实体
     */
    public PayBankAlias selectPayBankAlias(String agencyCode,String bankCode,Integer bankCardType);
}
