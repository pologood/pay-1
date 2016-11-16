package com.sogou.pay.common.utils;

import java.util.Date;

/**
 * Created by hujunfei Date: 15-1-9 Time: 上午10:57
 */
public class ConvertUtil {

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
