package com.sogou.pay.web.controller.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sogou.pay.common.cache.RedisUtils;
import com.sogou.pay.common.result.Result;
import com.sogou.pay.common.result.ResultBean;
import com.sogou.pay.common.result.ResultMap;
import com.sogou.pay.common.result.ResultStatus;
import com.sogou.pay.common.utils.JsonUtil;
import com.sogou.pay.common.utils.PMap;
import com.sogou.pay.common.utils.PMapUtil;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.manager.payment.AppManager;
import com.sogou.pay.manager.payment.ChannelAdaptManager;
import com.sogou.pay.manager.payment.PayTranferRequestManager;
import com.sogou.pay.manager.payment.PayTransManager;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.service.utils.AppXmlPacket;
import com.sogou.pay.service.utils.orderNoGenerator.SequencerGenerator;
import com.sogou.pay.thirdpay.api.PayApi;
import com.sogou.pay.web.controller.BaseController;
import com.sogou.pay.web.form.PayTransParams;
import com.sogou.pay.web.form.PayTransferQueryParams;
import com.sogou.pay.web.form.PayTransferRefundQueryParams;
import com.sogou.pay.web.form.Record;
import com.sogou.pay.web.utils.ControllerUtil;
import com.sogou.pay.web.utils.ServletUtil;

/**
 * @Author huangguoqing
 * @ClassName PayController
 * @Date 2015年2月28日
 * @Description: 支付请求controller
 */
@Controller
@RequestMapping(value = "/payTrans")
@SuppressWarnings("all")
public class PayTransferController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PayTransferController.class);

    @Autowired
    private PayTransManager payTransManager;

    @Autowired
    private ChannelAdaptManager channelAdaptMaanger;

    @Autowired
    private AppManager appManager;

    @Autowired
    private SecureManager secureManager;

    @Autowired
    private PayApi payApi;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SequencerGenerator sequencerGenerator;

    @Autowired
    private PayTranferRequestManager payTranferRequestManager;

    /**
     * @param params  营销提交参数
     * @param request
     * @return ModelAndView
     * @Author huangguoqing
     * @MethodName doPay
     * @Date 2015年2月28日
     * @Description: 支付请求业务
     * 1.验证签名
     * 2.验证参数
     * 3.生成支付单数据
     * 4.支付业务处理
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/payTrans/doPay",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("/doPay")
    @ResponseBody
    public String doPay(PayTransParams params, HttpServletRequest request, HttpServletResponse response) {
        logger.info("【支付请求】进入dopay,请求参数为：" + JsonUtil.beanToJson(params));
        //将参数转化为map
        PMap<String, String> paramMap = PMapUtil.fromBean(params);
        //获得用户IP
        String ip = ServletUtil.getRealIp(request);
        paramMap.put("userIp", ip);
        /**1.验证签名**/
