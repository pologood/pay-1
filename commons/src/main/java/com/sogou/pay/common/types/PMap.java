package com.sogou.pay.common.types;

import com.sogou.pay.common.utils.ConvertUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hujunfei Date: 15-1-9 Time: 上午10:53
 * <br/>无法转换则抛出IllegalArgumentException
 */
public class PMap<K, V> extends HashMap<K, V> {
    public PMap() {
        super();
    }

    public PMap(int initialCapacity) {
        super(initialCapacity);
    }

    public PMap(Map m) {
        super(m);
    }

    /**
     * 实际调用toString()方法
     *
     * @param key
     * @return
     */
    public String getString(K key) {
        Object value = this.get(key);
        return value == null ? null : value.toString();
    }

    public String[] getStrings(K key) {
        return (String[]) this.get(key);
    }

    public int getInt(K key) {
        return ConvertUtil.toInt(this.get(key));
    }

    public long getLong(K key) {
        return ConvertUtil.toLong(this.get(key));
    }

    public boolean getBoolean(K key) {
        return ConvertUtil.toBool(this.get(key));
    }

    public float getFloat(K key) {
        return ConvertUtil.toFloat(this.get(key));
    }

    public Date getDate(K key) {
        return ConvertUtil.toDate(this.get(key));
    }

    public PMap getPMap(K key) {
        try {
            return (PMap) this.get(key);
        } catch (Exception e) {
            throw new IllegalArgumentException("Get PMap Error: " + key);
        }
    }
}
