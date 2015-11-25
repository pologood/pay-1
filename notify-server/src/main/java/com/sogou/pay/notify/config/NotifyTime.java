package com.sogou.pay.notify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author qibaichao
 * @ClassName NotifyTime
 * @Date 2014年9月18日
 * @Description:通知时间
 */
@Service
public class NotifyTime {

    @Value("${avg_notify}")
    public String AVG_NOTIFY;

    @Value("${first_notify}")
    public String FIRST_NOTIFY;

    @Value("${second_notify}")
    public String SECOND_NOTIFY;

    @Value("${third_notify}")
    public String THIRD_NOTIFY;

    @Value("${fourth_notify}")
    public String FOURTH_NOTIFY;

    @Value("${fifth_notify}")
    public String FIFTH_NOTIFY;

}
