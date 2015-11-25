package com.sogou.pay.thirdpay.biz;


import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.thirdpay.biz.enums.CheckType;
import com.sogou.pay.thirdpay.biz.modle.TenpayCheckRequest;
import com.sogou.pay.thirdpay.biz.modle.TenpayCheckResponse;


/**
 * @Author qibaichao
 * @ClassName TenpayClearService
 * @Date 2015年2月16日
 * @Description:TENPAY 财付通对账数据获取接口
 */
public interface TenpayCheckService {

    public ResultMap doQuery(String merchantNo,CheckType checkType,String checkDate, String key) throws ServiceException;
}
