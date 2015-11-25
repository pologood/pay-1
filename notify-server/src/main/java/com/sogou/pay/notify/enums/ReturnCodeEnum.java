package com.sogou.pay.notify.enums;

import java.util.HashSet;

/**
 * @Author qibaichao
 * @ClassName ReturnCodeEnum
 * @Date 2014年8月12日
 * @Description:系统返回码
 */
public enum ReturnCodeEnum {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 失败
     */
    FAILURE(1, "失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(2, "参数错误"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(3, "系统错误"),

    /************************ 账户表 *********************/

    ERROR(9, "d");

    /** The code. */
    private final int code;

    /** The desc. */
    private final String desc;

    private ReturnCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    private static HashSet<Integer> hashSet;

    static {
        hashSet = new HashSet<Integer>();
        hashSet.clear();
        for (ReturnCodeEnum returnStatus : ReturnCodeEnum.values()) {
            hashSet.add(returnStatus.getCode());
        }
    }

    public static boolean isDefined(int value) {
        if (hashSet.contains(value)) {
            return true;
        }
        return false;
    }

}
