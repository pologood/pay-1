package com.sogou.pay.service.utils.orderNoGenerator;

import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.service.utils.StringHelper;

/**
 * @Author qibaichao
 * @ClassName AbstractSequence
 * @Date 2014年8月14日
 * @Description:序号生成器框架实现，多线程安全 调用getNo()即可得到生成的序号
 */
public abstract class AbstractSequence {

    private long currentNumber;

    private String lastTime;

    public synchronized String getNo() throws Exception {
        String currentTime = getCurrentTime();
        if (currentTime.equals(lastTime)) {
            currentNumber++;
        } else {
            // 时间改变，回到起点
            currentNumber = 1;
            lastTime = currentTime;
        }
        return new StringBuffer().append(getFirstPart()).append(getSecondPart()).append(getThridPart()).toString();
    }

    protected String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getDateFormat());
        return dateFormat.format(new Date());
    }

    protected String getFirstPart() {
        return getEncryptString(lastTime);
    }

    protected String getThridPart() {
        String currentNumberString = String.valueOf(currentNumber);
        if (getSecondPartLength() < currentNumberString.length()) {
            throw new RuntimeException(String.format("Number %s is overflow!!", currentNumber));
        }
        return new DecimalFormat(StringUtil.getZeroString(getSecondPartLength())).format(currentNumber);
    }

    protected String getSecondPart() {

        String ipSuffix = "";
        try {
            InetAddress ia = InetAddress.getLocalHost();
            String ipAddress = ia.getHostAddress();
            String[] atoms = ipAddress.split("\\.");
            ipSuffix = atoms[atoms.length - 1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        String defaultString = getZeroString(3);
        DecimalFormat df = new DecimalFormat(defaultString);
        return df.format(Long.parseLong(ipSuffix));
    }

    public static String getZeroString(int length) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append("0");
        }
        return buffer.toString();
    }

    public String getEncryptString(String arg) {
        return arg;
    }

    public abstract String getDateFormat();

    public abstract int getSecondPartLength();

}
