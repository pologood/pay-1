package com.sogou.pay.wallet.service.dao;

import com.sogou.pay.wallet.service.entity.WalletUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by xiepeidong on 2016/2/29.
 */
@Repository
public interface UserDao {

    public int insertUser(@Param("openid") String openid, @Param("uname") String uname, @Param("pay_passwd") String pay_passwd);

    public int updateUserLoginTime(@Param("openid") String openid, @Param("login_time") Date login_time);

    public WalletUser queryUserByOpenID(@Param("openid") String openid);

    public WalletUser queryUserByUname(@Param("uname") String uname);

    public int setPayPasswd(@Param("uid") int uid, @Param("pay_passwd") String pay_passwd);

    public int verifyPayPasswd(@Param("uid") int uid, @Param("pay_passwd") String pay_passwd);


}
