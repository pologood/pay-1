<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户订单数据(模拟1)</title>
<script type="text/javascript" src="static/js/jq.js"></script>
<script type="text/javascript">
	function doPay(){
		var channel = document.getElementById("bankId").value;
		var accessPlatform = document.getElementById("accessPlatform").value;
		if(accessPlatform == "1"){
			document.dbform.action="gw/pay/web";
		} else if (accessPlatform == "2"){
			document.dbform.action="gw/pay/wap";
		} else if (accessPlatform == "3"){
			document.dbform.action="api/pay/sdk";
		}
		dbform.submit();
	}
	function getSignData(){
		$.ajax({ 
            type: "post", 
            url: "pay/getSignData.j", 
            data:{version : document.getElementById("version").value,
            	pageUrl : document.getElementById("pageUrl").value,
            	bgUrl : document.getElementById("bgUrl").value,
            	orderId : document.getElementById("orderId").value,
            	accountId : document.getElementById("accountId").value,
            	orderAmount : document.getElementById("orderAmount").value,
            	orderTime : document.getElementById("orderTime").value,
            	productName : document.getElementById("productName").value,
            	productNum : document.getElementById("productNum").value,
            	productDesc : document.getElementById("productDesc").value,
            	bankId : document.getElementById("bankId").value,
            	appId : document.getElementById("appId").value,
            	signType : document.getElementById("signType").value,
            	accessPlatform : document.getElementById("accessPlatform").value,
            	bankCardType : document.getElementById("bankCardType").value
          	     },
            dataType: "json", 
            success: function (data) {
            	document.getElementById('sign').value = data.substring(1, data.length-1);
            }
        });
	}
</script>
</head>
<body>
商户订单数据
	<form action="gw/pay/web" method="post" name="dbform">
	<input type="text" id="version" name="version" value="v1.0"/>版本号<br>
	<input type="text" id="pageUrl" name="pageUrl" value="http://center.pay.sogou.com/notify/ali/pay/testBgUrl">支付结果前台通知页面(不为空，必须是合法URL,字节数不超过256)<br>
    <input type="text" id="bgUrl" name="bgUrl" value="http://center.pay.sogou.com/notify/ali/pay/testBgUrl">支付结果后台通知地址(不为空，必须是合法URL,字节数不超过256)<br>
	<input type="text" id="orderId" name="orderId" value="OD<%=new SimpleDateFormat("yyyyMMddHHmmssS").format(Calendar.getInstance().getTime())%>">商户订单号(不为空，长度小于30，成功支付的订单不允许重复支付)<br>
	<input type="text" id="accountId" name="accountId" value="hgq">可为空付款方账号(账号在支付平台存在并开通)<br>
	<input type="text" id="orderAmount" name="orderAmount" value="0.01">订单金额(不能为空，必须是大于0.00浮点数DECIMAL(12,2))<br>
	<input type="text" id="orderTime" name="orderTime" value="<%=new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())%>">订单时间(不为空,一共14 位，格式为：年[4 位]月[2 位]日[2 位]时[2 位]分[2 位]秒[2位])<br>
	<input type="text" id="productName" name="productName" value="测试商品">产品名称(字节数不能超过256字节)<br>
	<input type="text" id="productNum" name="productNum" value="1">商品数量(1到6位整型数字)<br>
	<input type="text" id="productDesc" name="productDesc" value="">商品描述(字节数不能大于400)<br>
	<input type="text" id="bankId" name="bankId" value="ALIPAY">银行编码（可空，必须与支付中心所使用的编码一致）<br>
	<input type="text" id="appId" name="appId" value="1999">业务平台标识(不为空)<br>
	<input type="text" id="signType" name="signType" value="0">签名类型（可空 0：MD5 1:SHA）<br>
	<input type="text" id="sign" name="sign" value="50D29CF5740C27E9419CB9BDA426B3A6">签名值（不可为空）<br>
	<input type="text" id="accessPlatform" name="accessPlatform" value="1">接入平台  1:PC  2:WAP 3：SDK<br>
    <input type="text" id="bankCardType" name="bankCardType" value="">银行卡类型  1，借记卡  2，信用卡<br>
	<input type="button" value="提交订单" onclick="doPay()">
	<input type="button" value="获得sign" onclick="getSignData()">
	</form>
	温馨提示：<br>
	1.银行编码字段可空<br>
	  该字段为空，进入收银台。<br>
	  该字段可以填ALIPAY(支付宝)等，详见接口文档。<br>
	2.点击"获得sign"，再提交订单。
	  
</body>
</html>