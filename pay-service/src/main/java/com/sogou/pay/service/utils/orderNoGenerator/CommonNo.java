package com.sogou.pay.service.utils.orderNoGenerator;

import org.springframework.stereotype.Service;


/**
 * @Author WJP
 * @Description:公共流水号1
 */
@Service
public class CommonNo extends AbstractSequence {

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
