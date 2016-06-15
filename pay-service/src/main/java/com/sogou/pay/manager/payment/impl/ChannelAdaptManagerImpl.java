package com.sogou.pay.manager.payment.impl;

import java.util.ArrayList;
import java.util.List;

import com.sogou.pay.manager.model.PayChannelAdapts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.ResultBean;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.manager.model.PayChannelAdapt;
import com.sogou.pay.manager.payment.ChannelAdaptManager;
import com.sogou.pay.service.payment.PayChannelAdaptService;

/**
 * Created by wujingpan on 2015/3/6.
 */
@Component
public class ChannelAdaptManagerImpl implements ChannelAdaptManager {

  private static final Logger logger = LoggerFactory.getLogger(ChannelAdaptManagerImpl.class);

  @Autowired
  private PayChannelAdaptService adaptService;

  @Override
  public ResultBean<PayChannelAdapts> getChannelAdapts(Integer appId, Integer accessPlatform) {
    ResultBean<PayChannelAdapts> result = ResultBean.build();
    try {
      List<PayChannelAdapt> adaptList = adaptService.getChannelAdaptList(appId, accessPlatform);
      if (null == adaptList || adaptList.size() == 0) {
        result.withError(ResultStatus.PAY_CHANNEL_ADAPT_NOT_EXIST);
        return result;
      }
      result.success(buildAdaptData(adaptList));
    } catch (ServiceException e) {
      logger.error("收银台银行列表适配异常，业务平台编码:" + appId + "接入平台：" + accessPlatform);
      result.withError(ResultStatus.SYSTEM_ERROR);
    }
    return result;
  }

  private PayChannelAdapts buildAdaptData(List<PayChannelAdapt> adaptList) {
    PayChannelAdapts channelBean = new PayChannelAdapts();
    List<PayChannelAdapt> bankDebitList = new ArrayList<PayChannelAdapt>();// 网银支付银行列表(储蓄卡)
    List<PayChannelAdapt> bankCreditList = new ArrayList<PayChannelAdapt>();// 网银支付银行列表(信用卡)
    List<PayChannelAdapt> thirdPayList = new ArrayList<PayChannelAdapt>();
    List<PayChannelAdapt> qrCodeList = new ArrayList<PayChannelAdapt>();
    List<PayChannelAdapt> b2bList = new ArrayList<PayChannelAdapt>();
    for (PayChannelAdapt adapt : adaptList) {
      int type = adapt.getChannelType();
      if (type == 1) {
        // 网银支付
        int bankCardType = adapt.getBankCardType();
        if (bankCardType == 1) {
          // 储蓄卡
          bankDebitList.add(adapt);
        } else if (bankCardType == 2) {
          // 信用卡
          bankCreditList.add(adapt);
        } else if (bankCardType == 3) {
          // 不区分储蓄卡和信用卡
          bankDebitList.add(adapt);
          bankCreditList.add(adapt);
        }
      } else if (type == 2) {
        // 第三方支付
        int accessPlatform = adapt.getAccessPlatform();
        if (accessPlatform == 4)
          qrCodeList.add(adapt);
        else
          thirdPayList.add(adapt);
      } else if (type == 3) {
        // B2B支付
        b2bList.add(adapt);
      }
    }
    channelBean.setBankCreditList(bankCreditList);
    channelBean.setBankDebitList(bankDebitList);
    channelBean.setThirdPayList(thirdPayList);
    channelBean.setQrCodeList(qrCodeList);
    channelBean.setB2bList(b2bList);
    return channelBean;
  }
}
