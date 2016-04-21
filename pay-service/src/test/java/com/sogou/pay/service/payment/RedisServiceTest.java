package com.sogou.pay.service.payment;

import com.sogou.pay.common.cache.RedisUtils;
import com.sogou.pay.BaseTest;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Liwei
 * Date: 2014/12/30
 * Time: 17:45
 */
public class RedisServiceTest extends BaseTest {
    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void set() {
        redisUtils.set("key1", "yes");
        String result = redisUtils.get("key1");
        Assert.assertEquals("yes", result);
    }
}
