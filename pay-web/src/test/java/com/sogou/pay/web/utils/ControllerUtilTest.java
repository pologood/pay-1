package com.sogou.pay.web.utils;

import com.sogou.pay.common.constraint.Amount;
import com.sogou.pay.web.BaseTest;
import com.sogou.pay.web.form.RefundParams;
import org.junit.Test;

import javax.validation.constraints.NotNull;

/**
 * Created by hjf on 15-3-2.
 */
public class ControllerUtilTest extends BaseTest {

    @Test
    public void testValidate() {
        TestBean bean = new TestBean();
        bean.setAmount("0.123");
        System.out.println(ControllerUtil.validateParams(bean));
    }

    class TestBean {
        @NotNull
        @Amount
        private String amount;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }

}
