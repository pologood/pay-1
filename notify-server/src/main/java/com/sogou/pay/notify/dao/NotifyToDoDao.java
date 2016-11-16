package com.sogou.pay.notify.dao;

import com.sogou.pay.notify.entity.NotifyToDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotifyToDoDao {

    public void insertNotifyToDo(NotifyToDo notifyToDo);


    public void deleteNotifyToDo(long id);


    public int updateNotifyToDo(NotifyToDo notifyToDo);

    /**
     * 根据通知类型，状态查询
     */
    public List<NotifyToDo> queryByNotifyTypeStatus(@Param("notifyType") int notifyType,
                                                    @Param("notifyStatus") int notifyStatus, @Param("currentTime") Date currentTime);

    public NotifyToDo queryById(Long notifyType);

}
