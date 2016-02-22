package com.sogou.pay.thirdpay.api;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.PMap;

/**
 * 批量银行代付
 *
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/06/02 10:04
 */
public interface BankPayApi {
    /**
     * 1.批量银行代付提交接口
     */
    public ResultMap<String> paySubmit(PMap params);

    /**
     * 2.批量银行代付查询接口
     */
    public ResultMap<String> payQuery(PMap params);

    /**
     * 3.退票查询接口
     */
    public ResultMap<String> refundQuery(PMap params);

    /**
     * 4.对账单下载接口
     */
    public ResultMap<String> downloadBill(PMap params);
}
