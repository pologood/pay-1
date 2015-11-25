package com.sogou.pay.service.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayBankRouter;

/**
 * @User: huangguoqing
 * @Date: 2015/03/02
 * @Description: 银行路由Dao
 */
@Repository
public interface PayBankRouterDao {

    public List<PayBankRouter> selectPayBankRouterList(PayBankRouter payBankRouter);

}
