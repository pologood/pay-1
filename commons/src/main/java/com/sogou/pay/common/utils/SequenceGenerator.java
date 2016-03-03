package com.sogou.pay.common.utils;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by xiepeidong on 2016/2/24.
 * thread-safe, lock-less sequence generator.
 */
public abstract class SequenceGenerator {


    private static class SequenceStatus {
        public long currentNumber;
        public String lastTime;
        public String workerId;
    }

    private static ThreadLocal<SequenceStatus> threadLocal = new ThreadLocal<SequenceStatus>() {
        @Override
        protected SequenceStatus initialValue() {
            SequenceStatus status = new SequenceStatus();
            status.currentNumber = 0;
            status.lastTime = null;
            status.workerId = SequenceGenerator.getWorkerId();
            return status;
        }
    };

    public String getNo() throws Exception {
        String currentTime = getCurrentTime();
        SequenceStatus status = threadLocal.get();
        if (currentTime.equals(status.lastTime)) {
            //同一时间点内计数
            status.currentNumber++;
        } else {
            // 时间点改变，重置计数
            status.currentNumber = 0;
            status.lastTime = currentTime;
        }
        return new StringBuffer().
                append(status.lastTime).
                append(status.workerId).
                append(String.format("%03d", status.currentNumber)).
                toString();
    }

    protected String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getDateFormat());
        return dateFormat.format(new Date());
    }

    protected static String getWorkerId() {
        long machineId = 0;
        long procId = 0;
        long threadId = 0;
        try {
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            String[] atoms = ipAddress.split("\\.");
            machineId = Long.parseLong(atoms[atoms.length - 1]);
            procId = new Random().nextInt(1000);
            threadId = Thread.currentThread().getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("%03d%03d%03d", machineId, procId, threadId);
    }

    public abstract String getDateFormat();
}
