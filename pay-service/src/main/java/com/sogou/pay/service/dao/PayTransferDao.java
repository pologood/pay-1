package com.sogou.pay.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sogou.pay.service.entity.PayTransfer;

/**
 * 代付单
 */
@Repository
public interface PayTransferDao {

    public int insert(PayTransfer PayTransfer);

    public int batchInsert(List<PayTransfer> list);

    public int updateStatusByBatchNo(@Param("appId") String appId,
                                     @Param("batchNo") String batchNo,
                                     @Param("payStatus") int payStatus);

    public int updateStatusById(@Param("id") String id,
                                @Param("payStatus") int payStatus,
                                @Param("resultDesc") String resultDesc);

    public int updateStatusBySerialNo(@Param("serialNo") String serialNo,
                                      @Param("payStatus") int payStatus,
                                      @Param("resultDesc") String resultDesc);

    public List<PayTransfer> queryByBatchNo(@Param("appId") String appId,
                                            @Param("batchNo") String batchNo);

    public List<PayTransfer> queryByBatchNoAndStatus(@Param("batchNo") String batchNo,
                                                     @Param("payStatus") int payStatus);

    public PayTransfer queryBySerialNo(@Param("serialNo") String serialNo);

    public List<PayTransfer> queryRefund(@Param("startTime") String startTime,
                                         @Param("endTime") String endTime,
                                         @Param("recBankacc") String recBankacc,
                                         @Param("recName") String recName);

    public List<PayTransfer> queryByOutRefAndAppId(@Param("list") List<String> list,
                                                   @Param("appId") int appId);

}
