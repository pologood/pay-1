package com.sogou.pay.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/1/21 18:00
 */
public class OrderStatusList {

    public static List<Integer> getUnPayOrderStatusList() {
        List<Integer> unPayOrderStatusList = new ArrayList<Integer>();
        unPayOrderStatusList.add(1);
        unPayOrderStatusList.add(10);
        return unPayOrderStatusList;
    }

}
