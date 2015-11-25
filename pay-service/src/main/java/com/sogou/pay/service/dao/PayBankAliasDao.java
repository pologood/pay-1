package com.sogou.pay.service.dao;

import com.sogou.pay.service.entity.PayBankAlias;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author huangguoqing
 * @Date 2015/3/6 10:47
 * @Description: 银行别名Dao
 */
@Repository
public interface PayBankAliasDao {
    public PayBankAlias selectPayBankAlias(@Param("agencyCode")String agencyCode,@Param("bankCode")String bankCode,@Param("bankCardType") Integer bankCardType);
}
