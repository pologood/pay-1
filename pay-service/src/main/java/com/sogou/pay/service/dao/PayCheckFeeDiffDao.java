package com.sogou.pay.service.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by qibaichao on 2015/3/23.
 */
@Repository
public interface PayCheckFeeDiffDao {
    /**
     *
     * @param checkDate
     * @param agencyCode
     */
    public void insertFeeDiff(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);

    /**
     * 清除
     *
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public int delete(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);
}
