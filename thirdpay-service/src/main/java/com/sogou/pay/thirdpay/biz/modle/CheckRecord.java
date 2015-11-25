/*
 * Copyright 2012-2014 Wanda.cn All right reserved. This software is the
 * confidential and proprietary information of Wanda.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Wanda.cn.
 */
package com.sogou.pay.thirdpay.biz.modle;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author qibaichao
 * @ClassName ClearRecord
 * @Date 2015年2月16日
 * @Description:对账记录
 */
public  class CheckRecord {
    /**
     * 已处理清算下周记录，防止同一账户多次清算
     * key : 账号 + StartTime + ClearType
     * paygeNo=1时检查
     */
    public static Set<String> processedRecord = new HashSet<String>();

    /**
     * 已处理清算TENPAY记录，防止同一账户多次清算
     * key : 账号 + checkData + CheckType
     */
    public static Set<String> processedTenpayRecord = new HashSet<String>();

    /**
     * 已处理清算Wechat记录，防止同一账户多次清算
     * key : 账号 + checkData + CheckType
     */
    public static Set<String> processedWechatRecord = new HashSet<String>();

    public static void clearRecords(){
        processedRecord.clear();
        processedTenpayRecord.clear();
        processedWechatRecord.clear();
    }
}
