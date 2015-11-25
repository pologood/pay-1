package com.sogou.pay.service.payment;

import java.util.List;

import com.sogou.pay.service.entity.PayBankRouter;

/**
 * @User: huangguoqing
 * @Date: 2015/03/02
 * @Description: 银行路由服务
 */
public interface PayBankRouterService {

    /**
     * 根据规则ID查询银行路由第三方机构List
     * @param *路由规则
     * @return 银行路由List
     */
    public List<PayBankRouter> selectPayBankRouterList(PayBankRouter payBankRouter);
}
