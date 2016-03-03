package com.sogou.pay.wallet.service;

import com.sogou.pay.wallet.enums.AccountType;
import com.sogou.pay.wallet.service.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by xiepeidong on 2016/2/28.
 */
@Component
public class AccountService {
    @Autowired
    private AccountDao accountDao;


    public int insertBalance(int uid) {
        return accountDao.insertAccount(uid, AccountType.BALANCE);
    }

    public int setBalance(int uid, BigDecimal balance) {
        return accountDao.setAccount(uid, AccountType.BALANCE, balance);
    }

    public int increaseBalance(int uid, BigDecimal balance) {
        return accountDao.increaseAccount(uid, AccountType.BALANCE, balance);
    }

    public BigDecimal queryBalance(int uid) {
        return accountDao.queryAccount(uid, AccountType.BALANCE);
    }

    public int transferBalance(int uid, BigDecimal money, int payeeid) {
        return accountDao.transferAccount(uid, AccountType.BALANCE, money, payeeid);
    }

    public int insertLucky(int uid) {
        return accountDao.insertAccount(uid, AccountType.LUCKY);
    }

    public int setLucky(int uid, BigDecimal balance) {
        return accountDao.setAccount(uid, AccountType.LUCKY, balance);
    }

    public int increaseLucky(int uid, BigDecimal balance) {
        return accountDao.increaseAccount(uid, AccountType.LUCKY, balance);
    }

    public BigDecimal queryLucky(int uid) {
        return accountDao.queryAccount(uid, AccountType.LUCKY);
    }
}
