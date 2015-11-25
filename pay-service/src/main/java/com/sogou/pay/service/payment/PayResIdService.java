package com.sogou.pay.service.payment;


/**
 * User: Liwei
 * Date: 15/3/5
 * Time: 上午10:25
 * Description:
 */
public interface PayResIdService {

    /**
     * 插入响应流水信息
     *
     * @param String
     * @return 返回值
     */
    public int insertPayResId(String payDetailId) throws Exception;

}
