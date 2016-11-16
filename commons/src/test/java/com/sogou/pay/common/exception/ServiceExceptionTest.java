package com.sogou.pay.common.exception;

import com.sogou.pay.common.BaseTest;
import com.sogou.pay.common.types.ResultStatus;

import org.junit.Test;

/**
 * Created by hujunfei Date: 15-1-5 Time: 下午1:23
 */
public class ServiceExceptionTest extends BaseTest {

    @Test
    public void testServiceException() {
        try {
            throw new ServiceException(ResultStatus.SYSTEM_ERROR);
        } catch (ServiceException e) {
            System.out.println(e);
            System.out.println(e.getErrorCode());
        }
    }
}
