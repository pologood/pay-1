package com.sogou.pay.service.dao;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayTransferBatch;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by qibaichao on 2015/6/1.
 * 代付任务
 */
@Repository
public interface PayTransferBatchDao {

    public int insert(PayTransferBatch payTransferBatch);

    public PayTransferBatch queryByBatchNo(@Param("appId")String appId, @Param("batchNo")String batchNo);

    public List<PayTransferBatch> queryByTradeStatus(@Param("tradeState") int tradeState);

    public List<PayTransferBatch> queryByNotifyFlag(@Param("notifyFlag")int notifyFlag,@Param("notifyDate")String notifyDate);

    public int updateTradeStatusByBatchNo(@Param("appId")String appId, @Param("batchNo")String batchNo,@Param("tradeState") int tradeState, @Param("resultDesc")String resultDesc);

    public int updateTransferBatch(PayTransferBatch payTransferBatch);

    public int updateNotifyFlagByBatchNo(@Param("appId")String appId, @Param("batchNo")String batchNo, @Param("notifyFlag")int notifyFlag);

    public PayTransferBatch queryByYurref(String Yurref);

}
