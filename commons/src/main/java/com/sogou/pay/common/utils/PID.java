package com.sogou.pay.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * 获取当前jvm pid的工具类
 * http://blog.igorminar.com/2007/03/how-java-application-can-discover-its.html
 * User: liwei
 * Date: 1/6/15
 * Time: 17:20 PM
 */
public class PID {

    private static Integer pid;

    private static final Logger LOGGER = LoggerFactory.getLogger(PID.class);

    private static boolean isSunJVM() {
        try {
            Class.forName("sun.misc.Unsafe");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean hasBashEnv() {
        String osName = System.getProperty("os.name");
        if (osName == null) {
            LOGGER.debug("No system property os.name exists, ignore");
            return false;
        }

        LOGGER.debug("Using osName {}", osName);
        return osName.startsWith("Mac") || osName.contains("Linux");
    }

    public static int get(boolean required) {
        return pid != null ? pid : (pid = readPID(required));
    }

    private static int readPID(boolean required) {
        if (isSunJVM()) {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            //name is $pid@$hostname
            String[] parts = name.split("@");
            if (parts != null && parts.length == 2) {
                try {
                    return Integer.parseInt(parts[0]);
                } catch (NumberFormatException ignored) {

                }
            }

            LOGGER.debug("ManagementFactory.getRuntimeMXBean().getName()  returns unexpected result '{}'", name);
        }

        if (hasBashEnv()) {
            byte[] out = new byte[100];
            String[] cmd = {"bash", "-c", "echo $PPID"};
            try {
                Process p = Runtime.getRuntime().exec(cmd);
                p.getInputStream().read(out);
                return Integer.parseInt(new String(out));
            } catch (IOException e) {
                LOGGER.debug("echo $$PPID returns unexpected result '{}'", new String(out));
            }
        }

        String pid = System.getProperty("pid");
        if (pid != null) {
            try {
                return Integer.parseInt(pid);
            } catch (NumberFormatException e) {
                LOGGER.debug("System property pid '{}' is not a number", pid);
            }
        }

        if (required) {
            throw new IllegalStateException("Can not determine pid. Please declare the system property -Dpid=$$");
        }

        return -1;
    }

}
