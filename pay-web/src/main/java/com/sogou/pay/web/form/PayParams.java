package com.sogou.pay.web.form;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import com.sogou.pay.common.constraint.Amount;
import com.sogou.pay.common.constraint.BankCardType;
import com.sogou.pay.common.constraint.Date;
import com.sogou.pay.common.constraint.PositiveNumber;


/**
 * @Author	huangguoqing 
 * @ClassName	PayParams 
 * @Date	2015年2月28日 
 * @Description:支付参数
 */
public class PayParams {
    
    @NotBlank(message = "版本号不能为空！")
    private String version;     //版本号
    
    @NotBlank(message = "商户订单号不能为空！")
    @Length(max = 32)
    private String orderId;     //商户订单号
    
    @NotBlank(message = "订单金额不能空,！")
    @DecimalMin(value="0.01",message="金额最小为0.01元")
    @Amount(message = "订单金额格式为必须是大于0的浮点数，整数部分最多10位，两位小数")
    private String orderAmount;  //订单金额
    
    @Date
    private String orderTime;   //订单时间

    @Length(max = 20)
    private String accountId;   //付款方账号
    
    @NotBlank(message = "商品名称不能为空！")
    @Length(max = 20)
    private String productName; //商品名称
    
    @NotBlank(message = "商品数量不能为空")
    @Length(max=6,message="商品数量最多为6位")
    @PositiveNumber
    private String productNum;   //商品数量
    
    @Length(max=400,message="商品描述最多400位")
    private String productDesc;  //商品描述
    
    private String bankId;       //  渠道ID(一期做有银行ID)
    
    @NotBlank(message = "业务ID不能为空！")
    @Digits(integer=6,fraction=0,message = "业务平台ID必须为整数")
    private String appId;        //业务平台ID
    
    @Digits(integer =1,fraction = 0)
    @Min(value = 1,message = "接入平台代码最小为1！")
    @Max(value = 3,message = "接入平台代码最大为3！")
    private String accessPlatform;//接入平台 1：PC 2：WAP 3：SDK
    
    @Digits(integer =1,fraction = 0,message = "签名类型必须为整数！")
    @Min(value = 0,message = "签名类型数值最小是0！")
    @Max(value = 1,message = "签名类型数值最大为1！")
    private String signType;     //签名类型 0：MD5 1：RSA
    
    private String sign;         //签名值
   
    @NotBlank(message = "前台通知地址不能为空！")
    @URL(message = "前台通知地址必须为URL格式！")
    @Length(max = 256)
    private String pageUrl;       // 前台通知地址
    
    @NotBlank(message = "后台通知地址不能为空！")
    @URL(message = "后台通知地址必须为URL格式！")
    @Length(max = 256)
    private String bgUrl;       // 后台通知地址
    
    private String appData;      //透传参数

    @BankCardType
    private String bankCardType; //银行卡类型

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = StringUtils.trim(version);
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = StringUtils.trim(orderId);
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = StringUtils.trim(orderAmount);
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = StringUtils.trim(orderTime);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = StringUtils.trim(accountId);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = StringUtils.trim(productNum);
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = StringUtils.trim(bankId);
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = StringUtils.trim(appId);
    }

    public String getAccessPlatform() {
        return accessPlatform;
    }

    public void setAccessPlatform(String accessPlatform) {
        this.accessPlatform = StringUtils.trim(accessPlatform);
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = StringUtils.trim(signType);
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = StringUtils.trim(sign);
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = StringUtils.trim(pageUrl);
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = StringUtils.trim(bgUrl);
    }

    public String getAppData() {
        return appData;
    }

    public void setAppData(String appData) {
        this.appData = StringUtils.trim(appData);
    }

    public String getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(String bankCardType) {
        this.bankCardType = StringUtils.trim(bankCardType);
    }
}
