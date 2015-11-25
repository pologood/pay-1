package com.sogou.pay.notify.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by qibaichao on 2015/10/30.
 */
@Repository
public interface RefundInfoDao {
    /**
     * @param refundId
     * @return
     * @Author qibaichao
     * @MethodName updateNotifyStatus
     * @Date 2014年9月22日
     * @Description:修改退款单通知状态
     */
    public int updateNotifyStatus(@Param("refundId") String refundId, @Param("notifyStatus") int notifyStatus);

}
