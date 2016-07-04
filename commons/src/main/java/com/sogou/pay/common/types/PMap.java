package com.sogou.pay.common.types;

import com.sogou.pay.common.utils.ConvertUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

public class PMap<K, V> extends HashMap<K, V> {

  private static final long serialVersionUID = 2294806098649305447L;

  public PMap() {
    super();
  }

  public PMap(int initialCapacity) {
    super(initialCapacity);
  }

  public PMap(Map<K, V> m) {
    super(m);
  }

  /**
   * 实际调用toString()方法
   *
   * @param key
   * @return
   */
  public String getString(K key) {
    return MapUtils.getString(this, key);
  }

  public String[] getStrings(K key) {
    return (String[]) this.get(key);
  }

  public int getInt(K key) {
    return MapUtils.getIntValue(this, key);
  }

  public long getLong(K key) {
    return MapUtils.getLongValue(this, key);
  }

  public boolean getBoolean(K key) {
    return MapUtils.getBooleanValue(this, key);
  }

  public double getDouble(K key) {
    return MapUtils.getDoubleValue(this, key);
  }

  public Date getDate(K key) {
    return ConvertUtil.toDate(this.get(key));
  }

  @SuppressWarnings("unchecked")
  public PMap<String, ?> getPMap(K key) {
    try {
      return (PMap<String, ?>) this.get(key);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Get PMap Error:%s", key));
    }
  }
}
