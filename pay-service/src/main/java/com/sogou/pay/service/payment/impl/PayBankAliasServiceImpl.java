package com.sogou.pay.service.payment.impl;

import com.sogou.pay.service.dao.PayBankAliasDao;
import com.sogou.pay.service.entity.PayBankAlias;
import com.sogou.pay.service.payment.PayBankAliasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author huangguoqing
 * @Date 2015/3/6 10:52
 * @Description:
 */
@Service
public class PayBankAliasServiceImpl implements PayBankAliasService {

    @Autowired
    private PayBankAliasDao payBankAliasDao;
    /**
     * 获取银行别名
     * @param agencyCode 第三方支付机构编码
     * @param bankCode 银行编码
     * @param bankCardType 银行卡类型
     * @return 银行别名实体
     */
    @Override
    public PayBankAlias selectPayBankAlias(String agencyCode, String bankCode,Integer bankCardType) {
        return payBankAliasDao.selectPayBankAlias(agencyCode,bankCode,bankCardType);
    }
}
