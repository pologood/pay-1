<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>搜狗钱包</title>
</head>
<body>
<c:choose>
<c:when test="${success}=true">
	充值成功
</c:when>
    <c:otherwise>
        充值失败,${error}
    </c:otherwise>
</c:choose>
</body>
</html>