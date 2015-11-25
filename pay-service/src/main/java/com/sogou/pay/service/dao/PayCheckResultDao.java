package com.sogou.pay.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayCheckResult;

/**
 * @Author qibaichao
 * @ClassName PayCheckResultDao
 * @Date 2015年2月16日
 * @Description:
 */
@Repository
public interface PayCheckResultDao {

    /**
     * 新增
     *
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public int insert(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);


    /**
     * 清除
     *
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public int delete(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);

    /**
     * @param id
     * @return
     * @Author qibaichao
     * @MethodName updateStatus
     * @Date 2015年3月2日
     * @Description:根据ID更新金额
     */
    public int updateStatus(@Param("id") long id,
                            @Param("status") int status);

    /**
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public int queryCountByDateAndAgency(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);

    /**
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public List<PayCheckResult> queryByDateAndAgency(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);

}
