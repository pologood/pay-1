package com.sogou.pay.thirdpay.biz.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @Author qibaichao
 * @ClassName DateUtils
 * @Date 2015年2月16日
 * @Description:
 */
public class DateUtils {

    public static final String DEFAULT_FORMAt = "yyyy-MM-dd hh:mm:ss";

    public static final String dtLong = "yyyyMMddHHmmssSSSS";

    private static final int LEAST_ALIPAY_INTERVAL = 3;

    private static final int LEAST_TENPAY_INTERVAL = 3;

    private static final int DEFAULT_LEAST_INTERVAL = 3;

    public static Date toDate(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAt);

            Date date = sdf.parse(str);

            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public static Date toDate(String str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);

            Date date = sdf.parse(str);

            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public static String toString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAt);

        return sdf.format(date);
    }

    public static String toString(Date date, String format) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(date);
    }

    /**
     * 昨天开始时间
     * 格式 :yyyy-MM-dd 01:00:00
     *
     * @return
     */
    public static Date yesterday() {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, -1);

        calendar.set(Calendar.HOUR_OF_DAY, 1);

        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 十天前
     *
     * @return
     */
    public static Date tenDaysAgo() {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, -10);

        calendar.set(Calendar.HOUR_OF_DAY, 1);

        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date threeDaysAgo() {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, -3);

        calendar.set(Calendar.HOUR_OF_DAY, 1);

        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * @return
     */
    public static Date nextHour() {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.HOUR, 1);

        return calendar.getTime();
    }

    /**
     * 当前时间
     *
     * @return
     */
    public static String now() {
        Date now = new Date();

        return toString(now);
    }

    /**
     * 序列号
     *
     * @return
     */
    public static String getOrderNum() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat(dtLong);
        return df.format(date);
    }


}
