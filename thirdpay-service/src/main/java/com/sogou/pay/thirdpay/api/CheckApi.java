package com.sogou.pay.thirdpay.api;


import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.thirdpay.biz.modle.*;

/**
 * Created by qibaichao on 2015/3/4.
 * 对账获取第三方支付机构对账文件
 */
public interface CheckApi {

    /**
     * 支付宝对账数据获取
     *
     * @param params
     * @return
     */
    public ResultMap doQueryAlipay(PMap params);

    /**
     * 财付通对账数据获取
     *
     * @param params
     * @return
     */
    public ResultMap doQueryTenpay(PMap params);

    /**
     * 微信对账数据获取
     *
     * @param params
     * @return
     */
    public ResultMap doQueryWechat(PMap params);
    /**
     * 快钱支付对账数据获取
     *
     * @param params
     * @return
     */
    public ResultMap doPayQueryBill99(PMap params);
    /**
     * 快钱退款对账数据获取
     *
     * @param params
     * @return
     */
    public ResultMap doRefundQueryBill99(PMap params);

}
