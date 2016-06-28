<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>提交代付单</title>
    <script type="text/javascript" src="static/js/jquery-1.11.2.min.js"></script>
    <script type="text/javascript">
        function getRecordList() {
            var params = {
                payId: $("#payId").prop("value"),
                recBankacc: $("#recBankacc").prop("value"),
                recName: $("#recName").prop("value"),
                payAmt: $("#payAmt").prop("value"),
                bankFlg: $("#bankFlg").prop("value"),
                eacBank: $("#eacBank").prop("value"),
                eacCity: $("#eacCity").prop("value"),
                desc: $("#desc").prop("value"),
            };
            $.each(params, function (idx, item) {
                if (item == "")delete params[idx];
            });

            var recordList = JSON.stringify([params]);
            return recordList;
        }

        function doTransfer() {
            dbform.submit();
        }

        function getSign() {
            $("#recordList").prop("value", getRecordList());
            $.ajax({
                url: "api/pay/sign",
                type: "GET",
                data: {
                    version: document.getElementById("version").value,
                    batchNo: document.getElementById("batchNo").value,
                    appId: document.getElementById("appId").value,
                    companyName: document.getElementById("companyName").value,
                    dbtAcc: document.getElementById("dbtAcc").value,
                    bbkNbr: document.getElementById("bbkNbr").value,
                    memo: document.getElementById("memo").value,
                    recordList: document.getElementById("recordList").value,
                    signType: document.getElementById("signType").value
                },
                dataType: "json",
                success: function (ret, status, jqXHR) {
                    var rsp = jqXHR.responseJSON;
                    if (rsp.status == "SUCCESS") {
                        document.getElementById('sign').value = rsp.data.sign;
                    }
                }
            });
        }
    </script>
</head>
<body>
提交代付单

<form action="api/transfer" method="post" name="dbform" id="dbform">
    <input type="text" id="version" name="version" value="v1.0">版本号<br>
    <input type="text" id="batchNo" name="batchNo"
           value="BT<%=new SimpleDateFormat("yyyyMMddHHmmssS").format(Calendar.getInstance().getTime())%>">批次号<br>
    <input type="text" id="appId" name="appId" value="1999">业务线ID<br>
    <input type="text" id="companyName" name="companyName" value="搜狗科技">公司名称<br>
    <input type="text" id="dbtAcc" name="dbtAcc" value="591902896010504">付款方银行账号<br>
    <input type="text" id="bbkNbr" name="bbkNbr" value="59">银行分行代码<br>
    <input type="text" id="memo" name="memo" value="发工资">备注<br>
    <input type="text" id="recordList" name="recordList" hidden>
    <input type="text" id="payId"
           value="PAY<%=new SimpleDateFormat("yyyyMMddHHmmssS").format(Calendar.getInstance().getTime())%>">业务线单号<br>
    <input type="text" id="recBankacc" value="6225885910000108">收款方银行帐号<br>
    <input type="text" id="recName" value="Judy Zeng">收款方姓名<br>
    <input type="text" id="payAmt" value="1.00">付款金额<br>
    <input type="text" id="bankFlg">系统内标志，可选值Y：开户行是招商银行，N：开户行是他行，为空默认为招行<br>
    <input type="text" id="eacBank">他行户口开户行，当bankFlg=N时必填<br>
    <input type="text" id="eacCity">他行户口开户地，当bankFlg=N时必填<br>
    <input type="text" id="desc">付款说明<br>
    <input type="text" id="sign" name="sign">签名值（不可为空）<br>
    <input type="text" id="signType" name="signType" value="0">签名类型（可空 0：MD5 1:RSA）<br>
    <input type="button" value="提交" onclick="doTransfer()">
    <input type="button" value="获得sign" onclick="getSign()">
</form>
</body>
</html>