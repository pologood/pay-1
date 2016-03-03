package com.sogou.pay.wallet.service.dao;

import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.wallet.enums.TranscStatus;
import com.sogou.pay.wallet.service.entity.WalletTranscLucky;
import com.sogou.pay.wallet.service.entity.WalletTranscTopup;
import com.sogou.pay.wallet.service.entity.WalletTranscTransfer;
import com.sogou.pay.wallet.service.entity.WalletTranscWithdraw;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiepeidong on 2016/2/29.
 */
@Repository
public interface TransactionDao {

    public int insertTopup(@Param("uid") int uid, @Param("money") BigDecimal money, @Param("channel_code") String channel_code, @Param("orderId") String orderId);

    public int updateTopupStatus(@Param("orderId") String orderId, @Param("status") TranscStatus status);

    public WalletTranscTopup queryTopupByUid(@Param("uid") int uid, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    public WalletTranscTopup queryTopupByOrderId(@Param("orderId") String orderId);

    public int insertWithdraw(@Param("uid") int uid, @Param("money") BigDecimal money, @Param("channel_code") String channel_code, @Param("orderId") String orderId);

    public int updateWithdrawStatus(@Param("orderId") String orderId, @Param("status") TranscStatus status);

    public WalletTranscWithdraw queryWithdrawByUid(@Param("uid") int uid, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    public WalletTranscWithdraw queryWithdrawByOrderId(@Param("orderId") String orderId);

    public int insertTransfer(@Param("uid") int uid, @Param("money") BigDecimal money, @Param("payeeId") int payeeId);

    public int updateTransferStatus(@Param("tid") int tid, @Param("status") TranscStatus status);

    public WalletTranscTransfer queryTransferByUid(@Param("uid") int uid, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    public int insertLucky(@Param("uid") int uid, @Param("money") BigDecimal money, @Param("luckyId") String luckyId, @Param("operation") int operation);

    public WalletTranscLucky queryLuckyByUid(@Param("uid") int uid, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    public int insertTopupResponse(@Param("appid") int appid, @Param("orderId") String orderId, @Param("payId") String payId, @Param("money") BigDecimal money, @Param("status") OrderStatus status);

    public int insertWithdrawResponse(@Param("appid") int appid, @Param("orderId") String orderId, @Param("payId") String payId, @Param("money") BigDecimal money, @Param("status") OrderStatus status);


}
