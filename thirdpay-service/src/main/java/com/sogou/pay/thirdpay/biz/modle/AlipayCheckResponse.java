package com.sogou.pay.thirdpay.biz.modle;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author qibaichao
 * @ClassName AlipayClearResponse
 * @Date 2015年2月16日
 * @Description:
 */
public class AlipayCheckResponse {

    private boolean isSuccess = false; // 是否成功
    private boolean hasNext = false; // 是否有下一页

    private List<OutCheckRecord> records = new ArrayList<OutCheckRecord>(1000);

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<OutCheckRecord> getRecords() {
        return records;
    }

}
