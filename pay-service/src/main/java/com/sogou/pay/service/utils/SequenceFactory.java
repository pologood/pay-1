package com.sogou.pay.service.utils;

import javax.annotation.Resource;

import com.sogou.pay.common.utils.OrderNo;
import org.springframework.stereotype.Service;

/**
 * 单号生成器
 */
@Service
public class SequenceFactory {

  private final static String OD = "OD";// 订单
  private final static String TK = "TK";// 订单
  private final static String ZZ = "ZZ";// 转账
  private final static String ZF = "ZF";// 支付流水
  private final static String ZFD = "ZFD";// 支付单

  @Resource
  private PayNo payNo;
  @Resource
  private PayDetailNo payDetailNo;
  @Resource
  private RefundPayDetailNo refundPayDetailNo;
  @Resource
  private OrderNo orderNo;

  /**
   * 支付流水号 ：ZF+时间17位+序列号3位 共22位
   */
  public String getPayDetailId() {
    String no = null;
    try {
      no = ZF + payDetailNo.getNo();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return no;
  }

  /**
   * 退款流水号 ：TK+时间17位+序列号3位 共22位
   */
  public String getRefundDetailId() {
    String no = null;
    try {
      no = TK + refundPayDetailNo.getNo();
    } catch (Exception e) {
    }
    return no;
  }

  /**
   * 支付单号 ：ZFD+时间17位+3位序列 23位
   */
  public String getPayId() {
    String no = null;
    try {
      no = ZFD + payNo.getNo();
    } catch (Exception e) {
    }
    return no;
  }

  /**
   * 订单号 ：OD+时间17位+3位序列 23位
   * KPI使用
   */
  public String getOrderNo() {
    String no = null;
    try {
      no = OD + orderNo.getNo();
    } catch (Exception e) {
    }
    return no;
  }
}
