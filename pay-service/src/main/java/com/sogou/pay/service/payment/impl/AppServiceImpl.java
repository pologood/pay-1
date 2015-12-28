package com.sogou.pay.service.payment.impl;

import com.sogou.pay.service.dao.AppDao;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.payment.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: Liwei
 * Date: 2014/12/25
 * Time: 10:23
 */
@Service
public class AppServiceImpl implements AppService {
    @Autowired
    private AppDao appDao;

    @Override
    public int insertApp(App app) { return appDao.insertApp(app);}
    @Override
    public App selectApp(int appId) {
        return  appDao.selectApp(appId);
    }
    @Override
    public void deleteApp(int appId) { appDao.deleteApp(appId);}
    @Override
    public List selectAppList() { return  appDao.selectAppList();}

}
