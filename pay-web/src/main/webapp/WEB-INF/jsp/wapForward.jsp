<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>移动网页支付</title>
</head>
<body>

<form name="form1" action="${payUrl}" method="post">
</form>
<script type="text/javascript">
    if(document.form1.action!=""){document.form1.submit();}
</script>
</body>
</html>
