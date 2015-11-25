package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import org.apache.ibatis.annotations.Param;

/**
 * Created by qibaichao on 2015/3/23.
 */
public interface PayCheckFeeDiffService {

    /**
     *
     * @param checkDate
     * @param agencyCode
     */
    public void insertFeeDiff( String checkDate,  String agencyCode)throws ServiceException;

    /**
     * 清除
     *
     * @param checkDate
     * @param agencyCode
     * @return
     */
    public void delete( String checkDate,  String agencyCode)throws ServiceException;
}
