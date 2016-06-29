package com.sogou.pay.web.manager;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.service.utils.DataSignUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class SecureManager {

  private static final Logger logger = LoggerFactory.getLogger(SecureManager.class);

  private static final String DEFAULT_SIGN_TYPE = "0";

  public Result verifyAppSign(Map params, Map excludes, String secret) {
    try {
      String sign = (String) params.get("sign");
      Result result = doAppSign(params, excludes, secret);
      if (!Result.isSuccess(result))
        return result;
      Map resultMap = (Map) result.getReturnValue();
      String ourSign = (String) resultMap.get("sign");
      return Objects.equals(sign, ourSign) ? result : result.withError(ResultStatus.VERIFY_SIGN_ERROR);
    } catch (Exception e) {
      logger.error("[verifyAppSign] verify sign failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return ResultMap.build(ResultStatus.VERIFY_SIGN_ERROR);
    }
  }

  public Result doAppSign(Map params, Map excludes, String secret) {
    try {
      String signType = (String) params.get("signType");
      if (StringUtils.isBlank(signType)) {
        signType = DEFAULT_SIGN_TYPE;
        params.put("signType", signType);
      }
      String sign = DataSignUtil.sign(packParams(params, secret, excludes), signType);
      params.put("sign", sign);
      return ResultMap.build().withReturn(params);
    } catch (Exception e) {
      logger.error("[doAppSign] sign failed, params={}, {}", JSONUtil.Bean2JSON(params), e);
      return ResultMap.build(ResultStatus.SIGN_ERROR);
    }
  }

  private String packParams(Map paramMap, String secret, Map excludes) {
    List<String> keyList = new ArrayList<String>(paramMap.keySet());
    Collections.sort(keyList);

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < keyList.size(); i++) {
      String key = keyList.get(i);
      String value = (String) paramMap.get(key);
      if (StringUtils.isBlank(value) || (excludes != null && excludes.containsKey(key))) continue;
      sb.append(key).append("=").append(value.toString()).append("&");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(secret);
    return sb.toString();
  }
}