//        Result signResult = secureManager.verifyAppSign(params);
        Result signResult = ResultBean.build();
        if (!Result.isSuccess(signResult)) {
            logger.error("【支付请求】验证签名错误！");
            //获取业务平台签名失败,跳到错误页面
            return signResult.toString();
        }
        logger.info("【支付请求】通过验证签名！");
        /**2.验证参数**/
        ResultMap<List<String>> validateResult = checkParams(params);
        if (!Result.isSuccess(validateResult)) {
            return validateResult.toString();
        }
        logger.info("【支付请求】通过验证参数！");
        /**3.生成代付单信息**/
        //代付处理
        Result doProcessResult = payTransManager.doProcess(paramMap, validateResult.getReturnValue());
        if (!Result.isSuccess(doProcessResult))
            logger.error("【支付请求】" + doProcessResult.getStatus().getMessage());
        logger.info("【支付请求】支付请求结束！");
        return doProcessResult.toString();
    }

    /**
     * @param batchNo
     * @param request
     * @return ModelAndView
     * @Author qibaichao
     * @MethodName doRequest
     * @Date 2015年06月16日
     * @Description:
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/payTrans/doRequest",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("/doRequest")
    @ResponseBody
    public String doRequest(String appId, String batchNo) {
        logger.info("【代付提交】进入doRequest,请求参数为appId:" + appId + ",batchNo：" + batchNo);
        Result result = ResultBean.build();
        /**2.验证参数**/
        if (batchNo == null || "".equals(batchNo)) {
            logger.error("【代付提交参数错误】");
            return result.withError(ResultStatus.PARAM_ERROR).toString();
        }
        result = payTranferRequestManager.doProcess(appId, batchNo);
        logger.info("【代付提交结束！】");
        return result.toString();
    }

    /**
     * @param batchNo
     * @param request
     * @return ModelAndView
     * @Author qibaichao
     * @MethodName queryByBatchNo
     * @Date 2015年06月16日
     * @Description:
     */
    @Profiled(el = true, logger = "webTimingLogger", tag = "/payTrans/queryByBatchNo",
            timeThreshold = 500, normalAndSlowSuffixesEnabled = true)
    @RequestMapping("/queryByBatchNo")
    @ResponseBody
    public String queryByBatchNo(PayTransferQueryParams payTransferQueryParams) {
        logger.info("【代付查询】queryByBatchNo,请求参数为：" + JsonUtil.beanToJson(payTransferQueryParams));
        AppXmlPacket appXmlPacket = new AppXmlPacket();
        /**2.验证参数**/
        if (StringUtil.isBlank(payTransferQueryParams.getAppId())
                || StringUtil.isBlank(payTransferQueryParams.getBatchNo())
                || StringUtil.isBlank(payTransferQueryParams.getSign())
                || StringUtil.isBlank(payTransferQueryParams.getSignType())) {
            logger.error("【代付查询参数错误】");
            appXmlPacket.withError(ResultStatus.PARAM_ERROR);
            return appXmlPacket.toQueryXmlString();
        }
        /**1.验证签名**/
//        Result signResult = secureManager.verifyAppSign(payTransferQueryParams);
//        if(!Result.isSuccess(signResult)){
//            logger.error("【支付请求】验证签名错误！");
//          //获取业务平台签名失败,跳到错误页面
//          return signResult.toString();
//        }
        appXmlPacket = payTransManager.queryByBatchNo(payTransferQueryParams.getAppId(), payTransferQueryParams.getBatchNo());
        logger.info("【代付查询结束】");
        return appXmlPacket.toQueryXmlString();
    }

    @RequestMapping("/queryRefund")
    @ResponseBody
    public String queryRefund(PayTransferRefundQueryParams payTransferRefundQueryParams) {
        logger.info("【代付退票查询】queryRefund,请求参数为：" + JsonUtil.beanToJson(payTransferRefundQueryParams));
        AppXmlPacket appXmlPacket = new AppXmlPacket();
        /**2.验证参数**/
        if (StringUtil.isBlank(payTransferRefundQueryParams.getAppId())
                || StringUtil.isBlank(payTransferRefundQueryParams.getStartTime())
                || StringUtil.isBlank(payTransferRefundQueryParams.getEndTime())
                || StringUtil.isBlank(payTransferRefundQueryParams.getSign())
                || StringUtil.isBlank(payTransferRefundQueryParams.getSignType())) {
            logger.error("【代付退票查询参数错误】");
            appXmlPacket.withError(ResultStatus.PARAM_ERROR);
            return appXmlPacket.toRefundXmlString();
        }
        /**1.验证签名**/
//        Result signResult = secureManager.verifyAppSign(payTransferQueryParams);
//        if(!Result.isSuccess(signResult)){
//            logger.error("【支付请求】验证签名错误！");
//          //获取业务平台签名失败,跳到错误页面
//          return signResult.toString();
//        }
        appXmlPacket = payTransManager.queryRefund(payTransferRefundQueryParams.getStartTime(), payTransferRefundQueryParams.getEndTime(),
                payTransferRefundQueryParams.getRecBankacc(), payTransferRefundQueryParams.getRecName());
        logger.info("【代付退票查询结束】");
        return appXmlPacket.toRefundXmlString();
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
    public ResultMap<List<String>> checkParams(PayTransParams params) {
        ResultMap<List<String>> returnResult = ResultMap.build();

        //格式校验
        List validateResult = ControllerUtil.validateParams(params);
        if (validateResult.size() > 0) {
            //验证参数失败，调到错误页面
            logger.error("【支付请求】" + validateResult.toString().substring(1, validateResult.toString().length() - 1));
            returnResult.withError(ResultStatus.PARAM_ERROR);
            return returnResult;
        }
        //代付单校验
        Record record = null;
        List<String> payIdList = new ArrayList<String>();
        List<String> repeatPayIdList = new ArrayList();
        try {
            List<JSONObject> recordListParam = JSONObject.parseObject(params.getRecordList(), List.class);
            for (JSONObject r : recordListParam) {
                record = new Record();
                String payId = r.getString("payId");
                record.setPayId(payId);
                record.setRecBankacc(r.getString("recBankacc"));
                record.setRecName(r.getString("recName"));
                record.setPayAmt(r.getString("payAmt"));
                record.setBankFlg(r.getString("bankFlg"));
                record.setEacBank(r.getString("eacBank"));
                record.setEacCity(r.getString("eacCity"));
                record.setDesc(r.getString("desc"));
                //系统内表示只能是Y或N
                if (!StringUtil.isEmpty(record.getBankFlg())) {
                    if ((!"Y".equals(record.getBankFlg())) && (!"N".equals(record.getBankFlg()))) {
                        logger.error("【支付请求】系统内表示字段只能是Y或N。代付单号为：" + payId);
                        returnResult.withError(ResultStatus.PARAM_ERROR);
                        return returnResult;
                    }
                    //当BNKFLG=N时，他行开户行以及他行开户地必填
                    if (("N".equals(record.getBankFlg())) &&
                            (StringUtil.isEmpty(record.getEacBank()) || StringUtil.isEmpty(record.getEacCity()))) {
                        logger.error("【支付请求】当BNKFLG=N时，他行开户行以及他行开户地必填。代付单号为：" + payId);
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
                if (payIdList.contains(payId)) {
                    repeatPayIdList.add(payId);
                    continue;
                }
                payIdList.add(r.getString("payId"));
            }
            if (repeatPayIdList.size() > 0) {
                String payIds = null;
                for (String payId : repeatPayIdList)
                    payIds = payId + ",";
                logger.error("【代付请求】提交代付单中有相同的代付单号!重复的代付单号为：" + payIds.substring(0, payIds.length() - 1));
                returnResult.withError(ResultStatus.REPEAT_ORDER_ERROR);
                return returnResult;
            }
        } catch (Exception e) {
            logger.error("【支付请求】系统错误！");
            returnResult.withError(ResultStatus.SYSTEM_ERROR);
            return returnResult;
        }
        returnResult.withReturn(payIdList);
        return returnResult;
    }
}
