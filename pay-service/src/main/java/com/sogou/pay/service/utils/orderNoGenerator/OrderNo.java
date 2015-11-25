package com.sogou.pay.service.utils.orderNoGenerator;

import org.springframework.stereotype.Service;


/**
 * @Author hgq
 * @Description:订单
 */
@Service
public class OrderNo extends AbstractSequence {

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
