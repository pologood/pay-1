package com.sogou.pay.manager.payment.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sogou.pay.common.exception.ServiceException;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.payment.PayManager;
import com.sogou.pay.service.entity.AgencyInfo;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.entity.Channel;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.service.entity.PayBankAlias;
import com.sogou.pay.service.entity.PayBankRouter;
import com.sogou.pay.service.entity.PayOrderInfo;
import com.sogou.pay.service.entity.PayOrderRelation;
import com.sogou.pay.service.entity.PayReqDetail;
import com.sogou.pay.service.payment.AgencyInfoService;
import com.sogou.pay.service.payment.AppService;
import com.sogou.pay.service.payment.ChannelService;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.service.payment.PayBankAliasService;
import com.sogou.pay.service.payment.PayBankRouterService;
import com.sogou.pay.service.payment.PayChannelRuleService;
import com.sogou.pay.service.payment.PayOrderRelationService;
import com.sogou.pay.service.payment.PayOrderService;
import com.sogou.pay.service.payment.PayReqDetailService;
import com.sogou.pay.service.utils.Constant;
import com.sogou.pay.service.utils.ThirdConfig;
import com.sogou.pay.service.utils.orderNoGenerator.SequenceFactory;

/**
 * @Author	huangguoqing 
 * @ClassName	PayManager 
 * @Date	2015年2月28日 
 * @Description:支付请求服务
 */
@SuppressWarnings("all")
@Component
public class PayManagerImpl implements PayManager {

    private static final Logger logger = LoggerFactory.getLogger(PayManagerImpl.class);

    @Autowired
    private AppService appService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private SequenceFactory sequencerGenerator;

    @Autowired
    private PayReqDetailService payReqDetailService;

    @Autowired
    private PayChannelRuleService payChannelRuleService;

    @Autowired
    private PayBankRouterService payBankRouterService;

    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;

    @Autowired
    private PayOrderRelationService payOrderRelationService;

    @Autowired
    private AgencyInfoService agencyInfoService;

    @Autowired
    private PayBankAliasService payBankAliasService;
    private static final String APP_INFO = "appInfo";

    //支付渠道类型 1：银行 2：第三方机构 3：扫码支付
    private static final int CHANNEL_TYPE_BANK = 1;
    
    private static final int CHANNEL_TYPE_AGENCY = 2;
    
    private static final int CHANNEL_TYPE_SY = 3;

