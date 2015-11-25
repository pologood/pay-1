package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayCheckResult;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author qibaichao
 * @ClassName PayCheckResultDao
 * @Date 2015年2月16日
 * @Description:
 */
public interface PayCheckResultService {

    /**
     * 新增
     *
     * @param checkDate
     * @throws ServiceException
     */
    public void insert(String checkDate, String agencyCode) throws ServiceException;

    /**
     * 清除
     *
     * @param checkDate
     * @param agencyCode
     * @throws ServiceException
     */
    public void delete(String checkDate, String agencyCode) throws ServiceException;

    /**
     * 根据ID更新
     *
     * @param id
     * @param status
     * @throws ServiceException
     */
    public void updateStatus(long id, int status) throws ServiceException;

    /**
     * @param checkDate
     * @param agencyCode
     * @return
     * @throws ServiceException
     */
    public int queryCountByDateAndAgency(String checkDate, String agencyCode) throws ServiceException;

    /**
     * 根据对账日期,机构编码查询
     *
     * @param checkDate
     * @param agencyCode
     * @return
     * @throws ServiceException
     */
    public List<PayCheckResult> queryByDateAndAgency(String checkDate, String agencyCode) throws ServiceException;

}
