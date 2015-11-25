package com.sogou.pay.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayCheckFeeResult;

/**
 * Created by qibaichao on 2015/3/20.
 * 手续费对账结果dao
 */
@Repository
public interface PayCheckFeeResultDao {


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
     * @MethodName updateFeeAndNumById
     * @Date 2015年3月2日
     * @Description:根据ID更新金额
     */
    public int updateFeeStatus(@Param("id") long id, @Param("status") int status);

    /**
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public List<PayCheckFeeResult> queryByDateAndAgency(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode);


}
