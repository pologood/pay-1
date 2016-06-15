
package com.sogou.pay.service.utils.email;

import com.sogou.pay.common.http.client.HttpService;
import com.sogou.pay.common.utils.DateUtil;
import com.sogou.pay.service.config.PayConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class EmailSender extends EmailSenderAbstract implements EmailSenderInterface {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void sendEmail(String templateFtl, String subject, String content, String... toAddress) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", PayConfig.mailServiceUid);
        params.put("fr_name", PayConfig.mailServiceUname);
        params.put("fr_addr", PayConfig.mailServiceUid);
        params.put("mode", "html");
        subject = "[AutoMail][搜狗支付]" + subject + "[" + DateUtil.formatDate(new Date()) + "]";
        params.put("title", subject);
        String htmlContent = getMailText(freeMarkerConfigurer,templateFtl,content);
        params.put("body", htmlContent);
        String mailList="";
        for(String mailAddr: toAddress){
            mailList+=mailAddr+";";
        }
        mailList = mailList.substring(0, mailList.length()-1);
        params.put("maillist", mailList);
        try {
            HttpService.getInstance().doPost(PayConfig.mailServiceUrl, params, "GBK", null);
        }catch (Exception e){
            logger.error("[sendEmail] failed, {}",e);
        }
    }

}
