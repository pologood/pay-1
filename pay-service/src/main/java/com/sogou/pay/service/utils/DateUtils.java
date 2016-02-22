package com.sogou.pay.service.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @Author qibaichao
 * @Date 2014年9月18日
 * @Description:
 */
public class DateUtils {

    private static String avgNotify = "60";

    /**
     * @param date
     * @param number
     * @return
     * @Author qibaichao
     * @MethodName nextTime
     * @Date 2015年6月2日
     * @Description: 执行时间
     */
    public static Date nextTime(Date date, int number) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        if (number == -1) {
            calendar.add(Calendar.MINUTE, Integer.parseInt(avgNotify));
        } else {
            calendar.add(Calendar.MINUTE, Integer.parseInt(avgNotify) * number);
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
