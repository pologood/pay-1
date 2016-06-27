package com.sogou.pay.service.payment;

import java.util.List;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.entity.AgencyInfo;

/**
 * Created by wujingpan on 2015/3/2.
 */
public interface AgencyInfoService {

    public List<AgencyInfo> getAllAgencyInfo() throws ServiceException;

    /**
     * 获取支付机构基本信息
     * @param agencyCode 编码
     * @param accessPlatform 1：PC 2：移动 3：不区分 可空，默认为3
     * @return AgencyInfo
     */
    public AgencyInfo getAgencyInfoByCode(String agencyCode,Integer accessPlatform);


    /**
     * 获取支付机构基本信息
     * @param id 主键
     * @return AgencyInfo
     */
    public AgencyInfo getById(Integer id) throws ServiceException;
}
