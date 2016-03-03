package com.sogou.pay.wallet.manager;

import com.sogou.pay.common.types.ResultMap;
import com.sogou.pay.common.types.ResultStatus;
import com.sogou.pay.common.utils.Base64;
import com.sogou.pay.common.utils.JSONUtil;
import com.sogou.pay.common.utils.StringUtil;
import com.sogou.pay.wallet.service.AccountService;
import com.sogou.pay.wallet.service.entity.WalletUser;
import com.sogou.pay.wallet.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by xiepeidong on 2016/2/23.
 */
@Component
public class UserManager {
    private static Logger log = LoggerFactory.getLogger(UserManager.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    private Key key;

    public UserManager() {
        String keyFile = "e:" + ResourceBundle.getBundle("config").getString("pandora.sso.keyFile");
        key = loadPubKey(keyFile);
    }

    private Key loadPubKey(String keyFile) {
        try {
            String keyString = "";
            String line = "", tmp = null;
            FileReader fr = new FileReader(keyFile);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            while ((tmp = br.readLine()) != null) {
                keyString += line;
                line = tmp;
            }
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decode(keyString));
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
            return publicKey;

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("[loadPubKey] 加载公钥失败:", keyFile);
        }
        return null;
    }


    private String decrypt(Key publicKey, String decryptedText) {
        try {
            byte[] base64Decoded = Base64.decode(decryptedText);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(cipher.doFinal(base64Decoded), "UTF-8");
        } catch (Exception ex) {
            log.error("[decrypt] 解密失败:" + decryptedText);
        }
        return null;
    }


    public ResultMap addUser(String openid, String uname) {
        String payPasswd = "000000";
        if (userService.insertUser(openid, uname, payPasswd) > 0) {
            WalletUser user = userService.queryUserByOpenID(openid);
            if (accountService.insertBalance(user.getUid()) > 0 &&
                    accountService.insertLucky(user.getUid()) > 0    ) {
                return (ResultMap) ResultMap.build().withReturn(user);
            }
        }
        return ResultMap.build(ResultStatus.SYSTEM_DB_ERROR);
    }

    public ResultMap userLogin(String ptoken) {
        ResultMap result = ResultMap.build();
        //解析pandora返回的ptoken, 获取openid和uname
        if (ptoken == null) {
            log.error("[userLogin] ptoken不存在");
            result.withError(ResultStatus.WALLET_LOGIN_FAILED);
            return result;
        }
        Map<String, String> map = JSONUtil.JSON2Map(decrypt(key, ptoken));
        String openid = map.get("uid");
        String uname = map.get("name");
        if (openid == null || uname == null) {
            log.error("[userLogin] uid或name不存在:", map);
            result.withError(ResultStatus.WALLET_LOGIN_FAILED);
            return result;
        }
        //查询钱包user
        WalletUser user = userService.queryUserByOpenID(openid);
        if (user == null) {
            return addUser(openid, uname);
        }
        userService.updateUserLoginTime(openid);
        result.withReturn(user);
        return result;
    }


    public WalletUser queryUser(String openid, String uname) {
        if (!StringUtil.isEmpty(openid))
            return userService.queryUserByOpenID(openid);
        else if (!StringUtil.isEmpty(uname))
            return userService.queryUserByUname(uname);
        return null;
    }

    public ResultMap resetPayPasswd(int uid, String cur_pay_passwd, String new_pay_passwd) {
        if (userService.verifyPayPasswd(uid, cur_pay_passwd)) {
            if (userService.setPayPasswd(uid, new_pay_passwd) > 0)
                return ResultMap.build();
            else
                return ResultMap.build(ResultStatus.SYSTEM_DB_ERROR);
        } else
            return ResultMap.build(ResultStatus.WALLET_PAY_PASSWD_WRONG);
    }

    public ResultMap verifyPayPasswd(int uid, String pay_passwd) {
        if (userService.verifyPayPasswd(uid, pay_passwd))
            return ResultMap.build(ResultStatus.WALLET_PAY_PASSWD_WRONG);
        else
            return ResultMap.build();
    }
}
