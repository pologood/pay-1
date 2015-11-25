package com.sogou.pay.manager.payment;

/**
 * @Author qibaichao
 * @ClassName PayCheckJobManager
 * @Date 2015年2月16日
 * @Description:
 */
public interface PayCheckManager {

    /**
     * @param checkDate
     * @param agencyCode
     * @Author qibaichao
     * @MethodName downloadCheckData
     * @Date 2015年2月16日
     * @Description:下载第三方对账数据并入库 <p>
     * 1. 根据对账日期、机构编码、商户号查询对账日志
     * 2. 不存在对账日志，创建新日志记录；如果已经存在，且状态不为INIT，直接返回
     * 3. 根据各渠道分别下载支付、退款、对账数据，批量入库
     * 4. 更新对账日志状态为SUCCESS
     */
    public void downloadCheckData(String checkDate, String agencyCode);

    /**
     * @param checkDate
     * @param agencyCode
     * @Author qibaichao
     * @MethodName checkData
     * @Date 2015年2月16日
     * @Description: 对账并更新相关的对账状态.
     */
    public void checkData(String checkDate, String agencyCode);

    public void updatePayCheckResult(String checkDate, String agencyCode) throws Exception;

    public void updatePayCheckDiff() throws Exception;
}
