<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>PC网页支付</title>
</head>
<body>
<div style="width:810px; margin: 200px auto;">
    <div class="userform">
        <form id="congratulation_form" name="congratulation" method="post" action="/index">
            <div  class="conp">
                <p align="center"><img src="/static/img/loading.gif" width="60" height="60" /></p>
                <p><strong>尊敬的客户：</strong></p>
                <p><strong class="redtext">页面跳转中，请稍候……</strong></p>
                <p>感谢您对搜狗支付的信任和支持！</p>
                <p></p>
            </div>
        </form>
    </div>
</div>
<form name="form1" action="${payUrl}" method="post">
    <%--<c:forEach var="item" items="${bankMap}">
        <input type="text" name="${item.key}" value="${item.value}"/>
    </c:forEach>--%>
</form>
<script type="text/javascript">
    if(document.form1.action!=""){document.form1.submit();}
</script>
</body>
</html>
