package com.sogou.pay.service.dao;

import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.service.AppService;
import org.junit.Test;
import org.mockito.internal.matchers.Contains;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * User: Liwei
 * Date: 2014/12/25
 * Time: 10:50
 */
public class AppTest extends BaseTest {
    @Autowired
    private AppService appService;


    @Test
    public void addApp(){
        App app = new App();
        app.setAppName("搜狗某产品");
        app.setAppId(2001);
        app.setBelongCompany(1);
        app.setSignKey("862653da5865293b1ec8cc");
        app.setStatus(1);
        app.setCreateTime(new Date());
        app.setModifyTime(new Date());
        assertEquals(1, appService.insertApp(app));
    }

    @Test
    public void selectApp() {
        try {
            App app = appService.selectApp(1999);
            assertEquals(1999,app.getAppId().intValue());
        } catch (Exception e){
            e.printStackTrace();

        }
    }

    @Test
    public void selectAppList() {
        String result = appService.selectAppList().toString();
        assertThat(result, new Contains("appid"));
        assertThat(result, new Contains("appkey"));
        assertThat(result, new Contains("appsecret"));

    }



/*   @Test
    public void updateApp() {
        App app = new App();
        app.setAppId(1100);
        app.setSignKey("110b57b4671f307372cb0c67caae42d27af0ab1b9f21c32b72698ba4f507c570");
        appService.updateApp(app);
        System.out.println("updateApp success");
    }*/

    @Test
    public void deleteApp() {
        appService.deleteApp(2001);
        System.out.println("deleteApp success");
    }


}
