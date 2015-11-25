/*
 * Copyright 2014-2016 cyou.cn All right reserved. This software is the
 * confidential and proprietary information of cyou.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cyou.cn.
 */
package com.sogou.pay.notify.dao;

import com.sogou.pay.notify.entity.NotifyErrorLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


/**
 * @Author qibaichao
 * @ClassName NotifyErrorLogDao
 * @Date 2014年9月16日
 * @Description:
 */
@Repository
public interface NotifyErrorLogDao {
    /**
     * @param notifyErrorLog
     * @Author qibaichao
     * @MethodName insertErrorLog
     * @Date 2014年9月16日
     * @Description:
     */

    public void insertErrorLog(NotifyErrorLog notifyErrorLog);

    /**
     * 删除任务
     *
     * @param id
     */
    public void deleteErrorLog(long id);

    /**
     * @param notifyErrorLog
     * @return
     * @Author qibaichao
     * @MethodName updateErrorLog
     * @Date 2014年9月16日
     * @Description:
     */
    public int updateErrorLog(NotifyErrorLog notifyErrorLog);

    /**
     * @param notifyType
     * @param status
     * @param currentTime
     * @return
     * @Author qibaichao
     * @MethodName queryByNotifyTypeStatus
     * @Date 2014年9月18日
     * @Description:根据通知类型，状态查询
     */
    public List<NotifyErrorLog> queryByNotifyTypeStatus(@Param("notifyType") int notifyType,
                                                        @Param("status") int status, @Param("currentTime") Date currentTime);

    public NotifyErrorLog queryById(Long notifyType);

}
