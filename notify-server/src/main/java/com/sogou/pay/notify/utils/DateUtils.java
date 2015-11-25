package com.sogou.pay.notify.utils;

import java.util.Calendar;
import java.util.Date;


import com.sogou.pay.notify.config.NotifyTime;
import com.sogou.pay.notify.server.NotifyServerLocator;

/**
 * @Author qibaichao
 * @ClassName DateUtils
 * @Date 2014年9月18日
 * @Description:
 */
public class DateUtils {

    private static NotifyTime notifyTime = NotifyServerLocator.getNotifyTime();

    /**
     * @Author qibaichao
     * @MethodName nextTime
     * @param date
     * @param number
     * @return
     * @Date 2014年9月18日
     * @Description: 下次通知时间
     */
    public static Date nextTime(Date date, int number) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        if (number == -1) {
            calendar.add(Calendar.MINUTE, Integer.parseInt(notifyTime.AVG_NOTIFY));
        }

        if (number == 1) {
            calendar.add(Calendar.MINUTE, Integer.parseInt(notifyTime.FIRST_NOTIFY));
        }

        if (number == 2) {
            calendar.add(Calendar.MINUTE, Integer.parseInt(notifyTime.SECOND_NOTIFY));
        }

        if (number == 3) {
            calendar.add(Calendar.MINUTE, Integer.parseInt(notifyTime.THIRD_NOTIFY));
        }

        if (number == 4) {
            calendar.add(Calendar.MINUTE, Integer.parseInt(notifyTime.FOURTH_NOTIFY));
        }

        if (number >= 5) {
            calendar.add(Calendar.MINUTE, Integer.parseInt(notifyTime.FIFTH_NOTIFY));
        }

        return calendar.getTime();
    }

    /**
     * @return
     */
    public static Date nextTime() {
        Date date = new Date();
        return nextTime(date, 1);
    }

    /**
     * @return
     */
    public static Date nextAvgTime(Date date) {
        return nextTime(date, -1);
    }

}
