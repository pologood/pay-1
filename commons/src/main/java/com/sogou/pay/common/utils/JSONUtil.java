package com.sogou.pay.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogou.pay.common.types.PMap;

import java.util.Map;

public class JSONUtil {

  private static ObjectMapper objectMapper = new ObjectMapper();

  public static Map<?, ?> JSON2Map(String json) {
    try {
      return objectMapper.readValue(json, Map.class);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static PMap<?, ?> JSON2PMap(String json) {
    try {
      return objectMapper.readValue(json, PMap.class);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String Map2JSON(Map<?, ?> map) {
    try {
      return objectMapper.writeValueAsString(map);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <T> T JSON2Bean(String json, Class<T> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String Bean2JSON(Object bean) {
    try {
      return objectMapper.writeValueAsString(bean);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