    /**
     * @param params 支付请求参数
     * @return result
     * @Author huangguoqing
     * @MethodName confirmPay
     * @Date 2015年2月28日
     * @Description: 确认支付
     */
    @Profiled(el = true, logger = "dbTimingLogger", tag = "PayManager_confirmPay",
            timeThreshold = 100, normalAndSlowSuffixesEnabled = true)
    @Transactional
    public ResultMap confirmPay(PMap params) {
        logger.info("【支付请求】进入confirmPay()，参数为："+params);
        ResultMap result = ResultMap.build();
        //业务参数校验
        ResultMap checkResult = checkParams(params);
        if (!Result.isSuccess(checkResult)) {
            //校验业务参数失败
            logger.error(checkResult.getStatus().getMessage());
            return checkResult;
        }
        /*判断渠道是否是第三方余额支付*/
        String channelType = params.getString("channelType");
        String channelCode = params.getString("channelCode");
        Channel channel = channelService.selectChannelByCode(channelCode);
        String agencyCode = null;
        String bankCode = null;
        String insertBankCode = null;
        //付款方式 1，网银 2，第三方  3，扫码支付
        int payFeeType = 0;
        //若是SDK，则支付机构类型为第三方支付
        if(Integer.parseInt(params.getString("accessPlatform")) == 3){
          //渠道为第三方机构，机构编码即为渠道编码
            agencyCode = channelCode;
            //银行编码即为渠道编码
            insertBankCode = channelCode;
            payFeeType = 2;
        } else {
            if(null == channelType){
                if (CHANNEL_TYPE_BANK == channel.getChannelType()) {
                    //渠道为银行支付,路由出第三方支付机构
                    agencyCode = getBankRouter(params);
                    if (null == agencyCode) {
                        //路由第三方支付机构失败
                        result.withError(ResultStatus.PAY_BANK_ROUTER_NOT_EXIST);
                        return result;
                    }
                    insertBankCode = channelCode;
                    bankCode = channelCode;
                    payFeeType = 1;
                } else if(CHANNEL_TYPE_AGENCY == channel.getChannelType()){
                    //渠道为第三方机构，机构编码即为渠道编码
                    agencyCode = channelCode;
                    //银行编码即为渠道编码
                    insertBankCode = channelCode;
                    payFeeType = 2;
                } else {
                  //渠道为扫码支付，机构编码即为渠道编码
                    agencyCode = channelCode;
                    //银行编码即为渠道编码
                    insertBankCode = channelCode;
                    payFeeType = 3;
                }
            } else {
                int channelTypeId = Integer.parseInt(channelType);
                if (CHANNEL_TYPE_BANK == channelTypeId) {
                    //渠道为银行支付,路由出第三方支付机构
                    agencyCode = getBankRouter(params);
                    if (null == agencyCode) {
                        //路由第三方支付机构失败
                        result.withError(ResultStatus.PAY_BANK_ROUTER_NOT_EXIST);
                        return result;
                    }
                    insertBankCode = channel.getChannelCode();
                    bankCode = insertBankCode;
                    payFeeType = 1;
                } else if(CHANNEL_TYPE_AGENCY == channelTypeId){
                    //渠道为第三方机构，机构编码即为渠道编码
                    agencyCode = channel.getChannelCode();
                    //银行编码即为渠道编码
                    insertBankCode = agencyCode;
                    payFeeType = 2;
                } else if(CHANNEL_TYPE_SY == channelTypeId){
                  //渠道为扫码支付，机构编码即为渠道编码
                    agencyCode = channel.getChannelCode();
                    //银行编码即为渠道编码
                    insertBankCode = agencyCode;
                    payFeeType = 3;
                } else {
                    //B2B支付
                    agencyCode = getBankRouter(params);
                    if (null == agencyCode) {
                        //路由第三方支付机构失败
                        result.withError(ResultStatus.PAY_BANK_ROUTER_NOT_EXIST);
                        return result;
                    }
                    insertBankCode = channel.getChannelCode();
                    bankCode = insertBankCode;
                    payFeeType = 4;
                }
            }
        }
        /*获取机构商户信息*/
        //获得业务平台所属公司信息
        App app = appService.selectApp(params.getInt("appId"));
        if (null == app) {
            //业务平台不存在
            logger.error(ResultStatus.PAY_APP_NOT_EXIST.getMessage());
            result.withError(ResultStatus.PAY_APP_NOT_EXIST);
            return result;
        }
        PayAgencyMerchant agencyMerchant = new PayAgencyMerchant();
        agencyMerchant.setAgencyCode(agencyCode);
        agencyMerchant.setCompanyCode(app.getBelongCompany());
        agencyMerchant.setAppId(params.getInt("appId"));
        PayAgencyMerchant merchant = payAgencyMerchantService.selectPayAgencyMerchant(agencyMerchant);
        if(null == merchant){
            //支付机构商户不存在
            logger.error(ResultStatus.PAY_MERCHANT_NOT_EXIST.getMessage());
            result.withError(ResultStatus.PAY_MERCHANT_NOT_EXIST);
            return result;
        }
        /*生成支付流水单、支付单与支付流水对应信息*/
        //生成支付流水单
        String payDetailId = sequencerGenerator.getPayDetailId();
        Date payTime = new Date();
        try {
            PayReqDetail payReqDetail = new PayReqDetail();
            payReqDetail.setPayDetailId(payDetailId);
            payReqDetail.setAccessPlatform(Integer.parseInt(params.getString("accessPlatform")));
            payReqDetail.setPayFeeType(payFeeType);
            payReqDetail.setBalance(BigDecimal.ZERO);
            payReqDetail.setTrueMoney(new BigDecimal(params.getString("orderAmount")));
            payReqDetail.setAgencyCode(agencyCode);
            payReqDetail.setBankCode(insertBankCode);
            payReqDetail.setMerchantNo(merchant.getMerchantNo());
            if(!StringUtils.isEmpty(params.getString("bankCardType"))){
                payReqDetail.setBankCardType(params.getInt("bankCardType"));
            } else {
                //银行卡类型不区分
                payReqDetail.setBankCardType(3);
            }
            payReqDetail.setCreateTime(payTime);
            payReqDetailService.insertPayReqDetail(payReqDetail);
            //生成支付单与支付流水关联
            PayOrderRelation payOrderRelation = new PayOrderRelation();
            payOrderRelation.setPayDetailId(payReqDetail.getPayDetailId());
            payOrderRelation.setPayId(params.getString("payId"));
            payOrderRelation.setInfoStatus(0);//未支付
            payOrderRelation.setCreateTime(new Date());
            payOrderRelationService.insertPayOrderRelation(payOrderRelation);
        } catch (ServiceException e) {
            //拦截异常
            logger.error("【支付请求】生成支付单或支付单与支付流水单关联信息失败!");
            result.withError(e.getStatus());
            return result;
        }
        //支付成功将银行信息、支付机构、支付机构商户信息、支付流水号、支付时间回传
        result.addItem("agencyCode", agencyCode);
        result.addItem("bankCode", bankCode);
        result.addItem("agencyMerchant", merchant);
        result.addItem("payDetailId", payDetailId);
        result.addItem("payFeeType", payFeeType);
        result.addItem("payTime", payTime);
        return result;
    }

