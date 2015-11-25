package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.manager.model.PayCheckUpdateModle;
import com.sogou.pay.service.entity.PayCheck;
import com.sogou.pay.thirdpay.biz.modle.OutCheckRecord;

import java.util.List;
import java.util.Map;

public interface PayCheckService {

    /**
     * 批量插入
     *
     * @param payCheckList
     * @throws ServiceException
     */
    public void batchInsert(List<PayCheck> payCheckList) throws ServiceException;

    /**
     * 批量更新状态
     *
     * @param list
     * @throws ServiceException
     */
    public void batchUpdateStatus(List<PayCheckUpdateModle> list) throws ServiceException;

    /**
     * 删除
     *
     * @param checkDate
     * @param agencyCode
     * @param merchantNo
     * @throws ServiceException
     */
    public void deleteInfo(String checkDate, String agencyCode, String merchantNo) throws ServiceException;

    /**
     * 根据支付指令ID和业务码查询PayCheckPo
     *
     * @param instructId
     * @param bizCode
     * @return
     * @throws ServiceException
     */
    public PayCheck getByInstructIdAndBizCode(String instructId, int bizCode) throws ServiceException;

    /**
     * @param checkDate
     * @param agencyCode
     * @param bizCode
     * @param startRow
     * @param batchSize
     * @return
     * @throws ServiceException
     */
    public List<Map<String, Object>> queryByMerAndDateAndBizCode(
            String checkDate, String agencyCode,
            int bizCode, int startRow, int batchSize) throws ServiceException;

    /**
     * 批量修改手续费
     *
     * @param list
     * @throws ServiceException
     */
    public void batchUpdateFee(List<OutCheckRecord> list) throws ServiceException;

}
