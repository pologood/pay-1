package com.sogou.pay.manager.notify;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;

/**
 * User: hujunfei
 * Date: 2015-03-04 17:48
 */
public interface RefundNotifyManager {

    /**
     * 处理支付宝回调结果
     *
     * @param params 支付宝回调参数
     * @return 处理结果
     */
    public Result handleAliNotify(PMap<String, Object> params);

    /**
     * 处理财付通回调结果
     *
     * @param params 财付通回调参数
     * @return 处理结果
     */
    public Result handleTenNotify(PMap<String, Object> params);

    /**
     * 处理回调应用请求，封装发送至队列
     *
     * @param result
     * @return 处理结果
     */
    public Result notifyApp(ResultMap result);

    /**
     * 供对账做退款成功补偿（产生原因：退款异步回调出现没有回调等问题）
     *
     * @param
     * refundId 支付中心退款单id
     * thirdRefundId第三方退款单id
     * @return 处理结果
     */
    public Result repairRefundOrder(String refundId, String thirdRefundId);
}
