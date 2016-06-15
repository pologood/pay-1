package com.sogou.pay.service.dao;

import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.manager.model.PayCheckUpdateModel;
import com.sogou.pay.BaseTest;
import com.sogou.pay.service.entity.PayCheck;
import com.sogou.pay.service.enums.AgencyCode;
import com.sogou.pay.service.enums.OrderType;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Author qibaichao
 * @ClassName PayCheckDaoTest
 * @Date 2015年2月28日
 * @Description:
 */
public class PayCheckDaoTest extends BaseTest {



    @Autowired
    private PayCheckDao payCheckDao;

    @Autowired
    private PayCheckWaitingDao payCheckWaitingDao;

    @Autowired
    private SequenceFactory sequencerGenerator;

    @Test
    public void batchInsertCheck() {

        try {
            String merchantNo = "sogoucaipiao";
            String checkDate = "20150303";
            String agencyCode = AgencyCode.ALIPAY.name();

            List<PayCheck> payCheckList = new ArrayList<PayCheck>();
            PayCheck payCheck = null;
            DecimalFormat dcmFmt = new DecimalFormat("0.00");
            Random rand = new Random();
            for (int i = 0; i < 1000; i++) {
                payCheck = new PayCheck();
                String instructId = sequencerGenerator.getPayDetailId();
                payCheck.setInstructId(instructId);
                payCheck.setOutOrderId(instructId);
                payCheck.setBizCode(OrderType.PAYCASH.getValue());
                payCheck.setOutTransTime(new Date());
                float f = rand.nextFloat() * 1000;
                payCheck.setBizAmt(new BigDecimal(dcmFmt.format(f)));
                payCheck.setCommissionFeeAmt(BigDecimal.valueOf(0));
                payCheck.setCheckDate(checkDate);
                payCheck.setAgencyCode(agencyCode);
                payCheck.setMerchantNo(merchantNo);
                payCheckList.add(payCheck);

                if (i % 1000 == 0) {
                    payCheckDao.batchInsert(payCheckList);
                    payCheckList.clear();
                }
            }
            if (payCheckList.size() != 0) {
                payCheckDao.batchInsert(payCheckList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Test
    public void batchUpdateStatus() {

        try {
            List<PayCheckUpdateModel> list = new ArrayList<PayCheckUpdateModel>();
            PayCheckUpdateModel payCheckUpdateVo = new PayCheckUpdateModel();
            payCheckUpdateVo.setInstructId("ZF20150318104155391002");
            payCheckUpdateVo.setPayCheckStatus(3);
            payCheckUpdateVo.setPayCheckId(3754);
            list.add(payCheckUpdateVo);

            PayCheckUpdateModel payCheckUpdateVo2 = new PayCheckUpdateModel();
            payCheckUpdateVo2.setInstructId("1111234274801201503182132785");
            payCheckUpdateVo2.setPayCheckStatus(3);
            payCheckUpdateVo2.setPayCheckId(3755);
            list.add(payCheckUpdateVo2);

            PayCheckUpdateModel payCheckUpdateVo3 = new PayCheckUpdateModel();
            payCheckUpdateVo3.setInstructId("1111234274801201503182146373");
            payCheckUpdateVo3.setPayCheckStatus(3);
            payCheckUpdateVo3.setPayCheckId(3756);
            list.add(payCheckUpdateVo3);


            list.add(payCheckUpdateVo2);
            payCheckDao.batchUpdateStatus(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getByPayDetailIdAndBizCode() {

        try {
            String instructId = "ZF20150317160007997002";
            int bizCode = 1;
            PayCheck payCheck =payCheckDao.getByInstructIdAndBizCode(instructId, bizCode);
            System.out.println(JSONUtil.Bean2JSON(payCheck));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void queryByPaymentAndDateAndBizCode() {

        try {
            String merchantNo = "sogoucaipiao";
            String checkDate = "20150303";
            String agencyCode = AgencyCode.ALIPAY.name();
            int bizCode = OrderType.PAYCASH.getValue();

            int BATCH_SIZE = 500;
            int page = 1;
            int total = 0;
            boolean hasNext = true;

            while (hasNext) {
                // 查询指定渠道、日期范围内，未对账成功的记录，每次查500条
                List<Map<String, Object>> list = payCheckDao
                        .queryByMerAndDateAndBizCode(checkDate, agencyCode,
                                 bizCode,
                                (page - 1) * BATCH_SIZE, BATCH_SIZE);


                int size = list.size();
                if (size > 0) {
                    total += size;
                    try {
                        System.out.println(list.size());
                    } catch (Exception e) {
                       e.printStackTrace();
                    }
                }
                // 查询结果数量小于每批次的数量，说明已经是最后一页了
                if (size < BATCH_SIZE) {
                    hasNext = false;
                }
                System.out.println("page=" + page);
                page++;
            }

            System.out.println("total=" + total);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
