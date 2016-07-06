package com.sogou.pay.service.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.service.dao.PayFeeDao;
import com.sogou.pay.service.entity.PayFee;

@Service
public class PayFeeService {

  private static final Logger logger = LoggerFactory.getLogger(PayFeeService.class);
  @Autowired
  private PayFeeDao payFeeDao;

  public PMap<String, BigDecimal> getPayFee(BigDecimal payAmount, String merchantNo, Integer payFeeType, Integer accessPlatform) throws ServiceException {
    logger.info("[getPayFee] 计算商户手续费开始, merchantNo={}, payFeeType={}, accessPlatform={}", merchantNo, payFeeType, accessPlatform);
    BigDecimal fee = new BigDecimal(-1);
    BigDecimal feeRate = new BigDecimal(0);
    PayFee feeInfo = payFeeDao.getPayFee(merchantNo, payFeeType, accessPlatform);
    if (feeInfo == null) {
      logger.error("[getPayFee] 无手续费方案, merchantNo={}, payFeeType={}, accessPlatform={}", merchantNo, payFeeType, accessPlatform);
      return null;
    }
    if (feeInfo.getFeeType() == PayFee.FEETYPE_FEERATE) {
      //按比例计算手续费
//      int scaleLen = 2;//默认保留2位小数
//      if (AgencyCode.getValue(feeInfo.getAgencyCode()) == AgencyCode.WECHAT)
//        scaleLen = 5;
      feeRate = feeInfo.getFeeRate();
      fee = payAmount.multiply(feeRate).setScale(2, BigDecimal.ROUND_HALF_UP);
      if (feeInfo.getLowerLimit().compareTo(BigDecimal.valueOf(-1).setScale(2)) != 0) {
        //有保底值,小于保底值则取保底值
        BigDecimal lower = feeInfo.getLowerLimit().setScale(2, BigDecimal.ROUND_HALF_UP);
        fee = fee.compareTo(lower) < 0 ? lower : fee;
      }
      if (feeInfo.getUpperLimit().compareTo(BigDecimal.valueOf(-1).setScale(2)) != 0) {
        //有封顶值，大于封顶值则取封顶值
        BigDecimal upper = feeInfo.getUpperLimit().setScale(2, BigDecimal.ROUND_HALF_UP);
        fee = fee.compareTo(upper) < 0 ? fee : upper;
      }
    } else if (feeInfo.getFeeType() == PayFee.FEETYPE_FEE) {
      //按定额计算手续费
      fee = feeInfo.getFee();
    }
    PMap<String, BigDecimal> result = new PMap<>();
    result.put("fee", fee);
    result.put("feeRate", feeRate);
    logger.info("[getPayFee] 手续费计算完毕, fee={}, feeRate={}", fee, feeRate);
    return result;
  }
}

