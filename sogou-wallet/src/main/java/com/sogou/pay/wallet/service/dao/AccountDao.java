package com.sogou.pay.wallet.service.dao;

import com.sogou.pay.wallet.enums.AccountType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Created by xiepeidong on 2016/2/29.
 */
@Repository
public interface AccountDao {
    public int insertAccount(@Param("uid") int uid, @Param("account_type") AccountType account_type);

    public int setAccount(@Param("uid") int uid, @Param("account_type") AccountType account_type, @Param("balance") BigDecimal balance);

    public int increaseAccount(@Param("uid") int uid, @Param("account_type") AccountType account_type, @Param("balance") BigDecimal balance);

    public BigDecimal queryAccount(@Param("uid") int uid, @Param("account_type") AccountType account_type);

    public int transferAccount(@Param("uid") int uid, @Param("account_type") AccountType account_type, @Param("money") BigDecimal money, @Param("payeeid") int payeeid);
}
