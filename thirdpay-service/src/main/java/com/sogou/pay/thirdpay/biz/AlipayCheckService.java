package com.sogou.pay.thirdpay.biz;


import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.thirdpay.biz.enums.CheckType;

/**
 * @Author qibaichao
 * @ClassName AlipayCheckApi
 * @Date 2015年2月16日
 * @Description: 支付宝对账数据获取接口
 */
public interface AlipayCheckService {

    public ResultMap doQuery(String merchantNo,CheckType checkType,  String startTime, String endTime, String pageNo, String pageSize, String key) throws ServiceException;
}
