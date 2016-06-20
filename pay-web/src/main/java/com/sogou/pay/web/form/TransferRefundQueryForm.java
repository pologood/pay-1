package com.sogou.pay.web.form;


public class TransferRefundQueryForm {
    /**
     * 应用ID
     */
    private String appId;
    /**
     * 签名
     */
    private String sign;
    /**
     * 签名方式
     */
    private String signType;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 收款方银行帐号
     */
    private String recBankacc;
    /**
     * 收款方真实姓名
     */
    private String recName;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRecBankacc() {
        return recBankacc;
    }

    public void setRecBankacc(String recBankacc) {
        this.recBankacc = recBankacc;
    }

    public String getRecName() {
        return recName;
    }

    public void setRecName(String recName) {
        this.recName = recName;
    }
}
