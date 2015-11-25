package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.manager.model.PayCheckUpdateModle;
import com.sogou.pay.service.entity.PayCheckWaiting;

import java.util.List;
import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName PayCheckWaitingService
 * @Date 2015年3月2日
 * @Description:
 */
public interface PayCheckWaitingService {
    /**
     * 插入
     * @param payCheckWaiting
     * @throws ServiceException
     */
    public void insert(PayCheckWaiting payCheckWaiting)throws ServiceException;

    /**
     * 根据流水号查询
     * @param instructId
     * @return
     * @throws ServiceException
     */
    public PayCheckWaiting getByInstructId(String instructId)throws ServiceException;

    /**
     * 批量修改
     * @param list
     * @throws ServiceException
     */
    public void batchUpdateStatus(List<PayCheckUpdateModle> list)throws ServiceException;

    /**
     * 求总金额 总数
     * @param checkDate
     * @param agencyCode
     * @param bizCode
     * @return
     * @throws ServiceException
     */
    public Map<String, Object> sumAmtAndNum(String checkDate, String agencyCode, int bizCode)throws ServiceException;

    public Map<String, Object> sumFeeAmtAndNum(String checkDate, String agencyCode, int bizCode)throws ServiceException;

}
