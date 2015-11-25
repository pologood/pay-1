package com.sogou.pay.service.utils.email;

/**
 * @Author qibaichao
 * @ClassName EmailSender
 * @Date 2014年9月12日
 * @Description:
 */
public interface EmailSenderInterface {

    /**
     * @Author qibaichao
     * @MethodName sendEmail
     * @param templateFtl
     * @param subject
     * @param content
     * @param toAddress
     * @Date 2014年9月12日
     * @Description:
     *               发送html格式邮件
     */
    public void sendEmail(String templateFtl, String subject, String content, String... toAddress) ;

}
