package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.service.dao.RefundInfoDAO;
import com.sogou.pay.service.entity.RefundInfo;
import com.sogou.pay.service.enums.RefundStatus;
import com.sogou.pay.service.payment.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * User: hujunfei
 * Date: 2015-03-03 18:51
 */
@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundInfoDAO refundInfoDAO;

    @Override
    public int insertRefundInfo(RefundInfo refundInfo) throws ServiceException {
        try {
            return refundInfoDAO.insert(refundInfo);
        } catch (DuplicateKeyException dke) {
            return 0;
        } catch (Exception e) {
            throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
        }
    }

    @Override
    public RefundInfo selectByRefundId(String refundId) throws ServiceException {
        try {
            return refundInfoDAO.selectByRefundId(refundId);
        } catch (Exception e) {
            throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
        }
    }

    @Override
    public List<RefundInfo> selectByPayId(String payId) throws ServiceException {
        try {
            return refundInfoDAO.selectByPayId(payId);
        } catch (Exception e) {
            throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
        }
    }


    @Override
    public List<RefundInfo> selectByPayIdAndRefundStatus(String payId, int refundStatus) throws ServiceException {
        try {
            return refundInfoDAO.selectByPayIdAndRefundStatus(payId, refundStatus);
        } catch (Exception e) {
            throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
        }
    }

    @Override
    public int updateRefundSuccess(String refundId, Date resTime) throws ServiceException {
        try {
            return refundInfoDAO.updateRefundStatus(refundId, RefundStatus.SUCCESS.getValue(), null, null, resTime);
        } catch (Exception e) {
            throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
        }
    }


    @Override
    public int updateRefundFail(String refundId, String errorCode, String errorInfo) throws ServiceException {
        try {
            return refundInfoDAO.updateRefundStatus(refundId, RefundStatus.FAIL.getValue(), errorCode, errorInfo, null);
        } catch (Exception e) {
            throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
        }
    }

    @Override
    public List<RefundInfo> selectRefundByOrderId(String orderId) throws ServiceException {
        try {
            return refundInfoDAO.selectRefundByOrderId(orderId);
        } catch (Exception e) {
            throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
        }
    }
}
