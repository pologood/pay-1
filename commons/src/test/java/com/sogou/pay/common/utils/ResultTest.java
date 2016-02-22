package com.sogou.pay.common.utils;

import com.sogou.pay.common.BaseTest;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultList;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by hujunfei Date: 15-1-5 Time: 下午12:27
 */
public class ResultTest extends BaseTest {

    @Test
    public void testResultStatus() {
        assertEquals(ResultStatus.SUCCESS.toString(), "SUCCESS");
        assertTrue(ResultStatus.SUCCESS.getCode() == 0);
        assertEquals(ResultStatus.SUCCESS.getName(), "SUCCESS");
        assertEquals(ResultStatus.SUCCESS.getMessage(), "成功");
    }

    @Test
    public void testResult() {
        ResultMap result = ResultMap.build();
        assertTrue(result.getStatus() == ResultStatus.SUCCESS);
        result.withError(ResultStatus.SYSTEM_ERROR);
        assertEquals(result.getStatus().getName(), "SYSTEM_ERROR");
        assertEquals(result.getMessage(), ResultStatus.SYSTEM_ERROR.getMessage());
        result.withMessage("系统错误！！！");
        assertEquals(result.getMessage(), "系统错误！！！");
        result.addItem("name", "jerry");
        result.withReturn(ResultMap.build());

        System.out.println(result);

    }

    @Test
    public void testResultList() {
        ResultList result = (ResultList) queryS();
        System.out.println(result.addItem("b"));
    }

    @Test
    public void testNewResult() {
        /*com.sogou.pay.common.result.tmp.ResultList result = (com.sogou.pay.common.result.tmp.ResultList) com.sogou.pay.common.result.tmp.ResultList.build();
        result.withData(Arrays.asList("a", "b"));
        System.out.println(result.toString());
        com.sogou.pay.common.result.tmp.Result r = query();
        System.out.println(r);
        com.sogou.pay.common.result.tmp.ResultList list = (com.sogou.pay.common.result.tmp.ResultList) r;
        list.withItem("c");
        System.out.println(list);

        com.sogou.pay.common.result.tmp.Result result1 = com.sogou.pay.common.result.tmp.ResultList.build();*/
    }

    private Result<String> queryS() {
        ResultList resultList = ResultList.build();
        resultList.addItems(Arrays.asList("a", "b"));
        return resultList;
    }
}
