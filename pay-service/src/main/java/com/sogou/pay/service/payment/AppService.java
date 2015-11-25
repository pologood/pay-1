package com.sogou.pay.service.payment;

import com.sogou.pay.service.entity.App;

import java.util.List;

/**
 * User: Liwei
 * Date: 2014/12/25
 * Time: 10:22
 */
public interface AppService {
    public int insertApp(App app);
    public App selectApp(int appId);
    public void updateApp(App app);
    public void deleteApp(int appId);
    public List selectAppList();
}
