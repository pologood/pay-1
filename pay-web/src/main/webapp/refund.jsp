<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>订单退款</title>
    <script type="text/javascript" src="static/js/jquery-1.11.2.min.js"></script>
    <script type="text/javascript">
        function doRefund() {
            dbform.submit();
        }
        function getSign(){
            $.ajax({
                url: "api/pay/sign",
                type: "GET",
                data: {
                    appId: document.getElementById("appId").value,
                    orderId: document.getElementById("orderId").value,
                    refundAmount: document.getElementById("refundAmount").value,
                    bgUrl: document.getElementById("bgUrl").value,
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
订单退款

<form action="api/refund" method="post" name="dbform">
    <input type="text" id="appId" name="appId" value="1999">业务线ID<br>
    <input type="text" id="orderId" name="orderId"
           value="OD20150901115018285">业务线订单号<br>
    <input type="text" id="refundAmount" name="refundAmount"
           value="0.01">退款金额(不能为空，必须是大于0.00浮点数DECIMAL(12,2))<br>
    <input type="text" id="bgUrl" name="bgUrl"
           value="http://center.pay.sogou.com/notify/testBgUrl">异步回调url<br>
    <input type="text" id="sign" name="sign">签名值（不可为空）<br>
    <input type="text" id="signType" name="signType"
           value="0">签名类型（可空 0：MD5 1:RSA）<br>
    <input type="button" value="提交退款" onclick="doRefund()">
    <input type="button" value="获得sign" onclick="getSign()">
</form>
</body>
</html>