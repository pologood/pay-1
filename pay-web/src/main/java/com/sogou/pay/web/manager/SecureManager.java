package com.sogou.pay.web.manager;

import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.MapUtil;
import com.sogou.pay.common.utils.SignUtil;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SecureManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecureManager.class);

  public Result<?> verifyAppSign(Map<String, String> params, List<String> excludes, String key) {
    try {
      String sign = params.remove("sign");
      Result<?> result = doAppSign(params, excludes, key);
      if (!Result.isSuccess(result)) return result;
      if (Objects.equals(sign, params.get("sign"))) return result;
    } catch (Exception e) {
      LOGGER.error("[verifyAppSign]error, params={}, {}", params, e);
    }
    return ResultMap.build(ResultStatus.VERIFY_SIGN_ERROR);
  }

  public Result<?> doAppSign(Map<String, String> params, List<String> excludes, String key) {
    try {
      String text = MapUtil.buildSignSource(MapUtil.filter(params, excludes)) + key;
      String sign = SignType.MD5 == getSignType(params) ? SignUtil.md5Hex(text) : SignUtil.shaHex(text);
      params.put("sign", sign);
      return ResultMap.build().withReturn(params);
    } catch (Exception e) {
      LOGGER.error("[doAppSign]error, params={}, {}", params, e);
      return ResultMap.build(ResultStatus.SIGN_ERROR);
    }
  }

  private static SignType getSignType(Map<String, ?> map) {//0 for md5;1 for sha
    return Objects.equals(SignType.MD5.getValue(), MapUtils.getInteger(map, "signType")) ? SignType.MD5 : SignType.SHA;
  }

  enum SignType {
    MD5(0), SHA(1);

    private int value;

    private SignType(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

}
