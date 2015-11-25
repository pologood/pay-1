package com.sogou.pay.notify.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.common.utils.httpclient.MerchantHttpClient;
import com.sogou.pay.common.utils.httpclient.MerchantResponse;
import com.sogou.pay.notify.dao.NotifyErrorLogDao;
import com.sogou.pay.notify.dao.PayOrderDao;
import com.sogou.pay.notify.dao.RefundInfoDao;
import com.sogou.pay.notify.entity.NotifyErrorLog;
import com.sogou.pay.notify.enums.NotifyStatusEnum;
import com.sogou.pay.notify.enums.NotifyTypeEnum;
import com.sogou.pay.notify.service.AbstractNotifyService;
import com.sogou.pay.notify.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qibaichao on 2015/4/8.
 */
@Service
public class NotifyServiceImpl extends AbstractNotifyService {

    private static final Logger logger = LoggerFactory.getLogger(NotifyServiceImpl.class);

    @Autowired
    private NotifyErrorLogDao notifyErrorLogDao;

    @Autowired
    private PayOrderDao payOrderDao;

    @Autowired
    private RefundInfoDao refundInfoDao;

    public static final int NOTIFY_NUM_LIMIT = 5;

    /**
     * @param notifyErrorLog
     * @Author qibaichao
     * @MethodName insertErrorLog
     * @Date 2014年9月22日
     * @Description:
     */
    @Override
    public void insertErrorLog(NotifyErrorLog notifyErrorLog) {
        notifyErrorLogDao.insertErrorLog(notifyErrorLog);

    }

    /**
     * @param notifyErrorLog
     * @Author qibaichao
     * @MethodName updateErrorLog
     * @Date 2014年9月22日
     * @Description:
     */
    @Override
    public void updateErrorLog(NotifyErrorLog notifyErrorLog) {
        notifyErrorLogDao.updateErrorLog(notifyErrorLog);
    }

    /**
     * @param notifyType
     * @param status
     * @param currentTime
     * @return
     * @Author qibaichao
     * @MethodName queryByNotifyTypeStatus
     * @Date 2014年9月22日
     * @Description:查询需要通知任务
     */
    @Override
    public List<NotifyErrorLog> queryByNotifyTypeStatus(int notifyType, int status, Date currentTime) {
        List<NotifyErrorLog> pos = null;
        pos = notifyErrorLogDao.queryByNotifyTypeStatus(notifyType, status, currentTime);
        return pos;

    }

    /**
     * @Author qibaichao
     * @MethodName doFirstNotify
     * @Date 2014年9月22日
     * @Description:初次通知
     */
    @Override
    public void doFirstNotify(int type, String outerId, String notifyUrl, Map<String, String> notifyParam) {

//        String paramsString = HttpUtil.packUrlParams(notifyParam);
//        String result = HttpUtil.sendPost(notifyUrl, "&" + paramsString);
//        logger.info(String.format("notify url: %s,paramsString: %s", notifyUrl, paramsString));
//        logger.info("notify result:" + result);
        MerchantResponse merchantResponse = MerchantHttpClient.getInstance().doPost(notifyUrl, notifyParam);
        logger.info(String.format("notify url: %s,paramsString: %s", notifyUrl, HttpUtil.packUrlParams(notifyParam)));
        logger.info("notify result:" + JSON.toJSONString(merchantResponse));
        if (merchantResponse.isSuccess() == true) {
            if (type == NotifyTypeEnum.PAY_NOTIFY.value()) {
                // 修改支付单为成功通知
                payOrderDao.updateNotifyStatus(outerId, NotifyStatusEnum.TASK_SUCCESS.value());
            } else {
//                refundInfoDao.updateNotifyStatus(outerId, NotifyStatusEnum.TASK_SUCCESS.value());
            }
        } else {
            // 通知失败,增加支付通知队列
            NotifyErrorLog notifyErrorLog = new NotifyErrorLog();
            notifyErrorLog.setErrorInfo("错误信息:"+merchantResponse.getMessage() + ",返回信息:" + merchantResponse.getStringResult());
            notifyErrorLog.setOuterId(outerId);
            notifyErrorLog.setNotifyType(type);
            notifyErrorLog.setNotifyUrl(notifyUrl);
            notifyErrorLog.setNotifyParams(JSON.toJSONString(notifyParam));
            notifyErrorLog.setNextTime(DateUtils.nextTime());
            notifyErrorLogDao.insertErrorLog(notifyErrorLog);
        }
    }

    /**
     * @param notifyErrorLog
     * @Author qibaichao
     * @MethodName doSchduledNotify
     * @Date 2014年9月22日
     * @Description:定时补偿通知
     */
    @SuppressWarnings("unchecked")
    @Override
    public void doSchduledNotify(NotifyErrorLog notifyErrorLog) {

        try {
            String outerId = notifyErrorLog.getOuterId();
            String notifyUrl = notifyErrorLog.getNotifyUrl();
            Map<String, String> notifyParam = (Map<String, String>) JSON.parse(notifyErrorLog.getNotifyParams());
            // 通知上限判断，防止死循环
            if (isNotifyable(notifyErrorLog)) {

//                String result = HttpUtil.sendPost(notifyUrl, "&" + paramsString);
//                logger.info(String.format("notify url: %s,paramsString: %s", notifyUrl, paramsString));
//                logger.info("notify result:" + result);

                MerchantResponse merchantResponse = MerchantHttpClient.getInstance().doPost(notifyUrl, notifyParam);

                logger.info(String.format("notify url: %s,paramsString: %s", notifyUrl, HttpUtil.packUrlParams(notifyParam)));
                logger.info("notify result:" + JSON.toJSONString(merchantResponse));

                if (merchantResponse.isSuccess() == true) {
                    notifyErrorLog.setStatus(NotifyStatusEnum.TASK_SUCCESS.value());
                    //删除任务
                    notifyErrorLogDao.deleteErrorLog(notifyErrorLog.getId());

                    // 修改支付单为成功通知
                    if (notifyErrorLog.getNotifyType() == NotifyTypeEnum.PAY_NOTIFY.value()) {
                        payOrderDao.updateNotifyStatus(outerId, NotifyStatusEnum.TASK_SUCCESS.value());
                    } else {
                    //refundInfoDao.updateNotifyStatus(outerId, NotifyStatusEnum.TASK_SUCCESS.value());
                    }
                } else {
                    // 通知失败,增加支付通知队列
                    notifyErrorLog.setErrorInfo("错误信息:"+merchantResponse.getMessage() + ",返回信息:" + merchantResponse.getStringResult());
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
     * @param notifyErrorLogPo
     * @return
     * @Author qibaichao
     * @MethodName isNotifyable
     * @Date 2014年9月22日
     * @Description:通知上限判断，防止死循环
     */
    public boolean isNotifyable(NotifyErrorLog notifyErrorLogPo) {
        if (notifyErrorLogPo.getNotifyNum() < NOTIFY_NUM_LIMIT) {
            return true;
        }
        return false;
    }
}

