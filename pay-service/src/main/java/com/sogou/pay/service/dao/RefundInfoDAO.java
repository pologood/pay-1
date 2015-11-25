package com.sogou.pay.service.dao;

import com.sogou.pay.service.entity.RefundInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by hjf on 15-3-2.
 */
@Repository
public interface RefundInfoDAO {

    public int insert(RefundInfo refundInfo);

    public RefundInfo selectByRefundId(String refundId);

    public List<RefundInfo> selectRefundByOrderIdAndTimeDesc(String orderId);

    public List<RefundInfo> selectByPayIdAndRefundStatus(@Param("payId") String payId, @Param("refundStatus") int refundStatus);

    /**
     * 更新旧退款状态为新状态
     *
     * @param refundId  退款订单号
     * @param newStatus 新状态
     * @param oldStatus 旧状态
     * @return 更新记录数
     */
    public int updateRefundStatusOldToNew(@Param("refundId") String refundId,
                                          @Param("newStatus") int newStatus,
                                          @Param("oldStatus") int oldStatus,
                                          @Param("errorCode") String errorCode,
                                          @Param("errorInfo") String errorInfo,
                                          @Param("resTime") Date resTime);

    /**
     * 更新退款状态为新状态，不检查旧状态
     *
     * @param refundId  退款订单号
     * @param newStatus 新状态
     * @return 更新记录数
     */
    public int updateRefundStatus(@Param("refundId") String refundId,
                                  @Param("newStatus") int newStatus,
                                  @Param("errorCode") String errorCode,
                                  @Param("errorInfo") String errorInfo,
                                  @Param("resTime") Date resTime);
}
