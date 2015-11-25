package com.sogou.pay.common.utils;

import com.sogou.pay.common.BaseTest;
import com.sogou.pay.common.annotation.MapField;
import com.sogou.pay.common.result.ResultStatus;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hujunfei Date: 15-1-9 Time: 下午6:05
 */
public class PMapUtilTest extends BaseTest {

    @Test
    public void testToBean() {
        PMap pMap = new PMap();
        pMap.put("id", new BigInteger("-21000000000"));
        pMap.put("Name", "Jerry");
        pMap.put("Date", new Date());
        pMap.put("obj", ResultStatus.SUCCESS);
        pMap.put("key", "key1");

        Map map = new HashMap();
        map.put("key", "value");
        pMap.put("map", map);


        TestPMapBean bean = MapUtil.toBean(pMap, TestPMapBean.class);
        System.out.println(JsonUtil.beanToJson(bean));

        TestPMapSubBean subBean = MapUtil.toBean(pMap, TestPMapSubBean.class);
        System.out.println(JsonUtil.beanToJson(subBean));


        System.out.println(PMapUtil.fromBean(bean).get("map"));
    }

    public static class TestPMapSubBean extends TestPMapBean {
        @MapField(key = "key")
        private String subKey;

        public String getSubKey() {
            return subKey;
        }

        public void setSubKey(String subKey) {
            this.subKey = subKey;
        }
    }

    public static class TestPMapBean {
        protected int id;
        @MapField(key = "Name")
        private String name;
        @MapField(key = "Date")
        protected Date date;
        @MapField(key = "obj")
        protected ResultStatus status;
        private Map map;

        public Map getMap() {
            return map;
        }

        public void setMap(Map map) {
            this.map = map;
        }

        public ResultStatus getStatus() {
            return status;
        }

        public void setStatus(ResultStatus status) {
            this.status = status;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
