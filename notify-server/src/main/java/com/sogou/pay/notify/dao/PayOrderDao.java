/*
 * Copyright 2014-2016 cyou.cn All right reserved. This software is the
 * confidential and proprietary information of cyou.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cyou.cn.
 */
package com.sogou.pay.notify.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @Author qibaichao
 * @ClassName PayOrderDao
 * @Date 2014年8月12日
 * @Description:支付单dao
 */
@Repository
public interface PayOrderDao {
    /**
     * @param payId
     * @return
     * @Author qibaichao
     * @MethodName updateNotifyStatus
     * @Date 2014年9月22日
     * @Description:修改支付单通知状态
     */
    public int updateNotifyStatus(@Param("payId") String payId, @Param("notifyStatus") int notifyStatus);

}
