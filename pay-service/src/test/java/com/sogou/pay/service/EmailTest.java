/*
 * Copyright 2014-2016 cyou.cn All right reserved. This software is the
 * confidential and proprietary information of cyou.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cyou.cn.
 */
package com.sogou.pay.service;

import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.service.utils.email.EmailSenderInterface;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Author qibaichao
 * @ClassName EmailTest
 * @Date 2014年9月12日
 * @Description:
 */

public class EmailTest extends  BaseTest{

    @Autowired
    private EmailSenderInterface emailSender;

    @Test
    public void testEmail() {

        try {
            String templateFtl = "error.ftl";
            String subject = "测试邮件";
            String content = "这是一封测试邮件，请忽略！<br>This is a test email, ignore it!";
            String[] addressList = {
                "xiepeidong@sogou-inc.com"
            };
            emailSender.sendEmail(templateFtl, subject, content, addressList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }

}
