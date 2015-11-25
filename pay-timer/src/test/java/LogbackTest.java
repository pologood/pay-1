
import com.sogou.pay.common.utils.DateUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * Created by hujunfei Date: 14-12-30 Time: 下午4:48
 */
public class LogbackTest extends BaseTest {

    private final static Logger log = LoggerFactory.getLogger(LogbackTest.class);

    @Test
    public void testLog() {
        for (int i = 0; i < 100; i++) {
            log.trace("======trace");
            log.debug("======debug");
            log.info("======info");
            log.warn("======warn");
            log.error("======error");
        }
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) throws Exception{
//        log.info("======info");
//        log.error("======error");

        System.out.println(DateUtil.parse("2015-04-21 12:07:20",DateUtil.DATE_FORMAT_SECOND));


        /**
         *
         * 获取充值的手续费 手续费收取 标准：个人银行 ：工，农，建，招，民生，中信 收取本金千分之2的手续费，其余银行 收取本金千分之3的手续费 企业
         * 收取 10元手续费
         *
         * 新手续费标准 工、农、建、中信、华夏，充值费率为1.4‰ ； 招行充值费率1.5‰ ； 其他银行: 2.5‰ ；
         *
         */
//        public static BigDecimal getRechargeFee(final BigDecimal amount,
//        final String bankcode, final String openBankType) {
//            if ("0".equals(openBankType)) {
//                if (CommonDef.CONSTANT_BANK[0][0].equals(bankcode)
//                        || CommonDef.CONSTANT_BANK[1][0].equals(bankcode)
//                        || CommonDef.CONSTANT_BANK[3][0].equals(bankcode)
//                        || CommonDef.CONSTANT_BANK[13][0].equals(bankcode)
//                        || CommonDef.CONSTANT_BANK[17][0].equals(bankcode)) {
//                    // 工、农、建、中信、华夏，充值费率为1.4‰
//                    return new BigDecimal(
//                            amount.multiply(LoanDef.RECHARE_MONTHLY_FEE_RATE_IACCH) + "")
//                            .setScale(2, BigDecimal.ROUND_HALF_UP);
//                } else if (CommonDef.CONSTANT_BANK[2][0].equals(bankcode)) {
//                    // 招行千分之1.5
//                    return new BigDecimal(
//                            amount.multiply(LoanDef.RECHARE_MONTHLY_FEE_RATE_CMB) + "")
//                            .setScale(2, BigDecimal.ROUND_HALF_UP);
//                } else {
//                    // 其他千分之2.5
//                    return new BigDecimal(
//                            amount.multiply(LoanDef.RECHARE_MONTHLY_FEE_RATE_OTHERBANK)
//                                    + "").setScale(2, BigDecimal.ROUND_HALF_UP);
//                }
//            }
//            return LoanDef.RECHARE_MONTHLY_FEE_RATE_10;
//
//        }

    }
}
