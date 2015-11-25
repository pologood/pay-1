package com.sogou.pay.common;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hujunfei Date: 14-12-26 Time: 下午6:49
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:*.xml")
public class BaseTest extends Assert {
    @Before
    public void baseBefore() {
        System.out.println("-----------开始测试用例----------");
    }

    @After
    public void baseAfter() {
        System.out.println("-----------结束测试用例----------");
    }

    public static TestBean getBean() {
        TestBean bean = new TestBean();
        bean.setId(10);
        bean.setName("Test");
        bean.setArr(new String[]{"Hello", "World", "!"});

        List list = new ArrayList();
        list.add("list1");
        list.add("list2");

        Map map = new HashMap();
        map.put("key1", "value1");
        map.put("key2", "value2");
        bean.setList(list);
        bean.setMap(map);

        return bean;
    }

    /*
     * Jackson和Fastjson无法解析非静态内部类
     */
    public static class TestBean {
        private int id;
        private String name;
        private String nickname;
        private String[] arr;
        private List list;
        private Map map;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String[] getArr() {
            return arr;
        }

        public void setArr(String[] arr) {
            this.arr = arr;
        }

        public List getList() {
            return list;
        }

        public void setList(List list) {
            this.list = list;
        }

        public Map getMap() {
            return map;
        }

        public void setMap(Map map) {
            this.map = map;
        }
    }
}
