package com.sogou.pay.service.payment.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.service.dao.PayFeeDao;
import com.sogou.pay.service.entity.PayFee;
import com.sogou.pay.service.payment.PayFeeService;
import com.sogou.pay.service.utils.Constant;

/**
 * Created by wujingpan on 2015/3/5.
 */
@Service
public class PayFeeServiceImpl implements PayFeeService {

    private static final Logger logger = LoggerFactory.getLogger(PayFeeServiceImpl.class);
    @Autowired
    private PayFeeDao payFeeDao;

    @Override
    public PMap<String,BigDecimal> getPayFee(BigDecimal payAmount,String merchantNo, Integer payFeeType,Integer accessPlatform) throws ServiceException {
        PMap<String,BigDecimal> map = new PMap<String,BigDecimal>();
        logger.info("计算商户手续费开始 merchantNo:"+merchantNo +"payFeeType :"+payFeeType+"accessPlatform:"+accessPlatform);
        BigDecimal fee = new BigDecimal(-1);
        BigDecimal feeRate = new BigDecimal(0);
        PayFee feeInfo = payFeeDao.getPayFee(merchantNo,payFeeType,accessPlatform);

        if(feeInfo == null){
            logger.error("无手续费方案,merchantNo:"+merchantNo+"payFeeType:"+payFeeType+"accessPlatform:"+accessPlatform);
            return map;
        }
        /* 按定额方式获取手续费 */
        if(feeInfo.getFeeType() == 2){
            fee = feeInfo.getFee();
        }
        int scaleLen = 2;//默认保留2位小数
        if(feeInfo.getAgencyCode().equals(Constant.TENPAY))
            scaleLen = 5;
        //按 比例
        if(feeInfo.getFeeType() == 1){
            feeRate = feeInfo.getFeeRate();
            fee = payAmount.multiply(feeRate).setScale(scaleLen, BigDecimal.ROUND_HALF_UP);
            if(!feeInfo.getLowerLimit().equals(BigDecimal.valueOf(-1).setScale(2))){
                //有保底值,小于保底值则取保底值
                BigDecimal lower = feeInfo.getLowerLimit().setScale(2, BigDecimal.ROUND_HALF_UP);
                fee = fee.compareTo(lower) < 0 ? lower :fee;
            }
            if(!feeInfo.getUpperLimit().equals(BigDecimal.valueOf(-1).setScale(2))){
                //有封顶值，大于封顶值则取封顶值
                BigDecimal upper = feeInfo.getUpperLimit().setScale(2, BigDecimal.ROUND_HALF_UP);
                fee = fee.compareTo(upper) < 0 ? fee : upper;
            }
        }
        map.put("fee", fee);
        map.put("feeRate",feeRate);
        logger.info("手续费计算完毕，totalFee:"+fee+",feeRate="+feeRate);
        return map;
    }
}

