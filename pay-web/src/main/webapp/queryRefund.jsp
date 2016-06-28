<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>退款单查询</title>
    <script type="text/javascript" src="static/js/jquery-1.11.2.min.js"></script>
    <script type="text/javascript">
        function doQuery() {
            dbform.submit();
        }
        function getSign(){
            $.ajax({
                url: "api/pay/sign",
                type: "GET",
                data: {
                    appId: document.getElementById("appId").value,
                    orderId: document.getElementById("orderId").value,
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
退款单查询

<form action="api/refund/query" method="post"  name="dbform">
    <input type="text" id="appId" name="appId" value="4000">业务线ID<br>
    <input type="text" id="orderId" name="orderId"
           value="OD1440657659558">业务线订单号<br>
    <input type="text" id="sign" name="sign">签名值（不可为空）<br>
    <input type="text" id="signType" name="signType"
           value="0">签名类型（可空 0：MD5 1:RSA）<br>
    <input type="button" value="提交" onclick="doQuery()">
    <input type="button" value="获得sign" onclick="getSign()">
</form>
</body>
</html>