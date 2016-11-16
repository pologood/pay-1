package com.sogou.pay.service.dao;

import com.sogou.pay.service.entity.App;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AppDao {

    public int insertApp(App app);

    public App selectApp(@Param("appId")int appId);

    public void updateApp(App app);

    public void deleteApp(@Param("appId")int appId);

    public List selectAppList();
}
