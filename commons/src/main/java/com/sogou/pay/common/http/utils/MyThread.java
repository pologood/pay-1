package com.sogou.pay.common.http.utils;
public class MyThread extends Thread{
    String ip = null;//定义线程内变量
    public MyThread(String ip){//定义带参数的构造函数,达到初始化线程内变量的值
       this.ip=ip;
    }
    @Override
    public void run() {
        
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    
}
