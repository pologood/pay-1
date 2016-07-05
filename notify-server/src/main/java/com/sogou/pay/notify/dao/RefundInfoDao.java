package com.sogou.pay.notify.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundInfoDao {
    /**
     * 修改退款单通知状态
     */
    public int updateNotifyStatus(@Param("refundId") String refundId, @Param("notifyStatus") int notifyStatus);

}
