package com.sogou.pay.manager.secure.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.sogou.pay.common.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.types.PMap;
import com.sogou.pay.manager.secure.SecureManager;
import com.sogou.pay.service.entity.App;
import com.sogou.pay.service.entity.PayAgencyMerchant;
import com.sogou.pay.thirdpay.biz.enums.AgencyType;
import com.sogou.pay.service.payment.AppService;
import com.sogou.pay.service.payment.PayAgencyMerchantService;
import com.sogou.pay.service.utils.DataSignUtil;
import com.sogou.pay.thirdpay.biz.utils.SecretKeyUtil;

/**
 * User: hujunfei
 * Date: 2015-01-05 11:42
 */
@Component
public class SecureManagerImpl implements SecureManager {

    private static final Logger logger = LoggerFactory.getLogger(SecureManagerImpl.class);

    private static final String DEFAULT_SIGN_TYPE = "0";

    @Autowired
    private AppService appService;
    @Autowired
    private PayAgencyMerchantService payAgencyMerchantService;

    @Override
    public Result verifyAppSign(Object params) {
        try {
            ResultMap result = ResultMap.build();
            Map paramMap = convertToMap(params);

            int appId = Integer.parseInt((String) paramMap.get("appId"));
            String signType = (String) paramMap.get("signType");
            String sign = (String) paramMap.remove("sign");
            if (StringUtil.isBlank(sign)) {
                return result.withError(ResultStatus.PAY_PARAM_ERROR);
            }

            App app = appService.selectApp(appId);
            if (app == null) {
                return result.withError(ResultStatus.PAY_APP_NOT_EXIST);
            }

            String key = app.getSignKey();
            boolean verifyResult = DataSignUtil.verifySign(packParams(paramMap, key), signType, sign);

            return verifyResult ? result : result.withError(ResultStatus.SIGNATURE_ERROR);
        } catch (Exception e) {
            logger.error("Verify App Sign Error: " + JSONUtil.Bean2JSON(params), e);
            return ResultMap.build(ResultStatus.PAY_SIGN_ERROR);
        }
    }

    @Override
    public Result appSign(Object params) {
        try {
            Map paramMap = convertToMap(params);
            int appId = Integer.parseInt((String) paramMap.get("appId")); // 不需要返回appId
            App app = appService.selectApp(appId);
            if (app == null) {
                logger.error("App Sign Error: App " + appId + " Not Existed, " + JSONUtil.Bean2JSON(paramMap));
                return ResultMap.build(ResultStatus.PAY_SIGN_ERROR);
            }
            String key = app.getSignKey();
            String sign = DataSignUtil.sign(packParams(paramMap, key), "0");
            paramMap.put("sign", sign);
            return ResultMap.build().withReturn(paramMap);
        } catch (Exception e) {
            logger.error("App Sign Error: " + JSONUtil.Bean2JSON(params), e);
            return ResultMap.build(ResultStatus.PAY_SIGN_ERROR);
        }
    }

    @Override
    public Result verifyThirdSign(Object params, int merchantId) {
        try {
            ResultMap result = ResultMap.build();
            Map paramMap = convertToMap(params);

            PayAgencyMerchant payAgencyMerchant = payAgencyMerchantService.selectPayAgencyMerchantById(merchantId);
            if (payAgencyMerchant == null) {
                return result.withError(ResultStatus.THIRD_REFUND_NOTIFY_PARAM_ERROR);
            }

            String key = payAgencyMerchant.getEncryptKey();
            boolean verifyResult;
            switch (AgencyType.getType(payAgencyMerchant.getAgencyCode())) {
                case ALIPAY:
                    verifyResult = verifyThirdAliSign(paramMap, key);
                    break;
                case TENPAY:
                    verifyResult = verifyThirdTenSign(paramMap, key);
                    break;
                default:
                    verifyResult = false;
            }
            return verifyResult ? result : result.withError(ResultStatus.THIRD_REFUND_NOTIFY_SIGN_ERROR);
        } catch (Exception e) {
            logger.error("Verify Third Sign Error: " + params, e);
            return ResultMap.build(ResultStatus.REFUND_PARAM_ERROR);
        }
    }

