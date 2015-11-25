package com.sogou.pay.manager.payment;

import java.util.List;

import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultBean;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.manager.model.PayOrderQueryModel;
import com.sogou.pay.service.utils.AppXmlPacket;

/**
 * @Author huangguoqing
 * @ClassName PayTransManager
 * @Date 2015年6月3日
 * @Description:代付款manager
 */
public interface PayTransManager {

    public Result selectPayTransInfoByOutRef(List<String> recordList, int appId);

    public Result doProcess(PMap<String, String> params,List<String> payIdList);

    /**
     *  业务系统-->根据批次号查询
     * @param appId
     * @param batchNo
     * @return
     */
    public AppXmlPacket queryByBatchNo(String appId,String batchNo);

    /**
     *业务系统-->退票查询
     * @param startTime
     * @param endTime
     * @param recBankacc
     * @param recName
     * @return
     */
    public AppXmlPacket queryRefund(String startTime, String endTime, String recBankacc, String recName);
}
