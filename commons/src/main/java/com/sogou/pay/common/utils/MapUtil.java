package com.sogou.pay.common.utils;

import com.google.common.collect.Lists;

import com.sogou.pay.common.annotation.MapField;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hujunfei Date: 15-1-7 Time: 下午12:32
 */
public final class MapUtil {

  public static String buildSignSource(Map<String, ?> map, boolean needSorting) {
    if (MapUtils.isEmpty(map)) return StringUtils.EMPTY;
    StringBuilder sb = new StringBuilder();
    Stream<String> stream = map.keySet().stream();
    if (needSorting) stream = stream.sorted();
    stream.forEach(k -> sb.append(k).append('=').append(map.get(k)).append('&'));
    return sb.substring(0, sb.length() - 1);
  }

  public static String buildSignSource(Map<String, ?> map) {
    return buildSignSource(map, true);
  }

  public static Map<String, ?> filter(Map<String, ?> map, Set<String> excludedItems) {
    return map.entrySet().stream()
            .filter(e -> Objects.nonNull(e.getValue()) && StringUtils.isNoneEmpty(e.getValue().toString())
                    && (excludedItems == null || !excludedItems.contains(e.getKey())))
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  public static boolean isEmpty(Map map) {
    return (map == null || map.isEmpty());
  }

  public static boolean isNotEmpty(Map map) {
    return !isEmpty(map);
  }

  /**
   * 删除Key或Value为null的键值对
   */
  public static Map dropNulls(Map map) {
    if (isEmpty(map)) {
      return map;
    }
    map.remove(null);
    List keys = Lists.newArrayList(map.keySet());
    for (Object key : keys) {
      if (map.get(key) == null) {
        map.remove(key);
      }
    }
    return map;
  }

  /**
   * 删除Value为null的键值对
   */
  public static Map dropNullValues(Map map) {
    if (isEmpty(map)) {
      return map;
    }
    List keys = Lists.newArrayList(map.keySet());
    for (Object key : keys) {
      if (map.get(key) == null) {
        map.remove(key);
      }
    }
    return map;
  }

  public static boolean checkAllExist(Map map) {
    if (map == null) {
      return false;
    }
    for (Object value : map.values()) {
      if (StringUtils.isEmpty((String) value))
        return false;
    }
    return true;
  }

  /**
   * 检查List中的键是否都在Map中存在，Map为空返回false，List为空返回true
   */
  public static boolean checkAllExist(Map map, List keys) {
    if (map == null) {
      return false;
    }
    if (keys == null) {
      return true;
    }
    for (Object key : keys) {
      if (map.get(key) == null) {
        return false;
      }
    }
    return true;
  }

  /**
   * 检查List中的键是否都在Map中存在，Map为空返回false，List为空返回true
   */
  public static boolean checkAllExist(Map map, Object[] keys) {
    if (map == null) {
      return false;
    }
    if (keys == null) {
      return true;
    }
    for (Object key : keys) {
      if (map.get(key) == null) {
        return false;
      }
    }
    return true;
  }

}
