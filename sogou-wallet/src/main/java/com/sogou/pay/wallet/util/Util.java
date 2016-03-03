package com.sogou.pay.wallet.util;

import com.sogou.pay.common.utils.MD5Util;
import com.sogou.pay.common.utils.OrderNo;
import com.sogou.pay.common.utils.SequenceGenerator;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by xiepeidong on 2016/2/24.
 */
public class Util {

    private final static String OD = "OD";// 订单
    private final static String ZZ = "ZZ";// 转账

    @Resource
    private static OrderNo orderNo = new OrderNo();

    public static String signMD5(Map params, String md5_key){
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for(int i=0, size=keys.size();i<size;i++){
            String key= keys.get(i);
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(md5_key);
        String text = sb.toString();
        return MD5Util.MD5Encode(text, null);
    }

    /**
     * 订单号 ：OD+时间17位+3位序列 23位
     * KPI使用
     */
    public static String getOrderNo() {
        String number = null;
        try {
            number = OD + orderNo.getNo();
        } catch (Exception e) {
        }
        return number;
    }

    public static String getTransferNo() {
        String no=null;
        try{
            no = ZZ + orderNo.getNo();
        }catch(Exception e){
        }
        return no;
    }
}
