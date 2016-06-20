package com.sogou.pay.notify.service;

import com.sogou.pay.common.http.client.HttpService;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.notify.dao.NotifyErrorLogDao;
import com.sogou.pay.notify.dao.PayOrderDao;
import com.sogou.pay.notify.dao.RefundInfoDao;
import com.sogou.pay.notify.entity.NotifyErrorLog;
import com.sogou.pay.notify.enums.NotifyStatusEnum;
import com.sogou.pay.notify.enums.NotifyTypeEnum;
import com.sogou.pay.notify.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class NotifyService {

  private static final Logger logger = LoggerFactory.getLogger(NotifyService.class);

  @Autowired
  private NotifyErrorLogDao notifyErrorLogDao;

  @Autowired
  private PayOrderDao payOrderDao;

  @Autowired
  private RefundInfoDao refundInfoDao;

  public static final int NOTIFY_NUM_LIMIT = 5;

  /**
   * 单个CPU线程池大小
   */
  protected final int POOL_SIZE = 20;

  /**
   * 线程池
   */
  protected ExecutorService notifyExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime()
          .availableProcessors() * POOL_SIZE);

  public void insertErrorLog(NotifyErrorLog notifyErrorLog) {
    notifyErrorLogDao.insertErrorLog(notifyErrorLog);

  }

  public void updateErrorLog(NotifyErrorLog notifyErrorLog) {
    notifyErrorLogDao.updateErrorLog(notifyErrorLog);
  }

  /**
   * 查询需要通知任务
   */
  public List<NotifyErrorLog> queryByNotifyTypeStatus(int notifyType, int status, Date currentTime) {
    List<NotifyErrorLog> pos = null;
    pos = notifyErrorLogDao.queryByNotifyTypeStatus(notifyType, status, currentTime);
    return pos;

  }

  /**
   * 初次通知
   */
  public void doFirstNotify(int type, String outerId, String notifyUrl, Map<String, String> notifyParam) {

    Result result = HttpService.getInstance().doPost(notifyUrl, notifyParam, null, null);
    logger.info("[doFirstNotify] url={}, query={}, response={}",
            notifyUrl, JSONUtil.Bean2JSON(notifyParam), JSONUtil.Bean2JSON(result));
    if (Result.isSuccess(result)) {
      if (type == NotifyTypeEnum.PAY_NOTIFY.value()) {
        // 修改支付单为成功通知
        payOrderDao.updateNotifyStatus(outerId, NotifyStatusEnum.TASK_SUCCESS.value());
      } else {
        //refundInfoDao.updateNotifyStatus(outerId, NotifyStatusEnum.TASK_SUCCESS.value());
      }
    } else {
      // 通知失败,增加支付通知队列
      NotifyErrorLog notifyErrorLog = new NotifyErrorLog();
      notifyErrorLog.setErrorInfo(JSONUtil.Bean2JSON(result));
      notifyErrorLog.setOuterId(outerId);
      notifyErrorLog.setNotifyType(type);
      notifyErrorLog.setNotifyUrl(notifyUrl);
      notifyErrorLog.setNotifyParams(JSONUtil.Bean2JSON(notifyParam));
      notifyErrorLog.setNextTime(DateUtils.nextTime());
      notifyErrorLogDao.insertErrorLog(notifyErrorLog);
    }
  }

  /**
   * 定时补偿通知
   */
  public void doSchduledNotify(NotifyErrorLog notifyErrorLog) {

    try {
      String outerId = notifyErrorLog.getOuterId();
      String notifyUrl = notifyErrorLog.getNotifyUrl();
      Map<String, String> notifyParam = (Map<String, String>) JSONUtil.JSON2Map(notifyErrorLog.getNotifyParams());
      // 通知上限判断，防止死循环
      if (isNotifyable(notifyErrorLog)) {

        Result result = HttpService.getInstance().doPost(notifyUrl, notifyParam, null, null);
        logger.info("[doSchduledNotify] url={}, query={}, response={}",
                notifyUrl, JSONUtil.Bean2JSON(notifyParam), JSONUtil.Bean2JSON(result));
        if (Result.isSuccess(result)) {
          if (notifyErrorLog.getNotifyType() == NotifyTypeEnum.PAY_NOTIFY.value()) {
            // 修改支付单为成功通知
            payOrderDao.updateNotifyStatus(outerId, NotifyStatusEnum.TASK_SUCCESS.value());
          } else {
            //refundInfoDao.updateNotifyStatus(outerId, NotifyStatusEnum.TASK_SUCCESS.value());
          }
        } else {
          // 通知失败,增加支付通知队列
          notifyErrorLog.setErrorInfo(JSONUtil.Bean2JSON(result));
          // 下次通知时间
          notifyErrorLog.setNextTime(DateUtils.nextTime(new Date(), notifyErrorLog.getNotifyNum()));
          // 设置通知次数
          notifyErrorLog.setNotifyNum(notifyErrorLog.getNotifyNum() + 1);
          notifyErrorLogDao.updateErrorLog(notifyErrorLog);
        }
      } else {
        notifyErrorLog.setStatus(NotifyStatusEnum.TASK_FAIL.value());
        notifyErrorLogDao.updateErrorLog(notifyErrorLog);
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * 通知上限判断，防止死循环
   */
  public boolean isNotifyable(NotifyErrorLog notifyErrorLogPo) {
    if (notifyErrorLogPo.getNotifyNum() < NOTIFY_NUM_LIMIT) {
      return true;
    }
    return false;
  }

  public void firstNotify(final int type, final String payId, final String notifyUrl, final Map<String, String> notifyParam) {
    // 执行线程
    notifyExecutorService.execute(new Runnable() {
      @Override
      public void run() {
        doFirstNotify(type, payId, notifyUrl, notifyParam);
      }
    });
  }

  public void scheduledNotify(NotifyErrorLog notifyErrorLog) {
    doSchduledNotify(notifyErrorLog);
  }
}

