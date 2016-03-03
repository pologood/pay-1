package com.sogou.pay.wallet.service;

import com.sogou.pay.common.enums.OrderStatus;
import com.sogou.pay.wallet.enums.TranscStatus;
import com.sogou.pay.wallet.service.dao.AccountDao;
import com.sogou.pay.wallet.service.dao.TransactionDao;
import com.sogou.pay.wallet.service.entity.WalletTranscLucky;
import com.sogou.pay.wallet.service.entity.WalletTranscTopup;
import com.sogou.pay.wallet.service.entity.WalletTranscTransfer;
import com.sogou.pay.wallet.service.entity.WalletTranscWithdraw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiepeidong on 2016/2/24.
 */
@Component
public class TransactionService {

    @Autowired
    private TransactionDao transactionDao;

    public int insertTopup(int uid, BigDecimal money, String channel_code, String orderId){
        return transactionDao.insertTopup(uid, money, channel_code, orderId);
    }

    public int updateTopupStatus(String orderId, TranscStatus status){
        return transactionDao.updateTopupStatus(orderId, status);
    }

    public WalletTranscTopup queryTopup(int uid, Date startTime, Date endTime){
        return transactionDao.queryTopupByUid(uid, startTime, endTime);
    }

    public WalletTranscTopup queryTopup(String orderId){
        return transactionDao.queryTopupByOrderId(orderId);
    }

    public int insertWithdraw(int uid, BigDecimal money, String channel_code, String orderId){
        return transactionDao.insertWithdraw(uid, money, channel_code, orderId);
    }

    public int updateWithdrawStatus(String orderId, TranscStatus status){
        return transactionDao.updateWithdrawStatus(orderId, status);
    }

    public WalletTranscWithdraw queryWithdraw(int uid, Date startTime, Date endTime){
        return transactionDao.queryWithdrawByUid(uid, startTime, endTime);
    }
    public WalletTranscWithdraw queryWithdraw(String orderId){
        return transactionDao.queryWithdrawByOrderId(orderId);
    }

    public int insertTransfer(int uid, BigDecimal money, int payeeId){
        return transactionDao.insertTransfer(uid, money, payeeId);
    }
    public int updateTransferStatus(int tid, TranscStatus status){
        return transactionDao.updateTransferStatus(tid, status);
    }
    public WalletTranscTransfer queryTransfer(int uid, Date startTime, Date endTime){
        return transactionDao.queryTransferByUid(uid, startTime, endTime);
    }

    public int insertLucky(int uid, BigDecimal money, String luckyId, int operation){
        return transactionDao.insertLucky(uid, money, luckyId, operation);
    }

    public WalletTranscLucky queryLucky(int uid, Date startTime, Date endTime){
        return transactionDao.queryLuckyByUid(uid, startTime, endTime);
    }

    public int insertTopupResponse(int appid, String orderId, String payId, BigDecimal money, OrderStatus status){
        return transactionDao.insertTopupResponse(appid, orderId, payId, money, status);
    }
    public int insertWithdrawResponse(int appid, String orderId, String payId, BigDecimal money, OrderStatus status){
        return transactionDao.insertWithdrawResponse(appid, orderId, payId, money, status);
    }

}
