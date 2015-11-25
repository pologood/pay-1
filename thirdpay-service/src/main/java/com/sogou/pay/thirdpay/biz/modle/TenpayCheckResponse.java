package com.sogou.pay.thirdpay.biz.modle;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author qibaichao
 * @ClassName TenpayClearResponse
 * @Date 2015年2月16日
 * @Description:
 */
public class TenpayCheckResponse {

    private boolean isSuccess = false; // 是否成功

    private List<OutCheckRecord> records = new ArrayList<OutCheckRecord>(1000);

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<OutCheckRecord> getRecords() {
        return records;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (OutCheckRecord outClearRecord : records) {

            sb.append(outClearRecord + "\n");
        }
        return sb.toString();
    }

}
