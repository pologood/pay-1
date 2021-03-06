package com.sogou.pay.service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.service.dao.PayOrderRelationDao;
import com.sogou.pay.service.entity.PayOrderRelation;


@Service
public class PayOrderRelationService {

  @Autowired
  private PayOrderRelationDao payOrderRelationDao;

  /**
   * 插入支付单与支付流水关联信息
   *
   * @param payOrderRelation 支付单与支付流水关联实体
   * @return 返回值
   */

  public int insertPayOrderRelation(PayOrderRelation payOrderRelation) throws ServiceException {
    try {
      return payOrderRelationDao.insertPayOrderRelation(payOrderRelation);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServiceException(e, ResultStatus.SYSTEM_DB_ERROR);
    }
  }

  /**
   * 查询支付单与支付流水关联信息
   *
   * @param payOrderRelation 条件
   * @return 关联信息List
   */
  public List<PayOrderRelation> selectPayOrderRelation(PayOrderRelation payOrderRelation) {
    return payOrderRelationDao.selectPayOrderRelation(payOrderRelation);
  }


  public String selectPayOrderId(String payDetailId) {
    return payOrderRelationDao.selectPayOrderId(payDetailId);
  }


  public int updatePayOrderRelation(int status, String payDetailId) {
    return payOrderRelationDao.updatePayOrderRelation(status, payDetailId);
  }
}
