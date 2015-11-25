package com.sogou.pay.service.utils.orderNoGenerator;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * @Author WJP
 * @Description:单号生成器
 */
@Service
public class SequencerGenerator {

    private final static String ZF = "ZF";// 支付流水
    private final static String ZFD = "ZFD";// 支付单

    @Resource
    private PayNo payNo;
    @Resource
    private PayDetailNo payDetailNo;
    @Resource
    private RefundPayDetailNo refundPayDetailNo;
    @Resource
    private PayTransferBatchNo payTransferBatchNo;
    @Resource
    private PayTransferNo payTransferNo;
    @Resource
    private OrderNo orderNo;
    /**
     * 支付流水号 ：ZF+时间17位+序列号3位 共22位
     */
    public String getPayDetailId() {
        String number = null;
        try {
            number = ZF + payDetailNo.getNo();
        } catch (Exception e) {
        }
        return number;
    }

    /**
     * 退款流水号 ：TK+时间17位+序列号3位 共22位
     */
    public String getRefundDetailId() {
        String number = null;
        try {
            number = refundPayDetailNo.getNo();
        } catch (Exception e) {
        }
        return number;
    }

    /**
     * 支付单号 ：ZFD+时间17位+3位序列 23位
     */
    public String getPayId() {
        String number = null;
        try {
            number = ZFD + payNo.getNo();
        } catch (Exception e) {
        }
        return number;
    }

    /**
     * 代付单号
     *
     * @return
     */
    public String getPayTransferNo() {
        String number = null;
        try {
            number = payTransferNo.getNo();
        } catch (Exception e) {
        }
        return number;
    }

    /**
     * 代付-业务参考号
     *
     * @return
     */
    public String getPayTransferYurref() {
        String number = null;
        try {
            number = payTransferBatchNo.getNo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    public static void main(String[] args) {
        SequencerGenerator seq = new SequencerGenerator();
        System.out.println(seq.getPayDetailId());
    }
    
    /**
     * 订单号 ：OD+时间17位+3位序列 23位
     * KPI使用
     */
    public String getOrderNo() {
        String number = null;
        try {
            number = "OD" + orderNo.getNo();
        } catch (Exception e) {
        }
        return number;
    }
}
