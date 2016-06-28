package com.sogou.pay.service.payment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.service.dao.PayReqDetailDao;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.entity.PayReqDetail;


@Service
public class PayReqDetailService {

  @Autowired
  private PayReqDetailDao payReqDetailDao;

  /**
   * 插入支付单信息
   *
   * @param payReqDetail 支付单实体
   * @return 是否成功标识
   */

  public int insertPayReqDetail(PayReqDetail payReqDetail) throws ServiceException {
    try {
      return payReqDetailDao.insertPayReqDetail(payReqDetail);
    } catch (Exception e) {
      throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
    }
  }

  /**
   * 根据ID查询支付单流水信息
   *
   * @param payReqId 支付流水ID
   * @return 返回值
   */
  public PayReqDetail selectPayReqDetailById(String payReqId) {
    return payReqDetailDao.selectPayReqDetailById(payReqId);
  }


  public List<PayReqDetail> selectPayReqByReqIdList(List<PayOrderRelation> relationList)
          throws ServiceException {
    return payReqDetailDao.selectPayReqByReqIdList(relationList);
  }
}
