<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

<form action="${appUrl}" name="form1" id="form1" method="get">
    <c:forEach var="map" varStatus="status" items="${returnMap}">
        <input type="hidden" name='${map.key}' value='${map.value}' class="flag"/>
    </c:forEach>
</form>
<script type="text/javascript">
    document.form1.submit();
</script>
</body>
</html>
