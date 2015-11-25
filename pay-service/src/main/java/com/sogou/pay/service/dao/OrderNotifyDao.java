package com.sogou.pay.service.dao;

import com.sogou.pay.service.entity.OrderNotify;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * User: Liwei
 * Date: 2015/3/10
 * Time: 10:38
 */
@Repository
public interface OrderNotifyDao {
    public int insertTask(OrderNotify orderNotify);

    public OrderNotify selectByTaskId(String taskId); //获取单个OrderNotify

    public List<OrderNotify> listTasksByOwner(String owner);  //获取多个个OrderNotify

    public int updateOwnerAndTimestamp(@Param("owner") String owner, @Param("expectTime") Date expectTime, @Param("limit") Date limit);

    public int updateTask(@Param("taskId") String taskId, @Param("expectTime") Date expectTime, @Param("notifyNum") int notifyNum);

    public int deleteTask(String taskId);

    public int giveUpTasks(String ownerId);

    public HashMap listAppIdNum(int appId);
}
