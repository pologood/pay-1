/*
 * Copyright 2014-2016 cyou.cn All right reserved. This software is the
 * confidential and proprietary information of cyou.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with cyou.cn.
 */
package com.sogou.pay.service.utils.email;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author qibaichao
 * @ClassName EmailSenderAbstract
 * @Date 2014年9月12日
 * @Description:
 */
public abstract class EmailSenderAbstract {

    /**
     * @Author qibaichao
     * @MethodName getMailText
     * @param templateFtl
     * @param content
     * @return
     * @throws Exception
     * @Date 2014年9月12日
     * @Description:
     *               通过模板构造邮件内容，参数content将替换模板文件中的${content}标签。
     */
    public String getMailText(FreeMarkerConfigurer freeMarkerConfigurer, String templateFtl, String content) {
        String htmlText = "";
        Template tpl;

        Map<String, String> map = new HashMap<String, String>();
        map.put("content", content);
        try {
            tpl = freeMarkerConfigurer.getConfiguration().getTemplate(templateFtl);
            htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, map);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return htmlText;
    }

}
