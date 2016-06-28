package com.sogou.pay.service.payment;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.service.dao.AgencyInfoDao;
import com.sogou.pay.service.entity.AgencyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AgencyInfoService {

  @Autowired
  AgencyInfoDao dao;


  public List<AgencyInfo> getAllAgencyInfo() throws ServiceException {
    return dao.getAgencyInfoList();
  }


  public AgencyInfo getAgencyInfoByCode(String agencyCode, Integer accessPlatform) {
    return dao.getAgencyInfoByCode(agencyCode, accessPlatform);
  }


  public AgencyInfo getById(Integer id) throws ServiceException {
    return dao.getById(id);
  }
}
