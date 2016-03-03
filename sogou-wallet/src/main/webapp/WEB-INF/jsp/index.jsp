<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>wallet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <link href="${pageContext.request.contextPath}/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/css/wallet.css" rel="stylesheet">

    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/libs/jquery-1.10.2.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/libs/bootstrap.min.js"></script>

    <script type="text/javascript">

        function doPay(params){
            var formname = "payform";
            var payurl = params["payurl"];
            delete params["payurl"];
            var method = "POST";
            var form = document.createElement("form");
            form.setAttribute("id", formname);
            form.setAttribute("action", payurl);
            form.setAttribute("method", method);
            $.each(params, function(name, value){
                var input = document.createElement("input");
                input.setAttribute("name", name);
                input.setAttribute("value", value);
                form.appendChild(input);
            });
            form.hidden=true;
            document.body.appendChild(form);
            form.submit();
        }


        function doTopup(){
            var money = 0.01;
            var channel_code = 'ALIPAY';
            var url = '${pageContext.request.contextPath}/balance/topup?money='+money+'&channel_code='+channel_code;
            $.ajax({
                url: url,
                type: 'POST',
                dataType: 'json',
                contentType: false,
                processData: false
            }).done(function (ret, status, jqXHR) {
                var rsp = jqXHR.responseJSON;
                var params = rsp['data'];
                console.log(params);
                doPay(params);
            }).fail(function (jqXHR, textStatus) {
            });
        }




    </script>
</head>

<body>

<nav class="navbar navbar-default navbar-fixed-top" >
    <div class=" text-center" style="padding:10px 0px;">
        <h4>搜狗钱包</h4>
    </div>
</nav>

<div >
    <div class="container user-info" >
        <div class="row clearfix">
            <div class="col-xs-3 col-md-1 col-sm-offset-3 col-md-offset-4"  align="center">
                <img alt="User Pic" src="${pageContext.request.contextPath}/static/img/cat.jpg" class="img-circle img-responsive" style="padding:5px 0px;">
            </div>
            <div class="col-xs-6 col-md-2">

                <div style="color:#fff;">
                    <h4 id="openid" style="display: none">${openid}</h4>
                    <h4 id="uname">${uname}</h4>
                    <h4 id="balance">余额: ￥${balance}</h4>
                </div>
            </div>
        </div>
    </div>
    <div id="b-boxes" class="container wallet-content">
        <div class="row clearfix">
            <div onclick="doTopup();" id="topup" class="col-xs-4 col-md-4 b-box btn-default">
                <img alt="topup" src="${pageContext.request.contextPath}/static/img/topup.png" >
                <h4 class=" text-center">充值</h4>
            </div>

            <div onclick="location.href='${pageContext.request.contextPath}/balance/withdraw?money=0.01&channel_code=ALIPAY&paypwd=000000'" class="col-xs-4 col-md-4 b-box btn-default">
                <img alt="topup" src="${pageContext.request.contextPath}/static/img/withdraw.png" >
                <h4 class=" text-center">提现</h4>
            </div>

            <div onclick="location.href='${pageContext.request.contextPath}/balance/transfer?money=0.01&payeeid=1&paypwd=000000'" class="col-xs-4 col-md-4 b-box btn-default">
                <img alt="topup" src="${pageContext.request.contextPath}/static/img/transfer.png" >
                <h4 class=" text-center">转账</h4>
            </div>

            <div class="col-xs-4 col-md-4 b-box btn-default" style="border-bottom:2px solid #999">
                <img alt="topup" src="${pageContext.request.contextPath}/static/img/lucky.png" >
                <h4 class=" text-center">红包</h4>
            </div>
            <div class="col-xs-4 col-md-4 b-box btn-default" style="border-bottom:2px solid #999">
                <img alt="topup" src="${pageContext.request.contextPath}/static/img/transaction.png" >
                <h4 class=" text-center">交易记录</h4>
            </div>

            <div class="col-xs-4 col-md-4 b-box btn-default" style="border-bottom:2px solid #999">
                <img alt="topup" src="${pageContext.request.contextPath}/static/img/setting.png" >
                <h4 class=" text-center">设置</h4>
            </div>
        </div>
    </div>
</div>

<br>
<br>
</body>
</html>