    /**
     * @param 支付请求参数
     * @return 第三方支付机构编码
     * @Author huangguoqing
     * @MethodName getBankRouter
     * @Date 2015年3月3日
     * @Description: 根据渠道路由规则路由出第三方机构编码
     */
    private String getBankRouter(PMap params) {
        //获得路由规则
        PayBankRouter payBankRouter = new PayBankRouter();
        payBankRouter.setBankCode(params.getString("channelCode"));
        if (!StringUtils.isEmpty(params.getString("bankCardType"))){
            payBankRouter.setBankCardType(Integer.parseInt(params.getString("bankCardType")));
        }
        payBankRouter.setAppId(Integer.parseInt(params.getString("appId")));
        payBankRouter.setRouterStatus(1);//已启用
        //查询第三方机构路由信息
        List<PayBankRouter> routerList = payBankRouterService.selectPayBankRouterList(payBankRouter);
        if (null == routerList) {
            //该渠道未能适配出第三方支付机构
            logger.error(ResultStatus.PAY_BANK_ROUTER_NOT_EXIST.getMessage());
            return null;
        }
        //根据路由信息路由出一个支付机构
        return routerAgency(routerList);
    }

    /**
     * @param 路由信息List
     * @return 支付机构编码
     * @Author huangguoqing
     * @MethodName routerAgency
     * @Date 2015年2月28日
     * @Description: 根据路由规则路由出一个支付机构
     */
    public String routerAgency(List<PayBankRouter> routerList) {
        //产生随机数(1到10000)
        int random = (int) (Math.random() * 10000) + 1;
        logger.info("路由随机数为：" + random);
        int routerListSize = routerList.size();
        int totalSacle = 0;
        for (int i = 0; i < routerListSize; i++) {
            totalSacle += routerList.get(i).getScale() * 10000;
            if (random <= totalSacle) {
                logger.info("路由支付机构为:" + routerList.get(i).getAgencyCode());
                return routerList.get(i).getAgencyCode();
            }
        }
        //不会走到以下语句
        return null;
    }

