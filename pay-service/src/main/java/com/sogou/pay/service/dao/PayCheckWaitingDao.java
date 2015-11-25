package com.sogou.pay.service.dao;

import com.sogou.pay.manager.model.PayCheckUpdateModle;
import com.sogou.pay.service.entity.PayCheckWaiting;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName PayCheckWaitingDao
 * @Date 2015年2月16日
 * @Description:
 */
@Repository
public interface PayCheckWaitingDao {

    /**
     * @param payCheckWaiting
     * @return
     * @Author qibaichao
     * @MethodName insert
     * @Date 2015年2月16日
     * @Description:新增
     */
    int insert(PayCheckWaiting payCheckWaiting);

    /**
     * @param list
     * @return
     * @Author qibaichao
     * @MethodName batchInsert
     * @Date 2015年2月16日
     * @Description:批量插入
     */
    public int batchInsert(List<PayCheckWaiting> list);


    /**
     * @param instructId
     * @return
     * @Author qibaichao
     * @MethodName getByInstructId
     * @Date 2015年2月16日
     * @Description:根据ID主键查询待对象
     */
    public PayCheckWaiting getByInstructId(String instructId);

    /**
     * @param list
     * @return
     * @Author qibaichao
     * @MethodName batchUpdateStatus
     * @Date 2015年2月16日
     * @Description:批量更新状态
     */
    public int batchUpdateStatus(List<PayCheckUpdateModle> list);

    /**
     * 根据日期、支付、统计BizAmt
     *
     * @return
     */
    public Map<String, Object> sumAmtAndNum(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode, @Param("bizCode") int bizCode);

    /**
     *根据日期、支付、统计feeAmt
     * @param checkDate
     * @param agencyCode
     * @param bizCode
     * @return
     */
    public Map<String, Object> sumFeeAmtAndNum(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode, @Param("bizCode") int bizCode);


}
