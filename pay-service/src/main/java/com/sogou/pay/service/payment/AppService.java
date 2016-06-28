package com.sogou.pay.service.payment;

import com.sogou.pay.service.dao.AppDao;
import com.sogou.pay.service.entity.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AppService {
  @Autowired
  private AppDao appDao;


  public int insertApp(App app) {
    return appDao.insertApp(app);
  }

  public App selectApp(int appId) {
    return appDao.selectApp(appId);
  }

  public void deleteApp(int appId) {
    appDao.deleteApp(appId);
  }

  public List selectAppList() {
    return appDao.selectAppList();
  }

}
