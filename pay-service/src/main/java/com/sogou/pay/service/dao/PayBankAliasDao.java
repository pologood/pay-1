package com.sogou.pay.service.dao;

import com.sogou.pay.service.entity.PayBankAlias;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 银行别名Dao
 */
@Repository
public interface PayBankAliasDao {
    public PayBankAlias selectPayBankAlias(@Param("agencyCode")String agencyCode,
                                           @Param("bankCode")String bankCode);
}
