/*
 * Copyright 2014-2016 cyou.cn All right reserved. This software is the
 * confidential and proprietary information of cyou.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cyou.cn.
 */
package com.sogou.pay.service.utils.email;

import com.sogou.pay.common.http.model.RequestModel;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.service.config.PayConfig;
import com.sogou.pay.service.connect.HttpClientService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.MimeMessage;
import java.util.Date;

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
    private HttpClientService httpClientService;

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
        RequestModel request = new RequestModel(PayConfig.mailServiceUrl);
        request.setCharset("GBK");
        request.addParam("uid", PayConfig.mailServiceUid);
        request.addParam("fr_name", PayConfig.mailServiceUname);
        request.addParam("fr_addr", PayConfig.mailServiceUid);
        request.addParam("mode", "html");
        subject = "[AutoMail][搜狗支付]" + subject + "[" + DateUtil.formatDate(new Date()) + "]";
        request.addParam("title", subject);
        String htmlContent = getMailText(freeMarkerConfigurer,templateFtl,content);
        request.addParam("body", htmlContent);
        String mailList="";
        for(String mailAddr: toAddress){
            mailList+=mailAddr+";";
        }
        mailList = mailList.substring(0, mailList.length()-1);
        request.addParam("maillist", mailList);
        try {
            httpClientService.executeStr(request);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

}
