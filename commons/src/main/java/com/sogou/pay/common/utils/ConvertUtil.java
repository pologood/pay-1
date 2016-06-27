package com.sogou.pay.common.utils;

import java.util.Date;

/**
 * Created by hujunfei Date: 15-1-9 Time: 上午10:57
 */
public class ConvertUtil {

  /**
   * 转换对象为整型，若格式错误或超出Int的最大和最小值范围，则抛异常
   *
   * @param o
   * @return 整型值
   * @throws java.lang.IllegalArgumentException
   */
  public static int toInt(Object o) {
    try {
      if (o instanceof Integer) return (Integer) o;
      else return Integer.parseInt(o.toString());
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Convert Int Error:%s", o), e);
    }
  }

  /**
   * 转换对象为长整型，若格式错误或超出Long的最大和最小值范围，则抛异常
   *
   * @param o
   * @return 长整型值
   * @throws java.lang.IllegalArgumentException
   */
  public static long toLong(Object o) {
    try {
      if (o instanceof Long) return (Long) o;
      else return Long.parseLong(o.toString());
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Convert Long Error:%s", o), e);
    }
  }

  /**
   * 若是bool/Boolean类型，则转换，否则抛异常
   *
   * @param o
   * @return bool值
   * @throws java.lang.IllegalArgumentException
   */
  public static boolean toBool(Object o) {
    try {
      if (o instanceof Boolean) return (Boolean) o;
      else if ("true".equalsIgnoreCase(o.toString())) return true;
      else if ("false".equalsIgnoreCase(o.toString())) return false;
      throw new IllegalArgumentException(String.format("Convert Boolean Error:%s", o));
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Convert Boolean Error:%s", o), e);
    }
  }

  // TODO: 检测BigDecimal的情形
  public static float toFloat(Object o) {
    try {
      if (o instanceof Float) return (Float) o;
      else return Float.parseFloat(o.toString());
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Convert Long Error:%sf", o), e);
    }
  }

  // TODO: 检测BigDecimal的情形
  public static double toDouble(Object o) {
    try {
      if (o instanceof Double) return (Double) o;
      else return Double.parseDouble(o.toString());
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Convert Long Error:%s", o), e);
    }
  }

  /**
   * 检测Date类型，或转换toString()结果为Date型 <br/>
   * 支持格式yyyyMMdd、yyyyMMddHHmmss两种
   *
   * @param o 待转换对象
   * @return Date型日期
   * @throws java.lang.IllegalArgumentException
   */
  public static Date toDate(Object o) {
    try {
      if (o instanceof java.util.Date) return (Date) o;
      else return DateUtil.parse(o.toString());
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Convert Long Error:%s", o), e);
    }
  }

}
