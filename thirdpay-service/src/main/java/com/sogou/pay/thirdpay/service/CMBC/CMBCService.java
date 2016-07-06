package com.sogou.pay.thirdpay.service.CMBC;

import com.sogou.pay.common.enums.PayTransferStatus;
import com.sogou.pay.common.enums.PayTransferBatchStatus;
import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.http.HttpService;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.*;
import com.sogou.pay.thirdpay.service.ThirdpayService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.*;


/**
 * Created by xiepeidong on 2016/6/17.
 */
@Service
public class CMBCService implements ThirdpayService {
  private static final Logger log = LoggerFactory.getLogger(CMBCService.class);

  public static final String INPUT_CHARSET = "UTF-8";    // 字符编码格式

  /**
   * 业务模式编号
   */
  public static final String BUS_MOD_1 = "00001";

  /**
   * 业务类别
   */
  //代发工资
  public static final String BUSCOD_SALARY = "N03010";
  //代发
  public static final String BUSCOD_PAY = "N03020";
  //代扣
  public static final String BUSCOD_WITHHOLDING = "N03030";

  /**
   * 交易代码名称
   */
  //代发工资
  public static final String PAY_SALARY = "BYSA";
  //代发其他
  public static final String PAY_OTHER = "BYBK";


  @Override
  public ResultMap queryOrder(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap refundOrder(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap queryRefundOrder(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap downloadOrder(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  @Override
  public ResultMap prepareTransferInfo(PMap params) throws ServiceException {

    ResultMap result = ResultMap.build();

    String paramsStr = getTransferParams(params);

    Result httpResponse = HttpService.getInstance().doPost(params.getString("prepayUrl"), paramsStr, CMBCService.INPUT_CHARSET, null);
    if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
      log.error("[prepareTransferInfo] 招行代付HTTP请求失败, 参数: {}", JSONUtil.Bean2JSON(params));
      result.withError(ResultStatus.THIRD_HTTP_ERROR);
      return result;
    }
    String resContent = (String) httpResponse.getReturnValue();
    String reqNbr = parseResult(resContent);
    if (reqNbr == null) {
      result.withError(ResultStatus.THIRD_RESPONSE_PARAM_ERROR);
      return result;
    }
    return result;
  }

  @Override
  public ResultMap queryTransfer(PMap params) throws ServiceException {

    ResultMap result = ResultMap.build();
    boolean isDetail = params.getBoolean("isDetail");
    String paramsStr = null;

    if (isDetail) {
      paramsStr = getTransferDetailQueryParams(params);
    } else {
      paramsStr = getTransferQueryParams(params);
    }

    Result httpResponse = HttpService.getInstance().doPost(params.getString("queryUrl"), paramsStr, CMBCService.INPUT_CHARSET, null);
    if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
      log.error("[queryTransfer] 招行代付查询HTTP请求失败, 参数: {}", JSONUtil.Bean2JSON(params));
      result.withError(ResultStatus.THIRD_HTTP_ERROR);
      return result;
    }
    String resContent = (String) httpResponse.getReturnValue();

    if (isDetail) {
      PMap resultPMap = parseTransferQueryResult(resContent);
      return result.addItem("result", resultPMap);
    } else {
      List<PMap> resultPMap = parseTransferDetailQueryResult(resContent);
      return result.addItem("result", resultPMap);
    }
  }

  @Override
  public ResultMap queryTransferRefund(PMap params) throws ServiceException {
    ResultMap result = ResultMap.build();
    String paramsStr = getTransferQueryRefundParams(params);

    Result httpResponse = HttpService.getInstance().doPost(params.getString("queryRefundUrl"), paramsStr, CMBCService.INPUT_CHARSET, null);
    if (httpResponse.getStatus() != ResultStatus.SUCCESS) {
      log.error("[queryTransferRefund] 招行代付退票查询HTTP请求失败, 参数: {}", JSONUtil.Bean2JSON(params));
      result.withError(ResultStatus.THIRD_HTTP_ERROR);
      return result;
    }
    String resContent = (String) httpResponse.getReturnValue();

    List<String> resultList = parseTransferQueryRefundResult(resContent);
    return result.addItem("result", resultList);
  }

  public ResultMap getReqIDFromNotifyWebSync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifyWebAsync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifyWapSync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifyWapAsync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifySDKAsync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifyRefund(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap getReqIDFromNotifyTransfer(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap handleNotifyWebSync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap handleNotifyWebAsync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap handleNotifyWapSync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap handleNotifyWapAsync(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap handleNotifySDKAsync(PMap params) throws ServiceException {
    return handleNotifyWebAsync(params);
  }

  public ResultMap handleNotifyRefund(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  public ResultMap handleNotifyTransfer(PMap params) throws ServiceException {
    throw new ServiceException(ResultStatus.INTERFACE_NOT_IMPLEMENTED);
  }

  //直接代发代扣
  private String getTransferParams(PMap params) {

    PMap payTransferBatch = params.getPMap("payTransferBatch");
    List<PMap> payTransferList = (List<PMap>) params.get("payTransferList");

    PMap INFO = new PMap();
    INFO.put("FUNNAM", "AgentRequest");//接口名称
    INFO.put("DATTYP", "2");//请求包格式xml
    INFO.put("LGNNAM", params.getString("merchantNo"));//登录用户名

    PMap SDKATSRQX = new PMap();
    SDKATSRQX.put("BUSCOD", BUSCOD_PAY);//业务类别
    SDKATSRQX.put("BUSMOD", payTransferBatch.getString("BusMod"));//业务模式编号
    SDKATSRQX.put("TRSTYP", payTransferBatch.getString("TrsTyp"));//交易代码
    SDKATSRQX.put("DBTACC", payTransferBatch.getString("DbtAcc"));//转出账号/转入账号
    SDKATSRQX.put("BBKNBR", payTransferBatch.getString("BbkNbr"));//分行代码
    SDKATSRQX.put("SUM", payTransferBatch.getString("PlanAmt"));//总金额
    SDKATSRQX.put("TOTAL", payTransferBatch.getString("PlanTotal"));//总笔数
    SDKATSRQX.put("YURREF", payTransferBatch.getString("Yurref"));//业务参考号
    SDKATSRQX.put("MEMO", payTransferBatch.getString("Memo"));//用途

    List<PMap> SDKATDRQX = new ArrayList<>();

    for (PMap payTransfer : payTransferList) {
      PMap transfer = new PMap();
      transfer.put("ACCNBR", payTransfer.getString("RecBankAcc"));
      transfer.put("CLTNAM", payTransfer.getString("RecName"));
      transfer.put("TRSAMT", payTransfer.getString("PayAmt"));
      //跨行
      if (StringUtils.equals("N", payTransfer.getString("BankFlag"))) {
        transfer.put("BNKFLG", payTransfer.getString("BankFlag"));
        transfer.put("EACBNK", payTransfer.getString("OtherBank"));
        transfer.put("EACCTY", payTransfer.getString("OtherCity"));
      }
      //单笔序列号 付款说明中就是单笔序列号
      transfer.put("TRSDSP", payTransfer.getString("SerialNo"));
      SDKATDRQX.add(transfer);
    }

    PMap requestPMap = new PMap();
    requestPMap.put("INFO", INFO);
    requestPMap.put("SDKATSRQX", SDKATSRQX);
    requestPMap.put("SDKATDRQX", SDKATDRQX);
    return XMLUtil.Map2XML("xml", requestPMap);
  }

  /**
   * 生成概要请求报文
   */
  private String getTransferQueryParams(PMap params) {
    PMap payTransferBatch = params.getPMap("payTransferBatch");

    // 构造直接代发代扣的请求报文
    XmlPacket xmlPkt = new XmlPacket("GetAgentInfo", params.getString("merchantNo"));
    Map queryMap = new Properties();
    //业务类别
    queryMap.put("BUSCOD", payTransferBatch.getString("busCod"));
    //起始日期
    queryMap.put("BGNDAT", payTransferBatch.getString("createTime"));
    //结束日期
    queryMap.put("ENDDAT", DateUtil.formatCompactDate(new Date()));
    queryMap.put("BGNDAT", params.getString("queryDate"));
    //结束日期
    queryMap.put("ENDDAT", params.getString("queryDate"));
    //业务参考号
    queryMap.put("YURREF", payTransferBatch.getString("yurref"));
    xmlPkt.putProperty("SDKATSQYX", queryMap);
    return xmlPkt.toQueryXmlString();
  }

  /**
   * 生成详情请求报文
   */
  private String getTransferDetailQueryParams(PMap params) {

    String reqNbr = params.getString("reqNbr");
    XmlPacket xmlPkt = new XmlPacket("GetAgentDetail", params.getString("merchantNo"));
    Map queryMap = new Properties();
    //流程实例号
    queryMap.put("REQNBR", reqNbr);
    xmlPkt.putProperty("SDKATDQYX", queryMap);
    return xmlPkt.toQueryXmlString();
  }

  private String parseResult(String resContent) {
    XmlPacket pktRsp = XmlPacket.valueOf(resContent);
    if (pktRsp == null) {
      log.error("[parseResult] failed");
      return null;
    }
    if (pktRsp.isError()) {
      log.error("[parseResult] failed, errmsg={}", pktRsp.getERRMSG());
      return null;
    }
    Map propAcc = pktRsp.getProperty("NTREQNBRY", 0);
    //流程实例号
    String reqNbr = String.valueOf(propAcc.get("REQNBR"));
    log.info("[parseResult] success, reqNbr={}", reqNbr);
    return reqNbr;
  }

  /**
   * 处理概要返回的结果
   */
  private PMap parseTransferQueryResult(String resContent) {
    PMap payTransferBatch = null;
    XmlPacket pktRsp = XmlPacket.valueOf(resContent);
    if (pktRsp == null) {
      throw new RuntimeException("响应报文解析失败");
    }
    if (pktRsp.isError()) {
      log.error("【代发请求错误!】 :" + pktRsp.getERRMSG());
      return null;
    }
    Map propAgent = pktRsp.getProperty("NTQATSQYZ", 0);
    if (propAgent == null) {
      log.info("【查询交易概要信息,没有要处理的信息】 ");
      return null;
    }
    //业务请求状态
    String reqSta = String.valueOf(propAgent.get("REQSTA"));
    //判断业务请求状态 是否完成
    if (!"FIN".equals(reqSta)) {
      log.info("【查询交易概要信息,业务请求状态不是完成状态】 reqSta: {}", reqSta);
      return null;
    }
    //更新 代付批次状态 为成功
    payTransferBatch = new PMap();
    //业务处理结果
    String rtnFlg = String.valueOf(propAgent.get("RTNFLG"));
    // 流程实例号
    String reqNbr = String.valueOf(propAgent.get("REQNBR"));
    payTransferBatch.put("reqNbr", reqNbr);
    //业务处理结果是否成功
    if ("S".equals(rtnFlg)) {
      //修改代付单批次信息
      String sucNum = String.valueOf(propAgent.get("SUCNUM"));
      String sucAmt = String.valueOf(propAgent.get("SUCAMT"));
      payTransferBatch.put("TradeState", PayTransferBatchStatus.COMPLETION.getValue());
      ;
      payTransferBatch.put("ResultDesc", "");
      ;
      payTransferBatch.put("SucTotal", Integer.valueOf(sucNum));
      payTransferBatch.put("SucAmt", BigDecimal.valueOf(Double.valueOf(sucAmt)));
    } else {
      //业务处理结果是否失败
      String errDep = String.valueOf(propAgent.get("ERRDSP"));
      payTransferBatch.put("TradeState", PayTransferBatchStatus.FAIL.getValue());
      payTransferBatch.put("ResultDesc", errDep);
    }
    return payTransferBatch;
  }

  /**
   * 处理明细返回结果
   */
  private List<PMap> parseTransferDetailQueryResult(String resContent) {
    List<PMap> updateList = null;
    XmlPacket pktRsp = XmlPacket.valueOf(resContent);
    if (pktRsp == null) {
      throw new RuntimeException("响应报文解析失败");
    }
    if (pktRsp.isError()) {
      log.info("【查询交易明细信息，错误信息】，errorMsg: {}", pktRsp.getERRMSG());
      return null;
    }
    int size = pktRsp.getSectionSize("NTQATDQYZ");
    log.info("【查询交易明细信息】，size={}", size);
    if (size != 0) {
      updateList = new ArrayList<>();
      //代付序列号
      String serialNo = "";
      //付款状态S：成功；F：失败；C：撤消；I：数据录入
      String payStatus = "";
      //付款结果说明
      String resultDesc = "";
      PMap updatePayTransfer = null;
      for (int i = 0; i < size; i++) {
        updatePayTransfer = new PMap();
        Map propDtl = pktRsp.getProperty("NTQATDQYZ", i);
        serialNo = String.valueOf(propDtl.get("TRSDSP"));
        payStatus = String.valueOf(propDtl.get("STSCOD"));
        resultDesc = String.valueOf(propDtl.get("ERRDSP"));
        updatePayTransfer.put("serialNo", serialNo);
        //付款成功
        if ("S".equals(payStatus)) {
          //成功
          updatePayTransfer.put("PayStatus", PayTransferStatus.SUCCESS.getValue());
          updatePayTransfer.put("ResultDesc", "SUCCESS");
        } else {
          // 失败
          updatePayTransfer.put("PayStatus", PayTransferStatus.FAIL.getValue());
          updatePayTransfer.put("ResultDesc", resultDesc);
        }
        updateList.add(updatePayTransfer);
      }
    }
    return updateList;
  }


  /**
   * 生成查询账户交易信息 请求报文
   *
   * @return
   */
  private String getTransferQueryRefundParams(PMap params) {

    String beginDate = params.getString("beginDate");
    String endDate = params.getString("endDate");


    //构造查询账户交易信息xml
    XmlPacket xmlPkt = new XmlPacket("GetTransInfo", params.getString("merchantNo"));
    Map mpAccInfo = new Properties();
    //分行号
    mpAccInfo.put("BBKNBR", "59");
    //账号
    mpAccInfo.put("ACCNBR", "591902896010504");
    //起始日期
    mpAccInfo.put("BGNDAT", beginDate);
    //结束日期
    mpAccInfo.put("ENDDAT", endDate);
    //借贷码 C：收入 D：支出
//        mpAccInfo.put("AMTCDR", "D");
    xmlPkt.putProperty("SDKTSINFX", mpAccInfo);
    return xmlPkt.toQueryXmlString();
  }

  /**
   * 处理返回的结果
   */
  private List<String> parseTransferQueryRefundResult(String resContent) {

    XmlPacket pktRsp = XmlPacket.valueOf(resContent);
    if (pktRsp == null) {
      throw new RuntimeException("响应报文解析失败");
    }
    if (pktRsp.isError()) {
      log.error("【查询账户交易信息错误】,errorMsg: {}", pktRsp.getERRMSG());
      return null;
    }
    int size = pktRsp.getSectionSize("NTQTSINFZ");
    log.info("【查询账户交易信息】 size={}", size);
    if (size == 0) {
      return null;
    }
    //判断交易明细中是否有代发 退票记录
    List<String> yurrefList = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      Map propDtl = pktRsp.getProperty("NTQTSINFZ", i);
      String trsCode = String.valueOf(propDtl.get("TRSCOD"));
      String narYur = String.valueOf(propDtl.get("NARYUR"));
      // NARYUR=代发时，合作方余款退还
      if ("AGRD".equals(trsCode) && "代发时，合作方余款退还".equals(narYur)) {
        //业务参考号
        yurrefList.add(String.valueOf(propDtl.get("YURREF")));
      }
    }
    return yurrefList;
  }

}
