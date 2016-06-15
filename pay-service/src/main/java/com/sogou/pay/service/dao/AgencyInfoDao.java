package com.sogou.pay.service.dao;

import com.sogou.pay.service.entity.AgencyInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wujingpan on 2015/3/2.
 */
@Repository
public interface AgencyInfoDao {

    public List<AgencyInfo> getAgencyInfoList();


    /**
     * 获取支付机构基本信息
     * @param agencyCode 编码
     * @param accessPlatform 1：PC 2：移动 3：不区分 可空，默认为3
     * @return AgencyInfo
     */
    public AgencyInfo getAgencyInfoByCode(@Param("agencyCode")String agencyCode,
                                          @Param("accessPlatform")String accessPlatform);
    /**
     * 获取支付机构基本信息
     * @param id 主键
     * @return AgencyInfo
     */
    public  AgencyInfo getById(Integer id);

}
