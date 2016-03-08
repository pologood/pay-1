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

<form name="form1" action="${payUrl}" method="post">
<%--    <c:forEach var="item" items="${aliwapData}">
        <input type="hidden" name="${item.key}" value="${item.value}"/>
    </c:forEach>--%>
</form>
<script type="text/javascript">
    document.form1.submit();
</script>
</body>
</html>
