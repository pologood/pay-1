package com.sogou.pay.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayCheckDiff;

/**
 * Created by qibaichao on 2015/3/23.
 */
@Repository
public interface PayCheckDiffDao {

    /**
     * 清除
     *
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public int delete(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);
    /**
     * 插入金额差异
     *
     * @param checkDate
     * @param agencyCode
     */
    public void insertAmtDiff(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);

    /**
     *插入对方多单差异
     *
     * @param checkDate
     * @param agencyCode
     */
    public void insertOutMoreDiff(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);

    /**
     * 插入对方漏单单差异
     *
     * @param checkDate
     * @param agencyCode
     */
    public void insertOutLessDiff(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);

    public int selectUnResolvedCount();

    public List<PayCheckDiff> selectUnResolvedList();

    /**
     *
     * @param id
     * @param status
     * @param remark
     * @return
     */
    public int updateStatus(@Param("id") Long id,@Param("status")int status ,@Param("remark")String remark);




}
