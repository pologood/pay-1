package com.sogou.pay.service.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.manager.model.PayCheckUpdateModel;
import com.sogou.pay.service.entity.PayCheck;
import com.sogou.pay.thirdpay.biz.model.OutCheckRecord;

/**
 * @Author qibaichao
 * @ClassName PayCheckDao
 * @Date 2015年2月16日
 * @Description:
 */
@Repository
public interface PayCheckDao {

    /**
     * @param payCheckList
     * @return
     * @Author qibaichao
     * @MethodName batchInsert
     * @Date 2015年2月16日
     * @Description:批量插入
     */
    public int batchInsert(List<PayCheck> payCheckList);

    /**
     * @param list
     * @return
     * @Author qibaichao
     * @MethodName batchUpdateStatus
     * @Date 2015年2月16日
     * @Description:批量更新状态
     */
    public int batchUpdateStatus(List<PayCheckUpdateModel> list);

    public int deleteInfo(@Param("checkDate") String checkDate, @Param("agencyCode") String agencyCode,
                          @Param("merchantNo") String merchantNo);

    /**
     * @param instructId
     * @param bizCode
     * @return
     * @Author qibaichao
     * @MethodName getByInstructIdAndCheckType
     * @Date 2015年2月16日
     * @Description:根据支付指令ID和业务码查询PayCheckPo
     */
    public PayCheck getByInstructIdAndCheckType(
            @Param("instructId") String instructId,
            @Param("checkType") int checkType);

    public List<Map<String, Object>> queryByMerAndDateAndCheckType(
            @Param("checkDate") String checkDate,
            @Param("agencyCode") String agencyCode,
            @Param("checkType") int checkType, @Param("startRow") int startRow,
            @Param("batchSize") int batchSize);


    public void batchUpdateFee(List<OutCheckRecord> list);


}