    @Override
    public Result verifyNotifySign(PMap<String, String> params, String agencyCode, String partner, String... platform) {
        try {
            ResultMap result = ResultMap.build();

            PayAgencyMerchant payAgencyMerchant = payAgencyMerchantService.selectByAgencyAndMerchant(agencyCode, partner);
            if (payAgencyMerchant == null) {
                logger.error(String.format("No payAgencyMerchant for: %s|agencyCode: %s|partner: %s", agencyCode, partner));
                return result.withError(ResultStatus.THIRD_NOTIFY_PARAM_ERROR);
            }

            String key = payAgencyMerchant.getEncryptKey();
            String pub_keypath = payAgencyMerchant.getPubKeypath();
            boolean verifyResult = false;
            switch (AgencyType.getType(payAgencyMerchant.getAgencyCode())) {
                case ALIPAY:
                    if(null == platform || platform.length == 0)
                        verifyResult = verifyThirdAliSign(params, key);
                    else if(null != platform && "wap".equals(platform[0]))
                        verifyResult = verifyThirdAliWapSign(params, key);
                    else if(null != platform && "sdk".equals(platform[0]))
                        verifyResult = verifyThirdAliSdkSign(params,pub_keypath);
                    break;
                case TENPAY:
                    verifyResult = verifyThirdTenSign(params, key);
                    break;
                case WECHAT:
                    verifyResult = verifyThirdTenSign(params, key);
                    break;
                case BILL99:
                    verifyResult = verifyThirdBillSign(params, pub_keypath);
                    break;
                default:
                    verifyResult = false;
            }
            return verifyResult ? result : result.withError(ResultStatus.THIRD_NOTIFY_SIGN_ERROR);
        } catch (Exception e) {
            logger.error("Verify Third Sign Error: " + params, e);
            return ResultMap.build(ResultStatus.THIRD_NOTIFY_PARAM_ERROR);
        }
    }

    private boolean verifyThirdAliSign(Map paramMap, String key) {
        PMap newMap = new PMap(paramMap);
        String sign = (String) newMap.remove("sign");
        String signType = (String) newMap.remove("sign_type");
        
        return SecretKeyUtil.aliCheckMd5sign(newMap, key, sign, CommonConstant.DEFAULT_CHARSET);
    }
    private boolean verifyThirdAliSdkSign(Map paramMap,String publicCertFilePath){
        
        PMap newMap = new PMap(paramMap);
        String sign = (String) newMap.remove("sign");
        newMap.remove("sign_type");
        return SecretKeyUtil.aliCheckRSAsign(newMap, sign, publicCertFilePath);
    }
    private boolean verifyThirdAliWapSign(Map paramMap, String key) {
        PMap newMap = new PMap(paramMap);
        String sign = (String) newMap.remove("sign");
        
        return SecretKeyUtil.aliWapCheckMd5sign(newMap, key, sign, CommonConstant.DEFAULT_CHARSET);
    }
    
    private boolean verifyThirdTenSign(Map paramMap, String key) {
        PMap newMap = new PMap(paramMap);
        String sign = (String) newMap.remove("sign");
        String signType = (String) newMap.get("sign_type");

        return SecretKeyUtil.tenCheckMd5sign(newMap, key, sign, CommonConstant.DEFAULT_CHARSET);
    }

    private boolean verifyThirdBillSign(Map paramMap, String pub_keypath) {
        PMap newMap = new PMap(paramMap);
        String sign = (String) newMap.remove("signMsg");

        return SecretKeyUtil.billCheckRSAsign(newMap, pub_keypath, sign);
    }
    
    private String packParams(Map paramMap, String secret) {
        if (paramMap == null) {
            return null;
        }
        List<String> keyList = new ArrayList<String>(paramMap.keySet());
        Collections.sort(keyList);

        //拼接k1=v1k2=v2
        StringBuilder paramStrBuilder = new StringBuilder();
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            Object value = paramMap.get(key);

            if (!StringUtils.isEmpty((String)value)) {
                paramStrBuilder.append(key).append("=").append(value.toString());
                if (i != keyList.size() - 1) {//拼接时，不包括最后一个&字符
                    paramStrBuilder.append("&");
                }
            }
        }

        //拼接secretKey
        paramStrBuilder.append(secret);
        return paramStrBuilder.toString();
        /*String encodeParam = "";
        try {
            encodeParam = URLEncoder.encode(paramStrBuilder.toString(), CommonConstant.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return encodeParam;*/
    }

    private Map convertToMap(Object params) {
        if (params instanceof Map) {
            return MapUtil.dropNulls((Map) params);
        } else {
            return BeanUtil.Bean2MapNotNull(params);
        }
    }
}
