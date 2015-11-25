package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayCheckDiff;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by qibaichao on 2015/3/23.
 */
public interface PayCheckDiffService {

    /**
     * 插入金额差异
     *
     * @param checkDate
     * @param agencyCode
     */
    public void insertAmtDiff(String checkDate,  String agencyCode)throws ServiceException;

    /**
     *插入对方多单差异
     *
     * @param checkDate
     * @param agencyCode
     */
    public void insertOutMoreDiff( String checkDate,  String agencyCode)throws ServiceException;

    /**
     * 插入对方漏单单差异
     *
     * @param checkDate
     * @param agencyCode
     */
    public void insertOutLessDiff( String checkDate,  String agencyCode)throws ServiceException;

    /**
     * 清除
     *
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public void delete( String checkDate,  String agencyCode)throws ServiceException;


    public int selectUnResolvedCount( )throws ServiceException;


    public List<PayCheckDiff> selectUnResolvedList( )throws ServiceException;

    /**
     *
     * @param id
     * @param status
     * @param remark
     * @throws ServiceException
     */
    public void updateStatus(Long id,int status ,String remark)throws ServiceException;
}
