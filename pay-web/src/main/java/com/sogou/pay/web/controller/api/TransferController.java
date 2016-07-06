package com.sogou.pay.web.controller.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sogou.pay.common.utils.BeanUtil;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.service.AppService;
import com.sogou.pay.web.form.TransferForm;

import org.apache.commons.lang3.StringUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.web.manager.api.TransferManager;
import com.sogou.pay.web.manager.SecureManager;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.form.TransferQueryForm;
import com.sogou.pay.web.form.TransferRefundQueryForm;
import com.sogou.pay.web.form.TransferRecord;
import com.sogou.pay.web.utils.ControllerUtil;
import com.sogou.pay.web.utils.ServletUtil;


//代付、转账API
@Controller
public class TransferController extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

  @Autowired
  private AppService appService;

  @Autowired
  private TransferManager transferManager;

  @Autowired
  private SecureManager secureManager;

  private ResultMap commonCheck(Object params) {
    ResultMap resultMap = ResultMap.build();
    //验证参数
    List validateResult = ControllerUtil.validateParams(params);
    if (validateResult.size() > 0) {
      logger.error("[doPay][validateParams][Failed]{}", validateResult.toString().substring(1, validateResult.toString().length() - 1));
      return (ResultMap) resultMap.withError(ResultStatus.PARAM_ERROR);
    }
    PMap paramsMap = BeanUtil.Bean2PMap(params);
    //查询业务线信息
    int appId = paramsMap.getInt("appId");
    App app = appService.selectApp(appId);
    if (app == null) {
      logger.error("[commonCheck] appid not exists, params={}", JSONUtil.Bean2JSON(params));
      return (ResultMap) resultMap.withError(ResultStatus.APPID_NOT_EXIST);
    }
    //验证签名
    String secret = app.getSignKey();
    Result result = secureManager.verifyAppSign(paramsMap, signExcludes, secret);
    if (!Result.isSuccess(result)) {
      logger.info("[doPay][verifyAppSign][Failed]{}", result.getStatus());
      return (ResultMap) resultMap.withError(result.getStatus());
    }
    paramsMap.put("app", app);
    return (ResultMap) resultMap.withReturn(paramsMap);
  }


  /**
   * 发起代付请求
   */
  @Profiled(el = true, logger = "webTimingLogger", tag = "/payTrans/doPay",
          timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/api/transfer"}, produces = "application/json; charset=utf-8")
  @ResponseBody
  public Result doPay(TransferForm params, HttpServletRequest request, HttpServletResponse response) {

    ResultMap resultMap;

    resultMap = commonCheck(params);
    if (!Result.isSuccess(resultMap)) {
      return resultMap;
    }

    PMap paramsMap = (PMap) resultMap.getReturnValue();

    paramsMap.put("userIp", ServletUtil.getRealIp(request));

    /**2.验证参数**/
    ResultMap<List<String>> validateResult = checkParams(params);
    if (!Result.isSuccess(validateResult)) {
      return validateResult;
    }
    logger.info("【支付请求】通过验证参数！");
    /**3.生成代付单信息**/
    //代付处理
    Result doProcessResult = transferManager.doProcess(paramsMap, validateResult.getReturnValue());
    if (!Result.isSuccess(doProcessResult))
      logger.error("【支付请求】" + doProcessResult.getStatus().getMessage());
    logger.info("【支付请求】支付请求结束！");
    return doProcessResult;
  }

  @Profiled(el = true, logger = "webTimingLogger", tag = "/payTrans/queryByBatchNo",
          timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
  @RequestMapping(value = {"/api/transfer/query"}, produces = "application/json; charset=utf-8")
  @ResponseBody
  public Result queryByBatchNo(TransferQueryForm payTransferQueryParams) {
    logger.info("【代付查询】queryByBatchNo,请求参数为：" + JSONUtil.Bean2JSON(payTransferQueryParams));
    //AppXmlPacket appXmlPacket = new AppXmlPacket();
    ResultMap result;
    /**2.验证参数**/
    if (StringUtils.isBlank(payTransferQueryParams.getAppId())
            || StringUtils.isBlank(payTransferQueryParams.getBatchNo())
            || StringUtils.isBlank(payTransferQueryParams.getSign())
            || StringUtils.isBlank(payTransferQueryParams.getSignType())) {
      logger.error("【代付查询参数错误】");
      result = ResultMap.build(ResultStatus.PARAM_ERROR);
      return result;
    }
    /**1.验证签名**/
    Result signResult = secureManager.verifyAppSign(BeanUtil.Bean2Map(payTransferQueryParams), null, null);
    if (!Result.isSuccess(signResult)) {
      logger.error("【支付请求】验证签名错误！");
      //获取业务平台签名失败
      result = ResultMap.build(ResultStatus.VERIFY_SIGN_ERROR);
      return result;
    }
    result = transferManager.queryByBatchNo(payTransferQueryParams.getAppId(), payTransferQueryParams.getBatchNo());
    logger.info("【代付查询结束】");
    return result;
  }

  @RequestMapping(value = {"/api/transfer/refund/query"}, produces = "application/json; charset=utf-8")
  @ResponseBody
  public Result queryRefund(TransferRefundQueryForm payTransferRefundQueryParams) {
    logger.info("【代付退票查询】queryRefund,请求参数为：" + JSONUtil.Bean2JSON(payTransferRefundQueryParams));
    //AppXmlPacket appXmlPacket = new AppXmlPacket();
    ResultMap result;
    /**2.验证参数**/
    if (StringUtils.isBlank(payTransferRefundQueryParams.getAppId())
            || StringUtils.isBlank(payTransferRefundQueryParams.getStartTime())
            || StringUtils.isBlank(payTransferRefundQueryParams.getEndTime())
            || StringUtils.isBlank(payTransferRefundQueryParams.getSign())
            || StringUtils.isBlank(payTransferRefundQueryParams.getSignType())) {
      logger.error("【代付退票查询参数错误】");
      result = ResultMap.build(ResultStatus.PARAM_ERROR);
      return result;
    }
    /**1.验证签名**/
    Result signResult = secureManager.verifyAppSign(BeanUtil.Bean2Map(payTransferRefundQueryParams), null, null);
    if (!Result.isSuccess(signResult)) {
      logger.error("【支付请求】验证签名错误！");
      //获取业务平台签名失败,跳到错误页面
      result = ResultMap.build(ResultStatus.VERIFY_SIGN_ERROR);
      return result;
    }
    result = transferManager.queryRefund(payTransferRefundQueryParams.getStartTime(), payTransferRefundQueryParams.getEndTime(),
            payTransferRefundQueryParams.getRecBankacc(), payTransferRefundQueryParams.getRecName());
    logger.info("【代付退票查询结束】");
    return result;
  }


  /**
   * 校验参数
   *
   * @param params
   * @return 校验结果
   * @Author huangguoqing
   * @MethodName checkParams
   * @Date 2015年6月4日
   * @Description:
   */
  public ResultMap<List<String>> checkParams(TransferForm params) {
    ResultMap<List<String>> returnResult = ResultMap.build();

    //代付单校验
    List<String> payIdList = new ArrayList<String>();
    List<String> repeatPayIdList = new ArrayList();
    try {
      List<Map> recordListParam = JSONUtil.JSON2Bean(params.getRecordList(), ArrayList.class);
      for (Map map : recordListParam) {
        TransferRecord record = BeanUtil.Map2Bean(map, TransferRecord.class);
        //系统内表示只能是Y或N
        if (!StringUtils.isEmpty(record.getBankFlg())) {
          if ((!"Y".equals(record.getBankFlg())) && (!"N".equals(record.getBankFlg()))) {
            logger.error("【支付请求】系统内表示字段只能是Y或N。代付单号为：" + record.getPayId());
            returnResult.withError(ResultStatus.PARAM_ERROR);
            return returnResult;
          }
          //当BNKFLG=N时，他行开户行以及他行开户地必填
          if (("N".equals(record.getBankFlg())) &&
                  (StringUtils.isEmpty(record.getEacBank()) || StringUtils.isEmpty(record.getEacCity()))) {
            logger.error("【支付请求】当BNKFLG=N时，他行开户行以及他行开户地必填。代付单号为：" + record.getPayId());
            returnResult.withError(ResultStatus.PARAM_ERROR);
            return returnResult;
          }
        }
        List validateRecord = ControllerUtil.validateParams(record);
        if (validateRecord.size() > 0) {
          //验证参数失败
          logger.error("【支付请求】" + validateRecord.toString().substring(1, validateRecord.toString().length() - 1));
          returnResult.withError(ResultStatus.PARAM_ERROR);
          return returnResult;
        }
        //判断是否有重复的代付单号
        if (payIdList.contains(record.getPayId())) {
          repeatPayIdList.add(record.getPayId());
          continue;
        }
        payIdList.add(record.getPayId());
      }
      if (repeatPayIdList.size() > 0) {
        String payIds = null;
        for (String payId : repeatPayIdList)
          payIds = payId + ",";
        logger.error("【代付请求】提交代付单中有相同的代付单号!重复的代付单号为：" + payIds.substring(0, payIds.length() - 1));
        returnResult.withError(ResultStatus.ORDER_ALREADY_EXIST);
        return returnResult;
      }
    } catch (Exception e) {
      logger.error("【支付请求】系统错误！, {}", e);
      returnResult.withError(ResultStatus.SYSTEM_ERROR);
      return returnResult;
    }
    returnResult.withReturn(payIdList);
    return returnResult;
  }
}
