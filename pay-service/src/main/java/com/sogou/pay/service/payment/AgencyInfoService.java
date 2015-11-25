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
     * @param agencyType:1：网关支付 2：第三方支付 3：扫码支付 4:SDK 5：全部  可空，默认为5
     * @return AgencyInfo 
     */
    public AgencyInfo getAgencyInfoByCode(String agencyCode,String accessPlatform ,String agencyType);

    /**
     * 获取支付机构基本信息
     * @param id 主键
     * @return AgencyInfo
     */
    public AgencyInfo getById(Integer id) throws ServiceException;
}
