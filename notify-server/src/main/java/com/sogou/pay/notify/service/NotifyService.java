package com.sogou.pay.notify.service;

import com.sogou.pay.common.http.HttpService;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.notify.dao.NotifyToDoDao;
import com.sogou.pay.notify.dao.PayOrderDao;
import com.sogou.pay.notify.dao.RefundInfoDao;
import com.sogou.pay.notify.entity.NotifyToDo;
import com.sogou.pay.notify.enums.NotifyStatus;
import com.sogou.pay.notify.enums.NotifyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class NotifyService {

  private static final Logger logger = LoggerFactory.getLogger(NotifyService.class);
  private static final int NOTIFY_NUM_LIMIT = 5;
  /**
   * 单个CPU线程池大小
   */
  private final int POOL_SIZE = 20;
  @Value("${second_notify}")
  public String SECOND_NOTIFY;
  @Value("${third_notify}")
  public String THIRD_NOTIFY;
  @Value("${fourth_notify}")
  public String FOURTH_NOTIFY;
  @Value("${fifth_notify}")
  public String FIFTH_NOTIFY;
  @Value("${sixth_notify}")
  public String SIXTH_NOTIFY;
  @Autowired
  private NotifyToDoDao notifyToDoDao;
  @Autowired
  private PayOrderDao payOrderDao;
  @Autowired
  private RefundInfoDao refundInfoDao;
  /**
   * 线程池
   */
  private ExecutorService notifyExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime()
          .availableProcessors() * POOL_SIZE);
  private String[] nextNotifyTimes;
  private Map<NotifyType, UpdateNotifyStatus> updateNotifyStatusMap;


  @Autowired
  void init() {
    updateNotifyStatusMap = new HashMap<>();
    updateNotifyStatusMap.put(NotifyType.PAY_NOTIFY,
            (payId, notifyStatus) -> payOrderDao.updateNotifyStatus(payId, notifyStatus));
    updateNotifyStatusMap.put(NotifyType.REFUND_NOTIFY,
            (payId, notifyStatus) -> refundInfoDao.updateNotifyStatus(payId, notifyStatus));
//    updateNotifyStatusMap.put(NotifyType.TRANSFER_NOTIFY,
//            (payId, notifyStatus) -> payOrderDao.updateNotifyStatus(payId, notifyStatus));
    nextNotifyTimes = new String[]{
            SECOND_NOTIFY, THIRD_NOTIFY, FOURTH_NOTIFY, FIFTH_NOTIFY, SIXTH_NOTIFY
    };
  }

  /**
   * 查询通知任务
   */
  public List<NotifyToDo> getNotifyToDo(int notifyType) {
    return notifyToDoDao.queryByNotifyTypeStatus(notifyType, NotifyStatus.INIT.getValue(), new Date());
  }

  /**
   * 通知
   */
  public void doNotify(NotifyType type, String payId, String notifyUrl, Map<String, String> notifyParam, NotifyToDo notifyToDo) {

    Result result = HttpService.getInstance().doPost(notifyUrl, notifyParam, null, null);
    logger.info("[doNotify] url={}, params={}, response={}",
            notifyUrl, JSONUtil.Bean2JSON(notifyParam), JSONUtil.Bean2JSON(result));
    if (Result.isSuccess(result) && Objects.equals("success", result.getReturnValue())) {
      //修改订单状态为通知成功
      updateNotifyStatusMap.get(type).updateNotifyStatus(payId, NotifyStatus.SUCCESS.getValue());
      if (notifyToDo != null) {
        notifyToDo.setNotifyStatus(NotifyStatus.SUCCESS.getValue());
        notifyToDoDao.updateNotifyToDo(notifyToDo);
      }
    } else {
      logger.error("[doNotify] failed");
      // 通知失败,添加到重试队列
      if (notifyToDo == null) {
        notifyToDo = new NotifyToDo();
        notifyToDo.setErrorInfo(JSONUtil.Bean2JSON(result));
        notifyToDo.setPayId(payId);
        notifyToDo.setNotifyType(type.value());
        notifyToDo.setNotifyUrl(notifyUrl);
        notifyToDo.setNotifyParams(JSONUtil.Bean2JSON(notifyParam));
        notifyToDo.setNextTime(nextNotifyTime(1));
        notifyToDoDao.insertNotifyToDo(notifyToDo);
      } else {
        notifyToDo.setErrorInfo(JSONUtil.Bean2JSON(result));
        notifyToDo.setNotifyNum(notifyToDo.getNotifyNum() + 1);
        notifyToDo.setNextTime(nextNotifyTime(notifyToDo.getNotifyNum()));
        notifyToDoDao.updateNotifyToDo(notifyToDo);
      }
    }
  }

  /**
   * 定时重试
   */
  public void scheduleNotify(NotifyToDo notifyToDo) {
    try {
      //是否达到重试上限
      if (isNotifiable(notifyToDo)) {
        doNotify(NotifyType.values()[notifyToDo.getNotifyType()-1],
                notifyToDo.getPayId(),
                notifyToDo.getNotifyUrl(),
                (Map<String, String>) JSONUtil.JSON2Map(notifyToDo.getNotifyParams()),
                notifyToDo);
      } else {
        notifyToDo.setNotifyStatus(NotifyStatus.FAIL.getValue());
        notifyToDoDao.updateNotifyToDo(notifyToDo);
      }
    } catch (Exception e) {
      logger.error("[scheduleNotify] failed, {}", e);
    }
  }

  public boolean isNotifiable(NotifyToDo notifyToDoPo) {
    return notifyToDoPo.getNotifyNum() < NOTIFY_NUM_LIMIT;
  }

  public void firstNotify(final NotifyType type, final String payId, final String notifyUrl, final Map<String, String> notifyParam) {
    // 执行线程
    notifyExecutorService.execute(new Runnable() {
      @Override
      public void run() {
        doNotify(type, payId, notifyUrl, notifyParam, null);
      }
    });
  }

  public LocalDateTime nextNotifyTime(int notifyNum) {
    long interval = Long.valueOf(nextNotifyTimes[notifyNum - 1]);
    LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(interval);
    return localDateTime;
  }

  @FunctionalInterface
  public interface UpdateNotifyStatus {

    public int updateNotifyStatus(String payId, int notifyStatus);

  }
}

