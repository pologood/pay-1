package com.sogou.pay.service.dao;

import org.springframework.stereotype.Repository;

/**
 * @User: huangguoqing
 * @Date: 2015/04/27
 * @Description: 支付回调流水排重
 */
@Repository
public interface PayResIdDao {

    public int insertPayResId(String payDetailId);

}
