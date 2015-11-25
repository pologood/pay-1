/*
 * Copyright 2014-2016 cyou.cn All right reserved. This software is the
 * confidential and proprietary information of cyou.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cyou.cn.
 */
package com.sogou.pay.service;

import com.sogou.pay.service.utils.email.EmailSenderInterface;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
            String subject = "报警邮件";
            String content = "aa";
            String[] addressList = {
                "qibaichao@sogou-inc.com"
            };
            emailSender.sendEmail(templateFtl, subject, content, addressList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }

}
