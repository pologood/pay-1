package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.manager.model.PayCheckUpdateModle;
import com.sogou.pay.service.dao.PayCheckDao;
import com.sogou.pay.service.entity.PayCheck;
import com.sogou.pay.service.payment.PayCheckService;
import com.sogou.pay.thirdpay.biz.model.OutCheckRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName PayCheckServiceImpl
 * @Date 2015年3月2日
 * @Description:
 */
@Service
public class PayCheckServiceImpl implements PayCheckService {

    @Autowired
    private PayCheckDao payCheckDao;

    @Override
    public void batchInsert(List<PayCheck> payCheckList) throws ServiceException {

        payCheckDao.batchInsert(payCheckList);
    }

    @Override
    public void batchUpdateStatus(List<PayCheckUpdateModle> list) throws ServiceException {

        payCheckDao.batchUpdateStatus(list);
    }

    @Override
    public void deleteInfo(String checkDate, String agencyCode, String merchantNo) throws ServiceException {

        payCheckDao.deleteInfo(checkDate, agencyCode, merchantNo);
    }

    @Override
    public PayCheck getByInstructIdAndBizCode(String instructId, int bizCode) throws ServiceException {

        return payCheckDao.getByInstructIdAndBizCode(instructId, bizCode);
    }

    @Override
    public List<Map<String, Object>> queryByMerAndDateAndBizCode(
            String checkDate, String agencyCode,
            int bizCode, int startRow, int batchSize) throws ServiceException {

        return payCheckDao.queryByMerAndDateAndBizCode(checkDate, agencyCode, bizCode, startRow, batchSize);
    }

    @Override
    public void batchUpdateFee(List<OutCheckRecord> list) throws ServiceException {

        payCheckDao.batchUpdateFee(list);
    }

}
