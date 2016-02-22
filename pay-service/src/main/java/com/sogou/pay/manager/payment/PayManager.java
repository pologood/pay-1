package com.sogou.pay.manager.payment;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;

/**
 * @Author	huangguoqing 
 * @ClassName	PayManager 
 * @Date	2015年2月28日 
 * @Description:支付请求服务
 */
@SuppressWarnings("all")
public interface PayManager {
    /**
     * @Author	huangguoqing 
     * @MethodName	confirmPay 
     * @param params 支付请求参数
     * @return result
     * @Date	2015年2月28日
     * @Description:确认支付
     */
    public ResultMap confirmPay(PMap params);
    
    /**
     * @Author	huangguoqing 
     * @MethodName	checkParams
     * @param params 支付请求参数
     * @return result
     * @Date	2015年2月28日
     * @Description:请求参数业务校验
     */
    public ResultMap checkParams(PMap params);

    /**
     * @param appId 业务平台编码
     * @return 签名KEY
     * @Date 2015年3月2日
     * @Description:根据业务平台编码获得该业务平台签名KEY
     */
    public ResultMap getSignKey(int appId);

    /**
     * @param params 支付请求参数
     * @return 插入是否成功
     * @Date 2015年3月3日
     * @Description: 插入支付单信息
     */
    public ResultMap insertPayOrder(PMap params);

    /**
     * @param params 支付请求参数
     * @return 支付网关所需要的参数
     * @Date 2015年3月4日
     * @Description: 根据请求参数组装支付网关所需要的参数
     */
    public ResultMap getPayGateParams(PMap params);

    /**
     * @Author	huangguoqing 
     * @MethodName	selectPayOrderInfoByOrderId 
     * @param orderId 订单ID
     * @param appId 业务平台ID
     * @return 支付单信息
     * @Date	2015年3月17日
     * @Description:根据订单ID查询支付单信息
     */
    public ResultMap selectPayOrderInfoByOrderId(String orderId,String appId);
    
    /**
     * @Author	huangguoqing 
     * @MethodName	selectPayOrderInfoById
     * @param map 
     * @return 校验结果
     * @Date	2015年3月19日
     * @Description:校验订单信息
     */
    public ResultMap checkPayOrderInfo(PMap map);
}