    /**
     * @param params 支付请求参数
     * @return result
     * @Author huangguoqing
     * @MethodName checkParams
     * @Date 2015年2月28日
     * @Description: 请求参数业务校验
     */
    public ResultMap checkParams(PMap params) {
        ResultMap result = ResultMap.build();
        //校验金额
        BigDecimal orderAmount = new BigDecimal(params.getString("orderAmount"));
        if (orderAmount.compareTo(orderAmount.ZERO) < 0) {
            //金额出错
            result.withError(ResultStatus.PAY_PARAM_ERROR);
            return result;
        }
        //校验支付渠道
        String channelCode = params.getString("channelCode");
        Channel channel = channelService.selectChannelByCode(channelCode);
        if (null == channel) {
            //渠道信息不存在
            result.withError(ResultStatus.PAY_CHANNEL_NOT_EXIST);
            return result;
        }
        return result;
    }

    /**
     * @param appId 业务平台编码
     * @return 签名KEY
     * @Date 2015年3月2日
     * @Description:根据业务平台编码获得该业务平台签名KEY
     */
    public ResultMap getSignKey(int appId) {
        ResultMap result = ResultMap.build();
        App app = appService.selectApp(appId);
        if (null == app) {
            //业务平台不存在
            logger.error(ResultStatus.PAY_APP_NOT_EXIST.getMessage());
            result.withError(ResultStatus.PAY_APP_NOT_EXIST);
            return result;
        }
        result.addItem(APP_INFO, app);
        return result;
    }

