package com.sogou.pay.service.dao;

import org.apache.ibatis.annotations.Param;

import com.sogou.pay.service.entity.PayCheckDayLog;
import org.springframework.stereotype.Repository;

/**
 * @Author qibaichao
 * @ClassName PayCheckDayLogDao
 * @Date 2015年2月16日
 * @Description:
 */
@Repository
public interface PayCheckDayLogDao {

    /**
     * @param payCheckDayLog
     * @return
     * @Author qibaichao
     * @MethodName insert
     * @Date 2015年2月16日
     * @Description:新增
     */
    public int insert(PayCheckDayLog payCheckDayLog);

    /**
     * @param checkDate
     * @param agencyCode
     * @return
     * @Author qibaichao
     * @MethodName getByCheckDateAndPayment
     * @Date 2015年2月16日
     * @Description:根据对账日期和渠道查询
     */
    public PayCheckDayLog getByCheckDateAndAgency(
            @Param("checkDate") String checkDate,
            @Param("agencyCode") String agencyCode);

    /**
     * @param id
     * @param status
     * @param version
     * @param remark
     * @return
     * @Author qibaichao
     * @MethodName updateStatus
     * @Date 2015年2月16日
     * @Description:结果状态状态更新
     */
    public int updateStatus(@Param("id") long id,
                            @Param("status") int status, @Param("version") int version,
                            @Param("remark") String remark);

}
