package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayCheckDayLog;

/**
 * @Author qibaichao
 * @ClassName PayCheckDayLogService
 * @Date 2015年2月16日
 * @Description:
 */
public interface PayCheckDayLogService {

    /**
     * 新增
     * @param payCheckDayLog
     * @throws ServiceException
     */
    public void insert(PayCheckDayLog payCheckDayLog)throws ServiceException;

    /**
     * 成功下载对账文件
     * @param checkDate
     * @param agencyCode
     * @return
     * @throws ServiceException
     */
    public boolean isSuccessDownload(String checkDate, String agencyCode) throws ServiceException;

    /**
     * 下载失败对账文件
     * @param checkDate
     * @param agencyCode
     * @return
     * @throws ServiceException
     */
    public boolean isFailDownload(String checkDate, String agencyCode) throws ServiceException;

    /**
     * @param checkDate
     * @param agencyCode
     * @return
     * @throws ServiceException
     */
    public PayCheckDayLog getByCheckDateAndAgency(String checkDate, String agencyCode) throws ServiceException;

    /**
     * @param id
     * @param status
     * @param version
     * @param remark
     * @throws ServiceException
     */
    public void updateStatus(long id, int status, int version, String remark) throws ServiceException;

}
