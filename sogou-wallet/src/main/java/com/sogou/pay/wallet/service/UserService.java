package com.sogou.pay.wallet.service;

import com.sogou.pay.wallet.service.dao.UserDao;
import com.sogou.pay.wallet.service.entity.WalletUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by xiepeidong on 2016/2/23.
 */
@Component
public class UserService {

    @Autowired
    private UserDao userDao;

    public int insertUser(String openid, String uname, String pay_passwd) {
        return userDao.insertUser(openid, uname, pay_passwd);
    }

    public int updateUserLoginTime(String openid) {
        return userDao.updateUserLoginTime(openid, new Date());
    }

    public WalletUser queryUserByOpenID(String openid) {
        return userDao.queryUserByOpenID(openid);
    }

    public WalletUser queryUserByUname(String uname) {
        return userDao.queryUserByUname(uname);
    }

    public int setPayPasswd(int uid, String pay_passwd) {
        return userDao.setPayPasswd(uid, pay_passwd);
    }

    public boolean verifyPayPasswd(int uid, String pay_passwd) {
        return userDao.verifyPayPasswd(uid, pay_passwd) == 1;
    }
}
