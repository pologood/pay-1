package com.sogou.pay.service.payment.impl;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.AgencyInfoDao;
import com.sogou.pay.service.entity.AgencyInfo;
import com.sogou.pay.service.payment.AgencyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wujingpan on 2015/3/2.
 */
@Service
public class AgencyInfoServiceImpl implements AgencyInfoService {

    @Autowired
    AgencyInfoDao dao;

    @Override
    public List<AgencyInfo> getAllAgencyInfo() throws ServiceException {
        return dao.getAgencyInfoList();
    }


    @Override
    public AgencyInfo getAgencyInfoByCode(String agencyCode,String accessPlatform ,String agencyType){
        return dao.getAgencyInfoByConn(agencyCode,accessPlatform,agencyType);
    }

    @Override
    public AgencyInfo getById(Integer id) throws ServiceException {
        return dao.getById(id);
    }
}
