<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script type="text/javascript" src="/static/js/jquery-1.11.2.min.js"></script>
</head>
<body>
<input type="hidden" value="${errorCode}" id="errorCode"/>
<input type="hidden" value="${errorMessage}" id="errorMessage"/>
<input type="hidden" value="${appUrl}" id="appUrl"/>
<input type="hidden" value="${payFeeType}" id="payFeeType"/>
<form action="${appUrl}" name="form" id="form" method="get">
    <c:forEach var="map" varStatus="status" items="${returnMap}">
        <input type="hidden" name='${map.key}' value='${map.value}' class="flag"/>
    </c:forEach>
</form>
<script type="text/javascript">
    var errorCode = document.getElementById("errorCode").value;
    if(0 == errorCode){
        //是否是支付宝扫码
        if($("#payFeeType").val() == "3"){
            var paramString = "";
            $("#from").find(".flag").each(function(){
                paramString = paramString + $(this).attr("name") + "=" + $(this).val()+"&";
            });
            paramString = paramString.substring(0, paramString.length-1);
            window.parent.location.href=$("#appUrl").val()+"?"+paramString;
        } else {
            document.form.submit();
        }
    } else {
        window.parent.location.href='error.j';
    }
</script>

</body>
</html>
