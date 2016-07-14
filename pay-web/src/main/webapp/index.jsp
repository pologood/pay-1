<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户订单数据(模拟1)</title>
<script type="text/javascript" src="static/js/jquery-1.11.2.min.js"></script>
<script type="text/javascript">
	function doPay(){
		var accessPlatform = document.getElementById("accessPlatform").value;
		if(accessPlatform == "1"){
			document.dbform.action="gw/pay/web";
		} else if (accessPlatform == "2"){
			document.dbform.action="gw/pay/wap";
		} else if (accessPlatform == "3"){
			document.dbform.action="api/pay/sdk";
		} else if (accessPlatform == "4"){
			document.dbform.action="api/pay/qrcode";
		}
		dbform.submit();
	}
	function getSign(){
		$.ajax({
			url: "api/pay/sign",
            type: "GET",
            data:{version : document.getElementById("version").value,
            	pageUrl : document.getElementById("pageUrl").value,
            	bgUrl : document.getElementById("bgUrl").value,
            	orderId : document.getElementById("orderId").value,
            	orderAmount : document.getElementById("orderAmount").value,
            	orderTime : document.getElementById("orderTime").value,
            	productName : document.getElementById("productName").value,
            	productNum : document.getElementById("productNum").value,
            	productDesc : document.getElementById("productDesc").value,
				channelCode : document.getElementById("channelCode").value,
            	appId : document.getElementById("appId").value,
            	signType : document.getElementById("signType").value,
            	accessPlatform : document.getElementById("accessPlatform").value,
            	bankCardType : document.getElementById("bankCardType").value,
				accountId : document.getElementById("accountId").value
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
商户订单数据
	<form action="gw/pay/web" method="post" name="dbform">
	<input type="text" id="version" name="version" value="v1.0"/>版本号(不可空)<br>
	<input type="text" id="pageUrl" name="pageUrl" value="http://center.pay.sogou.com/notify/testBgUrl">支付结果前台通知页面(不可空,必须是合法URL,不超过256个字符)<br>
    <input type="text" id="bgUrl" name="bgUrl" value="http://center.pay.sogou.com/notify/testBgUrl">支付结果后台通知地址(不可空,必须是合法URL,不超过256个字符)<br>
	<input type="text" id="orderId" name="orderId" value="OD<%=new SimpleDateFormat("yyyyMMddHHmmssS").format(Calendar.getInstance().getTime())%>">商户订单号(不可空,不超过32个字符,成功支付的订单不允许重复支付)<br>
	<input type="text" id="orderAmount" name="orderAmount" value="0.01">订单金额(不可空,单位元,大于0.00的浮点数DECIMAL(12,2))<br>
	<input type="text" id="orderTime" name="orderTime" value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())%>">订单时间(不可空,格式为yyyyMMddHHmmss)<br>
	<input type="text" id="productName" name="productName" value="测试商品">产品名称(不可空,不超过128个字符)<br>
	<input type="text" id="productNum" name="productNum" value="">商品数量(可空,1到6位整型数字)<br>
	<input type="text" id="productDesc" name="productDesc" value="">商品描述(可空,不超过512个字符)<br>
	<input type="text" id="channelCode" name="channelCode" value="ALIPAY">支付渠道编码(可空)<br>
	<input type="text" id="appId" name="appId" value="1999">业务线标识(不可空)<br>
	<input type="text" id="signType" name="signType" value="1">签名类型(不可空,0:MD5 1:SHA)<br>
	<input type="text" id="sign" name="sign" value="">签名值(不可空)<br>
	<input type="text" id="accessPlatform" name="accessPlatform" value="1">接入平台(不可空,1:PC 2:WAP 3:SDK 4:QRCode)<br>
    <input type="text" id="bankCardType" name="bankCardType" value="">银行卡类型(可空,1:储蓄卡 2:信用卡 3:不区分)<br>
	<input type="text" id="accountId" name="accountId" value="">付款方账号(可空,不超过32个字符)<br>
	<input type="button" value="提交订单" onclick="doPay()">
	<input type="button" value="获得sign" onclick="getSign()">
	</form>
	温馨提示：<br>
	1.支付渠道编码字段可空<br>
	  该字段为空，进入收银台。<br>
	  该字段可以填ALIPAY(支付宝)等，详见接口文档。<br>
	2.点击"获得sign"，再提交订单。
	  
</body>
</html>