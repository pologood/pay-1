package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.PayResDetailDao;
import com.sogou.pay.service.entity.PayResDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class PayResDetailService {

  @Autowired
  private PayResDetailDao payResDetailDao;


  public PayResDetail selectByAgencyOrderId(String agencyOrderId) throws ServiceException {
    return payResDetailDao.selectByAgencyOrderId(agencyOrderId);
  }


  public int insertPayResDetail(PayResDetail payResDetail) throws ServiceException {
    return payResDetailDao.insertPayResDetail(payResDetail);
  }


  public PayResDetail selectPayResById(String payResId) throws ServiceException {
    return payResDetailDao.selectPayResById(payResId);
  }


  public int updatePayResPayfeeById(BigDecimal payFee, BigDecimal feeRate, String payResId) throws ServiceException {
    return payResDetailDao.updatePayResPayfeeById(payFee, feeRate, payResId);

  }
}
