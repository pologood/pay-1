<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>商户接口模拟测试页面</title>
    <script type="text/javascript" src="static/js/jq.js"></script>
    <script type="text/javascript">
        function doPay() {
            var channel = document.getElementById("bankId").value;
            if (channel == "WECHAT") {
                document.dbform.action = "http://center.pay.sogou.com/pay/doPayForWechat.j";
            } else {
                document.dbform.action = "http://center.pay.sogou.com/pay/doPay.j";
            }
            dbform.submit();
        }
        function getSignData() {
            $.ajax({
                type: "post",
                url: "http://center.pay.sogou.com/pay/getSignData.j",
                data: {
                    version: document.getElementById("version").value,
                    pageUrl: document.getElementById("pageUrl").value,
                    bgUrl: document.getElementById("bgUrl").value,
                    orderId: document.getElementById("orderId").value,
                    accountId: document.getElementById("accountId").value,
                    orderAmount: document.getElementById("orderAmount").value,
                    orderTime: document.getElementById("orderTime").value,
                    productName: document.getElementById("productName").value,
                    productNum: document.getElementById("productNum").value,
                    productDesc: document.getElementById("productDesc").value,
                    bankId: document.getElementById("bankId").value,
                    appId: document.getElementById("appId").value,
                    signType: document.getElementById("signType").value,
                    accessPlatform: document.getElementById("accessPlatform").value,
                    bankCardType: document.getElementById("bankCardType").value
                },
                dataType: "json",
                success: function (data) {
                    document.getElementById('sign').value = data.substring(1, data.length - 1);
                }
            });
        }

        function getQuerySignData() {
            $.ajax({
                type: "post",
                url: "http://center.pay.sogou.com/pay/getSignData.j",
                data: {
                    appId: document.getElementById("appIds").value,
                    orderId: document.getElementById("orderIds").value,
                    signType: document.getElementById("signTypes").value
                },
                dataType: "json",
                success: function (data) {
                    document.getElementById('signs').value = data.substring(1, data.length - 1);
                }
            });
        }

        function getRefundSignData() {
            $.ajax({
                type: "post",
                url: "http://center.pay.sogou.com/pay/getSignData.j",
                data: {
                    appId: document.getElementById("appIdss").value,
                    orderId: document.getElementById("orderIdss").value,
                    refundAmount: document.getElementById("refundAmountss").value,
                    bgurl: document.getElementById("bgurlss").value,
                    signType: document.getElementById("signTypess").value
                },
                dataType: "json",
                success: function (data) {
                    document.getElementById('signss').value = data.substring(1, data.length - 1);
                }
            });
        }
        function getQueryRefundSignData() {
            $.ajax({
                type: "post",
                url: "http://center.pay.sogou.com/pay/getSignData.j",
                data: {
                    appId: document.getElementById("appIdsss").value,
                    orderId: document.getElementById("orderIdsss").value,
                    signType: document.getElementById("signTypesss").value
                },
                dataType: "json",
                success: function (data) {
                    document.getElementById('signsss').value = data.substring(1, data.length - 1);
                }
            });
        }
    </script>
    <style type="text/css"><!--
    #left {
        float: left;
        height: 700px;
        width: 700px;
        border: 2px solid #0000FF;
    }

    #right {
        float: right;
        height: 700px;
        width: 600px;
        border: 2px solid #0000FF;
    }

    .right_div {
        height: 240px;
    }

    --></style>
