package com.sogou.pay.thirdpay.biz;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.modle.WechatCheckRequest;
import com.sogou.pay.thirdpay.biz.modle.WechatCheckResponse;

/**
 * Created by qibaichao on 2015/3/4.
 */
public interface WechatCheckService {
    /**
     *
     * @param appId 公众账号ID
     * @param merchantNo 商户号
     * @param checkType 对账单类型
     * @param checkDate 对账日期
     * @return
     */
    public ResultMap doQuery(String appId,String merchantNo,CheckType checkType,String checkDate,String key)throws ServiceException;
}
