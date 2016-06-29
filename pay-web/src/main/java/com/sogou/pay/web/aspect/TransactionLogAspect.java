package com.sogou.pay.web.aspect;

import com.sogou.pay.common.utils.JSONUtil;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by hujunfei Date: 15-1-21 Time: 上午10:36
 */
@Component
@Aspect
public class TransactionLogAspect {

    private static final Logger tLog = LoggerFactory.getLogger("orderLoggerAsync");
    private static final ThreadLocal<Map<String, Long>> timeWatch = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Long>> traceWatch = new ThreadLocal<>();
    private static final ThreadLocal<AtomicLong> processCounter = new ThreadLocal<>();
    private static final ThreadLocal<AtomicLong> traceCounter = new ThreadLocal<>();
    private static final ThreadLocal<AtomicLong> orderTimeSetter = new ThreadLocal<>();

    private static final String FILTER_KEYS = "appkey|appsecret|encrypt_key";
    private static String LOCALIP = null;

    private static Map<String, Long> getTimeMap() {
        if (timeWatch.get() == null) {
            synchronized (timeWatch) {
                if (timeWatch.get() == null) {
                    timeWatch.set(new HashMap<String, Long>());
                }
            }
        }
        return timeWatch.get();
    }

    private static Map<String, Long> getTraceMap() {
        if (traceWatch.get() == null) {
            synchronized (traceWatch) {
                if (traceWatch.get() == null) {
                    traceWatch.set(new HashMap<String, Long>());
                }
            }
        }
        return traceWatch.get();
    }

    private static AtomicLong getProcessCounter() {
        if (processCounter.get() == null) {
            synchronized (processCounter) {
                if (processCounter.get() == null) {
                    processCounter.set(new AtomicLong());
                }
            }
        }
        return processCounter.get();
    }

    private static AtomicLong getTraceCounter() {
        if (traceCounter.get() == null) {
            synchronized (traceCounter) {
                if (traceCounter.get() == null) {
                    traceCounter.set(new AtomicLong());
                }
            }
        }
        return traceCounter.get();
    }

    private static AtomicLong getOrderTimeSetter() {
        if (orderTimeSetter.get() == null) {
            synchronized (orderTimeSetter) {
                if (orderTimeSetter.get() == null) {
                    orderTimeSetter.set(new AtomicLong());
                }
            }
        }
        return orderTimeSetter.get();
    }

    public void beforeService(JoinPoint joinPoint) throws InterruptedException {
        recordBefore(joinPoint);
    }

    public void afterReturningService(JoinPoint joinPoint, Object retVal) {
        recordReturning(joinPoint, retVal);
    }

    public void afterThrowingService(JoinPoint joinPoint, Throwable ex) {
        recordThrowing(joinPoint, ex);
    }

    private void recordBefore(JoinPoint joinPoint) {
        Long start = System.currentTimeMillis();
        if (getTimeMap().isEmpty()) {
            getProcessCounter().incrementAndGet();
            getTraceCounter().set(1);
            getOrderTimeSetter().set(start);
        } else {
            getTraceCounter().incrementAndGet();
        }
        String key = joinPoint.getThis().toString();
        getTraceMap().put(key, getTraceCounter().get());
        getTimeMap().put(key, start);
        // record(sb.toString());
    }

    private void recordReturning(JoinPoint joinPoint, Object retVal) {
        StringBuilder sb = newSb(joinPoint);
        sb.append("\t").append(JSONUtil.Bean2JSON(retVal).replaceAll("[\\t\\n]", " "));
        record(sb.toString());
    }

    private void recordThrowing(JoinPoint joinPoint, Throwable ex) {
        StringBuilder sb = newSb(joinPoint);
        sb.append("\t").append(ex.getClass().getSimpleName());
        if (ex.getMessage() != null) {
            sb.append("[").append(ex.getMessage().replaceAll("[\\t\\n]", " ")).append("]");
        }
        record(sb.toString());
    }

    // 通用参数封装成StringBuilder
    private StringBuilder newSb(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        List filterArgs = Arrays.asList(args);
        /*List filterArgs = new ArrayList(args.length);
        for (Object arg : args) {
            if (arg == null) {
                filterArgs.add(null);
            } else {
                String clazz = arg.getClass().getName();
                // com.sogou公司内部类及java SDK类对象显示对象内容，其他对象显示类名
                if (clazz.startsWith("com.sogou") || clazz.startsWith("java.")) {
                    filterArgs.add(arg);
                } else {
                    filterArgs.add(clazz);
                }
            }
        }*/
        String key = joinPoint.getThis().toString();
        StringBuilder sb = new StringBuilder();
        sb.append(getLocalIp(null));
        sb.append("\t").append(getOrderTimeSetter().get())
                .append("\t").append(Thread.currentThread().getId())
                .append("\t").append(getProcessCounter().get())
                .append("\t").append(getTraceMap().remove(key))
                .append("\t").append(getTraceCounter().incrementAndGet())
                .append("\t").append(signature.getDeclaringType().getSimpleName())
                .append("\t").append(signature.getMethod().getName());

        Long startV = getTimeMap().remove(key);
        long start = startV == null ? 0 : startV;
        long stop = System.currentTimeMillis();
        sb.append("\t").append(start);
        sb.append("\t").append(stop);
        sb.append("\t").append(stop - start);
        sb.append("\t").append(JSONUtil.Bean2JSON(filterArgs).replaceAll("[\\t\\n]", " "));

        return sb;
    }

    private void record(String log) {
        tLog.info(filter(log));
//        System.out.println(filter(log));
    }

    /**
     * 过滤敏感字段信息
     */
    private String filter(String log) {
        if (log == null) {
            return null;
        }

        String regex = "(?<=\"(" + FILTER_KEYS + ")\":\")[^\\s\"]*(?=(\\\\{2})*\"*)";
        return log.replaceAll(regex, "****");
    }

    private static String getLocalIp(HttpServletRequest request) {
        if (StringUtils.isEmpty(LOCALIP)) {
            synchronized (TransactionLogAspect.class) {
                try {
                    if (StringUtils.isEmpty(LOCALIP)) {
                        LOCALIP = InetAddress.getLocalHost().getHostAddress();
                    }
                } catch (Exception e) {
                    if (request != null) {
                        LOCALIP = request.getLocalAddr();
                    }
                }

            }
        }
        return LOCALIP;
    }
}