</head>
<body>
<div>
    <div id="left">
        <h2>模拟业务线支付订单</h2>

        <form action="https://cash.sogou.com/pay/doPay.j" method="post" name="dbform">
            <input type="text" id="version" name="version" value="v1.0"/>版本号<br>
            <input type="text" id="pageUrl" name="pageUrl"
                   value="http://sg.pay.sogou.com/notify/ali/pay/testBgUrl">支付结果前台通知页面(不为空，必须是合法URL,字节数不超过256)<br>
            <input type="text" id="bgUrl" name="bgUrl" value="http://sg.pay.sogou.com/notify/ali/pay/testBgUrl">支付结果后台通知地址(不为空，必须是合法URL,字节数不超过256)<br>
            <input type="text" id="orderId" name="orderId"
                   value="OD<%=new SimpleDateFormat("yyyyMMddHHmmssS").format(Calendar.getInstance().getTime())%>">商户订单号(不为空，长度小于30，成功支付的订单不允许重复支付)<br>
            <input type="text" id="accountId" name="accountId" value="hgq">可为空付款方账号(账号在支付平台存在并开通)<br>
            <input type="text" id="orderAmount" name="orderAmount" value="0.01">订单金额(不能为空，必须是大于0.00浮点数DECIMAL(12,2))<br>
            <input type="text" id="orderTime" name="orderTime"
                   value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())%>">订单时间(不为空,一共14
            位，格式为：年[4 位]月[2 位]日[2 位]时[2 位]分[2 位]秒[2位])<br>
            <input type="text" id="productName" name="productName" value="测试商品">产品名称(字节数不能超过256字节)<br>
            <input type="text" id="productNum" name="productNum" value="1">商品数量(1到6位整型数字)<br>
            <input type="text" id="productDesc" name="productDesc" value="测试商品">商品描述(字节数不能大于400)<br>
            <input type="text" id="bankId" name="bankId" value="ALIPAY">银行编码（可空，必须与支付中心所使用的编码一致）<br>
            <input type="text" id="appId" name="appId" value="1999">业务平台标识(不为空)<br>
            <input type="text" id="signType" name="signType" value="0">签名类型（可空 0：MD5 1:SHA）<br>
            <input type="text" id="sign" name="sign" value="50D29CF5740C27E9419CB9BDA426B3A6">签名值（不可为空）<br>
            <input type="text" id="accessPlatform" name="accessPlatform" value="1">接入平台 1:PC 2:WAP 3：SDK<br>
            <input type="text" id="bankCardType" name="bankCardType" value="">银行卡类型 1，借记卡 2，信用卡<br>
            <input type="button" value="提交订单" onclick="doPay()">
            <input type="button" value="获得sign" onclick="getSignData()">
        </form>
    </div>
    <div id="right">
        <div id="left_1" class="left_div">
            <h2>模拟业务线支付订单查询接口</h2>

            <form action="pay.j" method="post">
                <input type="text" id="appIds" name="appId" value="1999">使用支付中心开设的业务编码<br>
                <input type="text" id="orderIds" name="orderId"
                       value="">商户系统订单号<br>
                <input type="text" id="signs" name="sign" value="50D29CF5740C27E9419CB9BDA426B3A6">签名值（不可为空）<br>
                <input type="text" id="signTypes" name="signType"
                       value="0">签名类型（可空 0：MD5 1:RSA）<br>
                <input type="submit" value="支付订单查询接口">
                <input type="button" value="获得sign" onclick="getQuerySignData()">
            </form>
        </div>

        <div id="right_2" class="right_div">
            <h2>模拟业务线订单退款接口</h2>

            <form action="/refund/" method="post">
                <input type="text" id="appIdss" name="appId" value="1999">使用支付中心开设的业务编码<br>
                <input type="text" id="orderIdss" name="orderId"
                       value="">商户系统订单号 必须为支付成功订单<br>
                <input type="text" id="refundAmount" name="refundAmount" value="0.01">退款金额(不能为空，必须是大于0.00浮点数DECIMAL(12,2))<br>
                <input type="text" id="bgurlss" name="bgurl"
                       value="http://center.pay.sogou.com/notify/ali/pay/testBgUrl">异步回调url<br>
                <input type="text" id="signss" name="sign" value="50D29CF5740C27E9419CB9BDA426B3A6">签名值（不可为空）<br>
                <input type="text" id="signTypess" name="signType"
                       value="0">签名类型（可空 0：MD5 1:RSA）<br>
                <input type="submit" value="提交退款请求">
                <input type="button" value="获得sign" onclick="getRefundSignData()">
            </form>
        </div>
        <div id="right_3" class="right_div">
            <h2>模拟业务线退款单查询接口</h2>

            <form action="refund.j" method="post">
                <input type="text" id="appIdsss" name="appId" value="1999">使用支付中心开设的业务编码<br>
                <input type="text" id="orderIdsss" name="orderId"
                       value="">商户系统订单号<br>
                <input type="text" id="signsss" name="sign" value="50D29CF5740C27E9419CB9BDA426B3A6">签名值（不可为空）<br>
                <input type="text" id="signTypesss" name="signType"
                       value="0">签名类型（可空 0：MD5 1:RSA）<br>
                <input type="submit" value="退款单查询">
                <input type="button" value="获得sign" onclick="getQueryRefundSignData()">
            </form>
        </div>
    </div>

</div>
</body>
</html>