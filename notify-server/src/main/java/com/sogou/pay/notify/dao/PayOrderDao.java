package com.sogou.pay.notify.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface PayOrderDao {
    /**
     * 修改支付单通知状态
     */
    public int updateNotifyStatus(@Param("payId") String payId, @Param("notifyStatus") int notifyStatus);

}
