package com.sogou.pay.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogou.pay.common.types.PMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BeanUtil {

  private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

  private static ObjectMapper objectMapper = new ObjectMapper();

  public static ObjectMapper getMapper() {
    return objectMapper;
  }

  public static void setBeanProperty(Object object, String name, Object value) {
    try {
      BeanUtils.setProperty(object, name, value);
    } catch (IllegalAccessException e) {
      logger.error("Put Value To ResultDO IllegalAccessException! ParamName:" + name + ", Value:" + value, e);
    } catch (InvocationTargetException e) {
      logger.error("Put Value To ResultDO InvocationTargetException! ParamName:" + name + ", Value:" + value, e);
    }
  }

  public static String getBeanSimpleProperty(Object object, String name) {
    String value = "";
    try {
      value = BeanUtils.getSimpleProperty(object, name);
    } catch (IllegalAccessException e) {
      logger.error("Get Value From DO IllegalAccessException! ParamName:" + name, e);
    } catch (InvocationTargetException e) {
      logger.error("Get Value From DO InvocationTargetException! ParamName:" + name, e);
    } catch (NoSuchMethodException e) {
      logger.error("Get Value From DO NoSuchMethodException! ParamName:" + name, e);
    }
    return value;
  }

  /**
   * Bean转换为Map，去除key和value为null的项
   */
  public static Map<String, Object> Bean2MapNotNull(Object object) {
    Map<String, Object> map = Bean2Map(object);
    if (map != null) {
      map.remove(null);
      List<String> keys = new ArrayList(map.keySet());
      for (String key : keys) {
        if (map.get(key) == null) {
          map.remove(key);
        }
      }
    }
    return map;
  }

  public static Map<String, Object> Bean2MapNotBlank(Object object) {
    Map<String, Object> map = Bean2Map(object);
    if (map != null) {
      List<String> keys = new ArrayList<>(map.keySet());
      for (String key : keys) {
        if (StringUtils.isBlank(map.get(key).toString())) {
          map.remove(key);
        }
      }
    }
    return map;
  }


  public static <T> T Map2Bean(Map map, Class<T> clazz) {
    return objectMapper.convertValue(map, clazz);
  }

  public static Map Bean2Map(Object bean) {
    return objectMapper.convertValue(bean, Map.class);
  }

  public static PMap Bean2PMap(Object bean) {
    return objectMapper.convertValue(bean, PMap.class);
  }

}
