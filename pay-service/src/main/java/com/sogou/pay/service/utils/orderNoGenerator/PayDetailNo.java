package com.sogou.pay.service.utils.orderNoGenerator;

import org.springframework.stereotype.Service;


/**
 * @Author hgq
 * @Description:支付单流水号
 */
@Service
public class PayDetailNo extends AbstractSequence {

    private static final String DATA_FORMAT = "yyyyMMddHHmmss";

    @Override
    public String getDateFormat() {
        return DATA_FORMAT;
    }

    @Override
    public int getSecondPartLength() {
        return 3;
    }

    public static void main(String[] args) {
    	
	}
}
