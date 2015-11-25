package com.sogou.pay.web;

import com.sogou.pay.common.http.utils.HttpUtil;
import com.sogou.pay.common.utils.MapUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import static java.lang.System.*;

/**
 * Created by hujunfei Date: 14-12-26 Time: 下午6:49 <br/>
 * testGet(url)                                     <br/>
 * testGet(url, params)                             <br/>
 * testPost(url)                                    <br/>
 * testPost(url, params)                            <br/>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "classpath:*.xml")
public class BaseTest extends Assert {
    private static HandlerMapping handlerMapping;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    public void before() {

    }

    public void after() {

    }

    @Before
    public void baseBefore() {
        System.out.println("-----------开始测试用例----------");
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        before();
    }

    @After
    public void baseAfter() {
        after();
        System.out.println("-----------结束测试用例----------");
    }

    @Test
    public void justForTest() throws Exception {
        String url = "/web/gateway";
        Map<String, Object> params = new HashMap<>();

        testGet(url, params, null);
    }

    // ---------------------------------------------Mock测试调用方法---------------------------------------------
    // testGet/testPost两种调用方式，不需要启动Servlet服务器，直接测试Controller

    protected void testGet(String url) {
        testGet(url, null, null);
    }

    protected void testPost(String url) {
        testPost(url, null, null);
    }

    protected void testGet(String url, Map params) {
        testGet(url, params, null);
    }

    protected void testPost(String url, Map params) {
        testPost(url, params, null);
    }

    @SuppressWarnings("unchecked")
    protected void testGet(String url, Map params, Map headers) {
        handle(HttpMethod.GET, url, params, headers);
    }

    @SuppressWarnings("unchecked")
    protected void testPost(String url, Map params, Map headers) {
        handle(HttpMethod.POST, url, params, headers);
    }

    private void handle(HttpMethod method, String url, Map<String, Object> params, Map<String, Object> headers) {
        try {
            MockHttpServletRequestBuilder builder = request(method, url);
            addParams(builder, params);
            addHeaders(builder, headers);

            String mediaType = "application/json; charset=UTF-8";
            // String mediaType = "application/x-www-form-urlencoded; charset=UTF-8";
            builder.accept(MediaType.parseMediaType(mediaType));

            ResultActions resultActions = mockMvc.perform(builder);
            // resultActions.andDo(print());
            // resultActions.andExpect(status().isOk());
            MvcResult result = resultActions.andReturn();
            MockHttpServletResponse response = result.getResponse();
            int statusCode = response.getStatus();

            out.println("Request Url: \t\t" + url);
            out.println("Request Params: \t" + params);
            if (method == HttpMethod.GET) {
                out.println("Request Get Url: " + HttpUtil.packHttpGetUrl("http://localhost:8090" + url, params));
            }
            out.println();
            out.println("Response Status: \t" + statusCode);
            switch (HttpStatus.valueOf(statusCode)) {
                case OK:
                    String content = response.getContentAsString();
                    if (content == null || content.length() == 0) {
                        out.println("[返回内容为空或请求地址不存在]");
                    } else {
                        out.println("Response Content: \t" + response.getContentAsString());
                    }
                    break;
                case FOUND:
                    out.println("Response Redirect: \t" + response.getRedirectedUrl());
                    break;
                default:
                    err.println("请求错误");
                    err.println(response.getContentAsString());
            }
            assertTrue(statusCode == 200 || statusCode == 302);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addParams(MockHttpServletRequestBuilder builder, Map<String, Object> params) {
        if (params != null) {
            MapUtil.dropNulls(params);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.param(entry.getKey(), entry.getValue().toString());
            }
        }
    }

    private void addHeaders(MockHttpServletRequestBuilder builder, Map<String, Object> headers) {
        if (headers != null) {
            MapUtil.dropNulls(headers);
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue().toString());
            }
        }
    }
}
