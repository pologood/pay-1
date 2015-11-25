package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.PayResDetail;

import java.math.BigDecimal;

/**
 * User: Liwei
 * Date: 15/3/5
 * Time: 上午10:25
 * Description:
 */
public interface PayResDetailService {

    /**
     * 根据支付机构流水号查询
     *
     * @param agencyOrderId 支付机构流水号
     * @return 响应流水信息
     */
    public PayResDetail selectByAgencyOrderId(String agencyOrderId) throws ServiceException;

    /**
     * 插入响应流水信息
     *
     * @param payResDetail 支付流水实体
     * @return 返回值
     */
    public int insertPayResDetail(PayResDetail payResDetail) throws ServiceException;

    /**
     * 根据响应流水ID查询响应流水信息
     *
     * @param payDetailId 响应流水ID
     * @return 响应流水信息
     */
    public PayResDetail selectPayResById(String payDetailId) throws ServiceException;

    /**
     * 根据响应流水ID更新手续费
     *
     * @param payDetailId 响应流水ID
     * @return
     */
    public int updatePayResPayfeeById(BigDecimal payFee,BigDecimal feeRate, String payDetailId) throws ServiceException;

}
