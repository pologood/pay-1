package com.sogou.pay.service.dao;

import com.sogou.pay.service.entity.PayTransferLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by qibaichao on 2015/6/1.
 * 代付任务日志
 */
@Repository
public interface PayTransferLogDao {

    public int insert(PayTransferLog payTransferLog);

    public int batchInsert(List<PayTransferLog> list);

    public int queryNumByStatusAndBatchNo(@Param("status") int status, @Param("batchNo") String batchNo);

}
