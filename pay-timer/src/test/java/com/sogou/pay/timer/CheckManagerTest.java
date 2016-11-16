package com.sogou.pay.timer;

import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.timer.check.CheckManager;
import com.sogou.pay.service.enums.AgencyCode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


public class CheckManagerTest extends BaseTest {
  private static final Logger logger = LoggerFactory.getLogger(CheckManagerTest.class);

  @Autowired
  private CheckManager checkManager;

  @Test
  public void downloadData() {
    Date checkDate = DateUtil.parse("20160619");
    String agencyCode = AgencyCode.WECHAT.name();
    try {
      checkManager.downloadOrder(checkDate, agencyCode);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void checkData() {
    checkAlipay();
    checkWechat();
    checkTenpay();
  }

  @Test
  public void checkAlipay() {
    Date checkDate = DateUtil.parse("20150413");
    String agencyCode = AgencyCode.ALIPAY.name();
    try {
      checkManager.downloadOrder(checkDate, agencyCode);
      checkManager.checkOrder(checkDate, agencyCode);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void checkTenpay() {
    Date checkDate = DateUtil.parse("20150421");
    String agencyCode = AgencyCode.TENPAY.name();
    try {
      checkManager.downloadOrder(checkDate, agencyCode);
      checkManager.checkOrder(checkDate, agencyCode);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void checkWechat() {
    Date checkDate = DateUtil.parse("20160623");
    String agencyCode = AgencyCode.WECHAT.name();
    try {
      checkManager.downloadOrder(checkDate, agencyCode);
      checkManager.checkOrder(checkDate, agencyCode);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void checkUnionpay() {
    Date checkDate = DateUtil.parse("20160623");
    String agencyCode = AgencyCode.UNIONPAY.name();
    try {
      checkManager.downloadOrder(checkDate, agencyCode);
      //checkManager.checkOrder(checkDate, agencyCode);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void updatePayCheckDiff() {
    try {
      checkManager.updatePayCheckDiff();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void updatePayCheckResult() {
    Date checkDate = DateUtil.parse("20150303");
    String agencyCode = AgencyCode.ALIPAY.name();
    try {
      checkManager.updatePayCheckResult(checkDate, agencyCode);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
