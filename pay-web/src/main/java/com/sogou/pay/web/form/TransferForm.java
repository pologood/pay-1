package com.sogou.pay.web.form;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;


public class TransferForm {
    
    @NotBlank(message = "版本号不能为空！")
    private String version;     //版本号
    
    @NotBlank(message = "批次号不能为空！")
    @Length(max = 32,message = "批次号最长为32位！")
    private String batchNo;     //商户批次号

    @NotBlank(message = "业务ID不能为空！")
    @Digits(integer=6,fraction=0,message = "业务平台ID必须为整数")
    private String appId;        //业务平台ID
    
    @Digits(integer =1,fraction = 0,message = "签名类型必须为整数！")
    @Min(value = 0,message = "签名类型数值最小是0！")
    @Max(value = 1,message = "签名类型数值最大为1！")
    private String signType;     //签名类型 0：MD5
    
    @NotBlank(message="公司名称不能为空！")
    @Length(max = 16,message = "公司名称最长为16位！")
    private String companyName;
    
    @NotBlank(message="付款账号不能为空！")
    @Digits(integer=32,fraction=0,message = "付款账号必须为正整数，最长32位！")
    private String dbtAcc;//付款账号
    
    @NotBlank(message="分行代码不能为空！")
    @Length(max = 2,message = "分行代码最长为2位！")
    private String bbkNbr;//分行代码
    
    @NotBlank(message="签名值不能为空！")
    private String sign;         //签名值
   
    @NotBlank(message="备注不能为空！")
    @Length(max = 64,message = "备注最长为64位！")
    private String memo;         //备注
    
    @NotBlank(message="付款列表不能为空！")
    private String recordList;   //付款列表
    
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = StringUtils.trim(version);
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = StringUtils.trim(appId);
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

    public String getRecordList() {
        return recordList;
    }

    public void setRecordList(String recordList) {
        this.recordList = recordList;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getDbtAcc() {
        return dbtAcc;
    }

    public void setDbtAcc(String dbtAcc) {
        this.dbtAcc = dbtAcc;
    }

    public String getBbkNbr() {
        return bbkNbr;
    }

    public void setBbkNbr(String bbkNbr) {
        this.bbkNbr = bbkNbr;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
