package com.sogou.pay.web.manager;

import com.sogou.pay.common.types.ResultBean;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.service.entity.PayChannel;
import com.sogou.pay.service.model.PayChannelAdapts;
import com.sogou.pay.service.service.PayChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ChannelAdaptManager {

  private static final Logger logger = LoggerFactory.getLogger(ChannelAdaptManager.class);

  @Autowired
  private PayChannelService channelService;

  public ResultBean<PayChannelAdapts> getChannelAdapts(Integer appId, Integer accessPlatform) {
    ResultBean<PayChannelAdapts> result = ResultBean.build();
    try {
      List<PayChannel> channelList = channelService.getCashierChannels(appId, accessPlatform);
      if (channelList.size() == 0) {
        logger.error("[getChannelAdapts] 收银台银行列表适配异常, appId={}, accessPlatform={}", appId, accessPlatform);
        result.withError(ResultStatus.PAY_CHANNEL_ADAPT_NOT_EXIST);
        return result;
      }
      result.success(buildAdaptData(channelList));
    } catch (Exception e) {
      logger.error("[getChannelAdapts] 收银台银行列表适配异常, appId={}, accessPlatform={}", appId, accessPlatform, e);
      result.withError(ResultStatus.SYSTEM_ERROR);
    }
    return result;
  }

  private PayChannelAdapts buildAdaptData(List<PayChannel> channelList) {
    PayChannelAdapts channelAdapts = new PayChannelAdapts();
    List<PayChannel> bankDebitList = new ArrayList<PayChannel>();// 网银支付银行列表(储蓄卡)
    List<PayChannel> bankCreditList = new ArrayList<PayChannel>();// 网银支付银行列表(信用卡)
    List<PayChannel> thirdPayList = new ArrayList<PayChannel>();
    List<PayChannel> qrCodeList = new ArrayList<PayChannel>();
    List<PayChannel> b2bList = new ArrayList<PayChannel>();
    for (PayChannel channel : channelList) {
      int type = channel.getChannelType();
      if (type == 1) {
        // 网银支付
        int bankCardType = channel.getBankCardType();
        if (bankCardType == 1) {
          // 储蓄卡
          bankDebitList.add(channel);
        } else if (bankCardType == 2) {
          // 信用卡
          bankCreditList.add(channel);
        } else if (bankCardType == 3) {
          // 不区分储蓄卡和信用卡
          bankDebitList.add(channel);
          bankCreditList.add(channel);
        }
      } else if (type == 2) {
        // 第三方支付
        int accessPlatform = channel.getAccessPlatform();
        if (accessPlatform == 4)
          qrCodeList.add(channel);
        else
          thirdPayList.add(channel);
      } else if (type == 3) {
        // B2B支付
        b2bList.add(channel);
      }
    }
    channelAdapts.setBankCreditList(bankCreditList);
    channelAdapts.setBankDebitList(bankDebitList);
    channelAdapts.setThirdPayList(thirdPayList);
    channelAdapts.setQrCodeList(qrCodeList);
    channelAdapts.setB2bList(b2bList);
    return channelAdapts;
  }
}
