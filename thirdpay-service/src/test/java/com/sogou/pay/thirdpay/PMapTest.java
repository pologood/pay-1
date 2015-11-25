package com.sogou.pay.thirdpay;

import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.common.utils.XMLParseUtil;
import com.sogou.pay.thirdpay.biz.utils.Utils;

import org.junit.Test;

/**
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/2/28 15:41
 */
public class PMapTest extends BaseTest {

    @Test
    public void testPMapTest() {
        PMap pmap = new PMap();
        String return_code = pmap.getString("return_code");
        return_code = "SUCC";
        if (Utils.isEmpty(return_code) || !"SUCC".equals(return_code)) {
            String resContent = null;
        }

        String resContent = null;
        try {
            pmap = XMLParseUtil.doXMLParse(resContent);
        } catch (Exception e) {
        }
        if (Utils.isEmpty(pmap.getString("return_code"), pmap.getString("result_code"),
                          pmap.getString("sign"))) {
            System.out.print("result" + pmap);
        }
        String order_state = pmap.getString("order_state");
        ResultMap result = ResultMap.build();
        String ss = result.getStatus().getName();
        String sSs = result.getStatus().getMessage();
        System.out.print("result" + pmap);

        result.addItem("error_code", ResultStatus.THIRD_REFUND_ALI_SIGN_ERROR);
        result.addItem("error_msg", ResultStatus.THIRD_REFUND_ALI_SIGN_ERROR.getMessage());
        result.withError(ResultStatus.THIRD_REFUND_ALI_SIGN_ERROR);
        boolean ssssss = result.isSuccess(result);
        String sssss = result.getData().get("error_code").toString();
        System.out.print("result" + pmap);


    }

}
