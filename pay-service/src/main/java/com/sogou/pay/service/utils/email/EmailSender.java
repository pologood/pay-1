/*
 * Copyright 2014-2016 cyou.cn All right reserved. This software is the
 * confidential and proprietary information of cyou.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cyou.cn.
 */
package com.sogou.pay.service.utils.email;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.MimeMessage;

/**
 * @Author qibaichao
 * @ClassName EmailSenderImpl
 * @Date 2014年9月12日
 * @Description:
 */
@Service
public class EmailSender extends EmailSenderAbstract implements EmailSenderInterface {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     * @Author qibaichao
     * @MethodName sendEmail
     * @param templateFtl
     * @param subject
     * @param content
     * @param toAddress
     * @Date 2014年10月15日
     * @Description:
     */
    @Override
    public void sendEmail(String templateFtl, String subject, String content, String... toAddress) {

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, "utf-8");
        try {
            helper.setTo(toAddress);
            helper.setSubject(subject);
            String htmlText = getMailText(freeMarkerConfigurer, templateFtl, content);
            // 邮件内容，注意加参数true，表示启用html格式
            helper.setText(htmlText, true);
            javaMailSender.send(msg);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

}