    /**
     * @param params 支付请求参数
     * @return 插入是否成功
     * @Date 2015年3月3日
     * @Description: 插入支付单信息
     */
    public ResultMap insertPayOrder(PMap params) {
        ResultMap result = ResultMap.build();
        //创建支付单信息
        try {
            PayOrderInfo payOrderInfo = new PayOrderInfo();
            Date date = new Date();
            String payId = sequencerGenerator.getPayId();
            payOrderInfo.setPayId(payId);
            payOrderInfo.setOrderType(1);
            payOrderInfo.setOrderId(params.getString("orderId"));
            String productInfo = "商品名称：" + params.getString("productName") + "，商品数量：" + params.getString("productNum");
            if(!StringUtils.isEmpty(params.getString("productDesc"))){
                productInfo += "，商品描述：" + params.getString("productDesc");
            }
            payOrderInfo.setProductInfo(productInfo);
            payOrderInfo.setOrderMoney(new BigDecimal(params.getString("orderAmount")));
            payOrderInfo.setBuyHomeIp(params.getString("userIp"));
            payOrderInfo.setBuyHomeAccount(params.getString("accountId"));
            payOrderInfo.setAccessPlatForm(Integer.parseInt(params.getString("accessPlatform")));
            payOrderInfo.setChannelCode(params.getString("bankId"));
            payOrderInfo.setPayOrderStatus(1);//未支付
            payOrderInfo.setRefundMoney(BigDecimal.ZERO);
            payOrderInfo.setRefundFlag(1);//未退款
            payOrderInfo.setAppPageUrl(params.getString("pageUrl"));
            payOrderInfo.setAppBgUrl(params.getString("bgUrl"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            payOrderInfo.setOrderCreateTime(sdf.parse(params.getString("orderTime")));
            payOrderInfo.setCreateTime(date);
            payOrderInfo.setPaySuccessTime(date);
            payOrderInfo.setAppId(Integer.parseInt(params.getString("appId")));
            payOrderInfo.setNotifyStatus(0);//未通知
            int flag = payOrderService.insertPayOrder(payOrderInfo);
            //插入支付单失败
            if (flag != 1) {
                logger.error(ResultStatus.PAY_INSERT_PAY_ORDER_ERROR.getMessage());
                result.withError(ResultStatus.PAY_INSERT_PAY_ORDER_ERROR);
            }
            //将支付单ID回传
            result.withReturn(payId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("【支付请求】insertPayOrder() " + ResultStatus.SYSTEM_ERROR.getMessage());
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }

    /**
     * @Author  huangguoqing 
     * @MethodName  selectPayOrderInfoByOrderId 
     * @param orderId 订单ID
     * @param appId 业务平台ID
     * @return 支付单信息
     * @Date    2015年3月17日
     * @Description:根据订单ID查询支付单信息
     */
    @Override
    public ResultMap selectPayOrderInfoByOrderId(String orderId,String appIdStr) {
        ResultMap result = ResultMap.build();
        PayOrderInfo info = null;
        try {
            Integer appId = Integer.parseInt(appIdStr);
            info =  payOrderService.selectPayOrderInfoByOrderId(orderId,appId);
            if(null != info && Constant.PAY_SUCCESS == info.getPayOrderStatus()){
                //该支付单已经支付成功
                result.withError(ResultStatus.PAY_ORDER_ALREADY_DONE);
            } else {
                result.withReturn(info);
            }
        } catch (ServiceException e) {
            result.withError(ResultStatus.SYSTEM_DB_ERROR);
            return result;
        }
        return result;
    }
    /**
     * @param params 支付请求参数
     * @return 支付网关所需要的参数
     * @Date 2015年3月4日
     * @Description: 根据请求参数组装支付网关所需要的参数
     */
    public ResultMap getPayGateParams(PMap params) {
        ResultMap<PMap> result = ResultMap.build();
        PMap payGateMap = new PMap();
        try{
            //获得支付机构信息
            String agencyCode = params.getString("agencyCode");
            String bankCode = params.getString("bankCode");
            String payFeeType = params.getString("payFeeType");
            String accessPlatfrom = params.getString("accessPlatform"); 
            AgencyInfo agencyInfo = agencyInfoService.getAgencyInfoByCode(agencyCode,accessPlatfrom,payFeeType);
            if(null == agencyInfo){
                logger.error(ResultStatus.PAY_AGENCY_NOT_EXIST.getMessage()+
                        ".agencyCode="+agencyCode + ",accessPlatform="+accessPlatfrom + ",agencyType"+payFeeType);
                result.withError(ResultStatus.PAY_AGENCY_NOT_EXIST);
                return result;
            }
            if (Constant.PAY_FEE_TYPE_1.equals(payFeeType)) {
                //网关支付
                //判断该支付机构的银行是否有别名
                payGateMap.put("bankCode", bankCode);
                String bankCardType = params.getString("bankCardType");
                boolean isAlias = false;
                
                if(StringUtils.isEmpty(bankCardType)){
                    //没有传递银行卡类型
                    if(3 == agencyInfo.getAliasFlag()){
                      //银行有别名，检索出银行别名
                        PayBankAlias payBankAlias = payBankAliasService.selectPayBankAlias(agencyCode, bankCode,null);
                        if (null != payBankAlias) bankCode = payBankAlias.getAliasName();
                        payGateMap.put("bankCode", bankCode);
                    }
                } else {
                    //传递了银行卡类型
                    if (params.getInt("bankCardType") == (agencyInfo.getAliasFlag()) ||  3 == agencyInfo.getAliasFlag()) {
                        //银行有别名，检索出银行别名
                        PayBankAlias payBankAlias = payBankAliasService.selectPayBankAlias(agencyCode, bankCode,params.getInt("bankCardType"));
                        if (null != payBankAlias) bankCode = payBankAlias.getAliasName();
                        payGateMap.put("bankCode", bankCode);
                    }
                }
            }
            if (Constant.PAY_FEE_TYPE_4.equals(payFeeType)) {
                //企业网银支付
                payGateMap.put("bankCode", bankCode);
            }
            payGateMap.put("agencyCode", agencyCode);
            payGateMap.put("payChannle", ThirdConfig.getInstanceName(accessPlatfrom, payFeeType));
            PayAgencyMerchant payAgencyMerchant = (PayAgencyMerchant) params.get("agencyMerchant");
            //在第三方支付机构商户号
            payGateMap.put("merchantNo", payAgencyMerchant.getMerchantNo());
            //收款账号对应邮箱(支付宝支付时必填)
            if (Constant.ALIPAY.equals(agencyCode) || Constant.WECHAT.equals(agencyCode)) 
                payGateMap.put("sellerEmail", payAgencyMerchant.getSellerEmail());
            //支付宝WAP、微信支付时使用（）
            payGateMap.put("prepayUrl", agencyInfo.getPrepayUrl());
            //PC端必填
            if (null != agencyInfo.getPayUrl()) payGateMap.put("payUrl", agencyInfo.getPayUrl());
//            //如果是alipay wap，从配置文件中获得回调地址
//            if(Constant.ALIPAY.equals(agencyCode) && Constant.ACCESS_PLATFORM_WAP.equals(accessPlatfrom)){
//                //异步回调地址
//                payGateMap.put("serverNotifyUrl", ResourceBundle.getBundle("config").getString("alipay.wap.notify.url"));
//                //同步页面回调地址
//                payGateMap.put("pageNotifyUrl", ResourceBundle.getBundle("config").getString("alipay.wap.page.url"));
//            } else if(Constant.ALIPAY.equals(agencyCode) && Constant.ACCESS_PLATFORM_SDK.equals(accessPlatfrom)){
//              //异步回调地址
//                payGateMap.put("serverNotifyUrl", ResourceBundle.getBundle("config").getString("alipay.sdk.notify.url"));
//            } else {
                //异步回调地址
                payGateMap.put("serverNotifyUrl", agencyInfo.getNotifyBackUrl());
                //同步页面回调地址
                payGateMap.put("pageNotifyUrl", agencyInfo.getPageBackUrl());
//            }
            //商品名称
            payGateMap.put("subject", params.getString("productName"));
            //买家IP
            payGateMap.put("buyerIp", params.getString("userIp"));
            //MD5加密密钥
            payGateMap.put("md5securityKey", payAgencyMerchant.getEncryptKey());
            //支付机构公钥证书路径
            payGateMap.put("publicCertFilePath", payAgencyMerchant.getPubKeypath());
            //本地私钥证书路径
            payGateMap.put("privateCertFilePath", payAgencyMerchant.getPrivateKeypath());
            //支付请求时间
            payGateMap.put("payTime", new SimpleDateFormat("yyyyMMddHHmmss").format(params.getDate("payTime")));
            //订单金额
            payGateMap.put("orderAmount", params.getString("orderAmount"));
            //支付请求流水号
            payGateMap.put("serialNumber", params.getString("payDetailId"));
            result.addItem("payGateMap",payGateMap);
        } catch (Exception e){
            logger.equals(e.getStackTrace());
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }

    /**
     * @Author  huangguoqing 
     * @MethodName  selectPayOrderInfoById
     * @param map 
     * @return 校验结果
     * @Date    2015年3月19日
     * @Description:校验订单信息
     */
    @Override
    public ResultMap checkPayOrderInfo(PMap map) {
        ResultMap result = ResultMap.build();
        try{
            String payId = map.getString("payId");
            String orderAmountStr = map.getString("orderAmount");
            if(StringUtils.isEmpty(orderAmountStr)){
                //订单金额出错
                logger.error("订单金额不能为空！");
                result.withError(ResultStatus.PAY_PARAM_ERROR);
                return result;
            }
            BigDecimal orderAmount = new BigDecimal(orderAmountStr);
            PayOrderInfo orderInfo = payOrderService.selectPayOrderById(payId);
            if(null == orderInfo){
                //支付单信息不存在
                logger.error("payOrderInfo is not found.payId = " + payId);
                result.withError(ResultStatus.PAY_ORDER_NOT_EXIST);
            }
            if(Constant.PAY_SUCCESS == orderInfo.getPayOrderStatus()){
                //已经支付成功
                result.withError(ResultStatus.PAY_ORDER_ALREADY_DONE);
            }
            if(orderInfo.getOrderMoney().compareTo(orderAmount) != 0){
                //金额不符
                logger.error("request orderAmont is not compare to the amount in DB!request amount=" + orderAmount
                        + " the amount in DB = " + orderInfo.getOrderMoney());
                result.withError(ResultStatus.PAY_ORDER_MONEY_ERROR);
            }
        } catch (Exception e){
            logger.error("【支付请求】checkPayOrderInfo() " + ResultStatus.SYSTEM_ERROR.getMessage());
            result.withError(ResultStatus.SYSTEM_ERROR);
        }
        return result;
    }
}