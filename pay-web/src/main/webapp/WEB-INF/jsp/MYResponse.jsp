<%--
  Created by IntelliJ IDEA.
  User: huangguoqing
  Date: 2015/5/9
  Time: 11:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="java.util.*" %>

<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>支付宝手机网页支付</title>
</head>
<body>
aaaa
<button id="success" onclick="doForm(1)">支付成功</button>
<form name="form1" id="form1" method="post">
    <input type="hidden" name="sign" value="6750ae2e71567c88859faed2774f930a"/>
    <input type="hidden" name="v" value="1.0"/>
    <input type="hidden" name="sec_id" value="MD5"/>
    <input type="hidden" name="notify_data" value="${notify_data}"/>
    <input type="hidden" name="service" value="alipay.wap.trade.create.direct"/>
</form>
<script type="text/javascript">
    function doForm(flag){
    	if("1"==flag){
    		document.getElementById("form1").action="/pay-web/notify/ali/pay/wapAsync";  //赋值
    		document.getElementById("form1").submit();
    	}else{
    		document.getElementById("form1").action="doPay.j";  //赋值
    		document.getElementById("form1").submit();
    	}
    } 
</script>
</body>
</html>
