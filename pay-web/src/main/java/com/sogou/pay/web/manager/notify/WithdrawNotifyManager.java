package com.sogou.pay.web.manager.notify;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.service.entity.PayCheckWaiting;
import com.sogou.pay.service.payment.PayCheckWaitingService;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

/**
 * @Author xiepeidong
 * @ClassName WithdrawNotifyManager
 * @Date 2015年12月15日
 * @Description:
 */
@Component
public class WithdrawNotifyManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawNotifyManager.class);

    @Autowired
    private PayCheckWaitingService payCheckWaitingService;

    public ResultMap doProcess(HashMap<String,Object> params){
        ResultMap result = ResultMap.build();
        PayCheckWaiting payCheckWaiting = new PayCheckWaiting();

        try {
            payCheckWaiting.setInstructId((String)params.get("instructId"));//请求流水号
            payCheckWaiting.setOutOrderId((String)params.get("outOrderId"));//第三方流水号
            payCheckWaiting.setBizCode(CheckType.WITHDRAW.getValue());//业务代码；提现
            payCheckWaiting.setOutTransTime((Date) params.get("outTransTime"));//交易时间
            payCheckWaiting.setBizAmt((BigDecimal) params.get("bizAmt"));//交易金额
            payCheckWaiting.setFeeRate(BigDecimal.valueOf(0.0));//费率
            payCheckWaiting.setCommissionFeeAmt(BigDecimal.valueOf(0.0));//交易手续费
            payCheckWaiting.setAccessPlatform((int)params.get("accessPlatform"));//接入平台
            payCheckWaiting.setAppId((int)params.get("appId"));//应用id
            String date = DateUtil.formatCompactDate(new Date());
            payCheckWaiting.setCheckDate(date);//对账日期
            payCheckWaiting.setAgencyCode((String)params.get("agencyCode"));//机构编码
            payCheckWaiting.setMerchantNo((String)params.get("merchantNo"));//商户号
            payCheckWaiting.setPayType((int)params.get("payType"));//付款方式
            payCheckWaiting.setBankCode((String)params.get("bankCode"));//支付渠道

            payCheckWaitingService.insert(payCheckWaiting);
        }catch (Exception e){
            LOGGER.error("[doProcess] failed, {}", e);
            result.withError(ResultStatus.HANDLE_THIRD_NOTIFY_ERROR);
        }
        return result;
    }


}
