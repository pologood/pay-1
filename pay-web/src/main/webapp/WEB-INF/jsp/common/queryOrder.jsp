<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>支付订单查询</title>
    <script type="text/javascript" src="/static/js/jquery-1.11.2.min.js"></script>
    <script type="text/javascript">
        function doPay() {
            document.dbform.action = "orderQuery/pay.j";
            dbform.submit();
        }
        function getQueryRefundSignData() {
            $.ajax({
                type: "post",
                url: "getQueryOrderSignData.j",
                data: {
                    appId: document.getElementById("appId").value,
                    orderId: document.getElementById("orderId").value,
                    signType: document.getElementById("signType").value
                },
                dataType: "json",
                success: function (data) {
                    document.getElementById('sign').value = data.substring(1, data.length - 1);
                }
            });
        }
    </script>
</head>
<body>
支付订单查询

<form action="/pay-web/pay" method="post" name="dbform">
    <input type="text" id="appId" name="appId" value="4000">使用支付中心开设的业务编码<br>
    <input type="text" id="orderId" name="orderId"
           value="OD1440657659558">商户系统订单号 必须为支付成功订单<br>
    <input type="text" id="sign" name="sign" value="50D29CF5740C27E9419CB9BDA426B3A6">签名值（不可为空）<br>
    <input type="text" id="signType" name="signType"
           value="0">签名类型（可空 0：MD5 1:RSA）<br>
    <input type="button" value="提交" onclick="doPay()">
    <input type="button" value="获得sign" onclick="getQueryRefundSignData()">
</form>
</body>
</html>