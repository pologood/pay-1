import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by hujunfei Date: 14-12-26 Time: 下午6:49
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:*.xml")
public class BaseTest extends Assert {
    @Before
    public void baseBefore() {
        System.out.println("-----------开始测试用例-----------");
    }
    
    @Test
    public void DefaultTestWhichDoNothing() {

    }

    @After
    public void baseAfter() {
        System.out.println("-----------结束测试用例----------");
    }
}
