package com.sogou.pay.manager.payment.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultBean;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.manager.model.ChannelAdaptModel;
import com.sogou.pay.manager.model.CommonAdaptModel;
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
    public ResultBean<ChannelAdaptModel> getChannelAdapt(Integer appId, Integer accessPlatform) {
        ResultBean<ChannelAdaptModel> result = ResultBean.build();
        try {
            List<CommonAdaptModel> adaptList = adaptService.getChannelAdaptList(appId, accessPlatform);
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

	private ChannelAdaptModel buildAdaptData(List<CommonAdaptModel> adaptList) {
		ChannelAdaptModel channelBean = new ChannelAdaptModel();
		List<CommonAdaptModel> commonPay4DebitList = new ArrayList<CommonAdaptModel>();// 网银支付银行列表(储蓄卡)
		List<CommonAdaptModel> commonPay4CreditList = new ArrayList<CommonAdaptModel>();// 网银支付银行列表(信用卡)
		List<CommonAdaptModel> payOrgList = new ArrayList<CommonAdaptModel>();
		List<CommonAdaptModel> scanCodeList = new ArrayList<CommonAdaptModel>();
		List<CommonAdaptModel> b2bList = new ArrayList<CommonAdaptModel>();
		for (CommonAdaptModel adapt : adaptList) {
			int type = adapt.getChannelType();
			if (type == 1) {
			// 网银支付
                int bankCardType = adapt.getBankCodeType();
				if (bankCardType == 3) {
				// 不区分储蓄卡和信用卡
					commonPay4DebitList.add(adapt);
					commonPay4CreditList.add(adapt);
				} else if (bankCardType == 1) {
				// 储蓄卡
					commonPay4DebitList.add(adapt);
				} else if (bankCardType == 2) {
				// 信用卡
					commonPay4CreditList.add(adapt);
				} else {
					// do nothing.....
				}
			}
			if (type == 2) {
			// 第三方支付
				payOrgList.add(adapt);
			}
			if (type == 3) {
			// 扫码支付
				scanCodeList.add(adapt);
			}
			if (type == 4) {
			//B2B支付
			    b2bList.add(adapt);
			}

		}
		channelBean.setCommonPay4CreditList(commonPay4CreditList);
		channelBean.setCommonPay4DebitList(commonPay4DebitList);
		channelBean.setPayOrgList(payOrgList);
		channelBean.setScanCodeList(scanCodeList);
		channelBean.setB2bList(b2bList);
		return channelBean;
	}
}
