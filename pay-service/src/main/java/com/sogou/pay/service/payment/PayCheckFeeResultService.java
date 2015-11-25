package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayCheckFeeResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by qibaichao on 2015/3/20.
 */
public interface PayCheckFeeResultService {

    /**
     * 新增
     *
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public void insert(String checkDate, String agencyCode) throws ServiceException;

    /**
     * 清除
     *
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public void delete(String checkDate, String agencyCode) throws ServiceException;

    /**
     * @param id
     * @return
     * @Author qibaichao
     * @MethodName updateFeeAndNumById
     * @Date 2015年3月2日
     * @Description:根据ID更新金额
     */
    public void updateFeeStatus(long id, int status) throws ServiceException;

    /**
     * 根据对账日期,机构编码查询
     *
     * @param checkDate
     * @param agencyCode
     * @return
     * @throws ServiceException
     */
    public List<PayCheckFeeResult> queryByDateAndAgency(String checkDate, String agencyCode) throws ServiceException;
}
