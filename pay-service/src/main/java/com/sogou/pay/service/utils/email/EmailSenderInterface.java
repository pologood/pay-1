package com.sogou.pay.service.utils.email;


public interface EmailSenderInterface {


    public void sendEmail(String templateFtl, String subject, String content, String... toAddress) ;

}
