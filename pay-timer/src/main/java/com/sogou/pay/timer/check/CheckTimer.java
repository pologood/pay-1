package com.sogou.pay.timer.check;

import com.sogou.pay.service.enums.AgencyCode;
import com.sogou.pay.service.service.PayCheckResultService;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CheckTimer {

  private static final Logger logger = LoggerFactory.getLogger(CheckTimer.class);

  private static String DATE_FORMAT = "yyyyMMdd";

  private List<String> agencyCodeList = new ArrayList<String>();
  @Autowired
  private CheckManager checkManager;
  @Autowired
  private PayCheckResultService payCheckResultService;

  {
    agencyCodeList.add(AgencyCode.ALIPAY.name());
    agencyCodeList.add(AgencyCode.TENPAY.name());
    agencyCodeList.add(AgencyCode.WECHAT.name());
    agencyCodeList.add(AgencyCode.UNIONPAY.name());
  }

  /**
   * 支付宝对账任务
   * 每日凌晨2点15执行一次
   */
  public void doAlipayJob() {
    // 取前一天日期
    Date checkDate = getYesterday();
    logger.info("[doAlipayJob] start, checkDate={}", checkDate);
    String agencyCode = AgencyCode.ALIPAY.name();
    checkManager.downloadOrder(checkDate, agencyCode);
    checkManager.checkOrder(checkDate, agencyCode);
    logger.info("[doAlipayJob] end, checkDate={}", checkDate);
  }


  /**
   * 财付通对账任务
   * 7点以后进行
   * 每日7点30执行一次
   */
  public void doTenpayJob() {
    // 取前一天日期
    Date checkDate = getYesterday();
    logger.info("[doTenpayJob] start, checkDate={}", checkDate);
    String agencyCode = AgencyCode.TENPAY.name();
    checkManager.downloadOrder(checkDate, agencyCode);
    checkManager.checkOrder(checkDate, agencyCode);
    logger.info("[doTenpayJob] end, checkDate={}", checkDate);
  }

  /**
   * 微信对账任务
   * 微信在次日9点启动生成前一天的对账单，10点后再获取
   * 每日10点30执行一次
   */
  public void doWechatJob() {
    // 取前一天日期
    Date checkDate = getYesterday();
    logger.info("[doWechatJob] start, checkDate={}", checkDate);
    String agencyCode = AgencyCode.WECHAT.name();
    checkManager.downloadOrder(checkDate, agencyCode);
    checkManager.checkOrder(checkDate, agencyCode);
    logger.info("[doWechatJob] end, checkDate={}", checkDate);
  }

  /**
   * 取昨天开始，往前三天进行检查
   */
  public void doRecheckData() throws Exception {
    logger.info("[doRecheckData] start");
    Calendar ca = Calendar.getInstance();
    for (int i = 0; i < 3; i++) {
      ca.add(Calendar.DAY_OF_MONTH, -1);
      Date checkDate = ca.getTime();
      String checkDateStr = DateUtils.formatDate(checkDate, DATE_FORMAT);
      for (String agencyCode : agencyCodeList) {
        int count = payCheckResultService.queryCountByDateAndAgency(checkDateStr, agencyCode);
        //对账结果数据为0，重新比对
        if (count == 0) {
          checkManager.downloadOrder(checkDate, agencyCode);
          checkManager.checkOrder(checkDate, agencyCode);
        }
      }
    }
    logger.info("[doRecheckData] end");
  }

  private Date getYesterday() {
    Calendar ca = Calendar.getInstance();
    ca.add(Calendar.DATE, -1);
    return ca.getTime();
  }

}
